package com.example.ddmdemo.service.impl;

import com.example.ddmdemo.exceptionhandling.exception.NotFoundException;
import com.example.ddmdemo.exceptionhandling.exception.StorageException;
import com.example.ddmdemo.service.interfaces.FileService;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.http.Method;
import java.util.Collections;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class FileServiceMinioImpl implements FileService {

    private final MinioClient minioClient;

    @Value("${spring.minio.bucket}")
    private String bucketName;

    @Override
    public String store(MultipartFile file, String serverFilename) {
        if (file.isEmpty()) {
            throw new StorageException("Failed to store empty file.");
        }

        var originalFilenameTokens =
            Objects.requireNonNull(file.getOriginalFilename()).split("\\.");
        var extension = originalFilenameTokens[originalFilenameTokens.length - 1];

        try {
            PutObjectArgs args = PutObjectArgs.builder()
                .bucket(bucketName)
                .object(serverFilename + "." + extension)
                .headers(Collections.singletonMap("Content-Disposition",
                    "attachment; filename=\"" + file.getOriginalFilename() + "\""))
                .stream(file.getInputStream(), file.getInputStream().available(), -1)
                .build();
            minioClient.putObject(args);
        } catch (Exception e) {
            throw new StorageException("Error while storing file in Minio.");
        }

        return serverFilename + "." + extension;
    }

    @Override
    public void delete(String serverFilename) {
        try {
            RemoveObjectArgs args = RemoveObjectArgs.builder()
                .bucket(bucketName)
                .object(serverFilename)
                .build();
            minioClient.removeObject(args);
        } catch (Exception e) {
            throw new StorageException("Error while deleting " + serverFilename + " from Minio.");
        }
    }

    @Override
    public GetObjectResponse loadAsResource(String serverFilename) {
        try {
            // Get signed URL
            var argsDownload = GetPresignedObjectUrlArgs.builder()
                .method(Method.GET)
                .bucket(bucketName)
                .object(serverFilename)
                .expiry(60 * 5) // in seconds
                .build();
            var downloadUrl = minioClient.getPresignedObjectUrl(argsDownload);
            System.out.println(downloadUrl);

            // Get object response
            var args = GetObjectArgs.builder()
                .bucket(bucketName)
                .object(serverFilename)
                .build();
            return minioClient.getObject(args);
        } catch (Exception e) {
            throw new NotFoundException("Document " + serverFilename + " does not exist.");
        }
    }
}
