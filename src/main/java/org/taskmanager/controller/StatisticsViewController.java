package org.taskmanager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.taskmanager.dto.StatsDTO;
import org.taskmanager.repository.StatisticsDao;

@Controller
@RequestMapping("/statistics")
public class StatisticsViewController {
    private final StatisticsDao statisticsDao;

    public StatisticsViewController(StatisticsDao statisticsDao){
        this.statisticsDao = statisticsDao;
    }
    @GetMapping
    public String showStatistics(Model model) {
        StatsDTO stats = statisticsDao.getStatistics();

        model.addAttribute("stats", stats);

        return "statistics/index";
    }
}
