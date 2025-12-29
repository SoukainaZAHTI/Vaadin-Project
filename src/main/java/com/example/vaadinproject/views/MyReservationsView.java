package com.example.vaadinproject.views;

import com.example.vaadinproject.entities.Reservation;
import com.example.vaadinproject.entities.ReservationStatus;
import com.example.vaadinproject.entities.User;
import com.example.vaadinproject.services.ReservationService;
import com.example.vaadinproject.services.SessionService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Route(value = "my-reservations", layout =  MainLayout.class)
@PageTitle("Mes Réservations")
public class MyReservationsView extends VerticalLayout {

    private final ReservationService reservationService;
    private final SessionService sessionService;
    private Grid<Reservation> grid;

    public MyReservationsView(ReservationService reservationService, SessionService sessionService) {
        this.reservationService = reservationService;
        this.sessionService = sessionService;

        setSizeFull();
        setPadding(true);

        // Check authentication
        User currentUser = sessionService.getCurrentUser();
        if (currentUser == null) {
            UI.getCurrent().navigate("login");
            return;
        }

        buildView(currentUser);
    }

    private void buildView(User currentUser) {
        // Header
        H2 title = new H2("Mes Réservations");
        title.getStyle().set("color", "#A14C3A");

        // Stats summary
        HorizontalLayout stats = createStatsLayout(currentUser);

        // Grid
        grid = createReservationsGrid();
        loadReservations(currentUser);

        add(title, stats, grid);
    }

    private HorizontalLayout createStatsLayout(User currentUser) {
        List<Reservation> allReservations = reservationService.findByUtilisateur(currentUser.getId());

        long confirmed = allReservations.stream()
                .filter(r -> r.getStatut() == ReservationStatus.CONFIRMEE)
                .count();
        long pending = allReservations.stream()
                .filter(r -> r.getStatut() == ReservationStatus.EN_ATTENTE)
                .count();
        long cancelled = allReservations.stream()
                .filter(r -> r.getStatut() == ReservationStatus.ANNULEE)
                .count();

        HorizontalLayout statsLayout = new HorizontalLayout();
        statsLayout.setWidthFull();
        statsLayout.setSpacing(true);
        statsLayout.getStyle().set("margin-bottom", "20px");

        statsLayout.add(
                createStatCard("Confirmées", String.valueOf(confirmed), "#4CAF50"),
                createStatCard("En attente", String.valueOf(pending), "#FF9800"),
                createStatCard("Annulées", String.valueOf(cancelled), "#F44336")
        );

        return statsLayout;
    }

    private VerticalLayout createStatCard(String label, String value, String color) {
        VerticalLayout card = new VerticalLayout();
        card.setPadding(true);
        card.setSpacing(false);
        card.getStyle()
                .set("background", "white")
                .set("border-left", "4px solid " + color)
                .set("border-radius", "8px")
                .set("box-shadow", "0 2px 4px rgba(0,0,0,0.1)")
                .set("flex", "1");

        H3 valueText = new H3(value);
        valueText.getStyle()
                .set("margin", "0")
                .set("color", color)
                .set("font-size", "32px");

        Span labelText = new Span(label);
        labelText.getStyle()
                .set("color", "#666")
                .set("font-size", "14px");

        card.add(valueText, labelText);
        return card;
    }

    private Grid<Reservation> createReservationsGrid() {
        Grid<Reservation> reservationGrid = new Grid<>(Reservation.class, false);
        reservationGrid.setHeight("600px");

        // Columns
        reservationGrid.addColumn(r -> r.getCodeReservation())
                .setHeader("Code")
                .setAutoWidth(true)
                .setSortable(true);

        reservationGrid.addColumn(r -> r.getEvenement().getTitre())
                .setHeader("Événement")
                .setAutoWidth(true)
                .setSortable(true);

        reservationGrid.addColumn(r -> formatDateTime(r.getEvenement().getDateDebut()))
                .setHeader("Date Événement")
                .setAutoWidth(true)
                .setSortable(true);

        reservationGrid.addColumn(r -> r.getNombrePlaces())
                .setHeader("Places")
                .setAutoWidth(true)
                .setSortable(true);

        reservationGrid.addColumn(r -> r.getMontantTotal() + " MAD")
                .setHeader("Montant")
                .setAutoWidth(true)
                .setSortable(true);

        reservationGrid.addComponentColumn(this::createStatusBadge)
                .setHeader("Statut")
                .setAutoWidth(true);

        reservationGrid.addColumn(r -> formatDateTime(r.getDateReservation()))
                .setHeader("Date Réservation")
                .setAutoWidth(true)
                .setSortable(true);

        reservationGrid.addComponentColumn(this::createActionButtons)
                .setHeader("Actions")
                .setAutoWidth(true);

        return reservationGrid;
    }

