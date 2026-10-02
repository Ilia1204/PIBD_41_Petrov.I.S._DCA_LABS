package ru.ulstu.reportservice.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import ru.ulstu.reportservice.web.dto.StoredFileDto;

@Component
public class FileStorageClient {

    private final RestTemplate restTemplate;
    private final String fileStorageServiceUrl;

    public FileStorageClient(RestTemplate restTemplate,
            @Value("${file-storage-service.url}") String fileStorageServiceUrl) {
        this.restTemplate = restTemplate;
        this.fileStorageServiceUrl = fileStorageServiceUrl;
    }

    public StoredFileDto upload(byte[] content, String fileName) {
        ByteArrayResource resource = new ByteArrayResource(content) {
            @Override
            public String getFilename() {
                return fileName;
            }
        };

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", resource);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
        return restTemplate.postForObject(
                fileStorageServiceUrl + "/api/files", request, StoredFileDto.class);
    }
}
