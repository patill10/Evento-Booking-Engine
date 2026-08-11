package com.evento.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Seat {
    private String seatId;
    private String seatRow;
    private int seatNumber;
    private double price;
    private String status;
    private String heldByUserId;
    private Long holdExpirationTime;
}