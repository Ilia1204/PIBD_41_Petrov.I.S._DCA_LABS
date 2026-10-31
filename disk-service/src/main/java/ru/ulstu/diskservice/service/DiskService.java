package ru.ulstu.diskservice.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.ulstu.diskservice.domain.Disk;
import ru.ulstu.diskservice.domain.DiskStatus;
import ru.ulstu.diskservice.repository.DiskRepository;
import ru.ulstu.diskservice.web.dto.CreateDiskRequest;
import ru.ulstu.diskservice.web.dto.DiskReportDto;
import ru.ulstu.diskservice.web.dto.UpdateDiskRequest;
import ru.ulstu.sortinglib.ParallelMergeSort;
import ru.ulstu.sortinglib.SortResult;

@Service
@Transactional
public class DiskService {

    private final DiskRepository repository;

    public DiskService(DiskRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public Page<Disk> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Disk getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Диск с id=" + id + " не найден"));
    }

    public Disk create(CreateDiskRequest request) {
        if (repository.existsByInventoryNumber(request.inventoryNumber())) {
            throw new BusinessRuleException(
                    "Диск с инвентарным номером " + request.inventoryNumber() + " уже зарегистрирован");
        }
        Disk disk = new Disk();
        disk.setInventoryNumber(request.inventoryNumber());
        disk.setTitle(request.title());
        disk.setGenre(request.genre());
        disk.setStatus(DiskStatus.IN_STOCK);
        return repository.save(disk);
    }

    public Disk update(Long id, UpdateDiskRequest request) {
        Disk disk = getById(id);
        disk.setTitle(request.title());
        disk.setGenre(request.genre());
        return disk;
    }

    public Disk issue(Long id, String holderName) {
        Disk disk = getById(id);
        if (disk.getStatus() == DiskStatus.ON_HANDS) {
            throw new BusinessRuleException("Диск уже выдан на руки (" + disk.getHolderName() + ")");
        }
        disk.setStatus(DiskStatus.ON_HANDS);
        disk.setHolderName(holderName);
        disk.setIssuedAt(Instant.now());
        return disk;
    }

    public Disk returnBack(Long id) {
        Disk disk = getById(id);
        if (disk.getStatus() == DiskStatus.IN_STOCK) {
            throw new BusinessRuleException("Диск и так находится в прокате");
        }
        disk.setStatus(DiskStatus.IN_STOCK);
        disk.setHolderName(null);
        disk.setIssuedAt(null);
        return disk;
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Диск с id=" + id + " не найден");
        }
        repository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public DiskReportDto buildReport() {
        long onHands = repository.countByStatus(DiskStatus.ON_HANDS);
        long inStock = repository.countByStatus(DiskStatus.IN_STOCK);
        return new DiskReportDto(onHands, inStock, onHands + inStock);
    }

    @Transactional(readOnly = true)
    public SortResult<Disk> sortAllById(int threads, Integer syntheticSize) {
        List<Disk> disks = syntheticSize != null ? generateSyntheticDisks(syntheticSize) : repository.findAll();
        return ParallelMergeSort.sort(disks, threads, Comparator.comparing(Disk::getId));
    }

    private List<Disk> generateSyntheticDisks(int size) {
        List<Disk> disks = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            Disk disk = new Disk();
            disk.setId((long) ThreadLocalRandom.current().nextInt(size));
            disks.add(disk);
        }
        return disks;
    }
}
