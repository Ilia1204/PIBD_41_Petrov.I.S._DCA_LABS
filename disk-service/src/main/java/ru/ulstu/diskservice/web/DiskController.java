package ru.ulstu.diskservice.web;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import ru.ulstu.diskservice.client.ReportServiceClient;
import ru.ulstu.diskservice.service.DiskService;
import ru.ulstu.diskservice.web.dto.CreateDiskRequest;
import ru.ulstu.diskservice.web.dto.DiskDto;
import ru.ulstu.diskservice.web.dto.DiskReportDto;
import ru.ulstu.diskservice.web.dto.DiskReportItemDto;
import ru.ulstu.diskservice.web.dto.IssueDiskRequest;
import ru.ulstu.diskservice.web.dto.UpdateDiskRequest;

@RestController
@RequestMapping("/api/disks")
@Tag(name = "Диски в прокате", description = "Учёт дисков в прокате (вариант 22)")
public class DiskController {

    private final DiskService service;
    private final ReportServiceClient reportServiceClient;

    public DiskController(DiskService service, ReportServiceClient reportServiceClient) {
        this.service = service;
        this.reportServiceClient = reportServiceClient;
    }

    @GetMapping
    @Operation(summary = "Список дисков (с постраничным выводом)")
    public Page<DiskDto> list(@PageableDefault(size = 20) Pageable pageable) {
        return service.findAll(pageable).map(DiskDto::of);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить диск по идентификатору")
    public DiskDto get(@PathVariable Long id) {
        return DiskDto.of(service.getById(id));
    }

    @PostMapping
    @Operation(summary = "Принять новый диск в прокат")
    public ResponseEntity<DiskDto> create(@Valid @RequestBody CreateDiskRequest request,
            UriComponentsBuilder uriBuilder) {
        DiskDto created = DiskDto.of(service.create(request));
        return ResponseEntity
                .created(uriBuilder.path("/api/disks/{id}").build(created.id()))
                .body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Отредактировать информацию о диске")
    public DiskDto update(@PathVariable Long id, @Valid @RequestBody UpdateDiskRequest request) {
        return DiskDto.of(service.update(id, request));
    }

    @PostMapping("/{id}/issue")
    @Operation(summary = "Выдать диск на руки клиенту")
    public DiskDto issue(@PathVariable Long id, @Valid @RequestBody IssueDiskRequest request) {
        return DiskDto.of(service.issue(id, request.holderName()));
    }

    @PostMapping("/{id}/return")
    @Operation(summary = "Получить диск обратно в прокат")
    public DiskDto returnBack(@PathVariable Long id) {
        return DiskDto.of(service.returnBack(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить запись о диске")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/report")
    @Operation(summary = "Отчёт: сколько дисков на руках, а сколько в прокате")
    public DiskReportDto report() {
        return service.buildReport();
    }

    @GetMapping("/report/list")
    @Operation(summary = "Список дисков для отчёта (получаем из report-service через RestTemplate)")
    public List<DiskReportItemDto> reportList() {
        return reportServiceClient.fetchDiskList();
    }
}
