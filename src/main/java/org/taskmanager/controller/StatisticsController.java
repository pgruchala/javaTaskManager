package org.taskmanager.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks/statistics")
public class StatisticsController {
    @GetMapping("/export/csv")
    public void exportToCsv(){}
}
