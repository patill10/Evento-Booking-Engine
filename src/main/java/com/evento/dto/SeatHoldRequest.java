package com.evento.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class SeatHoldRequest {
    @NotNull(message = "Event ID is required")
    private String eventId;

    @NotNull(message = "User ID is required")
    private String userId;

    @NotEmpty(message = "At least one seat must be selected")
    private List<String> seatIds;
}