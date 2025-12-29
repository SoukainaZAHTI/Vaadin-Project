package com.example.vaadinproject.views;

import com.example.vaadinproject.entities.Event;
import com.example.vaadinproject.entities.Reservation;
import com.example.vaadinproject.entities.User;
import com.example.vaadinproject.exceptions.ReservationException;
import com.example.vaadinproject.services.EventService;
import com.example.vaadinproject.services.ReservationService;
import com.example.vaadinproject.services.SessionService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.router.*;

@Route(value = "booking", layout =  MainLayout.class)
@PageTitle("Réservation")
public class ReservationFormView extends VerticalLayout implements HasUrlParameter<Long> {

    private final ReservationService reservationService;
    private final EventService eventService;
    private final SessionService sessionService;


    private Event currentEvent;
    private IntegerField nombrePlacesField;
    private TextArea commentaireField;
    private Span totalPriceSpan;

    public ReservationFormView(ReservationService reservationService,
                               EventService eventService,
                               SessionService sessionService) {
        this.reservationService = reservationService;
        this.eventService = eventService;
        this.sessionService = sessionService;

        setSizeFull();
        setPadding(true);
        setAlignItems(Alignment.CENTER);
    }

    @Override
    public void setParameter(BeforeEvent event, Long eventId) {
        // Load event
        currentEvent = eventService.getEventById(eventId)
                .orElse(null);

        if (currentEvent == null) {
            Notification.show("Événement introuvable", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            event.rerouteTo(AllEventsView.class);
            return;
        }

        // Check if user is logged in
        User currentUser = sessionService.getCurrentUser();
        if (currentUser == null) {
            event.rerouteTo("login");
            return;
        }

        buildForm();
    }

    private void buildForm() {
        removeAll();

        // Container
        VerticalLayout container = new VerticalLayout();
        container.setWidth("600px");
        container.setPadding(true);
        container.getStyle()
                .set("background", "white")
                .set("border-radius", "8px")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.1)");

        // Title
        H2 title = new H2("Réserver: " + currentEvent.getTitre());
        title.getStyle().set("color", "#A14C3A");

        // Event info summary
        Div eventInfo = createEventInfoSummary();

        // Number of seats field
        nombrePlacesField = new IntegerField("Nombre de places");
        nombrePlacesField.setMin(1);
        nombrePlacesField.setMax(Math.min(10, currentEvent.getPlacesDisponibles()));
        nombrePlacesField.setValue(1);
        nombrePlacesField.setWidthFull();
        nombrePlacesField.setHelperText("Maximum: " + Math.min(10, currentEvent.getPlacesDisponibles()) + " places");

        // Update total price on change
        nombrePlacesField.addValueChangeListener(e -> updateTotalPrice());

        // Comment field
        commentaireField = new TextArea("Commentaire (optionnel)");
        commentaireField.setWidthFull();
        commentaireField.setMaxLength(500);

        // Total price display
        totalPriceSpan = new Span();
        totalPriceSpan.getStyle()
                .set("font-size", "20px")
                .set("font-weight", "bold")
                .set("color", "#A14C3A");
        updateTotalPrice();

        // Buttons
        Button confirmButton = new Button("Confirm Reservation", e -> handleReservation());
        confirmButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        confirmButton.getStyle().set("background", "#A14C3A");

        Button cancelButton = new Button("Cancel", e -> getUI().ifPresent(ui -> ui.navigate("all-events")));

        // Add components
        container.add(title, eventInfo, nombrePlacesField, commentaireField, totalPriceSpan, confirmButton, cancelButton);
        add(container);
    }

    private Div createEventInfoSummary() {
        Div info = new Div();
        info.getStyle()
                .set("background", "#F5F5F5")
                .set("padding", "15px")
                .set("border-radius", "6px")
                .set("margin", "10px 0");

        info.add(
                new Paragraph("📅 " + currentEvent.getDateDebut().toLocalDate()),
                new Paragraph("📍 " + currentEvent.getLieu() + ", " + currentEvent.getVille()),
                new Paragraph("💺 Places disponibles: " + currentEvent.getPlacesDisponibles()),
                new Paragraph("💰 Prix unitaire: " + currentEvent.getPrixUnitaire() + " MAD")
        );

        return info;
    }

    private void updateTotalPrice() {
        if (nombrePlacesField.getValue() != null) {
            double total = nombrePlacesField.getValue() * currentEvent.getPrixUnitaire();
            totalPriceSpan.setText("Total: " + total + " MAD");
        }
    }

    private void handleReservation() {
        try {
            User currentUser = sessionService.getCurrentUser();
            Integer places = nombrePlacesField.getValue();
            String comment = commentaireField.getValue();

            // Validate
            if (places == null || places < 1 || places>10) {
                showError("Please enter a number between 1 and 10");
                return;
            }

            // Create reservation
            Reservation reservation = reservationService.createReservation(
                    currentUser, currentEvent, places, comment
            );

            // Show success
            showSuccess("Réservation confirmée! Code: " + reservation.getCodeReservation());

            // Navigate to user reservations
            getUI().ifPresent(ui -> ui.navigate("my-reservations"));

        } catch (ReservationException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            showError("Erreur lors de la réservation: " + e.getMessage());
        }
    }

    private void showSuccess(String message) {
        Notification notification = Notification.show(message, 5000, Notification.Position.MIDDLE);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private void showError(String message) {
        Notification notification = Notification.show(message, 5000, Notification.Position.MIDDLE);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
}