package com.example.vaadinproject.components;

import com.example.vaadinproject.entities.Category;
import com.example.vaadinproject.entities.Event;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.function.Consumer;

public class EventCard extends HorizontalLayout {

    private final Event event;
    private Consumer<Event> detailsClickListener;

    public EventCard(Event event) {
        this.event = event;

        setWidthFull();
        setPadding(true);
        setSpacing(true);
        getStyle()
                .set("background-color", "white")
                .set("border-radius", "8px")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.1)")
                .set("margin-bottom", "15px")
                .set("cursor", "pointer")
                .set("transition", "transform 0.2s, box-shadow 0.2s");

        // Hover effect
        getElement().addEventListener("mouseenter", e -> {
            getStyle()
                    .set("transform", "translateY(-4px)")
                    .set("box-shadow", "0 4px 12px rgba(0,0,0,0.15)");
        });

        getElement().addEventListener("mouseleave", e -> {
            getStyle()
                    .set("transform", "translateY(0)")
                    .set("box-shadow", "0 2px 8px rgba(0,0,0,0.1)");
        });

        add(createImageContainer(), createDetailsContainer());
        setFlexGrow(1, getComponentAt(1));
    }

    public void setDetailsClickListener(Consumer<Event> listener) {
        this.detailsClickListener = listener;
    }

    private VerticalLayout createImageContainer() {
        VerticalLayout imageContainer = new VerticalLayout();
        imageContainer.setWidth("200px");
        imageContainer.setHeight("200px"); // Was 150px
        imageContainer.setJustifyContentMode(JustifyContentMode.CENTER);
        imageContainer.setAlignItems(Alignment.CENTER);
        imageContainer.getStyle()
                .set("background-color", "#e0e0e0")
                .set("border-radius", "8px")
                .set("flex-shrink", "0");

        String imageUrl = event.getImageUrl();

// If no image URL, use category-based default image
        if (imageUrl == null || imageUrl.isEmpty()) {
            imageUrl = getCategoryImage(event.getCategorie());
        }

        Image image = new Image(imageUrl, event.getTitre());
        image.setWidth("100%");
        image.setHeight("100%");
        image.getStyle().set("object-fit", "cover");
        imageContainer.add(image);

        return imageContainer;
    }

    private String getCategoryImage(com.example.vaadinproject.entities.Category category) {
        if (category == null) {
            return "https://images.unsplash.com/photo-1492684223066-81342ee5ff30?w=400";
        }

        switch (category) {
            case CONCERT:
                return "https://images.unsplash.com/photo-1470229722913-7c0e2dbbafd3?w=400";
            case THEATRE:
                return "https://images.unsplash.com/photo-1503095396549-807759245b35?w=400";
            case CONFERENCE:
                return "https://images.unsplash.com/photo-1540575467063-178a50c2df87?w=400";
            case SPORT:
                return "https://images.unsplash.com/photo-1461896836934-ffe607ba8211?w=400";
            case AUTRE:
                return "https://images.unsplash.com/photo-1492684223066-81342ee5ff30?w=400";
            default:
                return "https://images.unsplash.com/photo-1492684223066-81342ee5ff30?w=400";
        }
    }

    private VerticalLayout createDetailsContainer() {
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

        HorizontalLayout info = createInfoLayout();
        HorizontalLayout footer = createFooter();

        details.add(title, description, info, footer);

        return details;
    }

    private HorizontalLayout createInfoLayout() {
        HorizontalLayout info = new HorizontalLayout();
        info.setSpacing(true);

        Span category = new Span("📂 " + event.getCategorie());
        category.getStyle().set("color", "#555").set("font-size", "0.9em");

        Span date = new Span("📅 " + event.getDateDebut().toLocalDate());
        date.getStyle().set("color", "#555").set("font-size", "0.9em");

        Span location = new Span("📍 " + event.getVille());
        location.getStyle().set("color", "#555").set("font-size", "0.9em");

        info.add(category, date, location);

        return info;
    }

    private HorizontalLayout createFooter() {
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
        detailsBtn.getStyle().set("color", "white")
                .set("background-color", "#5E6E28");
        detailsBtn.addClickListener(e -> {
            if (detailsClickListener != null) {
                detailsClickListener.accept(event);
            }
        });

        footer.add(price, detailsBtn);

        return footer;
    }
}