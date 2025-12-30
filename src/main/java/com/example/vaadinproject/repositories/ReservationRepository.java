package com.example.vaadinproject.repositories;

import com.example.vaadinproject.entities.Reservation;
import com.example.vaadinproject.entities.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByStatut(ReservationStatus statut);
    @Query("SELECT r FROM Reservation r WHERE r.evenement.id IN :eventIds")
    List<Reservation> findByEventIds(@Param("eventIds") List<Long> eventIds);
    Optional<Reservation> findByCodeReservation(String codeReservation);
    boolean existsByCodeReservation(String codeReservation);

    @Query("SELECT r FROM Reservation r " +
            "LEFT JOIN FETCH r.evenement " +
            "LEFT JOIN FETCH r.utilisateur " +
            "WHERE r.utilisateur.id = :utilisateurId")
    List<Reservation> findByUtilisateurId(@Param("utilisateurId") Long utilisateurId);

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

    @Query("SELECT COALESCE(SUM(r.nombrePlaces), 0) FROM Reservation r " +
            "WHERE r.evenement.id = :eventId " +
            "AND r.statut IN ('EN_ATTENTE', 'CONFIRMEE')")
    Integer countTotalPlacesReserveesByEvent(@Param("eventId") Long eventId);
    @Query("SELECT r FROM Reservation r " +
            "WHERE r.dateReservation BETWEEN :startDate AND :endDate")
    List<Reservation> findByDateReservationBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}