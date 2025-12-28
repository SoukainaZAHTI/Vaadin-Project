package com.example.vaadinproject.components;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class HeroSection extends VerticalLayout {

    private int currentSlide = 0;
    private final Slide[] slides;
    private final Div slideContainer;
    private final Div indicatorsContainer;

    public HeroSection() {
        // Define your slides
        this.slides = new Slide[]{
                new Slide(
                        "Discover Amazing Events",
                        "Find and book the best events in your city",
                        "url('https://raw.githubusercontent.com/SoukainaZAHTI/Resources/main/pexels-wolfgang-1002140-2747449.jpg')"
                ),
                new Slide(
                        "Connect With Your Community",
                        "Join thousands of event-goers in exciting experiences",
                        "url('https://raw.githubusercontent.com/SoukainaZAHTI/Resources/main/pexels-pixabay-269140.jpg')"
                ),
                new Slide(
                        "Create Unforgettable Memories",
                        "From concerts to workshops, discover what moves you",
                        "url('https://raw.githubusercontent.com/SoukainaZAHTI/Resources/main/pexels-ugur-165173.jpg')"
                )
        };

        setWidthFull();
        setHeight("500px"); // Important: Set explicit height
        setPadding(false);
        setSpacing(false);
        getStyle()
                .set("position", "relative")
                .set("overflow", "hidden")
                .set("flex-shrink", "0"); // Prevent collapsing

        // Create slide container
        slideContainer = new Div();
        slideContainer.getStyle()
                .set("position", "relative")
                .set("width", "100%")
                .set("height", "100%");

        // Add all slides
        for (int i = 0; i < slides.length; i++) {
            slideContainer.add(createSlideElement(slides[i], i));
        }

        // Create navigation arrows
        Div leftArrow = createArrow("❮", true);
        Div rightArrow = createArrow("❯", false);

        // Create indicators (dots)
        indicatorsContainer = createIndicators();

        add(slideContainer, leftArrow, rightArrow, indicatorsContainer);

        System.out.println("HeroSection created successfully"); // Debug
    }

    private Div createSlideElement(Slide slide, int index) {
        Div slideDiv = new Div();
        slideDiv.getStyle()
                .set("position", "absolute")
                .set("top", "0")
                .set("left", "0")
                .set("width", "100%")
                .set("height", "100%")
                .set("background-image", slide.backgroundImage)
                .set("background-size", "cover")
                .set("background-position", "center center")  // Changed: center both axes
                .set("background-repeat", "no-repeat")        // Added: prevent repeat
                .set("display", index == 0 ? "flex" : "none")
                .set("flex-direction", "column")
                .set("justify-content", "center")
                .set("align-items", "center")
                .set("color", "white")
                .set("text-align", "center")
                .set("padding", "60px 20px")
                .set("opacity", index == 0 ? "1" : "0")
                .set("transition", "opacity 0.5s ease-in-out");

        // Add dark overlay for better text readability
        Div overlay = new Div();
        overlay.getStyle()
                .set("position", "absolute")
                .set("top", "0")
                .set("left", "0")
                .set("width", "100%")
                .set("height", "100%")
                .set("background", "rgba(0, 0, 0, 0.4)")  // 40% dark overlay
                .set("z-index", "0");

        slideDiv.add(overlay);

        H1 title = new H1(slide.title);
        title.getStyle()
                .set("margin", "0")
                .set("font-size", "3em")
                .set("color", "white")
                .set("text-shadow", "2px 2px 4px rgba(0,0,0,0.7)")  // Stronger shadow
                .set("z-index", "1")
                .set("position", "relative");  // Added

        Paragraph subtitle = new Paragraph(slide.subtitle);
        subtitle.getStyle()
                .set("font-size", "1.3em")
                .set("opacity", "0.95")
                .set("max-width", "600px")
                .set("margin-top", "20px")
                .set("text-shadow", "1px 1px 3px rgba(0,0,0,0.7)")  // Stronger shadow
                .set("z-index", "1")
                .set("position", "relative");  // Added

        slideDiv.add(title, subtitle);
        return slideDiv;
    }
    private Div createArrow(String symbol, boolean isLeft) {
        Div arrow = new Div();
        Span arrowSpan = new Span(symbol);
        arrow.add(arrowSpan);

        arrow.getStyle()
                .set("position", "absolute")
                .set("top", "50%")
                .set(isLeft ? "left" : "right", "30px")
                .set("transform", "translateY(-50%)")
                .set("font-size", "3em")
                .set("color", "white")
                .set("cursor", "pointer")
                .set("user-select", "none")
                .set("z-index", "10")
                .set("padding", "10px 20px")
                .set("background", "rgba(0,0,0,0.3)")
                .set("border-radius", "5px")
                .set("transition", "all 0.3s ease");

        arrow.getElement().addEventListener("mouseenter", e -> {
            arrow.getStyle().set("background", "rgba(0,0,0,0.6)");
        });

        arrow.getElement().addEventListener("mouseleave", e -> {
            arrow.getStyle().set("background", "rgba(0,0,0,0.3)");
        });

        arrow.getElement().addEventListener("click", e -> {
            if (isLeft) {
                previousSlide();
            } else {
                nextSlide();
            }
        });

        return arrow;
    }

    private Div createIndicators() {
        Div container = new Div();
        container.getStyle()
                .set("position", "absolute")
                .set("bottom", "20px")
                .set("left", "50%")
                .set("transform", "translateX(-50%)")
                .set("display", "flex")
                .set("gap", "10px")
                .set("z-index", "10");

        for (int i = 0; i < slides.length; i++) {
            final int index = i;
            Div dot = new Div();
            dot.getStyle()
                    .set("width", "12px")
                    .set("height", "12px")
                    .set("border-radius", "50%")
                    .set("background", i == 0 ? "white" : "rgba(255,255,255,0.5)")
                    .set("cursor", "pointer")
                    .set("transition", "all 0.3s ease");

            dot.getElement().addEventListener("click", e -> goToSlide(index));

            dot.getElement().addEventListener("mouseenter", e -> {
                if (index != currentSlide) {
                    dot.getStyle().set("background", "rgba(255,255,255,0.8)");
                }
            });

            dot.getElement().addEventListener("mouseleave", e -> {
                if (index != currentSlide) {
                    dot.getStyle().set("background", "rgba(255,255,255,0.5)");
                }
            });

            container.add(dot);
        }

        return container;
    }

    private void nextSlide() {
        currentSlide = (currentSlide + 1) % slides.length;
        updateSlide();
    }

    private void previousSlide() {
        currentSlide = (currentSlide - 1 + slides.length) % slides.length;
        updateSlide();
    }

    private void goToSlide(int index) {
        currentSlide = index;
        updateSlide();
    }

    private void updateSlide() {
        // Hide all slides
        for (int i = 0; i < slides.length; i++) {
            Div slide = (Div) slideContainer.getComponentAt(i);
            if (i == currentSlide) {
                slide.getStyle()
                        .set("display", "flex")
                        .set("opacity", "1");
            } else {
                slide.getStyle()
                        .set("display", "none")
                        .set("opacity", "0");
            }
        }

        // Update indicators
        for (int i = 0; i < slides.length; i++) {
            Div indicator = (Div) indicatorsContainer.getComponentAt(i);
            if (i == currentSlide) {
                indicator.getStyle().set("background", "white");
            } else {
                indicator.getStyle().set("background", "rgba(255,255,255,0.5)");
            }
        }
    }

    // Inner class to hold slide data
    private static class Slide {
        String title;
        String subtitle;
        String backgroundImage;

        Slide(String title, String subtitle, String backgroundImage) {
            this.title = title;
            this.subtitle = subtitle;
            this.backgroundImage = backgroundImage;
        }
    }
}