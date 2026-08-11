package com.evento.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    private String id;
    private String title;
    private String venue;
    private String date;
    private List<Seat> seats;
}