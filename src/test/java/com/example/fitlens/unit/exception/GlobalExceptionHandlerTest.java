package com.example.fitlens.unit.exception;

import com.example.fitlens.dto.response.ApiResponse;
import com.example.fitlens.exception.DuplicateEmailException;
import com.example.fitlens.exception.GlobalExceptionHandler;
import com.example.fitlens.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        request = new MockHttpServletRequest("GET", "/api/meals/1");
    }

    @Test
    void handleNotFound_returnsApiResponseWith404() {
        ResponseEntity<ApiResponse<Void>> response = handler.handleNotFound(
                new ResourceNotFoundException("Meal entry not found: 1"),
                request
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).isEqualTo("Meal entry not found: 1");
        assertThat(response.getBody().getStatus()).isEqualTo(404);
        assertThat(response.getBody().getPath()).isEqualTo("/api/meals/1");
    }

    @Test
    void handleUnauthorized_returnsApiResponseWith401() {
        ResponseEntity<ApiResponse<Void>> response = handler.handleUnauthorized(
                new BadCredentialsException("Invalid email or password"),
                request
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getStatus()).isEqualTo(401);
    }

    @Test
    void handleDuplicateEmail_returnsApiResponseWith409() {
        ResponseEntity<ApiResponse<Void>> response = handler.handleDuplicateEmail(
                new DuplicateEmailException("khoa@example.com"),
                request
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(409);
    }

    @Test
    void handleBadRequest_returnsApiResponseWith400() {
        ResponseEntity<ApiResponse<Void>> response = handler.handleBadRequest(
                new IllegalArgumentException("Invalid request"),
                request
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Invalid request");
    }

    @Test
    void handleValidation_returnsApiResponseWithFieldDetails() {
        Object target = new Object();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "request");
        bindingResult.addError(new FieldError("request", "email", "must not be blank"));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(
                null,
                bindingResult
        );

        ResponseEntity<ApiResponse<Void>> response = handler.handleValidation(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Validation failed");
        assertThat(response.getBody().getDetails()).containsExactly("email: must not be blank");
    }

    @Test
    void handleGeneric_returnsApiResponseWith500() {
        ResponseEntity<ApiResponse<Void>> response = handler.handleGeneric(
                new RuntimeException("boom"),
                request
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Unexpected server error");
        assertThat(response.getBody().getStatus()).isEqualTo(500);
    }
}
