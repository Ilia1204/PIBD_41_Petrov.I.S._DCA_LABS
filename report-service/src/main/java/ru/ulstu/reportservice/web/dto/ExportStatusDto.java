package ru.ulstu.reportservice.web.dto;

import java.time.Instant;

import ru.ulstu.reportservice.domain.ExportOutbox;

public record ExportStatusDto(
    Long id,
    String fileName,
    String status,
    Instant createdAt,
    Instant sentAt) {

    public static ExportStatusDto of(ExportOutbox outbox) {
        return new ExportStatusDto(
                outbox.getId(),
                outbox.getFileName(),
                outbox.getStatus().name(),
                outbox.getCreatedAt(),
                outbox.getSentAt());
    }
}
