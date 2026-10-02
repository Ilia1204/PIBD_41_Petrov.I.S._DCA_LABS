package ru.ulstu.diskservice.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import ru.ulstu.diskservice.web.dto.DiskReportItemDto;
import ru.ulstu.diskservice.web.dto.StoredFileDto;

@Component
public class ReportServiceClient {

    private final RestTemplate restTemplate;
    private final String reportServiceUrl;

    public ReportServiceClient(RestTemplate restTemplate,
            @Value("${report-service.url}") String reportServiceUrl) {
        this.restTemplate = restTemplate;
        this.reportServiceUrl = reportServiceUrl;
    }

    public List<DiskReportItemDto> fetchDiskList() {
        DiskReportItemDto[] items = restTemplate.getForObject(
                reportServiceUrl + "/api/reports/disks", DiskReportItemDto[].class);
        return items == null ? List.of() : List.of(items);
    }

    public StoredFileDto triggerExport() {
        return restTemplate.postForObject(
                reportServiceUrl + "/api/reports/export", null, StoredFileDto.class);
    }
}
