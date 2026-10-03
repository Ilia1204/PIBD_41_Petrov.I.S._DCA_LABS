package ru.ulstu.diskservice.web.dto;

import java.time.Instant;

public record ExportStatusDto(
    Long id,
    String fileName,
    String status,
    Instant createdAt,
    Instant sentAt) {
}
