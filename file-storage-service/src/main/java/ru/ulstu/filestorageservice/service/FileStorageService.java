package ru.ulstu.filestorageservice.service;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import ru.ulstu.filestorageservice.web.dto.StoredFileDto;

@Service
public class FileStorageService {

    private final Path storageDir;

    public FileStorageService(@Value("${file-storage.location}") String location) {
        this.storageDir = Paths.get(location).toAbsolutePath().normalize();
        try {
            Files.createDirectories(storageDir);
        } catch (IOException e) {
            throw new StorageException("не удалось создать каталог файлового хранилища", e);
        }
    }

    public StoredFileDto store(MultipartFile file) {
        String safeName = safeFileName(file.getOriginalFilename());
        String storedName = UUID.randomUUID() + "_" + safeName;
        Path target = storageDir.resolve(storedName);
        try {
            file.transferTo(target);
        } catch (IOException e) {
            throw new StorageException("не удалось сохранить файл " + safeName, e);
        }
        return new StoredFileDto(storedName, file.getSize(), Instant.now());
    }

    public byte[] read(String fileName) {
        Path target = resolveExisting(fileName);
        try {
            return Files.readAllBytes(target);
        } catch (IOException e) {
            throw new StorageException("не удалось прочитать файл " + fileName, e);
        }
    }

    public void delete(String fileName) {
        Path target = resolveExisting(fileName);
        try {
            Files.delete(target);
        } catch (IOException e) {
            throw new StorageException("не удалось удалить файл " + fileName, e);
        }
    }

    public List<StoredFileDto> listAll() {
        List<StoredFileDto> result = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(storageDir)) {
            for (Path path : stream) {
                if (Files.isRegularFile(path)) {
                    result.add(new StoredFileDto(
                            path.getFileName().toString(),
                            Files.size(path),
                            Files.getLastModifiedTime(path).toInstant()));
                }
            }
        } catch (IOException e) {
            throw new StorageException("не удалось прочитать содержимое хранилища", e);
        }
        result.sort(Comparator.comparing(StoredFileDto::savedAt).reversed());
        return result;
    }

    private Path resolveExisting(String fileName) {
        Path target = storageDir.resolve(safeFileName(fileName)).normalize();
        if (!target.startsWith(storageDir) || !Files.exists(target)) {
            throw new NotFoundException("файл не найден: " + fileName);
        }
        return target;
    }

    private String safeFileName(String original) {
        String name = original == null || original.isBlank() ? "file" : original;
        return Paths.get(name).getFileName().toString();
    }
}
