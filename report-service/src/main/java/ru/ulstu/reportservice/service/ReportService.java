package ru.ulstu.reportservice.service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.ulstu.reportservice.domain.DiskView;
import ru.ulstu.reportservice.domain.ExportOutbox;
import ru.ulstu.reportservice.repository.DiskViewRepository;
import ru.ulstu.reportservice.repository.ExportOutboxRepository;
import ru.ulstu.reportservice.web.dto.DiskReportItemDto;
import ru.ulstu.reportservice.web.dto.ExportStatusDto;

@Service
public class ReportService {

    private static final Logger log = LoggerFactory.getLogger(ReportService.class);
    private static final DateTimeFormatter FILE_TIMESTAMP = DateTimeFormatter
            .ofPattern("yyyyMMdd-HHmmss")
            .withZone(ZoneOffset.UTC);

    private final DiskViewRepository diskViewRepository;
    private final ExportOutboxRepository outboxRepository;

    public ReportService(DiskViewRepository diskViewRepository, ExportOutboxRepository outboxRepository) {
        this.diskViewRepository = diskViewRepository;
        this.outboxRepository = outboxRepository;
    }

    public List<DiskReportItemDto> listDisks() {
        log.info("отдаю список дисков для отчёта");
        return diskViewRepository.findAll().stream()
                .map(DiskReportItemDto::of)
                .toList();
    }

    @Transactional
    public ExportStatusDto queueExport() {
        List<DiskView> disks = diskViewRepository.findAll();
        byte[] csv = DiskReportCsvWriter.toCsv(disks);
        String fileName = "disks-report-" + FILE_TIMESTAMP.format(Instant.now()) + ".csv";
        ExportOutbox outbox = outboxRepository.save(new ExportOutbox(fileName, csv));
        log.info("csv-отчёт по дискам {} поставлен в очередь на отправку в файловое хранилище", fileName);
        return ExportStatusDto.of(outbox);
    }

    public ExportStatusDto getExportStatus(Long id) {
        ExportOutbox outbox = outboxRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("запись outbox не найдена: " + id));
        return ExportStatusDto.of(outbox);
    }
}
