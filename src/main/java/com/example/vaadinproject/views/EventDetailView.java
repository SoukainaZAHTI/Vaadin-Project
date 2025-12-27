package com.example.vaadinproject.views;

import com.example.vaadinproject.entities.Event;
import com.example.vaadinproject.entities.User;
import com.example.vaadinproject.services.SessionService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.time.format.DateTimeFormatter;

public class EventDetailView extends Dialog {

    private final Event event;
    private final SessionService sessionService;

    public EventDetailView(Event event, SessionService sessionService) {
        this.event = event;
        this.sessionService = sessionService;

        setWidth("700px");
        setCloseOnOutsideClick(true);
        setCloseOnEsc(true);

        // Create dialog content
        VerticalLayout dialogLayout = createDialogLayout();
        add(dialogLayout);
    }

    private VerticalLayout createDialogLayout() {
        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(true);
        layout.setSpacing(true);

        // Header with close button
        HorizontalLayout header = createHeader();

        // Event image (if available)
        if (event.getImageUrl() != null && !event.getImageUrl().isEmpty()) {
            Image eventImage = new Image(event.getImageUrl(), event.getTitre());
            eventImage.setWidthFull();
            eventImage.setHeight("300px");
            eventImage.getStyle().set("object-fit", "cover").set("border-radius", "8px");
            layout.add(eventImage);
        }

        // Category badge
        if (event.getCategorie() != null) {
            Span categoryBadge = new Span(event.getCategorie().getLabel());
            categoryBadge.getStyle()
                    .set("background", "#E8F5E9")
                    .set("color", "#2E7D32")
                    .set("padding", "4px 12px")
                    .set("border-radius", "12px")
                    .set("font-size", "12px")
                    .set("font-weight", "bold")
                    .set("display", "inline-block");
            layout.add(categoryBadge);
        }

        // Description
        H3 descTitle = new H3("Description");
        descTitle.getStyle().set("margin", "10px 0 5px 0");

        Paragraph description = new Paragraph(
                event.getDescription() != null ? event.getDescription() : "No description available"
        );
        description.getStyle().set("color", "#606770");

        layout.add(descTitle, description);

        // Event info
        VerticalLayout infoSection = new VerticalLayout();
        infoSection.setPadding(false);
        infoSection.setSpacing(false);
        infoSection.getStyle()
                .set("background", "#F5F5F5")
                .set("padding", "15px")
                .set("border-radius", "8px");

        infoSection.add(
                createInfoRow(VaadinIcon.CALENDAR, "Start Date", formatDateTime(event.getDateDebut())),
                createInfoRow(VaadinIcon.CALENDAR_CLOCK, "End Date", formatDateTime(event.getDateFin())),
                createInfoRow(VaadinIcon.MAP_MARKER, "Location",
                        (event.getLieu() != null ? event.getLieu() : "") +
                                (event.getVille() != null ? ", " + event.getVille() : "")),
                createInfoRow(VaadinIcon.MONEY, "Price",
                        event.getPrixUnitaire() != null ? event.getPrixUnitaire() + " MAD" : "Free"),
                createInfoRow(VaadinIcon.USERS, "Available Seats",
                        event.getPlacesDisponibles() + " / " + event.getCapaciteMax())
        );

        layout.add(infoSection);

        // Organizer info
        if (event.getOrganisateur() != null) {
            H3 orgTitle = new H3("Organizer");
            orgTitle.getStyle().set("margin", "15px 0 5px 0");

            String organizerName = event.getOrganisateur().getNom() + " " +
                    event.getOrganisateur().getPrenom();
            Paragraph orgInfo = new Paragraph("📧 " + organizerName + " - " +
                    event.getOrganisateur().getEmail());
            orgInfo.getStyle().set("color", "#606770");

            layout.add(orgTitle, orgInfo);
        }

        // Action buttons
        HorizontalLayout buttons = createActionButtons();
        layout.add(buttons);

        return layout;
    }

    private HorizontalLayout createHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setPadding(false);
        header.getStyle().set("margin-bottom", "15px");

        H2 title = new H2(event.getTitre());
        title.getStyle()
                .set("margin", "0")
                .set("color", "#A14C3A")
                .set("font-size", "24px");

        Button closeButton = new Button(new Icon(VaadinIcon.CLOSE));
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        closeButton.addClickListener(e -> close());

        header.add(title, closeButton);
        return header;
    }

    private HorizontalLayout createInfoRow(VaadinIcon icon, String label, String value) {
        HorizontalLayout row = new HorizontalLayout();
        row.setSpacing(true);
        row.setAlignItems(FlexComponent.Alignment.CENTER);
        row.getStyle().set("margin-bottom", "8px");

        Icon itemIcon = new Icon(icon);
        itemIcon.getStyle().set("color", "#A14C3A");

        Span labelSpan = new Span(label + ": ");
        labelSpan.getStyle().set("font-weight", "bold").set("color", "#333");

        Span valueSpan = new Span(value);
        valueSpan.getStyle().set("color", "#606770");

        row.add(itemIcon, labelSpan, valueSpan);
        return row;
    }

    private HorizontalLayout createActionButtons() {
        HorizontalLayout actions = new HorizontalLayout();
        actions.setWidthFull();
        actions.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        actions.setSpacing(true);
        actions.getStyle().set("margin-top", "20px");

        Button closeBtn = new Button("Close");
        closeBtn.addClickListener(e -> close());

        Button bookButton = new Button("Book Now", new Icon(VaadinIcon.TICKET));
        bookButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        bookButton.getStyle()
                .set("background", "#A14C3A")
                .set("color", "white");

        // Check if seats are available
        Integer availableSeats = event.getPlacesDisponibles() != null ? event.getPlacesDisponibles() : event.getCapaciteMax();
        if (availableSeats <= 0) {
            bookButton.setEnabled(false);
            bookButton.setText("Sold Out");
        } else {
            bookButton.addClickListener(e -> handleBooking());
        }

        actions.add(closeBtn, bookButton);
        return actions;
    }

    private void handleBooking() {
        User currentUser = null;

        // Safely check if user is logged in
        if (sessionService != null) {
            currentUser = sessionService.getCurrentUser();
        }

        close();

        if (currentUser == null) {
            // User not logged in - redirect to login
            UI.getCurrent().navigate("login");
        } else {
            // User logged in - proceed to booking
            UI.getCurrent().navigate("booking/" + event.getId());
        }
    }

    private String formatDateTime(java.time.LocalDateTime dateTime) {
        if (dateTime == null) return "TBA";

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            return dateTime.format(formatter);
        } catch (Exception e) {
            return dateTime.toString();
        }
    }
}