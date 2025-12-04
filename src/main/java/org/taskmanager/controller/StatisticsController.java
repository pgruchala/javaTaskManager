package org.taskmanager.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.taskmanager.dto.StatsDTO;
import org.taskmanager.service.PdfExportService;
import org.taskmanager.service.StatisticsService;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tasks/statistics")
public class StatisticsController {
    private final StatisticsService statisticsService;
    private final PdfExportService pdfExportService;

    @GetMapping
    @Operation(summary ="Pobierz statystyki: procent wykonania; liczba zadań według statusu")
    public ResponseEntity<StatsDTO> getStatistics() {
        StatsDTO stats = statisticsService.getStatistics();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/export/pdf")
    @Operation(summary = "Eksportuj statystyki do PDF")
    public ResponseEntity<byte[]> exportStatisticsToPdf() throws IOException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        StatsDTO stats = statisticsService.getStatistics();

        byte[] pdfBytes = pdfExportService.exportStatisticsToPdf(stats, username);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"statistics_" + username + ".pdf\"")
                .body(pdfBytes);
    }
}
