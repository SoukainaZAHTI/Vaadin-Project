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
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;

@Route(value = "events", layout = MainLayout.class)  // Add layout parameter
@PageTitle("Events")
public class EventListView extends VerticalLayout implements BeforeEnterObserver {

    private final SessionService sessionService;

    private final Grid<Event> grid = new Grid<>(Event.class, false);
    private final TextField filterText = new TextField();
    EventForm form;
    EventService service;

    @Autowired
    public EventListView(EventService service, SessionService sessionService) {
        this.service = service;
        this.sessionService = sessionService;


        addClassName("event-list-view");
        setSizeFull();

        configureGrid();
        configureForm();

        add(getToolbar(), getContent());

        updateList();
    }

    private void updateList() {
        // Check if user is logged in and is an organizer
        if (sessionService.isLoggedIn() && sessionService.isOrganizer()) {
            // Show only current organizer's events
            Long organizerId = sessionService.getCurrentUser().getId();

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
        } else {
            // For non-organizers or not logged in, show all events
            grid.setItems(service.findAllEvents(filterText.getValue()));
        }
    }

    private void configureForm() {
        form = new EventForm(service.findAllEvents());
        form.setWidth("25em");
        form.setVisible(false); // Hide by default

        form.addListener(EventForm.SaveEvent.class, this::saveEvent);
        form.addListener(EventForm.DeleteEvent.class, this::deleteEvent);
        form.addListener(EventForm.CloseEvent.class, e -> closeEditor());
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (!sessionService.isLoggedIn() || !sessionService.isOrganizer()) {
            event.rerouteTo("login");
        }
    }

    private void saveEvent(EventForm.SaveEvent event) {
        Event evt = event.getEvent();

        // Auto-assign current organizer to new events
        if (evt.getId() == null && sessionService.isLoggedIn()) {
            evt.setOrganisateur(sessionService.getCurrentUser());
        }

        service.saveEvent(evt);
        updateList();
        closeEditor();
    }
    private void deleteEvent(EventForm.DeleteEvent event) {
        service.deleteEvent(event.getEvent());
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
        grid.setSizeFull();

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

        HorizontalLayout toolbar = new HorizontalLayout(filterText, addEventButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }
    private void addEvent() {
        grid.asSingleSelect().clear();
        Event newEvent = new Event();
        form.setEvent(newEvent);
        form.setVisible(true);
        addClassName("editing");
    }


}
