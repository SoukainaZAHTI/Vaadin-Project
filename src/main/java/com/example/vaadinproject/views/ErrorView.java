package com.example.vaadinproject.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import jakarta.servlet.http.HttpServletResponse;

@PageTitle("Page Not Found")
@AnonymousAllowed
public class ErrorView extends VerticalLayout implements HasErrorParameter<NotFoundException> {

    public ErrorView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        getStyle()
                .set("background", "#f5f5f5");

        H1 title = new H1("404 - Page Not Found");
        title.getStyle()
                .set("margin", "0")
                .set("color", "#A14C3A");

        Paragraph message = new Paragraph(
                "The page you're looking for doesn't exist."
        );
        message.getStyle()
                .set("font-size", "18px")
                .set("color", "#606770")
                .set("margin-top", "20px");

        Button homeButton = new Button("Go to Home");
        homeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        homeButton.getStyle().set("background", "#A14C3A");
        homeButton.addClickListener(e -> UI.getCurrent().navigate(""));

        add(title, message, homeButton);
    }

    @Override
    public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<NotFoundException> parameter) {
        return HttpServletResponse.SC_NOT_FOUND;
    }
}