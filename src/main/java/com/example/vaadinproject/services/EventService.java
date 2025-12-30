package com.example.vaadinproject.services;

import com.example.vaadinproject.entities.Category;
import com.example.vaadinproject.entities.Event;
import com.example.vaadinproject.entities.Status;
import com.example.vaadinproject.repositories.EventRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service

public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @PostConstruct
    @Transactional
    public void initializeExistingEvents() {
        List<Event> events = eventRepository.findAll();
        boolean needsUpdate = false;

        for (Event event : events) {
            if (event.getPlacesDisponibles() == null) {
                event.setPlacesDisponibles(event.getCapaciteMax());
                needsUpdate = true;
            }
        }

        if (needsUpdate) {
            eventRepository.saveAll(events);
            System.out.println("✅ Initialized placesDisponibles for existing events");
        }
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

    public Event saveEvent(Event event) {
        // Initialize placesDisponibles for new events
        if (event.getId() == null && event.getPlacesDisponibles() == null) {
            event.setPlacesDisponibles(event.getCapaciteMax());
        }

        // If capacity changed, adjust available seats proportionally
        if (event.getId() != null) {
            Event existingEvent = eventRepository.findById(event.getId()).orElse(null);
            if (existingEvent != null &&
                    !existingEvent.getCapaciteMax().equals(event.getCapaciteMax())) {
                // Recalculate: keep the same number of reserved seats
                Integer reservedSeats = existingEvent.getCapaciteMax() - existingEvent.getPlacesDisponibles();
                event.setPlacesDisponibles(event.getCapaciteMax() - reservedSeats);
            }
        }

        return eventRepository.save(event);
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
    public List<Event> searchPublicEvents(String keyword, Category category, String city, LocalDate date) {
        System.out.println("EventService.searchPublicEvents called"); // Debug
        System.out.println("Params: keyword=" + keyword + ", category=" + category +
                ", city=" + city + ", date=" + date); // Debug

        List<Event> allEvents = eventRepository.findByStatut(Status.PUBLIE);
        System.out.println("Total published events: " + allEvents.size()); // Debug

        return allEvents.stream()
                .filter(event -> keyword == null || keyword.isEmpty() ||
                        event.getTitre().toLowerCase().contains(keyword.toLowerCase()) ||
                        (event.getDescription() != null &&
                                event.getDescription().toLowerCase().contains(keyword.toLowerCase())))
                .filter(event -> category == null || event.getCategorie() == category)
                .filter(event -> city == null || city.isEmpty() ||
                        event.getVille().toLowerCase().contains(city.toLowerCase()))
                .filter(event -> date == null ||
                        event.getDateDebut().toLocalDate().equals(date))
                .collect(Collectors.toList());
    }
    public List<Event> findByStatut(Status statut) {
        return eventRepository.findByStatut(statut);
    }

    public List<Event> findAll() {
        return eventRepository.findAll();
    }
    @Transactional(readOnly = true)
    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }


    @Transactional(readOnly = true)
    public List<Event> findByOrganisateurId(Long organisateurId) {
        return eventRepository.findByOrganisateurId(organisateurId);
    }
    public List<Event> filterEvents(String keyword, Category category, String city,
                                    LocalDate startDate, LocalDate endDate,
                                    Double minPrice, Double maxPrice, Long organizerId) {
        List<Event> events;

        // Get base list (all events or organizer's events)
        if (organizerId != null) {
            events = findEventsByOrganizer(organizerId);
        } else {
            events = findAllEvents(null);
        }

        // Apply filters
        return events.stream()
                .filter(e -> keyword == null || keyword.isEmpty() ||
                        e.getTitre().toLowerCase().contains(keyword.toLowerCase()) ||
                        (e.getDescription() != null && e.getDescription().toLowerCase().contains(keyword.toLowerCase())))
                .filter(e -> category == null || e.getCategorie() == category)
                .filter(e -> city == null || city.isEmpty() ||
                        (e.getVille() != null && e.getVille().toLowerCase().contains(city.toLowerCase())))
                .filter(e -> startDate == null || e.getDateDebut() == null ||
                        !e.getDateDebut().toLocalDate().isBefore(startDate))
                .filter(e -> endDate == null || e.getDateDebut() == null ||
                        !e.getDateDebut().toLocalDate().isAfter(endDate))
                .filter(e -> minPrice == null || e.getPrixUnitaire() == null || e.getPrixUnitaire() >= minPrice)
                .filter(e -> maxPrice == null || e.getPrixUnitaire() == null || e.getPrixUnitaire() <= maxPrice)
                .toList();
    }
}

