package ru.ulstu.diskservice;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ru.ulstu.diskservice.domain.DiskStatus;

class DiskStatusTest {

    @Test
    void hasTwoStatuses() {
        assertEquals(2, DiskStatus.values().length);
    }
}
