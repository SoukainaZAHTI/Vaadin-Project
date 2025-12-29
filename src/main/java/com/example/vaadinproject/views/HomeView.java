package com.example.vaadinproject.views;

import com.example.vaadinproject.components.*;
import com.example.vaadinproject.entities.Event;
import com.example.vaadinproject.services.EventService;
import com.example.vaadinproject.services.NavigationManager;
import com.example.vaadinproject.services.SessionService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import java.util.List;

@Route(value = "", layout = MainLayout.class)
@PageTitle("EventHub - Home")
@AnonymousAllowed
public class HomeView extends VerticalLayout {

    private final EventService eventService;
    private final SessionService sessionService;
    private final NavigationManager navigationManager;

    private SearchSection searchSection;
    private EventsSection eventsSection;

    public HomeView(EventService eventService, SessionService sessionService,
                    NavigationManager navigationManager) {
        this.eventService = eventService;
        this.sessionService = sessionService;
        this.navigationManager = navigationManager;


        setSizeFull();
        setPadding(false);
        setSpacing(false);

        // Initialize components
        HeroSection heroSection = new HeroSection();

        searchSection = new SearchSection();
        searchSection.setSearchListener(this::handleSearch);

        eventsSection = new EventsSection();
        eventsSection.setEventClickListener(this::showEventDetails);
        // Add this after eventsSection initialization, before add() method


        Button discoverButton = new Button("Discover All Events ↠ ");
        discoverButton.getStyle()
                .set("background-color", "white")
                .set("color", "#9C4B36")
                .set("font-size", "1.2em")
                .set("padding", "15px 40px")
                .set("border-radius", "8px")
                .set("cursor", "pointer")
                .set("border", "none");
        discoverButton.addClickListener(e ->
                navigationManager.navigateToAllEvents()
        );

// Wrap button in a centered container
        VerticalLayout buttonContainer = new VerticalLayout(discoverButton);
        buttonContainer.setWidthFull();
        buttonContainer.setAlignItems(Alignment.CENTER);
        buttonContainer.getStyle().set("margin", "30px 0");
// Then modify your add() to include the button:
        add(heroSection, searchSection, eventsSection, buttonContainer);

        // Load initial events
        loadEvents(null, null, null, null);
    }

    private void handleSearch(SearchSection.SearchCriteria criteria) {
        loadEvents(
                criteria.getKeyword(),
                criteria.getCategory(),
                criteria.getCity(),
                criteria.getDate()
        );
    }

    private void loadEvents(String keyword,
                            com.example.vaadinproject.entities.Category category,
                            String city,
                            java.time.LocalDate date) {
        List<Event> events = eventService.searchPublicEvents(keyword, category, city, date);

// If no search filters applied, show only one event per category (featured)
        if (keyword == null && category == null && city == null && date == null) {
            events = getFeaturedEvents(events);
        }

        eventsSection.setEvents(events);
    }


    private List<Event> getFeaturedEvents(List<Event> allEvents) {
        return allEvents.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        Event::getCategorie,
                        java.util.stream.Collectors.collectingAndThen(
                                java.util.stream.Collectors.toList(),
                                list -> list.isEmpty() ? null : list.get(0)
                        )
                ))
                .values()
                .stream()
                .filter(event -> event != null)
                .collect(java.util.stream.Collectors.toList());
    }

    private void showEventDetails(Event event) {
        EventDetailView dialog = new EventDetailView(event, sessionService);
        dialog.open();
    }
}