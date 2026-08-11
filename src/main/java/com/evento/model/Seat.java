package com.evento.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Seat {
    private String seatId;          // e.g., "A1", "B3"
    private String seatRow;         // e.g., "A"
    private int seatNumber;         // e.g., 1
    private double price;           // e.g., 100.0
    private String status;          // "AVAILABLE", "HELD", "BOOKED"
    private String heldByUserId;    // User ID holding the lock
    private Long holdExpirationTime; // Expiration timestamp in epoch ms
}