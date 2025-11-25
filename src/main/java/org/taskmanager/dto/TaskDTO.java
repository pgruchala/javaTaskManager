package org.taskmanager.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.taskmanager.model.Status;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {
    @NotBlank(message = "Tytuł zadania nie może być pusty")
    @Size(min = 3, max = 100, message = "Tytuł musi mieć od 3 do 100 znaków")
    private String title;

    @Size(max = 500, message = "Opis nie może być dłuższy niż 500 znaków")
    private String description;

    @NotNull(message = "Status zadania jest wymagany")
    private Status status;

    @FutureOrPresent(message = "Data wykonania nie może być z przeszłości")
    private LocalDate dueDate;

    private Long categoryId;
}
