package com.example.s3upload;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileMetaRepository extends CrudRepository<FileMeta, Integer> {

    Optional<FileMeta> findByName(String uploader);

    @Modifying
    @Query("Update FileMeta fm set fm.fileName = replace(fm.fileName, fm.fileName, :fileName), " +
            "fm.filePath = replace(fm.filePath, fm.filePath, :filePath) " +
            "where fm.name = :name")
    void updateFileNameAndPath(String name, String fileName, String filePath);
}
