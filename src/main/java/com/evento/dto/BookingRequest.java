package com.evento.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class BookingRequest {
    @NotNull(message = "Event ID is required")
    private String eventId;

    @NotNull(message = "User ID is required")
    private String userId;

    @Email(message = "Valid email is required")
    private String userEmail;

    @NotEmpty(message = "Seat list cannot be empty")
    private List<String> seatIds;
}