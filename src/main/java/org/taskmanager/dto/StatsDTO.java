package org.taskmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StatsDTO {
    private int totalTasks;
    private int todoTasks;
    private int inProgressTasks;
    private int doneTasks;
    private double completionsPercentage;
}
