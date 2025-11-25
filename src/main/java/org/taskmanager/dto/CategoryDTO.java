package org.taskmanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
    @NotBlank(message = "Nazwa kategorii nie może być pusta")
    @Size(min = 3, max = 50, message = "Nazwa musi mieć od 3 do 50 znaków")
    private String name;

    @NotBlank(message = "Kolor jest wymagany")
    private String color;
}
