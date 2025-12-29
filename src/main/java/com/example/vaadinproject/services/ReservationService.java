package com.example.vaadinproject.services;

import com.example.vaadinproject.entities.*;
import com.example.vaadinproject.exceptions.ReservationException;
import com.example.vaadinproject.repositories.EventRepository;
import com.example.vaadinproject.repositories.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final EventRepository eventRepository;  // ADD THIS LINE

    public ReservationService(ReservationRepository reservationRepository,
                              EventRepository eventRepository) {
        this.reservationRepository = reservationRepository;
        this.eventRepository = eventRepository;

    }

    @Transactional(readOnly = true)
    public List<Reservation> findAll() {
        List<Reservation> reservations = reservationRepository.findAllWithDetails();
        // Force initialization to avoid lazy loading issues
        reservations.forEach(r -> {
            r.getEvenement().getTitre();
            r.getUtilisateur().getNom();
        });
        return reservations;
    }

    public Optional<Reservation> findById(Long id) {
        return reservationRepository.findById(id);
    }


    public List<Reservation> findByStatut(ReservationStatus statut) {
        return reservationRepository.findByStatut(statut);
    }

    public Reservation save(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    public void delete(Reservation reservation) {
        reservationRepository.delete(reservation);
    }

    public void deleteById(Long id) {
        reservationRepository.deleteById(id);
    }

    // Business methods
    public void confirmerReservation(Reservation reservation) {
        reservation.setStatut(ReservationStatus.CONFIRMEE);
        reservationRepository.save(reservation);
    }

    public void annulerReservation(Reservation reservation) {
        if (!reservation.peutEtreAnnulee()) {
            throw new ReservationException("Cette réservation ne peut pas être annulée (délai de 48h dépassé)");
        }

        reservation.setStatut(ReservationStatus.ANNULEE);
        reservationRepository.save(reservation);

        // Free up seats
        Event event = reservation.getEvenement();
        event.updatePlacesDisponibles(event.getPlacesReservees());
        eventRepository.save(event);
    }

    public List<Reservation> findByEventIds(List<Long> eventIds) {
        return reservationRepository.findByEventIds(eventIds);
    }
    // Generate unique reservation code
    private String generateUniqueCode() {
        String code;
        do {
            code = "EVT-" + String.format("%05d", new Random().nextInt(100000));
        } while (reservationRepository.existsByCodeReservation(code));
        return code;
    }

    // Create reservation with validations
    public Reservation createReservation(User user, Event event, Integer nombrePlaces, String commentaire) {
        // Validation 1: Event must be published
        if (event.getStatut() != Status.PUBLIE) {
            throw new ReservationException("Cet événement n'est pas encore publié");
        }

        // Validation 2: Event must not be finished
        if (event.getDateDebut().isBefore(LocalDateTime.now())) {
            throw new ReservationException("Cet événement est déjà terminé");
        }

        // Validation 3: Check available seats
        if (event.getPlacesDisponibles() < nombrePlaces) {
            throw new ReservationException("Pas assez de places disponibles");
        }

        // Create and save reservation
        Reservation reservation = new Reservation(user, event, nombrePlaces, commentaire);
        reservation.setCodeReservation(generateUniqueCode());
        reservation = reservationRepository.save(reservation);

        event.setPlacesDisponibles(event.getCapaciteMax() - event.getPlacesReservees());
        eventRepository.save(event);

      return reservation;
    }

    public List<Reservation> findByUtilisateur(Long userId) {
        List<Reservation> reservations = reservationRepository.findByUtilisateurId(userId);
        // Force initialization while session is still open
        reservations.forEach(r -> {
            r.getEvenement().getTitre(); // Initialize event
        });
        return reservations;
    }

    // Find by reservation code
    public Optional<Reservation> findByCode(String code) {
        return reservationRepository.findByCodeReservation(code);
    }
}