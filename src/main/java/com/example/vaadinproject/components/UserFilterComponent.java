package com.example.vaadinproject.components;

import com.example.vaadinproject.entities.Role;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

import java.util.function.Consumer;

public class UserFilterComponent extends VerticalLayout {

    private TextField searchField;
    private ComboBox<Role> roleCombo;
    private Checkbox activeOnlyCheckbox;
    private Button searchButton;
    private Button clearButton;

    private Consumer<FilterCriteria> filterListener;

    public UserFilterComponent() {
        setSpacing(true);
        setPadding(false);
        setWidthFull();

        initializeComponents();
        layoutComponents();
        configureListeners();
    }

    private void initializeComponents() {
        searchField = new TextField("Search");
        searchField.setPlaceholder("Search by name or email...");
        searchField.setClearButtonVisible(true);
        searchField.setWidthFull();

        roleCombo = new ComboBox<>("Role");
        roleCombo.setItems(Role.values());
        roleCombo.setItemLabelGenerator(Role::name);
        roleCombo.setClearButtonVisible(true);

        activeOnlyCheckbox = new Checkbox("Active Users Only");
        activeOnlyCheckbox.setValue(false);

        searchButton = new Button("Search");
        clearButton = new Button("Clear");
    }

    private void layoutComponents() {
        // First row: Search field and Role
        HorizontalLayout row1 = new HorizontalLayout(searchField, roleCombo);
        row1.setWidthFull();
        searchField.setWidth("60%");
        roleCombo.setWidth("40%");

        // Second row: Active checkbox and buttons
        HorizontalLayout row2 = new HorizontalLayout(activeOnlyCheckbox, searchButton, clearButton);
        row2.setWidthFull();
        row2.setAlignItems(Alignment.CENTER);
        activeOnlyCheckbox.getStyle().set("margin-right", "auto");

        add(row1, row2);
    }

    private void configureListeners() {
        searchButton.addClickListener(e -> applyFilters());
        clearButton.addClickListener(e -> clearFilters());
    }

    private void applyFilters() {
        if (filterListener != null) {
            FilterCriteria criteria = new FilterCriteria(
                    searchField.getValue(),
                    roleCombo.getValue(),
                    activeOnlyCheckbox.getValue()
            );
            filterListener.accept(criteria);
        }
    }

    private void clearFilters() {
        searchField.clear();
        roleCombo.clear();
        activeOnlyCheckbox.setValue(false);
        applyFilters();
    }

    public void setFilterListener(Consumer<FilterCriteria> listener) {
        this.filterListener = listener;
    }

    // Inner class for filter criteria
    public static class FilterCriteria {
        private final String searchText;
        private final Role role;
        private final Boolean activeOnly;

        public FilterCriteria(String searchText, Role role, Boolean activeOnly) {
            this.searchText = searchText;
            this.role = role;
            this.activeOnly = activeOnly;
        }

        public String getSearchText() { return searchText; }
        public Role getRole() { return role; }
        public Boolean getActiveOnly() { return activeOnly; }
    }
}