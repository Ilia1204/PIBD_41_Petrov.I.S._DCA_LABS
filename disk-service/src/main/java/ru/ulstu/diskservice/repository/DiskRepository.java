package ru.ulstu.diskservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.ulstu.diskservice.domain.Disk;
import ru.ulstu.diskservice.domain.DiskStatus;

public interface DiskRepository extends JpaRepository<Disk, Long> {

    boolean existsByInventoryNumber(String inventoryNumber);

    long countByStatus(DiskStatus status);
}
