package ru.ulstu.reportservice.client;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import ru.ulstu.reportservice.config.RabbitConfig;
import ru.ulstu.reportservice.domain.ExportOutbox;
import ru.ulstu.reportservice.messaging.StoreFileCommand;

@Component
public class FileStorageClient {

    private final RabbitTemplate rabbitTemplate;

    public FileStorageClient(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void send(ExportOutbox outbox) {
        rabbitTemplate.convertAndSend(RabbitConfig.STORE_QUEUE,
                new StoreFileCommand(outbox.getId(), outbox.getFileName(), outbox.getContent()));
    }
}