    private Span createStatusBadge(Reservation reservation) {
        Span badge = new Span(reservation.getStatutLabel());
        badge.getElement().getThemeList().add("badge");
        badge.getStyle()
                .set("background", reservation.getStatutColor())
                .set("color", "white")
                .set("padding", "4px 12px")
                .set("border-radius", "12px")
                .set("font-size", "12px")
                .set("font-weight", "bold");
        return badge;
    }

    private HorizontalLayout createActionButtons(Reservation reservation) {
        HorizontalLayout actions = new HorizontalLayout();
        actions.setSpacing(true);

        // View details button
        Button detailsBtn = new Button(new Icon(VaadinIcon.EYE));
        detailsBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        detailsBtn.addClickListener(e -> showReservationDetails(reservation));

        actions.add(detailsBtn);

        // Cancel button (only if can be cancelled)
        if (reservation.peutEtreAnnulee()) {
            Button cancelBtn = new Button(new Icon(VaadinIcon.CLOSE));
            cancelBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR);
            cancelBtn.addClickListener(e -> confirmCancellation(reservation));
            actions.add(cancelBtn);
        }

        return actions;
    }

    private void loadReservations(User currentUser) {
        List<Reservation> reservations = reservationService.findByUtilisateur(currentUser.getId());
        grid.setItems(reservations);
    }

    private void showReservationDetails(Reservation reservation) {
        Dialog dialog = new Dialog();
        dialog.setWidth("500px");

        VerticalLayout content = new VerticalLayout();
        content.setPadding(true);
        content.setSpacing(true);

        H3 title = new H3("Détails de la réservation");
        title.getStyle().set("color", "#A14C3A");

        content.add(
                title,
                createDetailRow("Code", reservation.getCodeReservation()),
                createDetailRow("Événement", reservation.getEvenement().getTitre()),
                createDetailRow("Date événement", formatDateTime(reservation.getEvenement().getDateDebut())),
                createDetailRow("Lieu", reservation.getEvenement().getLieu() + ", " + reservation.getEvenement().getVille()),
                createDetailRow("Nombre de places", String.valueOf(reservation.getNombrePlaces())),
                createDetailRow("Montant total", reservation.getMontantTotal() + " MAD"),
                createDetailRow("Statut", reservation.getStatutLabel()),
                createDetailRow("Date réservation", formatDateTime(reservation.getDateReservation()))
        );

        if (reservation.getCommentaire() != null && !reservation.getCommentaire().isEmpty()) {
            content.add(createDetailRow("Commentaire", reservation.getCommentaire()));
        }

        Button closeBtn = new Button("Fermer", e -> dialog.close());
        closeBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        content.add(closeBtn);
        dialog.add(content);
        dialog.open();
    }

    private HorizontalLayout createDetailRow(String label, String value) {
        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();

        Span labelSpan = new Span(label + ":");
        labelSpan.getStyle()
                .set("font-weight", "bold")
                .set("color", "#333")
                .set("min-width", "150px");

        Span valueSpan = new Span(value);
        valueSpan.getStyle().set("color", "#666");

        row.add(labelSpan, valueSpan);
        return row;
    }

    private void confirmCancellation(Reservation reservation) {
        Dialog confirmDialog = new Dialog();
        confirmDialog.setWidth("400px");

        VerticalLayout content = new VerticalLayout();
        content.setPadding(true);

        H3 title = new H3("Confirmer l'annulation");
        title.getStyle().set("color", "#F44336");

        Paragraph message = new Paragraph(
                "Êtes-vous sûr de vouloir annuler cette réservation ?"
        );

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setWidthFull();
        buttons.setJustifyContentMode(JustifyContentMode.END);

        Button cancelBtn = new Button("Non", e -> confirmDialog.close());

        Button confirmBtn = new Button("Oui, annuler", e -> {
            try {
                reservationService.annulerReservation(reservation);
                Notification.show("Réservation annulée avec succès", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                confirmDialog.close();
                loadReservations(sessionService.getCurrentUser());
            } catch (Exception ex) {
                Notification.show("Erreur: " + ex.getMessage(), 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        confirmBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        buttons.add(cancelBtn, confirmBtn);
        content.add(title, message, buttons);
        confirmDialog.add(content);
        confirmDialog.open();
    }

    private String formatDateTime(java.time.LocalDateTime dateTime) {
        if (dateTime == null) return "N/A";
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            return dateTime.format(formatter);
        } catch (Exception e) {
            return dateTime.toString();
        }
    }
}