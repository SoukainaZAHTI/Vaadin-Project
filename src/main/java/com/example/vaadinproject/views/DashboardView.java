package com.example.vaadinproject.views;

import com.example.vaadinproject.entities.User;
import com.example.vaadinproject.services.SessionService;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.component.UI;

@Route(value = "client/dashboard", layout = MainLayout.class)
public class DashboardView extends VerticalLayout implements BeforeEnterObserver {

    private final SessionService sessionService;

    public DashboardView(SessionService sessionService) {
        this.sessionService = sessionService;

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        add(
                new H1("Client Dashboard"),
                new Paragraph("Welcome, " + sessionService.getCurrentUser().getNomComplet() + "!"),
                new Paragraph("Browse and book events.")
        );
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (!sessionService.isLoggedIn()) {
            event.rerouteTo("login");
            return;
        }

        User currentUser = sessionService.getCurrentUser();

        if (!currentUser.isClient()) {
            event.rerouteTo("unauthorized");
            return;
        }
    }
}