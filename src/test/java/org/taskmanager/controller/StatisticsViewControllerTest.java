package org.taskmanager.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.taskmanager.dto.StatsDTO;
import org.taskmanager.service.StatisticsService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StatisticsViewController.class)
@AutoConfigureMockMvc(addFilters = false)
class StatisticsViewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StatisticsService statisticsService;

    @Test
    void testShouldDisplayStatistics() throws Exception {
        StatsDTO dto = new StatsDTO(10, 4, 3, 3, 60.0);
        when(statisticsService.getStatistics()).thenReturn(dto);

        mockMvc.perform(get("/statistics"))
                .andExpect(status().isOk())
                .andExpect(view().name("statistics/index"))
                .andExpect(model().attributeExists("stats"))
                .andExpect(model().attribute("stats", dto));
    }
}
