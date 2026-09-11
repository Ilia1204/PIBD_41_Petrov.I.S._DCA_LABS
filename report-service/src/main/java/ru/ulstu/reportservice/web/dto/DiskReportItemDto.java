package ru.ulstu.reportservice.web.dto;

import java.time.Instant;

import ru.ulstu.reportservice.domain.DiskView;

public record DiskReportItemDto(
        Long id,
        String inventoryNumber,
        String title,
        String genre,
        String status,
        String holderName,
        Instant issuedAt) {
    public static DiskReportItemDto of(DiskView disk) {
        return new DiskReportItemDto(
                disk.getId(),
                disk.getInventoryNumber(),
                disk.getTitle(),
                disk.getGenre(),
                disk.getStatus(),
                disk.getHolderName(),
                disk.getIssuedAt());
    }
}
