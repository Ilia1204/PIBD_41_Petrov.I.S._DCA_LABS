package ru.ulstu.reportservice.domain;

public enum OutboxStatus {
    PENDING,
    SENDING,
    SENT,
    FAILED
}
