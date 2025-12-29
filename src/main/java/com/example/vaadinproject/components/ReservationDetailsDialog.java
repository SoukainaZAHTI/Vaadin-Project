package com.example.vaadinproject.components;

import com.example.vaadinproject.entities.Reservation;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReservationDetailsDialog extends Dialog {

    public ReservationDetailsDialog(Reservation reservation) {
        setWidth("600px");

        VerticalLayout content = new VerticalLayout();
        content.setPadding(true);
        content.setSpacing(true);

        H3 title = new H3("Détails de la réservation");
        title.getStyle().set("color", "#A14C3A");

        content.add(title);
        addReservationDetails(content, reservation);
        addCloseButton(content);

        add(content);
    }

    private void addReservationDetails(VerticalLayout content, Reservation reservation) {
        content.add(
                createDetailRow("Code", reservation.getCodeReservation()),
                createDetailRow("Utilisateur",
                        reservation.getUtilisateur().getNom() + " " +
                                reservation.getUtilisateur().getPrenom()),
                createDetailRow("Email", reservation.getUtilisateur().getEmail()),
                createDetailRow("Téléphone", reservation.getUtilisateur().getTelephone()),
                createDetailRow("Événement", reservation.getEvenement().getTitre()),
                createDetailRow("Date événement",
                        formatDateTime(reservation.getEvenement().getDateDebut())),
                createDetailRow("Lieu",
                        reservation.getEvenement().getLieu() + ", " +
                                reservation.getEvenement().getVille()),
                createDetailRow("Nombre de places",
                        String.valueOf(reservation.getNombrePlaces())),
                createDetailRow("Prix unitaire",
                        reservation.getEvenement().getPrixUnitaire() + " MAD"),
                createDetailRow("Montant total",
                        String.format("%.2f MAD", reservation.getMontantTotal())),
                createDetailRow("Statut", reservation.getStatutLabel()),
                createDetailRow("Date réservation",
                        formatDateTime(reservation.getDateReservation()))
        );

        if (reservation.getCommentaire() != null && !reservation.getCommentaire().isEmpty()) {
            content.add(createDetailRow("Commentaire", reservation.getCommentaire()));
        }
    }

    private void addCloseButton(VerticalLayout content) {
        Button closeBtn = new Button("Fermer", e -> close());
        closeBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        content.add(closeBtn);
    }

    private HorizontalLayout createDetailRow(String label, String value) {
        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();

        Span labelSpan = new Span(label + ":");
        labelSpan.getStyle()
                .set("font-weight", "bold")
                .set("color", "#333")
                .set("min-width", "150px");

        Span valueSpan = new Span(value != null ? value : "N/A");
        valueSpan.getStyle().set("color", "#666");

        row.add(labelSpan, valueSpan);
        return row;
    }

    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return "N/A";
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            return dateTime.format(formatter);
        } catch (Exception e) {
            return dateTime.toString();
        }
    }
}