package com.example.vaadinproject.views;

import com.example.vaadinproject.entities.Event;
import com.example.vaadinproject.entities.Reservation;
import com.example.vaadinproject.entities.ReservationStatus;
import com.example.vaadinproject.entities.User;
import com.example.vaadinproject.services.EventService;
import com.example.vaadinproject.services.ReservationService;
import com.example.vaadinproject.services.SessionService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Route(value = "client/dashboard", layout = MainLayout.class)
@PageTitle("My Dashboard")
@RolesAllowed("USER")
public class DashboardView extends VerticalLayout implements BeforeEnterObserver {

    private final ReservationService reservationService;
    private final EventService eventService;
    private final SessionService sessionService;

    public DashboardView(ReservationService reservationService,
                               EventService eventService,
                               SessionService sessionService) {
        this.reservationService = reservationService;
        this.eventService = eventService;
        this.sessionService = sessionService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Check if user is logged in
        if (!sessionService.isLoggedIn()) {
            event.rerouteTo("login");
            return;
        }

        User currentUser = sessionService.getCurrentUser();

        if (currentUser == null) {
            event.forwardTo("session-expired");
            return;
        }

        // Build the dashboard
        buildDashboard(currentUser);
    }

    private void buildDashboard(User currentUser) {
        // Welcome message
        H2 welcomeTitle = new H2("Welcome back, " + currentUser.getPrenom() + "!");
        welcomeTitle.getStyle().set("color", "#A14C3A").set("margin-bottom", "10px");
        add(welcomeTitle);

        Span subtitle = new Span("Here's an overview of your activities");
        subtitle.getStyle().set("color", "#666").set("margin-bottom", "20px");
        add(subtitle);

        // Get user's reservations
        List<Reservation> userReservations = reservationService.findByUtilisateurId(currentUser.getId());

        // Calculate statistics
        long totalReservations = userReservations.size();
        long confirmedReservations = userReservations.stream()
                .filter(r -> r.getStatut() == ReservationStatus.CONFIRMEE)
                .count();

        // Upcoming events (confirmed reservations with future dates)
        long upcomingEvents = userReservations.stream()
                .filter(r -> r.getStatut() == ReservationStatus.CONFIRMEE)
                .filter(r -> r.getEvenement().getDateDebut().isAfter(LocalDateTime.now()))
                .count();

        // Total amount spent (confirmed reservations only)
        double totalSpent = userReservations.stream()
                .filter(r -> r.getStatut() == ReservationStatus.CONFIRMEE)
                .mapToDouble(Reservation::getMontantTotal)
                .sum();

        // Pending reservations
        long pendingReservations = userReservations.stream()
                .filter(r -> r.getStatut() == ReservationStatus.EN_ATTENTE)
                .count();

        // Statistics cards
        HorizontalLayout statsCards = new HorizontalLayout();
        statsCards.setWidthFull();
        statsCards.setSpacing(true);

        statsCards.add(
                createStatCard("My Reservations", String.valueOf(totalReservations),
                        String.format("Confirmed: %d | Pending: %d", confirmedReservations, pendingReservations),
                        VaadinIcon.TICKET, "#2196F3"),
                createStatCard("Upcoming Events", String.valueOf(upcomingEvents),
                        "Events you're attending",
                        VaadinIcon.CALENDAR_CLOCK, "#4CAF50"),
                createStatCard("Total Spent", String.format("%.2f DH", totalSpent),
                        "From confirmed bookings",
                        VaadinIcon.DOLLAR, "#FF9800")
        );

        add(statsCards);

        // Quick actions section
        add(createQuickActionsSection());

        // Notifications section
        add(createNotificationsSection(userReservations));

        // Upcoming events section
        add(createUpcomingEventsSection(userReservations));

        // Recent reservations section
        add(createRecentReservationsSection(userReservations));
    }

