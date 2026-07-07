package com.example.fitlens.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    @Builder.Default
    private final boolean success = true;

    @Builder.Default
    private final String message = "Success";

    private final T data;

    @Builder.Default
    private final Instant timestamp = Instant.now();

    private final Integer status;
    private final String error;
    private final String path;
    private final List<String> details;
}
