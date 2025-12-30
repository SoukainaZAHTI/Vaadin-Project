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
                        String.format("Clients: %d | Organizers: %d | Admins: %d",
                                totalClients, totalOrganizers, totalAdmins),
                        "#5E35B1"),  // Purple
                createStatCard("Events", String.valueOf(totalEvents),
                        String.format("Published: %d | Draft: %d | Cancelled: %d",
                                publishedEvents, draftEvents, cancelledEvents),
                        "#1E88E5"),  // Blue
                createStatCard("Reservations", String.valueOf(totalReservations),
                        String.format("Confirmed: %d | Pending: %d | Cancelled: %d",
                                confirmedReservations, pendingReservations, cancelledReservations),
                        "#43A047"),  // Green
                createStatCard("Total Revenue", String.format("%.2f DH", totalRevenue),
                        "From all confirmed reservations",
                        "#FB8C00")   // Orange
        );
        add(statsCards);
        // Detailed statistics section
        H3 detailsTitle = new H3("Statistiques Détaillées");
        detailsTitle.getStyle().set("margin-top", "30px");
        add(detailsTitle);

        HorizontalLayout detailsLayout = new HorizontalLayout();
        detailsLayout.setWidthFull();
        detailsLayout.setSpacing(true);

// Users breakdown
        Div usersDetail = createDetailCard("Utilisateurs par Rôle",
                new String[]{"Clients", "Organisateurs", "Administrateurs"},
                new Long[]{totalClients, totalOrganizers, totalAdmins},
                "#5E35B1");

// Events breakdown
        Div eventsDetail = createDetailCard("Événements par Statut",
                new String[]{"Publiés", "Brouillons", "Annulés", "Terminés"},
                new Long[]{publishedEvents, draftEvents, cancelledEvents, finishedEvents},
                "#1E88E5");

// Reservations breakdown
        Div reservationsDetail = createDetailCard("Réservations par Statut",
                new String[]{"Confirmées", "En Attente", "Annulées"},
                new Long[]{confirmedReservations, pendingReservations, cancelledReservations},
                "#43A047");

        detailsLayout.add(usersDetail, eventsDetail, reservationsDetail);
        add(detailsLayout);
        // Top insights section
        H3 insightsTitle = new H3("Indicateurs Clés");
        insightsTitle.getStyle().set("margin-top", "30px");
        add(insightsTitle);

        HorizontalLayout insightsLayout = new HorizontalLayout();
        insightsLayout.setWidthFull();
        insightsLayout.setSpacing(true);

