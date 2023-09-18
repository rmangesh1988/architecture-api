package com.architecture.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiException {

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    private HttpStatus httpStatus;

    private String message;

    private List<String> errors;

}