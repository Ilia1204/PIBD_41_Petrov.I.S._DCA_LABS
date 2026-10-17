package ru.ulstu.filestorageservice.messaging;

public record FileStoredEvent(Long exportId, boolean success, String errorMessage) {
}
