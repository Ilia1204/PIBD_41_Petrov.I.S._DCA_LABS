package ru.ulstu.filestorageservice.messaging;

public record StoreFileCommand(Long exportId, String fileName, byte[] content) {
}
