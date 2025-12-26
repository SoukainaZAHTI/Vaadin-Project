package com.example.vaadinproject.services;

import com.example.vaadinproject.entities.Category;
import com.example.vaadinproject.entities.Event;
import com.example.vaadinproject.entities.Status;
import com.example.vaadinproject.repositories.EventRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service

public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<Event> findAllEvents() {

        return eventRepository.findAll();

    }

    public long countEvents() {
        return eventRepository.count();
    }

    public void deleteEvent(Event event) {
        eventRepository.delete(event);
    }

    public void saveEvent(Event event) {
        if (event == null) {
            System.err.println("Event is null. Are you sure you have connected your form to the application?");
            return;
        }
        eventRepository.save(event);
    }
    public List<Event> findAllEvents(String filterText) {
        if (filterText == null || filterText.isEmpty()) {
            return eventRepository.findAll();
        } else {
            return eventRepository.search(filterText);
        }
    }
    public List<Event> findEventsByOrganizer(Long organizerId) {
        return eventRepository.findByOrganisateurId(organizerId);
    }
    public List<Event> searchPublicEvents(String keyword,
                                          Category category,
                                          String city,
                                          LocalDate date) {

        return eventRepository.findByStatut(Status.PUBLIE)
                .stream()
                .filter(e -> keyword == null || keyword.isBlank()
                        || e.getTitre().toLowerCase().contains(keyword.toLowerCase()))
                .filter(e -> category == null || e.getCategorie() == category)
                .filter(e -> city == null || city.isBlank()
                        || e.getVille().equalsIgnoreCase(city))
                .filter(e -> date == null || e.getDateDebut().toLocalDate().equals(date))
                .filter(e -> e.getDateFin().isAfter(java.time.LocalDateTime.now()))
                .collect(Collectors.toList());
    }

}

