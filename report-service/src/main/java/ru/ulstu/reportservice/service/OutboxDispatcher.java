package ru.ulstu.reportservice.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import ru.ulstu.reportservice.client.FileStorageClient;
import ru.ulstu.reportservice.domain.ExportOutbox;
import ru.ulstu.reportservice.domain.OutboxStatus;
import ru.ulstu.reportservice.repository.ExportOutboxRepository;

@Component
public class OutboxDispatcher {

    private static final Logger log = LoggerFactory.getLogger(OutboxDispatcher.class);

    private final ExportOutboxRepository outboxRepository;
    private final FileStorageClient fileStorageClient;

    public OutboxDispatcher(ExportOutboxRepository outboxRepository, FileStorageClient fileStorageClient) {
        this.outboxRepository = outboxRepository;
        this.fileStorageClient = fileStorageClient;
    }

    @Scheduled(fixedDelayString = "${outbox.dispatch-interval-ms:5000}")
    public void dispatchPending() {
        List<ExportOutbox> pending = outboxRepository.findByStatus(OutboxStatus.PENDING);
        for (ExportOutbox outbox : pending) {
            try {
                fileStorageClient.upload(outbox.getContent(), outbox.getFileName());
                outbox.markSent();
                outboxRepository.save(outbox);
                log.info("csv-отчёт {} отправлен в файловое хранилище", outbox.getFileName());
            } catch (Exception ex) {
                log.warn("не удалось отправить csv-отчёт {} в файловое хранилище, повторю позже: {}",
                        outbox.getFileName(), ex.getMessage());
            }
        }
    }
}
