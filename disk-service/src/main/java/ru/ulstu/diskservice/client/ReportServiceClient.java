package ru.ulstu.diskservice.client;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import ru.ulstu.diskservice.web.dto.DiskReportItemDto;
import ru.ulstu.diskservice.web.dto.ExportStatusDto;

@Component
public class ReportServiceClient {

    private static final String REPORT_SERVICE_URL = "http://report-service";

    private final RestTemplate restTemplate;

    public ReportServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<DiskReportItemDto> fetchDiskList() {
        DiskReportItemDto[] items = restTemplate.getForObject(
                REPORT_SERVICE_URL + "/api/reports/disks", DiskReportItemDto[].class);
        return items == null ? List.of() : List.of(items);
    }

    public ExportStatusDto triggerExport() {
        return restTemplate.postForObject(
                REPORT_SERVICE_URL + "/api/reports/export", null, ExportStatusDto.class);
    }
}
