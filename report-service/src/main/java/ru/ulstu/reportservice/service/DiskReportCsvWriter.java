package ru.ulstu.reportservice.service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import ru.ulstu.reportservice.domain.DiskView;

public final class DiskReportCsvWriter {

    private static final String HEADER = "id,inventoryNumber,title,genre,status,holderName,issuedAt";

    private DiskReportCsvWriter() {
    }

    public static byte[] toCsv(List<DiskView> disks) {
        String rows = disks.stream()
                .map(DiskReportCsvWriter::toRow)
                .collect(Collectors.joining("\n"));
        String csv = rows.isEmpty() ? HEADER + "\n" : HEADER + "\n" + rows + "\n";
        return csv.getBytes(StandardCharsets.UTF_8);
    }

    private static String toRow(DiskView disk) {
        return String.join(",",
                String.valueOf(disk.getId()),
                escape(disk.getInventoryNumber()),
                escape(disk.getTitle()),
                escape(disk.getGenre()),
                escape(disk.getStatus()),
                escape(disk.getHolderName()),
                disk.getIssuedAt() == null ? "" : disk.getIssuedAt().toString());
    }

    private static String escape(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
