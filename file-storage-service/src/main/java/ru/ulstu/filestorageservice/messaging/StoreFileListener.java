package ru.ulstu.filestorageservice.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import ru.ulstu.filestorageservice.config.RabbitConfig;
import ru.ulstu.filestorageservice.service.FileStorageService;

// принимает команду на сохранение csv-отчёта и шлёт подтверждение обратно в report-service
@Component
public class StoreFileListener {

    private static final Logger log = LoggerFactory.getLogger(StoreFileListener.class);

    private final FileStorageService service;
    private final RabbitTemplate rabbitTemplate;

    public StoreFileListener(FileStorageService service, RabbitTemplate rabbitTemplate) {
        this.service = service;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = RabbitConfig.STORE_QUEUE)
    public void onStoreFile(StoreFileCommand command) {
        try {
            service.storeForExport(command.exportId(), command.fileName(), command.content());
            log.info("csv-отчёт {} сохранён в файловом хранилище (экспорт {})",
                    command.fileName(), command.exportId());
            rabbitTemplate.convertAndSend(RabbitConfig.STORED_QUEUE,
                    new FileStoredEvent(command.exportId(), true, null));
        } catch (Exception ex) {
            log.warn("не удалось сохранить csv-отчёт {} (экспорт {}): {}",
                    command.fileName(), command.exportId(), ex.getMessage());
            rabbitTemplate.convertAndSend(RabbitConfig.STORED_QUEUE,
                    new FileStoredEvent(command.exportId(), false, ex.getMessage()));
        }
    }
}
