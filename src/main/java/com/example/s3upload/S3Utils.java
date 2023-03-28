package com.example.s3upload;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.Optional;

@Service
public class S3Utils {

    private final AmazonS3 amazonS3;

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
}
