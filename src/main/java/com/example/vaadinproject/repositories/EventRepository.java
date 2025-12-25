package com.example.vaadinproject.repositories;

import com.example.vaadinproject.entities.Category;
import com.example.vaadinproject.entities.Event;
import com.example.vaadinproject.entities.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    /* ===== KEYWORD SEARCH ===== */
    @Query("select e from Event e " +
            "where lower(e.titre) like lower(concat('%', :searchTerm, '%')) " +
            "or lower(e.description) like lower(concat('%', :searchTerm, '%'))")
    List<Event> search(@Param("searchTerm") String searchTerm);

    /* ===== BASIC FILTERS ===== */
    List<Event> findByStatut(Status statut);

    List<Event> findByStatutAndCategorie(Status statut, Category categorie);

    List<Event> findByVilleIgnoreCase(String ville);

    /* ===== PUBLIC EVENTS ===== */
    @Query("""
        select e from Event e
        where e.statut = :statut
          and (:categorie is null or e.categorie = :categorie)
          and (:ville is null or lower(e.ville) = lower(:ville))
          and (:date is null or e.dateDebut >= :dateStart and e.dateDebut <= :dateEnd)
          and e.dateFin > CURRENT_TIMESTAMP
        """)
    List<Event> searchPublicEvents(@Param("statut") Status statut,
                                   @Param("categorie") Category categorie,
                                   @Param("ville") String ville,
                                   @Param("dateStart") LocalDateTime dateStart,
                                   @Param("dateEnd") LocalDateTime dateEnd);
}
