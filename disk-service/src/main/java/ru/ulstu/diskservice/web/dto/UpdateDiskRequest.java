package ru.ulstu.diskservice.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateDiskRequest(
                @NotBlank @Size(max = 255) String title,
                @Size(max = 128) String genre) {
}
