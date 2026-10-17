package ru.ulstu.reportservice.messaging;

public record StoreFileCommand(Long exportId, String fileName, byte[] content) {
}
