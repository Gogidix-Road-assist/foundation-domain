package com.gogidix.rapidassist.orchestration.dispatching.infrastructure.adapter.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Local file system storage adapter
 * Disabled by default - enable via storage.type=local
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "storage.type", havingValue = "local")
public class FileStorageAdapter {

    @Value("${storage.local.path:/tmp/dispatching-service}")
    private String storagePath;

    /**
     * Upload file to local storage
     */
    public String upload(String key, InputStream content, String contentType) throws IOException {
        Path targetPath = Paths.get(storagePath, key);
        Files.createDirectories(targetPath.getParent());
        Files.copy(content, targetPath, StandardCopyOption.REPLACE_EXISTING);
        log.debug("Uploaded file to local storage: {}", targetPath);
        return targetPath.toString();
    }

    /**
     * Download file from local storage
     */
    public InputStream download(String key) throws IOException {
        Path targetPath = Paths.get(storagePath, key);
        log.debug("Downloading file from local storage: {}", targetPath);
        return Files.newInputStream(targetPath);
    }

    /**
     * Delete file from local storage
     */
    public void delete(String key) throws IOException {
        Path targetPath = Paths.get(storagePath, key);
        Files.deleteIfExists(targetPath);
        log.debug("Deleted file from local storage: {}", targetPath);
    }
}
