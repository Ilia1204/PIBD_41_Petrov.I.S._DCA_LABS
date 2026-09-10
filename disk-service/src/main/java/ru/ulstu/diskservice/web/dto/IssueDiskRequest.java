package ru.ulstu.diskservice.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record IssueDiskRequest(@NotBlank @Size(max = 255) String holderName) {
}
