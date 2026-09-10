package ru.ulstu.diskservice.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateDiskRequest(
        @NotBlank @Size(max = 32) String inventoryNumber,
        @NotBlank @Size(max = 255) String title,
        @Size(max = 128) String genre) {
}
