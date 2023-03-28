package com.example.s3upload;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@Entity
@Table
@Getter
@Setter
public class FileMeta {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "FILE_NAME")
    private String fileName;

    @Column(name = "FILE_PATH")
    private String filePath;

    @Column(name = "VERSION")
    private String version;

    @Column(name = "uploader")
    private String name;

    public FileMeta(String fileName, String filePath, String version, String name) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.version = version;
        this.name = name;
    }
    public FileMeta(String fileName, String filePath, String version) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.version = version;
    }
}
