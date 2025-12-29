package com.example.vaadinproject.views;

import com.example.vaadinproject.entities.Event;
import com.example.vaadinproject.entities.User;
import com.example.vaadinproject.services.EventService;
import com.example.vaadinproject.services.SessionService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "events", layout = MainLayout.class)
@PageTitle("Events")
public class EventListView extends VerticalLayout implements BeforeEnterObserver {

    private final SessionService sessionService;
    private final Grid<Event> grid = new Grid<>(Event.class, false);
    private final TextField filterText = new TextField();
    EventForm form;
    EventService service;

    public EventListView(EventService service, SessionService sessionService) {
        this.service = service;
        this.sessionService = sessionService;

        addClassName("event-list-view");
        setWidthFull();
        setPadding(true);
        setSpacing(true);

        configureGrid();
        configureForm();

        add(getToolbar(), getContent());

        updateList();
    }



    private void updateList() {
        User currentUser = sessionService.getCurrentUser();

        if (currentUser == null) {
            grid.setItems();
            return;
        }

        // Admins can see all events
        if (currentUser.isAdmin()) {
            grid.setItems(service.findAllEvents(filterText.getValue()));
        }
        // Organizers can only see their own events
        else if (currentUser.isOrganizer()) {
            Long organizerId = currentUser.getId();

            if (filterText.getValue() == null || filterText.getValue().isEmpty()) {
                grid.setItems(service.findEventsByOrganizer(organizerId));
            } else {
                // Filter within organizer's own events
                grid.setItems(
                        service.findEventsByOrganizer(organizerId).stream()
                                .filter(e -> e.getTitre().toLowerCase()
                                        .contains(filterText.getValue().toLowerCase()))
                                .toList()
                );
            }
        }
    }

    private void configureForm() {
        form = new EventForm(service.findAllEvents());
        form.setWidth("25em");
        form.setVisible(false);

        form.addListener(EventForm.SaveEvent.class, this::saveEvent);
        form.addListener(EventForm.DeleteEvent.class, this::deleteEvent);
        form.addListener(EventForm.CloseEvent.class, e -> closeEditor());
    }



    private void saveEvent(EventForm.SaveEvent event) {
        Event evt = event.getEvent();
        User currentUser = sessionService.getCurrentUser();

        // Auto-assign current organizer to new events (for organizers only)
        if (evt.getId() == null && currentUser.isOrganizer()) {
            evt.setOrganisateur(currentUser);
        }

        // Initialize placesDisponibles for new events
        if (evt.getId() == null && evt.getPlacesDisponibles() == null) {
            evt.setPlacesDisponibles(evt.getCapaciteMax());
        }

        // Validation: Organizers can only edit their own events
        if (currentUser.isOrganizer() && evt.getId() != null) {
            if (!evt.getOrganisateur().getId().equals(currentUser.getId())) {
                form.setVisible(false);
                return;
            }
        }

        service.saveEvent(evt);
        updateList();
        closeEditor();
    }

    private void deleteEvent(EventForm.DeleteEvent event) {
        Event evt = event.getEvent();
        User currentUser = sessionService.getCurrentUser();

        // Validation: Organizers can only delete their own events
        if (currentUser.isOrganizer()) {
            if (!evt.getOrganisateur().getId().equals(currentUser.getId())) {
                closeEditor();
                return; // Prevent deleting other organizer's events
            }
        }

        // Admins can delete any event

        service.deleteEvent(evt);
        updateList();
        closeEditor();
    }

    private void closeEditor() {
        form.setEvent(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    private void editEvent(Event event) {
        if (event == null) {
            closeEditor();
        } else {
            User currentUser = sessionService.getCurrentUser();

            // Check permissions before allowing edit
            if (currentUser.isOrganizer()) {
                // Organizers can only edit their own events
                if (!event.getOrganisateur().getId().equals(currentUser.getId())) {
                    closeEditor();
                    return;
                }
            }
            // Admins can edit any event

            form.setEvent(event);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private Component getContent() {
        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);
        content.addClassNames("content");
        content.setSizeFull();
        return content;
    }

    private void configureGrid() {
        grid.addClassName("event-grid");
        grid.setWidthFull();  // Changed from setSizeFull()
        grid.setHeight("600px");

        grid.addColumn(Event::getTitre)
                .setHeader("Titre")
                .setAutoWidth(true);

        grid.addColumn(Event::getDescription)
                .setHeader("Description")
                .setAutoWidth(true);

        grid.addColumn(Event::getCategorie)
                .setHeader("Catégorie")
                .setAutoWidth(true);

        grid.addColumn(Event::getDateDebut)
                .setHeader("Date de début")
                .setAutoWidth(true);

        grid.addColumn(Event::getDateFin)
                .setHeader("Date de fin")
                .setAutoWidth(true);

        grid.addColumn(Event::getLieu)
                .setHeader("Lieu")
                .setAutoWidth(true);

        grid.addColumn(Event::getVille)
                .setHeader("Ville")
                .setAutoWidth(true);

        grid.addColumn(event -> {
                    User organisateur = event.getOrganisateur();
                    return organisateur != null ? organisateur.getNomComplet() : "N/A";
                })
                .setHeader("Organisateur")
                .setAutoWidth(true);

        grid.addColumn(Event::getStatut)
                .setHeader("Statut")
                .setAutoWidth(true);

        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                editEvent(event.getValue());
            } else {
                closeEditor();
            }
        });
    }

    private HorizontalLayout getToolbar() {
        filterText.setPlaceholder("Filter by title...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());

        Button addEventButton = new Button("Add Event");
        addEventButton.addClickListener(e -> addEvent());

        // Only show "Add Event" button for organizers, not admins
        if (sessionService.getCurrentUser().isOrganizer()) {
            addEventButton.setVisible(true);
        } else {
            addEventButton.setVisible(false);
        }

        HorizontalLayout toolbar = new HorizontalLayout(filterText, addEventButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void addEvent() {
        // Only organizers can add new events
        if (!sessionService.getCurrentUser().isOrganizer()) {
            return;
        }

        grid.asSingleSelect().clear();
        Event newEvent = new Event();
        form.setEvent(newEvent);
        form.setVisible(true);
        addClassName("editing");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Allow both organizers and admins
        if (!sessionService.isLoggedIn() ||
                (!sessionService.isOrganizer() && !sessionService.isAdmin())) {
            event.rerouteTo("unauthorized");
        }
        User currentUser = sessionService.getCurrentUser();

        if (currentUser == null) {
            event.forwardTo("session-expired");
            return;
        }
    }


}