package ru.ulstu.reportservice.web;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.ulstu.reportservice.client.FileStorageClient;
import ru.ulstu.reportservice.domain.DiskView;
import ru.ulstu.reportservice.repository.DiskViewRepository;
import ru.ulstu.reportservice.service.DiskReportCsvWriter;
import ru.ulstu.reportservice.web.dto.DiskReportItemDto;
import ru.ulstu.reportservice.web.dto.StoredFileDto;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private static final Logger log = LoggerFactory.getLogger(ReportController.class);
    private static final DateTimeFormatter FILE_TIMESTAMP = DateTimeFormatter
            .ofPattern("yyyyMMdd-HHmmss")
            .withZone(ZoneOffset.UTC);

    private final DiskViewRepository repository;
    private final FileStorageClient fileStorageClient;

    public ReportController(DiskViewRepository repository, FileStorageClient fileStorageClient) {
        this.repository = repository;
        this.fileStorageClient = fileStorageClient;
    }

    @GetMapping("/disks")
    public List<DiskReportItemDto> disks() {
        log.info("отдаю список дисков для отчёта");
        return repository.findAll().stream()
                .map(DiskReportItemDto::of)
                .toList();
    }

    @PostMapping("/export")
    public StoredFileDto export() {
        List<DiskView> disks = repository.findAll();
        byte[] csv = DiskReportCsvWriter.toCsv(disks);
        String fileName = "disks-report-" + FILE_TIMESTAMP.format(Instant.now()) + ".csv";
        log.info("формирую csv-отчёт по дискам и отправляю в файловое хранилище");
        return fileStorageClient.upload(csv, fileName);
    }
}
