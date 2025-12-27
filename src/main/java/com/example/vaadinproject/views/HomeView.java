package com.example.vaadinproject.views;

import com.example.vaadinproject.entities.Category;
import com.example.vaadinproject.entities.Event;
import com.example.vaadinproject.entities.Status;
import com.example.vaadinproject.services.EventService;
import com.example.vaadinproject.services.SessionService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import java.time.LocalDate;
import java.util.List;

@Route(value = "", layout = MainLayout.class)  // Add layout parameter
@PageTitle("EventHub - Home")
@AnonymousAllowed
public class HomeView extends VerticalLayout {

    private final EventService eventService;
    private final SessionService sessionService;

    // Search and filter components
    private TextField searchField;
    private ComboBox<Category> categoryFilter;
    private DatePicker dateFilter;
    private TextField cityFilter;
    private Button searchButton;

    // Container for event cards
    private VerticalLayout eventsContainer;

    public HomeView(EventService eventService, SessionService sessionService) {
        this.eventService = eventService;
        this.sessionService = sessionService;

        // Remove default spacing and padding for custom layout
        setSizeFull();
        setPadding(false);
        setSpacing(false);

        // Build the page sections
        add(
                createHeroSection(),
                createSearchSection(),
                createEventsSection()
        );

        // Load initial events
        loadEvents(null, null, null, null);
    }

    private Component createHeroSection() {
        VerticalLayout hero = new VerticalLayout();
        hero.setWidthFull();
        hero.setPadding(true);
        hero.setSpacing(true);
        hero.setAlignItems(Alignment.CENTER);
        hero.getStyle()
                .set("background", "linear-gradient(135deg, #667eea 0%, #764ba2 100%)")
                .set("color", "white")
                .set("padding", "60px 20px")
                .set("text-align", "center");

        H1 title = new H1("Discover Amazing Events");
        title.getStyle().set("margin", "0").set("font-size", "3em");

        Paragraph subtitle = new Paragraph("Find and book the best events in your city");
        subtitle.getStyle().set("font-size", "1.2em").set("opacity", "0.9");

        hero.add(title, subtitle);

        return hero;
    }

    private Component createSearchSection() {
        VerticalLayout searchSection = new VerticalLayout();
        searchSection.setWidthFull();
        searchSection.setPadding(true);
        searchSection.setSpacing(true);
        searchSection.setAlignItems(Alignment.CENTER);
        searchSection.getStyle()
                .set("background-color", "#f5f5f5")
                .set("padding", "30px");

        H3 searchTitle = new H3("Search Events");
        searchTitle.getStyle().set("margin-top", "0");

        // Search input
        searchField = new TextField();
        searchField.setPlaceholder("Search by event name or description...");
        searchField.setWidth("400px");
        searchField.setClearButtonVisible(true);

        // Filters in a horizontal layout
        HorizontalLayout filters = new HorizontalLayout();
        filters.setSpacing(true);
        filters.setWidthFull();
        filters.setJustifyContentMode(JustifyContentMode.CENTER);

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
        searchButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        searchButton.getStyle().set("margin-top", "30px");
        searchButton.addClickListener(e -> performSearch());

        Button clearButton = new Button("Clear");
        clearButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        clearButton.getStyle().set("margin-top", "30px");
        clearButton.addClickListener(e -> clearFilters());

        filters.add(categoryFilter, cityFilter, dateFilter, searchButton, clearButton);

        searchSection.add(searchTitle, searchField, filters);

        return searchSection;
    }