    private Div createStatCard(String cardTitle, String cardValue, String cardSubtitle,
                               VaadinIcon icon, String color) {
        Div card = new Div();
        card.getStyle()
                .set("background", "white")
                .set("padding", "20px")
                .set("border-radius", "8px")
                .set("box-shadow", "0 2px 4px rgba(0,0,0,0.1)")
                .set("flex", "1")
                .set("border-left", "4px solid " + color);

        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setJustifyContentMode(JustifyContentMode.BETWEEN);
        header.setAlignItems(Alignment.CENTER);

        H3 title = new H3(cardTitle);
        title.getStyle().set("margin", "0 0 10px 0").set("font-size", "14px").set("color", "#666");

        Icon cardIcon = icon.create();
        cardIcon.setColor(color);
        cardIcon.setSize("24px");

        header.add(title, cardIcon);

        Span value = new Span(cardValue);
        value.getStyle().set("font-size", "28px").set("font-weight", "bold")
                .set("display", "block").set("color", color);

        Span subtitle = new Span(cardSubtitle);
        subtitle.getStyle().set("font-size", "12px").set("color", "#999");

        card.add(header, value, subtitle);
        return card;
    }

    private VerticalLayout createQuickActionsSection() {
        VerticalLayout section = new VerticalLayout();
        section.setPadding(false);
        section.setSpacing(true);

        H3 sectionTitle = new H3("Quick Actions");
        sectionTitle.getStyle().set("margin-top", "30px").set("margin-bottom", "10px");

        HorizontalLayout actionsLayout = new HorizontalLayout();
        actionsLayout.setWidthFull();
        actionsLayout.setSpacing(true);

        Button browseEventsBtn = new Button("Browse Events", new Icon(VaadinIcon.SEARCH));
        browseEventsBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        browseEventsBtn.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("all-events")));

        Button myReservationsBtn = new Button("My Reservations", new Icon(VaadinIcon.LIST));
        myReservationsBtn.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        myReservationsBtn.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("my-reservations")));

        Button myProfileBtn = new Button("My Profile", new Icon(VaadinIcon.USER));
        myProfileBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        myProfileBtn.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("profile")));

        actionsLayout.add(browseEventsBtn, myReservationsBtn, myProfileBtn);

        section.add(sectionTitle, actionsLayout);
        return section;
    }

    private VerticalLayout createNotificationsSection(List<Reservation> reservations) {
        VerticalLayout section = new VerticalLayout();
        section.setPadding(false);
        section.setSpacing(true);

        H3 sectionTitle = new H3("Important Notifications");
        sectionTitle.getStyle().set("margin-top", "30px").set("margin-bottom", "10px");

        VerticalLayout notificationsContainer = new VerticalLayout();
        notificationsContainer.setPadding(true);
        notificationsContainer.setSpacing(true);
        notificationsContainer.getStyle()
                .set("background", "white")
                .set("border-radius", "8px")
                .set("box-shadow", "0 2px 4px rgba(0,0,0,0.1)");

        boolean hasNotifications = false;

        // Check for pending reservations
        long pendingCount = reservations.stream()
                .filter(r -> r.getStatut() == ReservationStatus.EN_ATTENTE)
                .count();
        if (pendingCount > 0) {
            notificationsContainer.add(createNotification(
                    "Pending Reservations",
                    String.format("You have %d reservation(s) waiting for confirmation", pendingCount),
                    VaadinIcon.CLOCK, "#FF9800"
            ));
            hasNotifications = true;
        }

        // Check for upcoming events (within 7 days)
        List<Reservation> upcomingSoon = reservations.stream()
                .filter(r -> r.getStatut() == ReservationStatus.CONFIRMEE)
                .filter(r -> {
                    LocalDateTime eventDate = r.getEvenement().getDateDebut();
                    LocalDateTime now = LocalDateTime.now();
                    LocalDateTime weekFromNow = now.plusDays(7);
                    return eventDate.isAfter(now) && eventDate.isBefore(weekFromNow);
                })
                .collect(Collectors.toList());

        if (!upcomingSoon.isEmpty()) {
            notificationsContainer.add(createNotification(
                    "Events Coming Soon",
                    String.format("%d event(s) happening in the next 7 days", upcomingSoon.size()),
                    VaadinIcon.BELL, "#4CAF50"
            ));
            hasNotifications = true;
        }

        // Check for cancelled reservations (recent)
        long cancelledCount = reservations.stream()
                .filter(r -> r.getStatut() == ReservationStatus.ANNULEE)
                .filter(r -> r.getDateReservation().isAfter(LocalDateTime.now().minusDays(30)))
                .count();
        if (cancelledCount > 0) {
            notificationsContainer.add(createNotification(
                    "Cancelled Reservations",
                    String.format("%d reservation(s) were cancelled in the last 30 days", cancelledCount),
                    VaadinIcon.CLOSE_CIRCLE, "#F44336"
            ));
            hasNotifications = true;
        }

        if (!hasNotifications) {
            Span noNotifications = new Span("No new notifications");
            noNotifications.getStyle().set("color", "#999").set("font-style", "italic");
            notificationsContainer.add(noNotifications);
        }

        section.add(sectionTitle, notificationsContainer);
        return section;
    }

    private HorizontalLayout createNotification(String title, String message, VaadinIcon icon, String color) {
        HorizontalLayout notification = new HorizontalLayout();
        notification.setAlignItems(Alignment.START);
        notification.setSpacing(true);
        notification.setPadding(true);
        notification.getStyle()
                .set("border-left", "4px solid " + color)
                .set("background", "#f9f9f9")
                .set("border-radius", "4px")
                .set("margin-bottom", "10px");

        Icon notifIcon = icon.create();
        notifIcon.setColor(color);
        notifIcon.setSize("24px");

        VerticalLayout content = new VerticalLayout();
        content.setPadding(false);
        content.setSpacing(false);

        Span titleSpan = new Span(title);
        titleSpan.getStyle().set("font-weight", "bold").set("color", "#333");

        Span messageSpan = new Span(message);
        messageSpan.getStyle().set("font-size", "14px").set("color", "#666");

        content.add(titleSpan, messageSpan);
        notification.add(notifIcon, content);

        return notification;
    }

    private VerticalLayout createUpcomingEventsSection(List<Reservation> reservations) {
        VerticalLayout section = new VerticalLayout();
        section.setPadding(false);
        section.setSpacing(true);

        H3 sectionTitle = new H3("Upcoming Events");
        sectionTitle.getStyle().set("margin-top", "30px").set("margin-bottom", "10px");

        Grid<Reservation> grid = new Grid<>(Reservation.class, false);
        grid.setHeight("300px");
        grid.getStyle()
                .set("background", "white")
                .set("border-radius", "8px")
                .set("box-shadow", "0 2px 4px rgba(0,0,0,0.1)");

        grid.addColumn(r -> r.getEvenement().getTitre())
                .setHeader("Event")
                .setAutoWidth(true)
                .setFlexGrow(2);

        grid.addColumn(r -> r.getEvenement().getDateDebut()
                        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
                .setHeader("Date")
                .setAutoWidth(true);

        grid.addColumn(r -> r.getEvenement().getVille())
                .setHeader("Location")
                .setAutoWidth(true);

        grid.addColumn(Reservation::getNombrePlaces)
                .setHeader("Seats")
                .setAutoWidth(true);

        grid.addColumn(new ComponentRenderer<>(reservation -> {
                    Span statusBadge = new Span(reservation.getStatut().toString());

                    switch (reservation.getStatut()) {
                        case CONFIRMEE:
                            statusBadge.getStyle()
                                    .set("background-color", "#4CAF50")
                                    .set("color", "white");
                            break;
                        case EN_ATTENTE:
                            statusBadge.getStyle()
                                    .set("background-color", "#FF9800")
                                    .set("color", "white");
                            break;
                        default:
                            statusBadge.getStyle()
                                    .set("background-color", "#9E9E9E")
                                    .set("color", "white");
                    }

                    statusBadge.getStyle()
                            .set("padding", "4px 8px")
                            .set("border-radius", "4px")
                            .set("font-size", "12px")
                            .set("font-weight", "bold");

                    return statusBadge;
                }))
                .setHeader("Status")
                .setAutoWidth(true);

        // Filter and sort upcoming events
        List<Reservation> upcomingReservations = reservations.stream()
                .filter(r -> r.getStatut() == ReservationStatus.CONFIRMEE)
                .filter(r -> r.getEvenement().getDateDebut().isAfter(LocalDateTime.now()))
                .sorted((r1, r2) -> r1.getEvenement().getDateDebut()
                        .compareTo(r2.getEvenement().getDateDebut()))
                .limit(5)
                .collect(Collectors.toList());

        if (upcomingReservations.isEmpty()) {
            grid.setVisible(false);
            Span noEvents = new Span("No upcoming events");
            noEvents.getStyle()
                    .set("color", "#999")
                    .set("font-style", "italic")
                    .set("padding", "20px")
                    .set("background", "white")
                    .set("border-radius", "8px")
                    .set("display", "block")
                    .set("text-align", "center");
            section.add(sectionTitle, noEvents);
        } else {
            grid.setItems(upcomingReservations);
            section.add(sectionTitle, grid);
        }

        return section;
    }

    private VerticalLayout createRecentReservationsSection(List<Reservation> reservations) {
        VerticalLayout section = new VerticalLayout();
        section.setPadding(false);
        section.setSpacing(true);

        H3 sectionTitle = new H3("Recent Reservations");
        sectionTitle.getStyle().set("margin-top", "30px").set("margin-bottom", "10px");

        Grid<Reservation> grid = new Grid<>(Reservation.class, false);
        grid.setHeight("300px");
        grid.getStyle()
                .set("background", "white")
                .set("border-radius", "8px")
                .set("box-shadow", "0 2px 4px rgba(0,0,0,0.1)");

        grid.addColumn(r -> r.getDateReservation()
                        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
                .setHeader("Booked On")
                .setAutoWidth(true);

        grid.addColumn(r -> r.getEvenement().getTitre())
                .setHeader("Event")
                .setAutoWidth(true)
                .setFlexGrow(2);

        grid.addColumn(Reservation::getNombrePlaces)
                .setHeader("Seats")
                .setAutoWidth(true);

        grid.addColumn(r -> String.format("%.2f DH", r.getMontantTotal()))
                .setHeader("Amount")
                .setAutoWidth(true);

        grid.addColumn(new ComponentRenderer<>(reservation -> {
                    Span statusBadge = new Span(reservation.getStatut().toString());

                    switch (reservation.getStatut()) {
                        case CONFIRMEE:
                            statusBadge.getStyle()
                                    .set("background-color", "#4CAF50")
                                    .set("color", "white");
                            break;
                        case EN_ATTENTE:
                            statusBadge.getStyle()
                                    .set("background-color", "#FF9800")
                                    .set("color", "white");
                            break;
                        case ANNULEE:
                            statusBadge.getStyle()
                                    .set("background-color", "#F44336")
                                    .set("color", "white");
                            break;
                    }

                    statusBadge.getStyle()
                            .set("padding", "4px 8px")
                            .set("border-radius", "4px")
                            .set("font-size", "12px")
                            .set("font-weight", "bold");

                    return statusBadge;
                }))
                .setHeader("Status")
                .setAutoWidth(true);

        // Sort by reservation date descending and limit to 10 most recent
        List<Reservation> recentReservations = reservations.stream()
                .sorted((r1, r2) -> r2.getDateReservation().compareTo(r1.getDateReservation()))
                .limit(10)
                .collect(Collectors.toList());

        grid.setItems(recentReservations);

        section.add(sectionTitle, grid);
        return section;
    }
}