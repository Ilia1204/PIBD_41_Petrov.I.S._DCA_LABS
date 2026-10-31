package ru.ulstu.diskservice.web.dto;

import java.util.List;

import ru.ulstu.sortinglib.SortResult;

public record SortedDisksDto(List<DiskDto> disks, long elapsedMillis, int threadsUsed) {
    public static SortedDisksDto of(SortResult<ru.ulstu.diskservice.domain.Disk> result) {
        return new SortedDisksDto(
                result.sorted().stream().map(DiskDto::of).toList(),
                result.elapsedMillis(),
                result.threadsUsed());
    }
}
