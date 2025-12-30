package com.example.vaadinproject.views;

import com.example.vaadinproject.components.*;
import com.example.vaadinproject.entities.*;
import com.example.vaadinproject.services.EventService;
import com.example.vaadinproject.services.ReservationService;
import com.example.vaadinproject.services.SessionService;
import com.example.vaadinproject.utils.CSVExporter;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.List;
import java.util.stream.Collectors;

@Route(value = "admin/reservations", layout = MainLayout.class)
@PageTitle("Gestion des Réservations")
public class AllReservationsView extends VerticalLayout implements BeforeEnterObserver {

    private final ReservationService reservationService;
    private final EventService eventService;
    private final SessionService sessionService;

    private Grid<Reservation> grid;
    private ReservationFilterPanel filterPanel;
    private ReservationStatisticsPanel statisticsPanel;
    private List<Reservation> allReservations;
    private User currentUser;

    public AllReservationsView(ReservationService reservationService,
                               EventService eventService,
                               SessionService sessionService) {
        this.reservationService = reservationService;
        this.eventService = eventService;
        this.sessionService = sessionService;

        setSizeFull();
        setPadding(true);

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        currentUser = sessionService.getCurrentUser();

        // Check if user is logged in
        if (currentUser == null) {
            event.forwardTo("session-expired");
            return;
        }

        // Check if user has admin OR organizer role (fixed logic)
        if (!currentUser.isAdmin() && !currentUser.isOrganizer()) {
            event.rerouteTo("unauthorized");
            return;
        }

        // User is authorized, build the view
        buildView();
    }

    private void buildView() {
        // Clear any existing components (in case of navigation back)
        removeAll();

        // Initialize grid first
        grid = new Grid<>(Reservation.class, false);
        ReservationGridConfigurator.configureGrid(
                grid,
                this::showReservationDetails,
                this::confirmReservation,
                this::cancelReservation
        );

        // Load data
        loadReservations();

        // Create UI components
        statisticsPanel = new ReservationStatisticsPanel(allReservations);
        filterPanel = createFilterPanel();

        // Add all components to view
        add(
                createHeader(),
                statisticsPanel,
                filterPanel,
                grid
        );
    }

    private Component createHeader() {
        H2 title = new H2(currentUser.getRole() == Role.ADMIN ?
                "Toutes les Réservations" : "Réservations de mes Événements");
        title.getStyle().set("color", "#A14C3A");

        Button exportBtn = new Button("Exporter CSV", new Icon(VaadinIcon.DOWNLOAD));
        exportBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        exportBtn.addClickListener(e -> exportToCSV());

        HorizontalLayout headerLayout = new HorizontalLayout(title, exportBtn);
        headerLayout.setWidthFull();
        headerLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        headerLayout.setAlignItems(Alignment.CENTER);

        return headerLayout;
    }

    private ReservationFilterPanel createFilterPanel() {
        List<Event> events = getEventsForCurrentUser();
        return new ReservationFilterPanel(events, this::applyFilters);
    }

    private List<Event> getEventsForCurrentUser() {
        if (currentUser.getRole() == Role.ADMIN) {
            return eventService.findAll();
        } else {
            // Organizer only sees their own events
            return eventService.findByOrganisateurId(currentUser.getId());
        }
    }

    private void loadReservations() {
        if (currentUser.getRole() == Role.ADMIN) {
            // Admin sees all reservations
            allReservations = reservationService.findAllWithDetails();
        } else {
            // Organizer only sees reservations for their events
            allReservations = getOrganizerReservations();
        }

        grid.setItems(allReservations);
    }

    private List<Reservation> getOrganizerReservations() {
        List<Long> eventIds = eventService.findByOrganisateurId(currentUser.getId())
                .stream()
                .map(Event::getId)
                .collect(Collectors.toList());

        return reservationService.findByEventIdsWithDetails(eventIds);
    }



    private void applyFilters() {
        List<Reservation> filtered = allReservations.stream()
                .filter(filterPanel::matches)
                .collect(Collectors.toList());

        grid.setItems(filtered);
    }

    private void showReservationDetails(Reservation reservation) {
        new ReservationDetailsDialog(reservation).open();
    }

    private void confirmReservation(Reservation reservation) {
        // Check authorization before confirming
        if (!canManageReservation(reservation)) {
            showErrorNotification("Vous n'êtes pas autorisé à gérer cette réservation");
            return;
        }

        new ReservationConfirmDialog(
                reservation,
                this::handleConfirmReservation,
                this::handleReservationError
        ).open();
    }

    private void handleConfirmReservation(Reservation reservation) {
        try {
            reservationService.confirmerReservation(reservation);
            showSuccessNotification("Réservation confirmée avec succès");
            refreshData();
        } catch (Exception ex) {
            showErrorNotification("Erreur: " + ex.getMessage());
        }
    }

    private void cancelReservation(Reservation reservation) {
        // Check authorization before canceling
        if (!canManageReservation(reservation)) {
            showErrorNotification("Vous n'êtes pas autorisé à gérer cette réservation");
            return;
        }

        new ReservationCancelDialog(
                reservation,
                this::handleCancelReservation,
                this::handleReservationError
        ).open();
    }

    private void handleCancelReservation(Reservation reservation) {
        try {
            reservationService.annulerReservation(reservation);
            showSuccessNotification("Réservation annulée avec succès");
            refreshData();
        } catch (Exception ex) {
            showErrorNotification("Erreur: " + ex.getMessage());
        }
    }

    private void handleReservationError() {
        showErrorNotification("Une erreur s'est produite lors de l'opération");
    }

    /**
     * Check if current user can manage this reservation
     * Admin can manage all reservations
     * Organizer can only manage reservations for their own events
     */
    private boolean canManageReservation(Reservation reservation) {
        if (currentUser.getRole() == Role.ADMIN) {
            return true;
        }

        if (currentUser.getRole() == Role.ORGANIZER) {
            // Check if the reservation's event belongs to this organizer
            return reservation.getEvenement()
                    .getOrganisateur()
                    .getId()
                    .equals(currentUser.getId());
        }

        return false;
    }

    private void exportToCSV() {
        new CSVExporter().export(allReservations);
    }

    private void refreshData() {
        loadReservations();
        applyFilters();
        updateStatistics();
    }

    private void updateStatistics() {
        remove(statisticsPanel);
        statisticsPanel = new ReservationStatisticsPanel(allReservations);
        addComponentAtIndex(1, statisticsPanel);
    }

    private void showSuccessNotification(String message) {
        Notification.show(message, 3000, Notification.Position.MIDDLE)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private void showErrorNotification(String message) {
        Notification.show(message, 3000, Notification.Position.MIDDLE)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
}