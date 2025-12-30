package com.example.vaadinproject.components;

import com.example.vaadinproject.entities.Category;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

import java.time.LocalDate;
import java.util.function.Consumer;

public class SearchSection extends VerticalLayout {

    private TextField searchField;
    private ComboBox<Category> categoryFilter;
    private TextField cityFilter;
    private DatePicker dateFilter;
    private Button searchButton;

    // Listener for search action
    private Consumer<SearchCriteria> searchListener;

    public SearchSection() {
        setWidthFull();
        setPadding(true);
        setSpacing(true);
        setAlignItems(FlexComponent.Alignment.CENTER);
        getStyle()
                .set("background-color", "#D7C8A6")
                .set("padding", "30px")
                .set("margin-top", "0")
                .set("margin-bottom", "20px");;



        // Search input
        searchField = new TextField();
        searchField.setPlaceholder("Search by event name or description...");
        searchField.setWidth("400px");
        searchField.setClearButtonVisible(true);

        // Filters in a horizontal layout
        HorizontalLayout filters = new HorizontalLayout();
        filters.setSpacing(true);
        filters.setWidthFull();
        filters.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        categoryFilter = new ComboBox<>("Category");
        categoryFilter.setItems(Category.values());
        categoryFilter.setItemLabelGenerator(Category::name);
        categoryFilter.setPlaceholder("All categories");
        categoryFilter.setWidth("200px");

        cityFilter = new TextField("City");
        cityFilter.setPlaceholder("e.g., Casablanca");
        cityFilter.setWidth("200px");

        dateFilter = new DatePicker("Date");
        dateFilter.setPlaceholder("Select date");
        dateFilter.setWidth("200px");

        searchButton = new Button("Search");
        searchButton.getStyle().set("background", "#9B4B33")
                .set("color", "white");
        searchButton.getStyle().set("margin-top", "37px");
        searchButton.addClickShortcut(Key.ENTER);
        searchButton.addClickListener(e -> performSearch());

        Button clearButton = new Button("Clear");
        clearButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        clearButton.getStyle().set("margin-top", "37px");
        clearButton.addClickListener(e -> clearFilters());


        filters.add(categoryFilter, cityFilter, dateFilter, searchButton, clearButton);

        add( searchField, filters);
    }

    // Method to set search listener from parent view
    public void setSearchListener(Consumer<SearchCriteria> listener) {
        this.searchListener = listener;
    }

    private void performSearch() {
        if (searchListener != null) {
            SearchCriteria criteria = new SearchCriteria(
                    searchField.getValue(),
                    categoryFilter.getValue(),
                    cityFilter.getValue(),
                    dateFilter.getValue()
            );
            searchListener.accept(criteria);
        }
    }

    private void clearFilters() {
        searchField.clear();
        categoryFilter.clear();
        cityFilter.clear();
        dateFilter.clear();
        performSearch(); // Trigger search with empty filters
    }

    // Inner class to hold search criteria
    public static class SearchCriteria {
        private final String keyword;
        private final Category category;
        private final String city;
        private final LocalDate date;

        public SearchCriteria(String keyword, Category category, String city, LocalDate date) {
            this.keyword = keyword;
            this.category = category;
            this.city = city;
            this.date = date;
        }

        public String getKeyword() { return keyword; }
        public Category getCategory() { return category; }
        public String getCity() { return city; }
        public LocalDate getDate() { return date; }
    }
}