package com.example.vaadinproject.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le titre est obligatoire")
    @Size(min = 5, max = 100)
    @Column(nullable = false, length = 100)
    private String titre;

    @Size(max = 1000)
    @Column(length = 1000)
    private String description;

    @NotNull(message = "La catégorie est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Category categorie;

    @NotNull(message = "La date de début est obligatoire")
    @Column(nullable = false)
    private LocalDateTime dateDebut;

    @NotNull(message = "La date de fin est obligatoire")
    @Column(nullable = false)
    private LocalDateTime dateFin;

    @NotBlank(message = "Le lieu est obligatoire")
    @Column(nullable = false, length = 200)
    private String lieu;

    @NotBlank(message = "La ville est obligatoire")
    @Column(nullable = false, length = 100)
    private String ville;

    @NotNull(message = "La capacité maximale est obligatoire")
    @Min(value = 1, message = "La capacité doit être au moins de 1")
    @Column(nullable = false)
    private Integer capaciteMax;

    @NotNull(message = "Le prix unitaire est obligatoire")
    @DecimalMin(value = "0.0", message = "Le prix doit être positif")
    @Column(nullable = false)
    private Double prixUnitaire;

    @Column(length = 500)
    private String imageUrl;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status statut = Status.BROUILLON;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    private LocalDateTime dateModification;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "organisateur_id")
    private User organisateur;

    @OneToMany(mappedBy = "evenement", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Reservation> reservations = new ArrayList<>();

    @Column(name = "places_disponibles")
    private Integer placesDisponibles;

    // Update this whenever a reservation is made/cancelled
    public void updatePlacesDisponibles(Integer reservedSeats) {
        this.placesDisponibles = this.capaciteMax - reservedSeats;
    }
    public Event() {}

    public Event(String titre, String description, Category categorie, LocalDateTime dateDebut,
                 LocalDateTime dateFin, String lieu, String ville, Integer capaciteMax,
                 Double prixUnitaire, User organisateur) {
        this.titre = titre;
        this.description = description;
        this.categorie = categorie;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.lieu = lieu;
        this.ville = ville;
        this.capaciteMax = capaciteMax;
        this.prixUnitaire = prixUnitaire;
        this.organisateur = organisateur;
        this.statut = Status.BROUILLON;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Category getCategorie() { return categorie; }
    public void setCategorie(Category categorie) { this.categorie = categorie; }

    public LocalDateTime getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDateTime dateDebut) { this.dateDebut = dateDebut; }

    public LocalDateTime getDateFin() { return dateFin; }
    public void setDateFin(LocalDateTime dateFin) { this.dateFin = dateFin; }

    public String getLieu() { return lieu; }
    public void setLieu(String lieu) { this.lieu = lieu; }

    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }

    public Integer getCapaciteMax() { return capaciteMax; }
    public void setCapaciteMax(Integer capaciteMax) { this.capaciteMax = capaciteMax; }

    public Double getPrixUnitaire() { return prixUnitaire; }
    public void setPrixUnitaire(Double prixUnitaire) { this.prixUnitaire = prixUnitaire; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Status getStatut() { return statut; }
    public void setStatut(Status statut) { this.statut = statut; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public LocalDateTime getDateModification() { return dateModification; }
    public void setDateModification(LocalDateTime dateModification) {
        this.dateModification = dateModification;
    }

    public User getOrganisateur() { return organisateur; }
    public void setOrganisateur(User organisateur) { this.organisateur = organisateur; }

    public List<Reservation> getReservations() { return reservations; }
    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    // Méthodes utilitaires
    public int getPlacesReservees() {
        return reservations.stream()
                .filter(r -> r.getStatut() == ReservationStatus.CONFIRMEE
                        || r.getStatut() == ReservationStatus.EN_ATTENTE)
                .mapToInt(Reservation::getNombrePlaces)
                .sum();
    }


    public Integer getPlacesDisponibles() {
        if (placesDisponibles == null) {
            return capaciteMax - getPlacesReservees();
        }
        return placesDisponibles;
    }
    public void setPlacesDisponibles(Integer placesDisponibles) {
        this.placesDisponibles = placesDisponibles;
    }
    public boolean isDisponible() {
        return statut == Status.PUBLIE &&
                dateDebut.isAfter(LocalDateTime.now()) &&
                getPlacesDisponibles() > 0;
    }

    public double getTauxRemplissage() {
        if (capaciteMax == 0) return 0;
        return (getPlacesReservees() * 100.0) / capaciteMax;
    }

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
        dateModification = LocalDateTime.now();
        validate();
    }

    @PreUpdate
    protected void onUpdate() {
        dateModification = LocalDateTime.now();
        validate();
    }

    private void validate() {
        if (dateFin != null && dateDebut != null && dateFin.isBefore(dateDebut)) {
            throw new IllegalStateException("La date de fin doit être après la date de début");
        }
    }
}