    private Component createEventsSection() {
        VerticalLayout eventsSection = new VerticalLayout();
        eventsSection.setWidthFull();
        eventsSection.setPadding(true);
        eventsSection.setSpacing(true);
        eventsSection.setAlignItems(Alignment.CENTER);

        H3 eventsTitle = new H3("Featured Events");
        eventsTitle.getStyle().set("color", "#333");

        // Container for event cards (will be populated dynamically)
        eventsContainer = new VerticalLayout();
        eventsContainer.setWidthFull();
        eventsContainer.setSpacing(true);
        eventsContainer.setPadding(false);
        eventsContainer.getStyle()
                .set("max-width", "1200px")
                .set("margin", "0 auto");

        eventsSection.add(eventsTitle, eventsContainer);

        return eventsSection;
    }
    private Component createEventCard(Event event) {
        HorizontalLayout card = new HorizontalLayout();
        card.setWidthFull();
        card.setPadding(true);
        card.setSpacing(true);
        card.getStyle()
                .set("background-color", "white")
                .set("border-radius", "8px")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.1)")
                .set("margin-bottom", "15px")
                .set("cursor", "pointer")
                .set("transition", "transform 0.2s, box-shadow 0.2s");

        // Hover effect
        card.getElement().addEventListener("mouseenter", e -> {
            card.getStyle()
                    .set("transform", "translateY(-4px)")
                    .set("box-shadow", "0 4px 12px rgba(0,0,0,0.15)");
        });

        card.getElement().addEventListener("mouseleave", e -> {
            card.getStyle()
                    .set("transform", "translateY(0)")
                    .set("box-shadow", "0 2px 8px rgba(0,0,0,0.1)");
        });

        // Left side: Image placeholder
        VerticalLayout imageContainer = new VerticalLayout();
        imageContainer.setWidth("200px");
        imageContainer.setHeight("150px");
        imageContainer.setJustifyContentMode(JustifyContentMode.CENTER);
        imageContainer.setAlignItems(Alignment.CENTER);
        imageContainer.getStyle()
                .set("background-color", "#e0e0e0")
                .set("border-radius", "8px")
                .set("flex-shrink", "0");

        if (event.getImageUrl() != null && !event.getImageUrl().isEmpty()) {
            Image image = new Image(event.getImageUrl(), event.getTitre());
            image.setWidth("100%");
            image.setHeight("100%");
            image.getStyle().set("object-fit", "cover");
            imageContainer.add(image);
        } else {
            // Placeholder icon
            Span icon = new Span("🎭");
            icon.getStyle().set("font-size", "4em");
            imageContainer.add(icon);
        }

        // Right side: Event details
        VerticalLayout details = new VerticalLayout();
        details.setSpacing(false);
        details.setPadding(false);
        details.setWidthFull();

        H4 title = new H4(event.getTitre());
        title.getStyle()
                .set("margin", "0 0 10px 0")
                .set("color", "#1976d2");

        Paragraph description = new Paragraph(
                event.getDescription() != null && event.getDescription().length() > 150
                        ? event.getDescription().substring(0, 150) + "..."
                        : event.getDescription()
        );
        description.getStyle()
                .set("margin", "0 0 10px 0")
                .set("color", "#666")
                .set("font-size", "0.9em");

        HorizontalLayout info = new HorizontalLayout();
        info.setSpacing(true);

        Span category = new Span("📂 " + event.getCategorie());
        category.getStyle().set("color", "#555").set("font-size", "0.9em");

        Span date = new Span("📅 " + event.getDateDebut().toLocalDate());
        date.getStyle().set("color", "#555").set("font-size", "0.9em");

        Span location = new Span("📍 " + event.getVille());
        location.getStyle().set("color", "#555").set("font-size", "0.9em");

        info.add(category, date, location);

        HorizontalLayout footer = new HorizontalLayout();
        footer.setWidthFull();
        footer.setJustifyContentMode(JustifyContentMode.BETWEEN);
        footer.setAlignItems(Alignment.CENTER);

        H3 price = new H3(event.getPrixUnitaire() + " MAD");
        price.getStyle()
                .set("margin", "0")
                .set("color", "#4caf50")
                .set("font-weight", "bold");

        Button detailsBtn = new Button("View Details");
        detailsBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        detailsBtn.addClickListener(e -> showEventDetails(event));

        footer.add(price, detailsBtn);

