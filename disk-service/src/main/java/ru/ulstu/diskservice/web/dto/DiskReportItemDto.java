package ru.ulstu.diskservice.web.dto;

import java.time.Instant;

public record DiskReportItemDto(
                Long id,
                String inventoryNumber,
                String title,
                String genre,
                String status,
                String holderName,
                Instant issuedAt) {
}
