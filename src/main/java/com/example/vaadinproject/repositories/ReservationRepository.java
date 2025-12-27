package com.example.vaadinproject.repositories;

import com.example.vaadinproject.entities.Reservation;
import com.example.vaadinproject.entities.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByStatut(ReservationStatus statut);
    @Query("SELECT r FROM Reservation r WHERE r.evenement.id IN :eventIds")
    List<Reservation> findByEventIds(@Param("eventIds") List<Long> eventIds);
}