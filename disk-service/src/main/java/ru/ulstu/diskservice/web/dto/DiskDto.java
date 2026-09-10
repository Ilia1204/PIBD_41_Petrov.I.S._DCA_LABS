package ru.ulstu.diskservice.web.dto;

import java.time.Instant;

import ru.ulstu.diskservice.domain.Disk;
import ru.ulstu.diskservice.domain.DiskStatus;

// Представление диска в ответах API
public record DiskDto(
        Long id,
        String inventoryNumber,
        String title,
        String genre,
        DiskStatus status,
        String holderName,
        Instant issuedAt,
        Instant createdAt) {
    public static DiskDto of(Disk disk) {
        return new DiskDto(
                disk.getId(),
                disk.getInventoryNumber(),
                disk.getTitle(),
                disk.getGenre(),
                disk.getStatus(),
                disk.getHolderName(),
                disk.getIssuedAt(),
                disk.getCreatedAt());
    }
}
