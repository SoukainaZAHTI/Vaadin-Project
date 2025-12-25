package com.example.vaadinproject.views;

import com.example.vaadinproject.entities.Event;
import com.example.vaadinproject.repositories.EventRepository;
import com.example.vaadinproject.services.EventService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;

@Route("events")
@PageTitle("Events")
@AnonymousAllowed
public class EventListView extends VerticalLayout {

    private final Grid<Event> grid = new Grid<>(Event.class, false);
    private final TextField filterText = new TextField();
    EventForm form;
    EventService service;

    @Autowired
    public EventListView(EventService service) {
        this.service = service;

        addClassName("event-list-view");
        setSizeFull();

        configureGrid();
        configureForm();

        add(getToolbar(), getContent());

        updateList();
    }

    private void updateList() {
        grid.setItems(service.findAllEvents(filterText.getValue()));

    }

    private void configureForm() {
        form = new EventForm(service.findAllEvents());
        form.setWidth("25em");

        form.addListener(EventForm.SaveEvent.class, this::saveEvent);
        form.addListener(EventForm.DeleteEvent.class, this::deleteEvent);
        form.addListener(EventForm.CreateNewEvent.class, this::CreateNewEvent);


    }

    private void CreateNewEvent(EventForm.CreateNewEvent event) {
        // Save to database
        service.saveEvent(event.getEvent());

        // Update UI list
        updateList();
    }

    private void saveEvent(EventForm.SaveEvent event) {
        service.saveEvent(event.getEvent());
        updateList();
    }

    private void deleteEvent(EventForm.DeleteEvent event) {
        service.deleteEvent(event.getEvent());
        updateList();
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

        grid.addColumn(Event::getOrganisateur)
                .setHeader("Organisateur")
                .setAutoWidth(true);

        grid.addColumn(Event::getStatut)
                .setHeader("Statut")
                .setAutoWidth(true);
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
        form.setEvent(new Event());
        form.setVisible(true);
        addClassName("editing");
    }


}
