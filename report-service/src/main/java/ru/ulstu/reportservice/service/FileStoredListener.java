package ru.ulstu.reportservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import ru.ulstu.reportservice.config.RabbitConfig;
import ru.ulstu.reportservice.domain.ExportOutbox;
import ru.ulstu.reportservice.domain.OutboxStatus;
import ru.ulstu.reportservice.messaging.FileStoredEvent;
import ru.ulstu.reportservice.repository.ExportOutboxRepository;

// слушает подтверждение от file-storage-service и завершает отправку из outbox
@Component
public class FileStoredListener {

    private static final Logger log = LoggerFactory.getLogger(FileStoredListener.class);

    private final ExportOutboxRepository outboxRepository;
    private final int maxRetries;

    public FileStoredListener(ExportOutboxRepository outboxRepository,
            @Value("${outbox.max-retries:5}") int maxRetries) {
        this.outboxRepository = outboxRepository;
        this.maxRetries = maxRetries;
    }

    @RabbitListener(queues = RabbitConfig.STORED_QUEUE)
    public void onFileStored(FileStoredEvent event) {
        ExportOutbox outbox = outboxRepository.findById(event.exportId()).orElse(null);
        if (outbox == null || outbox.getStatus() == OutboxStatus.SENT) {
            return;
        }
        if (event.success()) {
            outbox.markSent();
            outboxRepository.save(outbox);
            log.info("csv-отчёт {} подтверждён файловым хранилищем", outbox.getFileName());
        } else {
            outbox.markRetryOrFailed(maxRetries);
            outboxRepository.save(outbox);
            if (outbox.getStatus() == OutboxStatus.FAILED) {
                log.error("csv-отчёт {} не удалось сохранить за {} попыток, останавливаюсь: {}",
                        outbox.getFileName(), maxRetries, event.errorMessage());
            } else {
                log.warn("файловое хранилище не смогло сохранить csv-отчёт {} (попытка {}/{}): {}, повторю отправку",
                        outbox.getFileName(), outbox.getRetryCount(), maxRetries, event.errorMessage());
            }
        }
    }
}
