package com.example.vaadinproject.components;

import com.example.vaadinproject.entities.Event;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.List;
import java.util.function.Consumer;

public class EventsSection extends VerticalLayout {

    private final VerticalLayout eventsContainer;
    private Consumer<Event> eventClickListener;

    public EventsSection() {
        setWidthFull();
        setPadding(true);
        setSpacing(true);
        setAlignItems(Alignment.CENTER);

        H3 eventsTitle = new H3("Featured Events");
        eventsTitle.getStyle().set("color", "#333");

        eventsContainer = new VerticalLayout();
        eventsContainer.setWidthFull();
        eventsContainer.setSpacing(true);
        eventsContainer.setPadding(false);
        eventsContainer.getStyle()
                .set("max-width", "1200px")
                .set("margin", "0 auto");
        add(eventsTitle, eventsContainer);
    }

    public void setEventClickListener(Consumer<Event> listener) {
        this.eventClickListener = listener;
    }

    public void setEvents(List<Event> events) {
        eventsContainer.removeAll();

        if (events == null || events.isEmpty()) {
            Paragraph noEvents = new Paragraph("No events found. Try adjusting your filters.");
            noEvents.getStyle()
                    .set("text-align", "center")
                    .set("color", "#999")
                    .set("padding", "40px");
            eventsContainer.add(noEvents);
        } else {
            events.forEach(event -> {
                EventCard card = new EventCard(event);
                card.setDetailsClickListener(eventClickListener);
                eventsContainer.add(card);
            });
        }
    }
}