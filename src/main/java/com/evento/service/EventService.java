package evento.service;

import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import evento.model.Event;
import evento.model.Seat;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EventService {

    public String createEvent(Event event) throws Exception {
        Firestore db = FirestoreClient.getFirestore();
        
        if (event.getId() == null || event.getId().isBlank()) {
            event.setId(UUID.randomUUID().toString());
        }

        // Generate default 30-seat grid if seats list is empty
        if (event.getSeats() == null || event.getSeats().isEmpty()) {
            List<Seat> generatedSeats = new ArrayList<>();
            String[] rows = {"A", "B", "C", "D", "E"};

            for (String row : rows) {
                for (int i = 1; i <= 6; i++) {
                    double price = (row.equals("A") || row.equals("B")) ? 2500.0 : 1200.0;
                    generatedSeats.add(new Seat(row + i, row, i, price, "AVAILABLE", null, null));
                }
            }
            event.setSeats(generatedSeats);
        }

        // Save to Firestore asynchronously and wait for result
        db.collection("events").document(event.getId()).set(event).get();
        System.out.println("🔥 New Event successfully created: " + event.getTitle());
        return event.getId();
    }

    public Event getEventById(String eventId) throws Exception {
        Firestore db = FirestoreClient.getFirestore();
        DocumentSnapshot doc = db.collection("events").document(eventId).get().get();
        if (!doc.exists()) throw new RuntimeException("Event not found");
        return doc.toObject(Event.class);
    }

    public List<Event> getAllEvents() throws Exception {
        Firestore db = FirestoreClient.getFirestore();
        List<QueryDocumentSnapshot> docs = db.collection("events").get().get().getDocuments();
        List<Event> events = new ArrayList<>();
        for (QueryDocumentSnapshot doc : docs) {
            events.add(doc.toObject(Event.class));
        }
        return events;
    }
}