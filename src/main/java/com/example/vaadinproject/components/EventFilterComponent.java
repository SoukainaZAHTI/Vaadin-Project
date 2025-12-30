package com.example.vaadinproject.components;

import com.example.vaadinproject.entities.Category;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;

import java.time.LocalDate;
import java.util.function.Consumer;

public class EventFilterComponent extends VerticalLayout {

    private TextField keywordField;
    private ComboBox<Category> categoryCombo;
    private TextField cityField;
    private DatePicker startDatePicker;
    private DatePicker endDatePicker;
    private NumberField minPriceField;
    private NumberField maxPriceField;
    private Button searchButton;
    private Button clearButton;

    private Consumer<FilterCriteria> filterListener;

    public EventFilterComponent() {
        setSpacing(true);
        setPadding(false);
        setWidthFull();

        initializeComponents();
        layoutComponents();
        configureListeners();
    }

    private void initializeComponents() {
        keywordField = new TextField("Keyword");
        keywordField.setPlaceholder("Search by title or description...");
        keywordField.setClearButtonVisible(true);

        categoryCombo = new ComboBox<>("Category");
        categoryCombo.setItems(Category.values());
        categoryCombo.setItemLabelGenerator(Category::name);
        categoryCombo.setClearButtonVisible(true);

        cityField = new TextField("City");
        cityField.setPlaceholder("Filter by city...");
        cityField.setClearButtonVisible(true);

        startDatePicker = new DatePicker("Start Date");
        startDatePicker.setClearButtonVisible(true);

        endDatePicker = new DatePicker("End Date");
        endDatePicker.setClearButtonVisible(true);

        minPriceField = new NumberField("Min Price");
        minPriceField.setPlaceholder("0");
        minPriceField.setMin(0);

        maxPriceField = new NumberField("Max Price");
        maxPriceField.setPlaceholder("1000");
        maxPriceField.setMin(0);

        searchButton = new Button("Search");
        searchButton.getStyle().set("margin-top", "auto");

        clearButton = new Button("Clear");
        clearButton.getStyle().set("margin-top", "auto");
    }

    private void layoutComponents() {
        // First row: Keyword and Category
        HorizontalLayout row1 = new HorizontalLayout(keywordField, categoryCombo);
        row1.setWidthFull();
        keywordField.setWidth("50%");
        categoryCombo.setWidth("50%");

        // Second row: City and Date Range
        HorizontalLayout row2 = new HorizontalLayout(cityField, startDatePicker, endDatePicker);
        row2.setWidthFull();
        cityField.setWidth("34%");
        startDatePicker.setWidth("33%");
        endDatePicker.setWidth("33%");

        // Third row: Price Range
        HorizontalLayout row3 = new HorizontalLayout(minPriceField, maxPriceField);
        row3.setWidthFull();
        minPriceField.setWidth("50%");
        maxPriceField.setWidth("50%");

        // Fourth row: Buttons
        HorizontalLayout buttonRow = new HorizontalLayout(searchButton, clearButton);
        buttonRow.setSpacing(true);

        add(row1, row2, row3, buttonRow);
    }

    private void configureListeners() {
        searchButton.addClickListener(e -> applyFilters());
        clearButton.addClickListener(e -> clearFilters());
    }

    private void applyFilters() {
        if (filterListener != null) {
            FilterCriteria criteria = new FilterCriteria(
                    keywordField.getValue(),
                    categoryCombo.getValue(),
                    cityField.getValue(),
                    startDatePicker.getValue(),
                    endDatePicker.getValue(),
                    minPriceField.getValue(),
                    maxPriceField.getValue()
            );
            filterListener.accept(criteria);
        }
    }

    private void clearFilters() {
        keywordField.clear();
        categoryCombo.clear();
        cityField.clear();
        startDatePicker.clear();
        endDatePicker.clear();
        minPriceField.clear();
        maxPriceField.clear();
        applyFilters();
    }

    public void setFilterListener(Consumer<FilterCriteria> listener) {
        this.filterListener = listener;
    }

    // Inner class for filter criteria
    public static class FilterCriteria {
        private final String keyword;
        private final Category category;
        private final String city;
        private final LocalDate startDate;
        private final LocalDate endDate;
        private final Double minPrice;
        private final Double maxPrice;

        public FilterCriteria(String keyword, Category category, String city,
                              LocalDate startDate, LocalDate endDate,
                              Double minPrice, Double maxPrice) {
            this.keyword = keyword;
            this.category = category;
            this.city = city;
            this.startDate = startDate;
            this.endDate = endDate;
            this.minPrice = minPrice;
            this.maxPrice = maxPrice;
        }

        public String getKeyword() { return keyword; }
        public Category getCategory() { return category; }
        public String getCity() { return city; }
        public LocalDate getStartDate() { return startDate; }
        public LocalDate getEndDate() { return endDate; }
        public Double getMinPrice() { return minPrice; }
        public Double getMaxPrice() { return maxPrice; }
    }
}