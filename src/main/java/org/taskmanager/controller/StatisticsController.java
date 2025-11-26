package org.taskmanager.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.taskmanager.dto.StatsDTO;
import org.taskmanager.repository.StatisticsDao;

@RestController

@RequestMapping("/api/tasks/statistics")
public class StatisticsController {
    private final StatisticsDao taskStatisticsDao;

    public StatisticsController(StatisticsDao taskStatisticsDao) {
        this.taskStatisticsDao = taskStatisticsDao;
    }

    @GetMapping
    @Operation(summary ="Pobierz statystyki: procent wykonania; liczba zadań według statusu")
    public ResponseEntity<StatsDTO> getStatistics() {
        StatsDTO stats = taskStatisticsDao.getStatistics();
        return ResponseEntity.ok(stats);
    }
}
