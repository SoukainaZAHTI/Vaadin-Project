package com.example.vaadinproject.services;

import com.example.vaadinproject.entities.Reservation;
import com.example.vaadinproject.entities.ReservationStatus;
import com.example.vaadinproject.repositories.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public List<Reservation> findAll() {
        return reservationRepository.findAll();
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
        if (reservation.peutEtreAnnulee()) {
            reservation.setStatut(ReservationStatus.ANNULEE);
            reservationRepository.save(reservation);
        } else {
            throw new IllegalStateException("Cette réservation ne peut pas être annulée");
        }
    }

    public List<Reservation> findByEventIds(List<Long> eventIds) {
        return reservationRepository.findByEventIds(eventIds);
    }
}