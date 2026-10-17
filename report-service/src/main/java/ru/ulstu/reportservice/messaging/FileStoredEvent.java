package ru.ulstu.reportservice.messaging;

public record FileStoredEvent(Long exportId, boolean success, String errorMessage) {
}
