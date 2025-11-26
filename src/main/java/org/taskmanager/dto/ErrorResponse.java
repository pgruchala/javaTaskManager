package org.taskmanager.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ErrorResponse {
    private String message;
    private LocalDateTime timestamp;
    private int status;
    private String path;

    public ErrorResponse() {
    }

    public ErrorResponse(String message, LocalDateTime timestamp, int status, String path) {
        this.message = message;
        this.timestamp = timestamp;
        this.status = status;
        this.path = path;
    }

}