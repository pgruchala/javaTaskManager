package org.taskmanager.dto;

public class CategoryDTO {
    private Long id;
    private String name;
    private String color;

    public CategoryDTO(){}
    public CategoryDTO(String name, String color){
        this.name = name;
        this.color = color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
