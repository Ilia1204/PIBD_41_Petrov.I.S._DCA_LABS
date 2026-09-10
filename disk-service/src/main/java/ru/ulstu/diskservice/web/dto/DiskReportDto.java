package ru.ulstu.diskservice.web.dto;

public record DiskReportDto(
                long onHands,
                long inStock,
                long total) {
}
