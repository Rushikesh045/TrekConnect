package com.trekconnect.auth.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiErrorResponse {

    private int status;
    private String error;
    private String message;
    private String path;
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    private List<String> details;
}
