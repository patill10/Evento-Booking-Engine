package com.evento.controller;

import com.evento.model.Event;
import com.evento.service.EventService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    public String createEvent(@RequestBody Event event) throws Exception {
        return eventService.createEvent(event);
    }

    @GetMapping
    public List<Event> getAllEvents() throws Exception {
        return eventService.getAllEvents();
    }

    @GetMapping("/{id}")
    public Event getEventById(@PathVariable String id) throws Exception {
        return eventService.getEventById(id);
    }
}