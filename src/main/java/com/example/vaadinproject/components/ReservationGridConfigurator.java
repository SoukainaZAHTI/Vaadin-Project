package com.example.vaadinproject.components;

import com.example.vaadinproject.entities.Reservation;
import com.example.vaadinproject.entities.ReservationStatus;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

public class ReservationGridConfigurator {

    public static void configureGrid(Grid<Reservation> grid,
                                     Consumer<Reservation> onViewDetails,
                                     Consumer<Reservation> onConfirm,
                                     Consumer<Reservation> onCancel) {
        grid.setHeight("600px");
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

        addColumns(grid);
        addActionColumn(grid, onViewDetails, onConfirm, onCancel);
    }

    private static void addColumns(Grid<Reservation> grid) {
        grid.addColumn(Reservation::getCodeReservation)
                .setHeader("Code")
                .setWidth("120px")
                .setSortable(true);

        grid.addColumn(r -> r.getUtilisateur().getNom() + " " + r.getUtilisateur().getPrenom())
                .setHeader("Utilisateur")
                .setAutoWidth(true)
                .setSortable(true);

        grid.addColumn(r -> r.getUtilisateur().getEmail())
                .setHeader("Email")
                .setAutoWidth(true)
                .setSortable(true);

        grid.addColumn(r -> r.getEvenement().getTitre())
                .setHeader("Événement")
                .setAutoWidth(true)
                .setSortable(true);

        grid.addColumn(r -> formatDateTime(r.getEvenement().getDateDebut()))
                .setHeader("Date Événement")
                .setWidth("150px")
                .setSortable(true);

        grid.addColumn(Reservation::getNombrePlaces)
                .setHeader("Places")
                .setWidth("80px")
                .setSortable(true);

        grid.addColumn(r -> String.format("%.2f MAD", r.getMontantTotal()))
                .setHeader("Montant")
                .setWidth("120px")
                .setSortable(true);

        grid.addComponentColumn(ReservationGridConfigurator::createStatusBadge)
                .setHeader("Statut")
                .setWidth("130px");

        grid.addColumn(r -> formatDateTime(r.getDateReservation()))
                .setHeader("Date Réservation")
                .setWidth("150px")
                .setSortable(true);
    }

    private static void addActionColumn(Grid<Reservation> grid,
                                        Consumer<Reservation> onViewDetails,
                                        Consumer<Reservation> onConfirm,
                                        Consumer<Reservation> onCancel) {
        grid.addComponentColumn(reservation ->
                        createActionButtons(reservation, onViewDetails, onConfirm, onCancel))
                .setHeader("Actions")
                .setWidth("180px")
                .setFrozenToEnd(true);
    }

    private static Span createStatusBadge(Reservation reservation) {
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

    private static HorizontalLayout createActionButtons(Reservation reservation,
                                                        Consumer<Reservation> onViewDetails,
                                                        Consumer<Reservation> onConfirm,
                                                        Consumer<Reservation> onCancel) {
        HorizontalLayout actions = new HorizontalLayout();
        actions.setSpacing(true);

        // View details button
        Button detailsBtn = new Button(new Icon(VaadinIcon.EYE));
        detailsBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        detailsBtn.getElement().setAttribute("title", "Voir détails");
        detailsBtn.addClickListener(e -> onViewDetails.accept(reservation));
        actions.add(detailsBtn);

        // Confirm button (only for pending reservations)
        if (reservation.getStatut() == ReservationStatus.EN_ATTENTE) {
            Button confirmBtn = new Button(new Icon(VaadinIcon.CHECK));
            confirmBtn.addThemeVariants(
                    ButtonVariant.LUMO_TERTIARY,
                    ButtonVariant.LUMO_SMALL,
                    ButtonVariant.LUMO_SUCCESS
            );
            confirmBtn.getElement().setAttribute("title", "Confirmer");
            confirmBtn.addClickListener(e -> onConfirm.accept(reservation));
            actions.add(confirmBtn);
        }

        // Cancel button (only if can be cancelled)
        if (reservation.peutEtreAnnulee() && reservation.getStatut() != ReservationStatus.ANNULEE) {
            Button cancelBtn = new Button(new Icon(VaadinIcon.CLOSE));
            cancelBtn.addThemeVariants(
                    ButtonVariant.LUMO_TERTIARY,
                    ButtonVariant.LUMO_SMALL,
                    ButtonVariant.LUMO_ERROR
            );
            cancelBtn.getElement().setAttribute("title", "Annuler");
            cancelBtn.addClickListener(e -> onCancel.accept(reservation));
            actions.add(cancelBtn);
        }

        return actions;
    }

    private static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return "N/A";
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            return dateTime.format(formatter);
        } catch (Exception e) {
            return dateTime.toString();
        }
    }
}