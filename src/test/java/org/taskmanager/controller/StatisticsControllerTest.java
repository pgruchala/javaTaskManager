package org.taskmanager.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.taskmanager.dto.StatsDTO;
import org.taskmanager.service.PdfExportService;
import org.taskmanager.service.StatisticsService;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StatisticsController.class)
public class StatisticsControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StatisticsService statisticsService;

    @MockBean
    private PdfExportService pdfExportService;

    @Test
    @WithMockUser(username = "user")
    @DisplayName("GET /api/tasks/statistics - Pobranie JSON")
    void testShouldReturnStatisticsJson() throws Exception {
        StatsDTO stats = new StatsDTO(10, 5, 2, 3, 30.0);
        when(statisticsService.getStatistics()).thenReturn(stats);

        mockMvc.perform(get("/api/tasks/statistics"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalTasks", is(10)))
                .andExpect(jsonPath("$.completionsPercentage", is(30.0)));
    }

    @Test
    @WithMockUser(username = "user")
    @DisplayName("GET /api/tasks/statistics/export/pdf - Pobranie PDF")
    void testShouldExportStatisticsToPdf() throws Exception {
        byte[] fakePdf = "%PDF-1.4...".getBytes();
        when(statisticsService.getStatistics()).thenReturn(new StatsDTO(0,0,0,0,0));
        when(pdfExportService.exportStatisticsToPdf(any(StatsDTO.class), anyString())).thenReturn(fakePdf);

        mockMvc.perform(get("/api/tasks/statistics/export/pdf"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"statistics_user.pdf\""))
                .andExpect(content().bytes(fakePdf));
    }
}
