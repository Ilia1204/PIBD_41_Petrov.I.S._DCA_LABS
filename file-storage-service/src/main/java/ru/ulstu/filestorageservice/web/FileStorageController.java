package ru.ulstu.filestorageservice.web;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import ru.ulstu.filestorageservice.service.FileStorageService;
import ru.ulstu.filestorageservice.web.dto.StoredFileDto;

@RestController
@RequestMapping("/api/files")
@Tag(name = "Файловое хранилище", description = "Приём и выдача файлов (лаба 4)")
public class FileStorageController {

    private static final Logger log = LoggerFactory.getLogger(FileStorageController.class);

    private final FileStorageService service;

    public FileStorageController(FileStorageService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Сохранить файл в хранилище")
    public StoredFileDto upload(@RequestParam("file") MultipartFile file) {
        log.info("сохраняю файл {} в файловое хранилище", file.getOriginalFilename());
        return service.store(file);
    }

    @GetMapping
    @Operation(summary = "Список сохранённых файлов")
    public List<StoredFileDto> list() {
        return service.listAll();
    }

    @GetMapping("/{fileName}")
    @Operation(summary = "Скачать файл из хранилища")
    public ResponseEntity<byte[]> download(@PathVariable String fileName) {
        byte[] content = service.read(fileName);
        ContentDisposition disposition = ContentDisposition.attachment().filename(fileName).build();
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .body(content);
    }

    @DeleteMapping("/{fileName}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить файл из хранилища")
    public void delete(@PathVariable String fileName) {
        log.info("удаляю файл {} из файлового хранилища", fileName);
        service.delete(fileName);
    }
}
