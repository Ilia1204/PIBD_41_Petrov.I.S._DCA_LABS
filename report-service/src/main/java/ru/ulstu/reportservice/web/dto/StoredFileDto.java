package ru.ulstu.reportservice.web.dto;

import java.time.Instant;

public record StoredFileDto(
    String fileName,
    long size,
    Instant savedAt) {
}
