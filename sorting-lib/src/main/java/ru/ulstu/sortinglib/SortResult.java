package ru.ulstu.sortinglib;

import java.util.List;

public record SortResult<T>(List<T> sorted, long elapsedMillis, int threadsUsed) {
}
