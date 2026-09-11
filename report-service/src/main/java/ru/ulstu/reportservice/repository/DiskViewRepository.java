package ru.ulstu.reportservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.ulstu.reportservice.domain.DiskView;

public interface DiskViewRepository extends JpaRepository<DiskView, Long> {
}
