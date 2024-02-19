package com.example.s3upload.springintegrationtest.dto;

import lombok.*;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
@Component
@ToString
public class SftpInfoDto {

    private Long id;
    private String fileCategory;
    private String filePrefix;
    private String fileDownloadPath;
    private String remoteDirectoryPath;
    private String fileUploadPath;
    private String fileDownloadTimeStart;
    private String fileDownloadTimeEnd;
    private int frequency;
    private String fileType;
    private String invokeService;
    private String cronValue;
    private String intimationUrlContext;

    public SftpInfo toEntity() {
        return new SftpInfo().toBuilder()
                .fileCategory(this.fileCategory)
                .fileDownloadPath(this.fileDownloadPath)
                .fileDownloadTimeEnd(this.fileDownloadTimeEnd)
                .fileDownloadTimeStart(this.fileDownloadTimeStart)
                .filePrefix(this.filePrefix)
                .fileUploadPath(this.fileUploadPath)
                .frequency(this.frequency)
                .remoteDirectoryPath(this.remoteDirectoryPath)
                .id(this.id)
                .fileType(this.fileType)
                .invokeService(this.invokeService)
                .cronValue(this.cronValue)
                .intimationUrlContext(this.intimationUrlContext)
                .build();
    }
}
