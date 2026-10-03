package ru.ulstu.reportservice.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ru.ulstu.reportservice.service.ReportService;
import ru.ulstu.reportservice.web.dto.DiskReportItemDto;
import ru.ulstu.reportservice.web.dto.ExportStatusDto;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/disks")
    public List<DiskReportItemDto> disks() {
        return reportService.listDisks();
    }

    @PostMapping("/export")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ExportStatusDto export() {
        return reportService.queueExport();
    }

    @GetMapping("/export/{id}")
    public ExportStatusDto exportStatus(@PathVariable Long id) {
        return reportService.getExportStatus(id);
    }
}
