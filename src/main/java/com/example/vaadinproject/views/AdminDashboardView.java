package com.example.vaadinproject.views;

import com.example.vaadinproject.entities.ReservationStatus;
import com.example.vaadinproject.entities.Role;
import com.example.vaadinproject.entities.Status;
import com.example.vaadinproject.entities.User;
import com.example.vaadinproject.services.EventService;
import com.example.vaadinproject.services.ReservationService;
import com.example.vaadinproject.services.SessionService;
import com.example.vaadinproject.services.UserService;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route(value = "admin", layout = MainLayout.class)
@PageTitle("Admin Dashboard")
@RolesAllowed("ADMIN")
public class AdminDashboardView extends VerticalLayout implements BeforeEnterObserver {

    private final UserService userService;
    private final EventService eventService;
    private final ReservationService reservationService;
    private final SessionService sessionService;

    public AdminDashboardView(UserService userService, EventService eventService,
                              ReservationService reservationService, SessionService sessionService) {
        this.userService = userService;
        this.eventService = eventService;
        this.reservationService = reservationService;
        this.sessionService = sessionService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        H2 title = new H2("Admin Dashboard");
        add(title);

        // User statistics
        long totalUsers = userService.findAll().size();
        long totalClients = userService.findByRole(Role.CLIENT).size();
        long totalOrganizers = userService.findByRole(Role.ORGANIZER).size();
        long totalAdmins = userService.findByRole(Role.ADMIN).size();

        // Event statistics
        long totalEvents = eventService.findAll().size();
        long publishedEvents = eventService.findByStatut(Status.PUBLIE).size();
        long draftEvents = eventService.findByStatut(Status.BROUILLON).size();
        long cancelledEvents = eventService.findByStatut(Status.ANNULE).size();
        long finishedEvents = eventService.findByStatut(Status.TERMINE).size();

        // Reservation statistics
        long totalReservations = reservationService.findAll().size();
        long confirmedReservations = reservationService.findByStatut(ReservationStatus.CONFIRMEE).size();
        long pendingReservations = reservationService.findByStatut(ReservationStatus.EN_ATTENTE).size();
        long cancelledReservations = reservationService.findByStatut(ReservationStatus.ANNULEE).size();

        // Revenue calculation
        double totalRevenue = reservationService.findAll().stream()
                .filter(reservation -> reservation.getStatut() == ReservationStatus.CONFIRMEE)
                .mapToDouble(reservation -> reservation.getMontantTotal())
                .sum();

        // Statistics cards
        HorizontalLayout statsCards = new HorizontalLayout();
        statsCards.setWidthFull();
        statsCards.setSpacing(true);

        statsCards.add(
                createStatCard("Users", String.valueOf(totalUsers),
                        String.format("Clients: %d, Organizers: %d, Admins: %d",
                                totalClients, totalOrganizers, totalAdmins)),
                createStatCard("Events", String.valueOf(totalEvents),
                        String.format("Published: %d, Draft: %d, Cancelled: %d, Finished: %d",
                                publishedEvents, draftEvents, cancelledEvents, finishedEvents)),
                createStatCard("Reservations", String.valueOf(totalReservations),
                        String.format("Confirmed: %d, Pending: %d, Cancelled: %d",
                                confirmedReservations, pendingReservations, cancelledReservations)),
                createStatCard("Revenue", String.format("%.2f DH", totalRevenue),
                        "From confirmed reservations")
        );

        add(statsCards);
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

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Check if user is logged in
        if (!sessionService.isLoggedIn()) {
            event.rerouteTo("login");
            return;
        }

        User currentUser = sessionService.getCurrentUser();

        // Check if user has admin role
        if (!currentUser.isAdmin()) {
            event.rerouteTo("unauthorized");
            return;
        }
    }
}