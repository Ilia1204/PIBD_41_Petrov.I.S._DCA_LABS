package ru.ulstu.diskservice.client;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class FileStorageClient {

    private static final Logger log = LoggerFactory.getLogger(FileStorageClient.class);
    private static final String FILE_STORAGE_SERVICE_URL = "http://file-storage-service";

    private final RestTemplate restTemplate;
    private final Executor fileStorageExecutor;

    public FileStorageClient(RestTemplate restTemplate,
            @Qualifier("fileStorageExecutor") Executor fileStorageExecutor) {
        this.restTemplate = restTemplate;
        this.fileStorageExecutor = fileStorageExecutor;
    }

    // не блокируем вызывающий поток на время похода в file-storage-service
    public CompletableFuture<byte[]> downloadReportAsync(String fileName) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("выполняю запрос файла {} к file-storage-service в потоке {}",
                    fileName, Thread.currentThread().getName());
            return restTemplate.getForObject(FILE_STORAGE_SERVICE_URL + "/api/files/{fileName}",
                    byte[].class, fileName);
        }, fileStorageExecutor);
    }
}
