package ru.ulstu.reportservice.web;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.ulstu.reportservice.repository.DiskViewRepository;
import ru.ulstu.reportservice.web.dto.DiskReportItemDto;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final DiskViewRepository repository;

    public ReportController(DiskViewRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/disks")
    public List<DiskReportItemDto> disks() {
        return repository.findAll().stream()
                .map(DiskReportItemDto::of)
                .toList();
    }
}
