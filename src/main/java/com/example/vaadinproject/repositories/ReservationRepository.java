package com.example.vaadinproject.repositories;

import com.example.vaadinproject.entities.Reservation;
import com.example.vaadinproject.entities.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByStatut(ReservationStatus statut);
    @Query("SELECT r FROM Reservation r WHERE r.evenement.id IN :eventIds")
    List<Reservation> findByEventIds(@Param("eventIds") List<Long> eventIds);
    List<Reservation> findByUtilisateurId(Long utilisateurId);
    Optional<Reservation> findByCodeReservation(String codeReservation);
    boolean existsByCodeReservation(String codeReservation);


    @Query("SELECT DISTINCT r FROM Reservation r " +
            "LEFT JOIN FETCH r.evenement " +
            "LEFT JOIN FETCH r.utilisateur " +
            "WHERE r.id > 0")
    List<Reservation> findAllWithDetails();

    @Query("SELECT DISTINCT r FROM Reservation r " +
            "LEFT JOIN FETCH r.evenement " +
            "LEFT JOIN FETCH r.utilisateur " +
            "WHERE r.evenement.id IN :eventIds")
    List<Reservation> findByEventIdsWithDetails(@Param("eventIds") List<Long> eventIds);
}