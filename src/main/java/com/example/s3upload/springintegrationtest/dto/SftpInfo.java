package com.example.s3upload.springintegrationtest.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(name = "batch_file_metadata")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SftpInfo {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;
    @Column(name = "file_category")
    private String fileCategory;
    @Column(name = "file_prefix")
    private String filePrefix;
    @Column(name = "file_download_path")
    private String fileDownloadPath;
    @Column(name = "remote_directory_path")
    private String remoteDirectoryPath;
    @Column(name = "file_upload_path")
    private String fileUploadPath;
    @Column(name = "file_download_time_start")
    private String fileDownloadTimeStart;
    @Column(name = "file_download_time_end")
    private String fileDownloadTimeEnd;
    @Column(name = "frequency")
    private int frequency;
    @Column(name = "file_type")
    private String fileType;
    @Column(name = "invoke_service")
    private String invokeService;
    @Column(name = "cron_value")
    private String cronValue;
    @Column(name = "intimation_url_context")
    private String intimationUrlContext;

    public SftpInfoDto toDto() {
        return new SftpInfoDto().toBuilder()
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
