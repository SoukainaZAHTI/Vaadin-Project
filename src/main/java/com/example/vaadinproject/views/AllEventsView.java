package com.example.vaadinproject.views;

import com.example.vaadinproject.components.EventCard;
import com.example.vaadinproject.components.SearchSection;
import com.example.vaadinproject.entities.Event;
import com.example.vaadinproject.services.EventService;
import com.example.vaadinproject.services.SessionService;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import java.util.List;

@Route(value = "all-events", layout = MainLayout.class)
@PageTitle("All Events - EventHub")
@AnonymousAllowed
public class AllEventsView extends VerticalLayout {

    private final EventService eventService;
    private final SessionService sessionService;
    private VerticalLayout cardsContainer;
    private SearchSection searchSection;

    public AllEventsView(EventService eventService, SessionService sessionService) {
        this.eventService = eventService;
        this.sessionService = sessionService;
        searchSection = new SearchSection();
        searchSection.setSearchListener(this::handleSearch); // Add this line



        setSizeFull();
        setPadding(true);
        setSpacing(true);
        setAlignItems(Alignment.CENTER);

        H2 title = new H2("Discover All Events");
        title.getStyle().set("color", "#333").set("margin-bottom", "30px");

        cardsContainer = new VerticalLayout();
        cardsContainer.setWidthFull();
        cardsContainer.setMaxWidth("1400px");
        cardsContainer.setPadding(false);
        cardsContainer.setSpacing(true);

        add(title, searchSection, cardsContainer);

        loadEvents(null, null, null, null); // Load all events initially
        }

    private void loadEvents(String keyword,
                            com.example.vaadinproject.entities.Category category,
                            String city,
                            java.time.LocalDate date) {
        cardsContainer.removeAll(); // Clear existing cards

        List<Event> events = eventService.searchPublicEvents(keyword, category, city, date);

        // Create rows of 3 cards each
        HorizontalLayout currentRow = null;
        int cardCount = 0;

        for (Event event : events) {
            if (cardCount % 3 == 0) {
                currentRow = new HorizontalLayout();
                currentRow.setWidthFull();
                currentRow.setSpacing(true);
                currentRow.getStyle().set("margin-bottom", "20px");
                cardsContainer.add(currentRow);
            }

            EventCard card = new EventCard(event);
            card.setDetailsClickListener(this::showEventDetails);
            card.setWidth("calc(33.33% - 14px)");

            currentRow.add(card);
            cardCount++;
        }
    }
    private void showEventDetails(Event event) {
        EventDetailView dialog = new EventDetailView(event, sessionService);
        dialog.open();
    }
    private void handleSearch(SearchSection.SearchCriteria criteria) {
        loadEvents(
                criteria.getKeyword(),
                criteria.getCategory(),
                criteria.getCity(),
                criteria.getDate()
        );
    }
}