// Calculs avancés
        double avgReservationAmount = totalReservations > 0 ? totalRevenue / totalReservations : 0;
        double confirmationRate = totalReservations > 0 ? (confirmedReservations * 100.0 / totalReservations) : 0;
        double eventPublishRate = totalEvents > 0 ? (publishedEvents * 100.0 / totalEvents) : 0;

        insightsLayout.add(
                createInsightCard("Montant Moyen", String.format("%.2f DH", avgReservationAmount),
                        "par réservation", "#00897B"),
                createInsightCard("Taux de Confirmation", String.format("%.1f%%", confirmationRate),
                        "des réservations", "#7CB342"),
                createInsightCard("Taux de Publication", String.format("%.1f%%", eventPublishRate),
                        "des événements", "#5E35B1")
        );

        add(insightsLayout);
    }

    private Div createInsightCard(String label, String value, String subtitle, String color) {
        Div card = new Div();
        card.getStyle()
                .set("background", "white")
                .set("padding", "20px")
                .set("border-radius", "12px")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.1)")
                .set("flex", "1")
                .set("text-align", "center")
                .set("border-top", "3px solid " + color);

        Span labelSpan = new Span(label);
        labelSpan.getStyle()
                .set("display", "block")
                .set("color", "#666")
                .set("font-size", "14px")
                .set("margin-bottom", "10px");

        Span valueSpan = new Span(value);
        valueSpan.getStyle()
                .set("display", "block")
                .set("font-size", "28px")
                .set("font-weight", "bold")
                .set("color", color)
                .set("margin-bottom", "5px");

        Span subtitleSpan = new Span(subtitle);
        subtitleSpan.getStyle()
                .set("display", "block")
                .set("color", "#999")
                .set("font-size", "12px");

        card.add(labelSpan, valueSpan, subtitleSpan);
        return card;
    }
    private Div createDetailCard(String cardTitle, String[] labels, Long[] values, String accentColor) {
        Div card = new Div();
        card.getStyle()
                .set("background", "white")
                .set("padding", "20px")
                .set("border-radius", "12px")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.1)")
                .set("flex", "1")
                .set("border-left", "4px solid " + accentColor);

        H3 title = new H3(cardTitle);
        title.getStyle()
                .set("margin", "0 0 15px 0")
                .set("font-size", "16px")
                .set("color", "#333");

        card.add(title);

        // Calculate total
        long total = java.util.Arrays.stream(values).mapToLong(Long::longValue).sum();

        for (int i = 0; i < labels.length; i++) {
            HorizontalLayout row = new HorizontalLayout();
            row.setWidthFull();
            row.setJustifyContentMode(JustifyContentMode.BETWEEN);
            row.setAlignItems(Alignment.CENTER);
            row.getStyle().set("margin-bottom", "10px");

            Span label = new Span(labels[i]);
            label.getStyle().set("color", "#666").set("font-size", "14px");

            HorizontalLayout valueLayout = new HorizontalLayout();
            valueLayout.setSpacing(true);
            valueLayout.setAlignItems(Alignment.CENTER);

            // Progress bar
            Div progressBar = new Div();
            double percentage = total > 0 ? (values[i] * 100.0 / total) : 0;
            progressBar.getStyle()
                    .set("width", "100px")
                    .set("height", "8px")
                    .set("background", "#e0e0e0")
                    .set("border-radius", "4px")
                    .set("position", "relative")
                    .set("overflow", "hidden");

            Div progressFill = new Div();
            progressFill.getStyle()
                    .set("width", String.format("%.0f%%", percentage))
                    .set("height", "100%")
                    .set("background", accentColor)
                    .set("border-radius", "4px")
                    .set("transition", "width 0.3s ease");

            progressBar.add(progressFill);

            Span value = new Span(String.valueOf(values[i]));
            value.getStyle()
                    .set("font-weight", "bold")
                    .set("color", "#333")
                    .set("min-width", "30px")
                    .set("text-align", "right");

            Span percent = new Span(String.format("(%.0f%%)", percentage));
            percent.getStyle().set("color", "#999").set("font-size", "12px");

            valueLayout.add(progressBar, value, percent);

            row.add(label, valueLayout);
            card.add(row);
        }

        return card;
    }

    private Div createStatCard(String cardTitle, String cardValue, String cardSubtitle, String color) {
        Div card = new Div();
        card.getStyle()
                .set("background", "linear-gradient(135deg, " + color + " 0%, " + color + "dd 100%)")
                .set("padding", "25px")
                .set("border-radius", "12px")
                .set("box-shadow", "0 4px 6px rgba(0,0,0,0.1)")
                .set("flex", "1")
                .set("color", "white")
                .set("min-width", "200px");

        H3 title = new H3(cardTitle);
        title.getStyle()
                .set("margin", "0 0 10px 0")
                .set("font-size", "14px")
                .set("color", "rgba(255,255,255,0.9)")
                .set("text-transform", "uppercase")
                .set("letter-spacing", "1px");

        Span value = new Span(cardValue);
        value.getStyle()
                .set("font-size", "32px")
                .set("font-weight", "bold")
                .set("display", "block")
                .set("margin", "10px 0");

        Span subtitle = new Span(cardSubtitle);
        subtitle.getStyle()
                .set("font-size", "13px")
                .set("color", "rgba(255,255,255,0.8)")
                .set("line-height", "1.5");

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