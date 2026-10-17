package ru.ulstu.reportservice.domain;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "export_outbox")
public class ExportOutbox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "content", nullable = false)
    private byte[] content;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OutboxStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "sent_at")
    private Instant sentAt;

    @Column(name = "retry_count", nullable = false)
    private int retryCount;

    protected ExportOutbox() {
    }

    public ExportOutbox(String fileName, byte[] content) {
        this.fileName = fileName;
        this.content = content;
        this.status = OutboxStatus.PENDING;
        this.createdAt = Instant.now();
    }

    public void markSending() {
        this.status = OutboxStatus.SENDING;
    }

    public void markSent() {
        this.status = OutboxStatus.SENT;
        this.sentAt = Instant.now();
    }

    // неудачная попытка: либо повторяем, либо, исчерпав лимит, останавливаемся на FAILED
    public void markRetryOrFailed(int maxRetries) {
        this.retryCount++;
        this.status = this.retryCount >= maxRetries ? OutboxStatus.FAILED : OutboxStatus.PENDING;
    }

    public Long getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public byte[] getContent() {
        return content;
    }

    public OutboxStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getSentAt() {
        return sentAt;
    }

    public int getRetryCount() {
        return retryCount;
    }
}
