package com.example.vaadinproject.components;

import com.example.vaadinproject.entities.Event;
import com.example.vaadinproject.entities.Reservation;
import com.example.vaadinproject.entities.ReservationStatus;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;

import java.util.List;

public class ReservationFilterPanel extends HorizontalLayout {

    private TextField searchField;
    private ComboBox<ReservationStatus> statusFilter;
    private ComboBox<Event> eventFilter;
    private final Runnable onFilterChange;

    public ReservationFilterPanel(List<Event> events, Runnable onFilterChange) {
        this.onFilterChange = onFilterChange;

        setWidthFull();
        setSpacing(true);
        getStyle().set("margin-bottom", "20px");

        createSearchField();
        createStatusFilter();
        createEventFilter(events);
        createClearButton();

        add(searchField, statusFilter, eventFilter, createClearButton());
    }

    private void createSearchField() {
        searchField = new TextField();
        searchField.setPlaceholder("Rechercher par code, utilisateur...");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setWidth("300px");
        searchField.getStyle().set("margin-top", "25px");
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.addValueChangeListener(e -> onFilterChange.run());
    }

    private void createStatusFilter() {
        statusFilter = new ComboBox<>("Statut");
        statusFilter.setItems(ReservationStatus.values());
        statusFilter.setItemLabelGenerator(ReservationStatus::getLabel);
        statusFilter.setWidth("200px");
        statusFilter.addValueChangeListener(e -> onFilterChange.run());
    }

    private void createEventFilter(List<Event> events) {
        eventFilter = new ComboBox<>("Événement");
        eventFilter.setItems(events);
        eventFilter.setItemLabelGenerator(Event::getTitre);
        eventFilter.setWidth("250px");
        eventFilter.addValueChangeListener(e -> onFilterChange.run());
    }

    private Button createClearButton() {
        Button clearBtn = new Button("Réinitialiser", new Icon(VaadinIcon.REFRESH));
        clearBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        clearBtn.getStyle().set("margin-top", "37px");
        clearBtn.addClickListener(e -> clearFilters());
        return clearBtn;
    }

    public void clearFilters() {
        searchField.clear();
        statusFilter.clear();
        eventFilter.clear();
        onFilterChange.run();
    }

    public boolean matches(Reservation reservation) {
        return matchesSearch(reservation) &&
                matchesStatus(reservation) &&
                matchesEvent(reservation);
    }

    private boolean matchesSearch(Reservation r) {
        if (searchField.isEmpty()) return true;
        String search = searchField.getValue().toLowerCase();
        return r.getCodeReservation().toLowerCase().contains(search) ||
                r.getUtilisateur().getNom().toLowerCase().contains(search) ||
                r.getUtilisateur().getPrenom().toLowerCase().contains(search) ||
                r.getUtilisateur().getEmail().toLowerCase().contains(search) ||
                r.getEvenement().getTitre().toLowerCase().contains(search);
    }

    private boolean matchesStatus(Reservation r) {
        return statusFilter.isEmpty() || r.getStatut() == statusFilter.getValue();
    }

    private boolean matchesEvent(Reservation r) {
        return eventFilter.isEmpty() ||
                r.getEvenement().getId().equals(eventFilter.getValue().getId());
    }
}