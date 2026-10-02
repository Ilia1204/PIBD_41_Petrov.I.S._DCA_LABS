package ru.ulstu.diskservice.web.dto;

import java.time.Instant;

public record StoredFileDto(
    String fileName,
    long size,
    Instant savedAt) {
}
