package ru.ulstu.filestorageservice.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    // команда на сохранение файла, report-service -> file-storage-service
    public static final String STORE_QUEUE = "file-storage.store";
    // подтверждение сохранения, file-storage-service -> report-service
    public static final String STORED_QUEUE = "file-storage.stored";
    // сюда брокер сам переносит сообщение, если обработчик не смог его принять
    // (например, сообщение не десериализуется) - после единственной неудачной попытки
    private static final String STORE_DLQ = "file-storage.store.dlq";
    private static final String STORED_DLQ = "file-storage.stored.dlq";

    // очереди объявляю на обоих концах, чтобы не зависеть от порядка запуска сервисов
    @Bean
    public Queue storeQueue() {
        return QueueBuilder.durable(STORE_QUEUE)
                .deadLetterExchange("")
                .deadLetterRoutingKey(STORE_DLQ)
                .build();
    }

    @Bean
    public Queue storeDeadLetterQueue() {
        return new Queue(STORE_DLQ, true);
    }

    @Bean
    public Queue storedQueue() {
        return QueueBuilder.durable(STORED_QUEUE)
                .deadLetterExchange("")
                .deadLetterRoutingKey(STORED_DLQ)
                .build();
    }

    @Bean
    public Queue storedDeadLetterQueue() {
        return new Queue(STORED_DLQ, true);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }
}
