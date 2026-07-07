package com.example.fitlens.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SendFriendRequestRequest(
        @NotBlank @Email String email
) {
}
