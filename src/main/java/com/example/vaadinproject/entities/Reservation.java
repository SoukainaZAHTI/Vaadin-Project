package com.example.vaadinproject.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.Random;

@Entity
@Table(name = "reservations", uniqueConstraints = {
        @UniqueConstraint(columnNames = "codeReservation")
})
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Le nombre de places est obligatoire")
    @Min(value = 1, message = "Le nombre de places doit être au moins de 1")
    @Max(value = 10, message = "Le nombre de places ne peut pas dépasser 10")
    @Column(nullable = false)
    private Integer nombrePlaces;

    @NotNull(message = "Le montant total est obligatoire")
    @DecimalMin(value = "0.0", message = "Le montant doit être positif")
    @Column(nullable = false)
    private Double montantTotal;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dateReservation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReservationStatus statut = ReservationStatus.EN_ATTENTE;

    @Column(nullable = false, unique = true, length = 20)
    private String codeReservation;

    @Column(length = 500)
    private String commentaire;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private User utilisateur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evenement_id", nullable = false)
    private Event evenement;

    protected Reservation() {}

    public Reservation(User utilisateur, Event evenement, Integer nombrePlaces, String commentaire) {
        this.utilisateur = utilisateur;
        this.evenement = evenement;
        this.nombrePlaces = nombrePlaces;
        this.commentaire = commentaire;
        this.montantTotal = evenement.getPrixUnitaire() * nombrePlaces;
        this.statut = ReservationStatus.EN_ATTENTE;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getNombrePlaces() { return nombrePlaces; }
    public void setNombrePlaces(Integer nombrePlaces) { this.nombrePlaces = nombrePlaces; }

    public Double getMontantTotal() { return montantTotal; }
    public void setMontantTotal(Double montantTotal) { this.montantTotal = montantTotal; }

    public LocalDateTime getDateReservation() { return dateReservation; }
    public void setDateReservation(LocalDateTime dateReservation) {
        this.dateReservation = dateReservation;
    }

    public ReservationStatus getStatut() { return statut; }
    public void setStatut(ReservationStatus statut) { this.statut = statut; }

    public String getCodeReservation() { return codeReservation; }
    public void setCodeReservation(String codeReservation) {
        this.codeReservation = codeReservation;
    }

    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }

    public User getUtilisateur() { return utilisateur; }
    public void setUtilisateur(User utilisateur) { this.utilisateur = utilisateur; }

    public Event getEvenement() { return evenement; }
    public void setEvenement(Event evenement) { this.evenement = evenement; }

    // Méthodes utilitaires
    public boolean peutEtreAnnulee() {
        if (statut == ReservationStatus.ANNULEE) {
            return false;
        }
        LocalDateTime limite = evenement.getDateDebut().minusHours(48);
        return LocalDateTime.now().isBefore(limite);
    }

    public String getStatutLabel() {
        return statut.getLabel();
    }

    public String getStatutColor() {
        return statut.getColor();
    }

    @PrePersist
    protected void onCreate() {
        if (codeReservation == null) {
            codeReservation = "EVT-" + String.format("%05d", new Random().nextInt(100000));
        }
        if (dateReservation == null) {
            dateReservation = LocalDateTime.now();
        }
        if (montantTotal == null && evenement != null && nombrePlaces != null) {
            montantTotal = evenement.getPrixUnitaire() * nombrePlaces;
        }
    }
}