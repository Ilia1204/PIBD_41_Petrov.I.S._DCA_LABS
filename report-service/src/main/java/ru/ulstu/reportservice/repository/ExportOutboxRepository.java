package ru.ulstu.reportservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.ulstu.reportservice.domain.ExportOutbox;
import ru.ulstu.reportservice.domain.OutboxStatus;

public interface ExportOutboxRepository extends JpaRepository<ExportOutbox, Long> {

    List<ExportOutbox> findByStatus(OutboxStatus status);
}