        details.add(title, description, info, footer);

        card.add(imageContainer, details);
        card.setFlexGrow(1, details);

        return card;
    }
    private void performSearch() {
        String keyword = searchField.getValue();
        Category category = categoryFilter.getValue();
        String city = cityFilter.getValue();
        LocalDate date = dateFilter.getValue();

        loadEvents(keyword, category, city, date);
    }

    private void clearFilters() {
        searchField.clear();
        categoryFilter.clear();
        cityFilter.clear();
        dateFilter.clear();

        loadEvents(null, null, null, null);
    }

    private void loadEvents(String keyword, Category category, String city, LocalDate date) {
        eventsContainer.removeAll();

        // Get filtered events from service
        List<Event> events = eventService.searchPublicEvents(keyword, category, city, date);

        if (events.isEmpty()) {
            Paragraph noEvents = new Paragraph("No events found. Try adjusting your filters.");
            noEvents.getStyle()
                    .set("text-align", "center")
                    .set("color", "#999")
                    .set("padding", "40px");
            eventsContainer.add(noEvents);
        } else {
            // Create a card for each event
            events.forEach(event -> eventsContainer.add(createEventCard(event)));
        }
    }


    private void showEventDetails(Event event) {
        // Create a dialog to show full event details
        var dialog = new com.vaadin.flow.component.dialog.Dialog();
        dialog.setWidth("600px");

        VerticalLayout dialogContent = new VerticalLayout();
        dialogContent.setPadding(true);
        dialogContent.setSpacing(true);

        H2 title = new H2(event.getTitre());
        title.getStyle().set("margin-top", "0").set("color", "#1976d2");

        Paragraph description = new Paragraph(event.getDescription());
        description.getStyle().set("color", "#555");

        HorizontalLayout info = new HorizontalLayout();
        info.setWidthFull();

        VerticalLayout leftInfo = new VerticalLayout();
        leftInfo.setPadding(false);
        leftInfo.add(
                new Paragraph("📂 Category: " + event.getCategorie()),
                new Paragraph("📅 Start: " + event.getDateDebut()),
                new Paragraph("📅 End: " + event.getDateFin()),
                new Paragraph("📍 Location: " + event.getLieu() + ", " + event.getVille())
        );

        VerticalLayout rightInfo = new VerticalLayout();
        rightInfo.setPadding(false);
        rightInfo.add(
                new Paragraph("💰 Price: " + event.getPrixUnitaire() + " MAD"),
                new Paragraph("👥 Capacity: " + event.getCapaciteMax()),
                new Paragraph("✅ Available: " + event.getPlacesDisponibles()),
                new Paragraph("📊 Status: " + event.getStatut())
        );

        info.add(leftInfo, rightInfo);

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setWidthFull();
        buttons.setJustifyContentMode(JustifyContentMode.END);

        if (sessionService.isLoggedIn() && sessionService.isClient()) {
            Button bookButton = new Button("Book Now");
            bookButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
            bookButton.addClickListener(e -> {
                dialog.close();
                // TODO: Navigate to booking page
                com.vaadin.flow.component.notification.Notification.show(
                        "Booking feature coming soon!",
                        3000,
                        com.vaadin.flow.component.notification.Notification.Position.MIDDLE
                );
            });
            buttons.add(bookButton);
        } else if (!sessionService.isLoggedIn()) {
            Paragraph loginMessage = new Paragraph("Please login to book this event");
            loginMessage.getStyle().set("color", "#ff9800").set("font-weight", "bold");

            Button loginButton = new Button("Login");
            loginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            loginButton.addClickListener(e -> {
                dialog.close();
                UI.getCurrent().navigate("login");
            });

            buttons.add(loginMessage, loginButton);
        }

        Button closeButton = new Button("Close");
        closeButton.addClickListener(e -> dialog.close());
        buttons.add(closeButton);

        dialogContent.add(title, description, info, buttons);
        dialog.add(dialogContent);
        dialog.open();
    }







}