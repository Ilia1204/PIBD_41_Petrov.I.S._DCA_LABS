package ru.ulstu.diskservice.config;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AsyncConfig {

    // отдельный пул для асинхронных запросов в file-storage-service, чтобы не мешать остальным задачам
    @Bean("fileStorageExecutor")
    public Executor fileStorageExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("file-storage-");
        executor.initialize();
        return executor;
    }
}
