package org.taskmanager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.taskmanager.dto.StatsDTO;
import org.taskmanager.repository.StatisticsDao;
import org.taskmanager.service.StatisticsService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/statistics")
public class StatisticsViewController {
    private final StatisticsService statisticsService;

    @GetMapping
    public String showStatistics(Model model) {
        StatsDTO stats = statisticsService.getStatistics();
        model.addAttribute("stats", stats);
        return "statistics/index";
    }
}
