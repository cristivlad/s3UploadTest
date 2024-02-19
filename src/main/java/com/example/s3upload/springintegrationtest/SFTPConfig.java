package com.example.s3upload.springintegrationtest;

import com.example.s3upload.springintegrationtest.dto.SftpConfigLock;
import com.example.s3upload.springintegrationtest.dto.SftpInfoDto;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.GenericHandler;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.sftp.dsl.Sftp;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Configuration
@EnableIntegration
@IntegrationComponentScan
@Slf4j
public class SFTPConfig {

    @Autowired @Qualifier("cachedSftpSessionFactory") private DefaultSftpSessionFactory cachedSftpSessionFactory;

    @Autowired private SFTPConfigLockRepository sftpConfigLockRepository;

    public IntegrationFlow uploadImgSFTP(SftpInfoDto sftpInfo) {
        log.info("Initializing batch service: file category={}, fileDownloadPath={}, fileType={}, invokeService={}, filePrefix={}",
                sftpInfo.getFileCategory(), sftpInfo.getFileDownloadPath(), sftpInfo.getFileType(), sftpInfo.getInvokeService(), sftpInfo.getFilePrefix());
        return IntegrationFlows.from(
                Sftp.inboundAdapter(this.cachedSftpSessionFactory)
                        .remoteDirectory(sftpInfo.getRemoteDirectoryPath())
                        .regexFilter(sftpInfo.getFileType() == null || sftpInfo.getFileType().equals("") ? ".*..*$" : sftpInfo.getFileType())
                        .deleteRemoteFiles(true)
                        .localDirectory(new File(sftpInfo.getFileDownloadPath())),
                sftpInfo.getFrequency() == 0 && sftpInfo.getCronValue() != null ?
                        c -> c.poller(Pollers.cron(sftpInfo.getCronValue())).maxMessagesPerPoll(5) :
                        c -> c.poller(Pollers.fixedRate(sftpInfo.getFrequency()).maxMessagesPerPoll(5)))
                .handle(GenericHandler<File>) (file, messageHeaders) -> {
            Optional<SftpConfigLock> sftpConfigLock = sftpConfigLockRepository.findFirstByLockedFor(file.getName());
            if (sftpConfigLock.isEmpty()) {
                try {
                    log.info("Acquiring lock for processing file prefix: ".concat(sftpInfo.getFilePrefix()));
                    SftpConfigLock savedLock = sftpConfigLockRepository.save(SftpConfigLock.builder()
                                    .locked(true)
                                    .lockGranted(LocalDateTime.now())
                                    .lockedBy("processor service")
                            .lockedFor(file.getName()).build());

                    String path = DESTINATION_PATH + file.getName();

                    if (sftpInfo.getFilePrefix().equals(YAP_PREPAID_REPORT)) {
                        PGPFileProcessor p = new PGPFileProcessor();
                        p.setInputFileName(String.format(file.getAbsolutePath()));
                        String pgpPath = path.replaceAll(path.substring(path.lastIndexOf(".") + path.length()), ".pgp");
                        p.setOutputFileName(pgpPath);
                        p.setPassphrase(PASSPHRASE);
                        p.setPublicKeyFilePath(new String(Base64.getDecoder().decode(E_KEY_ENCODED)));
                        try {
                            p.encrypt();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        File pgpReport = new File(pgpPath);
                        documentAdapter.uploadFileOnRakSftp(pgpReport, combineRemoteSFTPPath(RAK_SFTP_DIR_PATH) + pgpReport.getName());
                    }

                    if (!sftpInfoService.findBatchFileByFileName(file.getName()).isPresent()) {
                        BatchFileInfo fileInfo = new BatchFileInfo().toBuilder()
                                .fileName(file.getName())
                                .fileType(sftpInfo.getFileType())
                                .intimatedService(sftpInfo.getInvokeService())
                                .download(true)
                                .receivedTime(LocalDateTime.now())
                                .build();
                        sftpInfoService.saveBatchFileInfo(fileInfo);
                        String filePath = sftpInfo.getFileUploadPath() + sftpInfo.getFileCategory() + "/" + file.getName();

                        documentAdapter.uploadFile(file, filePath);
                        fileInfo.setUpload(true);
                        sftpInfoService.saveBatchFileInfo(fileInfo);
                        if (sftpInfo.getInvokeService().equals(ADMIN.value())) {
                            adminAdapter.sendFileIntimation(filePath, sftpInfo.getIntimationUrlContext());
                        } else if (sftpInfo.getInvokeService().equals(TRANSACTIONS.value())) {
                            transactionAdapter.postBatchFile("/api/batch-file/" + sftpInfo.getFileCategory() + "?filename=", file.getName() + "&filePath=" +
                                    filePath + "&dateTime=" + LocalDateTime.now(), null);
                        } else if (sftpInfo.getInvokeService().equals(PARTNER_BANK.value())) {
                            rakUploadService.uploadDocumentToRakSFTP(file.getName(), filePath, Constant.TEXT_TYPE_CONTENT,
                                    Constant.EXCEL_FILE_EXT, sftpInfo.getFileCategory(), sftpInfo.getIntimationUrlContext());
                        }
                        fileInfo.setIntimated(true);
                        sftpInfoService.saveBatchFileInfo(fileInfo);
                    } else {
                        log.error("File {} already processed", file.getName());
                    }
                    log.info("flow completed releasing log for file prefix: ".concat(sftpInfo.getFilePrefix()));
                    sftpConfigLockRepository.delete(savedLock);
                } catch (DataIntegrityViolationException dataViolationException) {
                    log.error("DataIntegrityViolationException occurred while processing file prefix: ".concat(sftpInfo.getFilePrefix()), dataViolationException);
                } catch (Exception e) {
                    log.error("Exception occurred while processing file prefix: ".concat(sftpInfo.getFilePrefix()), e);
                    sftpConfigLockRepository.findAllByLockedFor(file.getName().forEach(sftpConfigLock1 -> sftpConfigLockRepository.delete(sftpConfigLock1)));
                }
            } else {
                log.error("File {} already locked for processing", file.getName());
            }
            return null;
        }
        )
        .log(LoggingHandler.Level.WARN, "headers.id+ ': ' + payload")
                .get();
    }

    @Bean
    @ServiceActivator(inputChannel = "inputChannelExample")
    public MessageHandler handler() {
        SftpMessageHandler handler = new SftpMessageHandler(cachedSftpSessionFactory);
        handler.setRemoteDirectoryExpression(new LiteralExpression(EXCHANGE_RATE_REMOTE_PATH));
        handler.setFileNameGenerator(new FileNameGenerator() {
            @Override
            public String generateFileName(Message<?> message) {
                if (message.getPayload() instaceof File) {
                    return ((File) message.getPayload()).getName();
                } else {
                    throw new IllegalArgumentException("File expected as payload.");
                }
            }
        });
        return handler;
    }

    @MessagingGateway
    public interface UploadGateway {
        @Gateway(requestChannel = "inputChannelExample")
        void upload(File file);
    }

    @Bean(name = "cachedSftpSessionFactory")
    public DefaultSftpSessionFactory sftpSessionFactory() {
        DefaultSftpSessionFactory factory = new DefaultSftpSessionFactory(true);
        factory.setHost("host");
        factory.setPort(1234);
        factory.setUser("user");
        factory.setPassword("pwd");
        factory.setAllowUnknownKeys(true);
        return factory;
    }
}
