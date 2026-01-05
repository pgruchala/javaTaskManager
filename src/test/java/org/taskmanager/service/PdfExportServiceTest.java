package org.taskmanager.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.taskmanager.dto.StatsDTO;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class PdfExportServiceTest {
    private final PdfExportService pdfExportService = new PdfExportService();

    @Test
    @DisplayName("Generowanie PDF ze statystyk")
    void testShouldGeneratePdfBytes() throws IOException {
        StatsDTO stats = new StatsDTO(10, 5, 2, 3, 30.0);
        String username = "testUser";

        byte[] pdfContent = pdfExportService.exportStatisticsToPdf(stats, username);

        assertAll("PDF Generation",
                () -> assertNotNull(pdfContent),
                () -> assertTrue(pdfContent.length > 0),
                () -> assertTrue(new String(pdfContent).startsWith("%PDF"))
        );
    }
}
