package com.example.s3upload;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.securitylake.model.S3Exception;
import com.example.s3upload.excelparse.ApplicationException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
public class S3Utils {
    private final Logger log = org.slf4j.LoggerFactory.getLogger(S3Utils.class);
    private final AmazonS3 amazonS3;
    @Value("${aws.s3.bucket}")
    private String bucketName;

    public S3Utils(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    public void uploadFile(String path, String fileName, Optional<Map<String, String>> optionalMetaData, InputStream inputStream, String bucketName) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        optionalMetaData.ifPresent(map -> {
            if (!map.isEmpty()) {
                map.forEach(objectMetadata::addUserMetadata);
            }
        });
        try {
            amazonS3.putObject(path, fileName, inputStream, objectMetadata);
        } catch (AmazonServiceException e) {
            throw new IllegalStateException("Failed to upload the file", e);
        }

    }

    public URL getFileLocation(String bucketName, String filename) {
        return amazonS3.getUrl(bucketName, filename);
    }

    public URL generatePreSignedUrl(String bucketName, String filename) {
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucketName, filename)
                        .withMethod(com.amazonaws.HttpMethod.GET);
        return amazonS3.generatePresignedUrl(generatePresignedUrlRequest);
    }

    public String uploadMultipartFile(MultipartFile multipartFile, String directoryPrefix, String customerId, String directory, String filename) {
        return uploadFile(convertMultipartFileToFile(multipartFile), directoryPrefix, customerId, directory, filename);
    }

    public String uploadFile(File file, String directoryPrefix, String customerId, String directory, String filename) {
        String fileUrl;
        try {
            uploadFileToS3Bucket(new StringBuilder().append(directoryPrefix)
                    .append(directory).append("/").append(customerId).append("/").append(file.getName()).toString(), file);
            fileUrl = generatePresignedUrl(directoryPrefix, customerId, directory, file.getName());
        } catch (Exception e) {
            log.error("Error uploading file to S3 bucket", e);
            throw new ApplicationException("Unable to upload documents. Please try again later.");
        }
        return fileUrl;
    }

    public String generatePresignedUrl(String directoryPrefix, String customerId, String path, String filename) {
        if (filename == null) {
            return null;
        }

        try {
            StringBuilder keyUrl = new StringBuilder();
            keyUrl.append(bucketName)
                    .append("/").append(directoryPrefix)
                    .append(path).append("/").append(customerId)
                    .append("/").append(filename);

            Date expiration = new Date();
            long expTimeMillis = expiration.getTime();
            expTimeMillis += 1000 * 60 * 5;
            expiration.setTime(expTimeMillis);

            GeneratePresignedUrlRequest generatePresignedUrlRequest =
                    new GeneratePresignedUrlRequest("", keyUrl.toString())
                            .withMethod(com.amazonaws.HttpMethod.GET)
                            .withExpiration(expiration);
            URL url = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);
            return url.toString();
        } catch (AmazonServiceException ase) {
            log.error(ase.getErrorMessage(), ase);
            throw new RuntimeException(ase);
        } catch (SdkClientException sce) {
            log.error(sce.getMessage(), sce);
            throw new RuntimeException(sce);
        }
    }

    private void uploadFileToS3Bucket(String filenameWithPath, File file) {
        amazonS3.putObject(new PutObjectRequest(bucketName, filenameWithPath, file));
    }

    private File convertMultipartFileToFile(MultipartFile multipartFile) {
        try {
            File convFile = new File(multipartFile.getOriginalFilename());
            FileOutputStream fos = new FileOutputStream(convFile);
            fos.write(multipartFile.getBytes());
            fos.close();
            return convFile;
        } catch (IOException e) {
            log.error("Error converting multipartFile to file", e);
            throw new ApplicationException("Unable to upload documents. Please try again later.");
        }
    }

    public void deleteFile(String sourceKey) {
        try {
            amazonS3.deleteObject(bucketName, sourceKey);
        } catch (Exception e) {
            log.error("Error deleting file from S3 bucket", e);
        }
    }

    public String moveFile(String sourceBucketName, String sourceKey, String destinationBucketName, String destinationKey) {
        CopyObjectRequest copyObjectRequest = new CopyObjectRequest(sourceBucketName, sourceKey, destinationBucketName, destinationKey);
        try {
            return amazonS3.copyObject(copyObjectRequest).toString();
        } catch (S3Exception exc) {
            log.error("Error copying file from S3 bucket", exc);
            throw new ApplicationException("Unable to upload documents. Please try again later.");
        }
    }
}
