package com.example.vaadinproject.views;

import com.example.vaadinproject.entities.Event;
import com.example.vaadinproject.entities.ReservationStatus;
import com.example.vaadinproject.entities.Status;
import com.example.vaadinproject.entities.User;
import com.example.vaadinproject.services.EventService;
import com.example.vaadinproject.services.ReservationService;
import com.example.vaadinproject.services.SessionService;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.example.vaadinproject.entities.Status.TERMINE;

@Route(value = "organizer", layout = MainLayout.class)
@PageTitle("Organizer Dashboard")
@RolesAllowed("ORGANIZER")

public class OrganizerDashboardView extends VerticalLayout implements BeforeEnterObserver {

    private final EventService eventService;
    private final ReservationService reservationService;
    private final SessionService sessionService;

    public OrganizerDashboardView(EventService eventService, ReservationService reservationService,
                                  SessionService sessionService) {
        this.eventService = eventService;
        this.reservationService = reservationService;
        this.sessionService = sessionService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);
    }

    private void buildDashboard(User currentUser) {
        H2 title = new H2("Organizer Dashboard");
        add(title);

        Long organizerId = currentUser.getId();

        // Get organizer's events
        List<Event> organizerEvents = eventService.findEventsByOrganizer(organizerId);

        // Event statistics
        long totalEvents = organizerEvents.size();
        long publishedEvents = organizerEvents.stream()
                .filter(e -> e.getStatut() == Status.PUBLIE)
                .count();
        long draftEvents = organizerEvents.stream()
                .filter(e -> e.getStatut() == Status.BROUILLON)
                .count();
        long cancelledEvents = organizerEvents.stream()
                .filter(e -> e.getStatut() == Status.ANNULE)
                .count();
        long finishedEvents = organizerEvents.stream()
                .filter(e -> e.getStatut() == TERMINE)
                .count();

        // Get event IDs for reservation queries
        List<Long> eventIds = organizerEvents.stream()
                .map(Event::getId)
                .toList();

        // Reservation statistics using service methods (avoids lazy loading)
        long totalReservations = 0;
        long confirmedReservations = 0;
        long pendingReservations = 0;
        double totalRevenue = 0.0;

        if (!eventIds.isEmpty()) {
            List<com.example.vaadinproject.entities.Reservation> allReservations =
                    reservationService.findByEventIds(eventIds);

            totalReservations = allReservations.size();
            confirmedReservations = allReservations.stream()
                    .filter(r -> r.getStatut() == ReservationStatus.CONFIRMEE)
                    .count();
            pendingReservations = allReservations.stream()
                    .filter(r -> r.getStatut() == ReservationStatus.EN_ATTENTE)
                    .count();
            totalRevenue = allReservations.stream()
                    .filter(r -> r.getStatut() == ReservationStatus.CONFIRMEE)
                    .mapToDouble(r -> r.getMontantTotal())
                    .sum();
        }

        // Statistics cards
        HorizontalLayout statsCards = new HorizontalLayout();
        statsCards.setWidthFull();
        statsCards.setSpacing(true);

        statsCards.add(
                createStatCard("My Events", String.valueOf(totalEvents),
                        String.format("Published: %d, Draft: %d, Cancelled: %d, Finished: %d",
                                publishedEvents, draftEvents, cancelledEvents, finishedEvents)),
                createStatCard("Reservations", String.valueOf(totalReservations),
                        String.format("Confirmed: %d, Pending: %d", confirmedReservations, pendingReservations)),
                createStatCard("Revenue", String.format("%.2f DH", totalRevenue),
                        "From confirmed reservations")
        );

        add(statsCards);

        // Recent events section
        H3 recentEventsTitle = new H3("Recent Events");
        recentEventsTitle.getStyle().set("margin-top", "30px").set("margin-bottom", "10px");
        add(recentEventsTitle);

        Grid<Event> recentEventsGrid = createRecentEventsGrid(organizerEvents);
        add(recentEventsGrid);
    }

    private Div createStatCard(String cardTitle, String cardValue, String cardSubtitle) {
        Div card = new Div();
        card.getStyle()
                .set("background", "white")
                .set("padding", "20px")
                .set("border-radius", "8px")
                .set("box-shadow", "0 2px 4px rgba(0,0,0,0.1)")
                .set("flex", "1");

        H3 title = new H3(cardTitle);
        title.getStyle().set("margin", "0 0 10px 0").set("font-size", "14px").set("color", "#666");

        Span value = new Span(cardValue);
        value.getStyle().set("font-size", "24px").set("font-weight", "bold").set("display", "block");

        Span subtitle = new Span(cardSubtitle);
        subtitle.getStyle().set("font-size", "12px").set("color", "#999");

        card.add(title, value, subtitle);
        return card;
    }

    private Grid<Event> createRecentEventsGrid(List<Event> events) {
        Grid<Event> grid = new Grid<>(Event.class, false);
        grid.setHeight("400px");
        grid.getStyle()
                .set("background", "white")
                .set("border-radius", "8px")
                .set("box-shadow", "0 2px 4px rgba(0,0,0,0.1)");

        grid.addColumn(Event::getTitre)
                .setHeader("Title")
                .setAutoWidth(true)
                .setFlexGrow(2);

        grid.addColumn(Event::getCategorie)
                .setHeader("Category")
                .setAutoWidth(true);

        grid.addColumn(event -> event.getDateDebut().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
                .setHeader("Start Date")
                .setAutoWidth(true);

        grid.addColumn(Event::getVille)
                .setHeader("City")
                .setAutoWidth(true);

        grid.addColumn(new ComponentRenderer<>(event -> {
                    String status = event.getStatut().toString();
                    Span statusBadge = new Span(status);

                    switch (event.getStatut()) {
                        case PUBLIE:
                            statusBadge.getStyle()
                                    .set("background-color", "#4CAF50")
                                    .set("color", "white");
                            break;
                        case BROUILLON:
                            statusBadge.getStyle()
                                    .set("background-color", "#FF9800")
                                    .set("color", "white");
                            break;
                        case ANNULE:
                            statusBadge.getStyle()
                                    .set("background-color", "#F44336")
                                    .set("color", "white");
                            break;
                        case TERMINE:
                            statusBadge.getStyle()
                                    .set("background-color", "#9E9E9E")
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

        grid.addColumn(Event::getPlacesDisponibles)
                .setHeader("Available Seats")
                .setAutoWidth(true);

        grid.addColumn(event -> String.format("%.2f DH", event.getPrixUnitaire()))
                .setHeader("Price")
                .setAutoWidth(true);

        // Sort by creation date descending and limit to 10 most recent
        List<Event> recentEvents = events.stream()
                .sorted((e1, e2) -> e2.getDateCreation().compareTo(e1.getDateCreation()))
                .limit(10)
                .toList();

        grid.setItems(recentEvents);

        return grid;
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
        // Check if user has organizer role
        if (!currentUser.isOrganizer()) {
            event.rerouteTo("unauthorized");
            return;
        }

        // User is authorized, NOW build the dashboard
        buildDashboard(currentUser);
    }

}