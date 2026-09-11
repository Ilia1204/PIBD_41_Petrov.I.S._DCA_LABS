package ru.ulstu.reportservice.domain;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// читаем таблицу disk, которой владеет и управляет disk-service, сама не создаёт и не меняет схему
@Entity
@Table(name = "disk")
public class DiskView {

    @Id
    private Long id;

    @Column(name = "inventory_number")
    private String inventoryNumber;

    @Column(name = "title")
    private String title;

    @Column(name = "genre")
    private String genre;

    @Column(name = "status")
    private String status;

    @Column(name = "holder_name")
    private String holderName;

    @Column(name = "issued_at")
    private Instant issuedAt;

    public Long getId() {
        return id;
    }

    public String getInventoryNumber() {
        return inventoryNumber;
    }

    public String getTitle() {
        return title;
    }

    public String getGenre() {
        return genre;
    }

    public String getStatus() {
        return status;
    }

    public String getHolderName() {
        return holderName;
    }

    public Instant getIssuedAt() {
        return issuedAt;
    }
}
