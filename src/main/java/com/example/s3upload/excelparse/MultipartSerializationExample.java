package com.example.s3upload.excelparse;

import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class MultipartSerializationExample {

    private void test() {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(MultipartFile.class, getMultipartSerialization());
        objectMapper.registerModule(module);
    }

    private JsonSerializer<MultipartFile> getMultipartSerialization() {
        return new JsonSerializer<MultipartFile>() {
            @Override
            public void serialize(MultipartFile multipartFile, com.fasterxml.jackson.core.JsonGenerator jsonGenerator, com.fasterxml.jackson.databind.SerializerProvider serializerProvider) throws IOException, IOException {
                jsonGenerator.writeStartObject();
                jsonGenerator.writeStringField("name", multipartFile.getName());
                jsonGenerator.writeStringField("originalFilename", multipartFile.getOriginalFilename());
                jsonGenerator.writeStringField("contentType", multipartFile.getContentType());
                jsonGenerator.writeNumberField("size", multipartFile.getSize());
                jsonGenerator.writeEndObject();
            }
        };
    }
}
