package com.example.vaadinproject.views;


import com.example.vaadinproject.services.SessionService;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "admin", layout = MainLayout.class)
@PageTitle("AdminDashboard")
public class AdminDashboardView extends VerticalLayout implements BeforeEnterObserver {

    private final SessionService sessionService;

    public AdminDashboardView(SessionService sessionService) {
        this.sessionService = sessionService;

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        add(
                new H1("Admin Dashboard"),
                new Paragraph("Welcome, " + sessionService.getCurrentUser().getNomComplet() + "!"),
                new Paragraph("You have admin access to all features.")
        );
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Check if user is logged in and has admin role
        if (!sessionService.isLoggedIn() || !sessionService.isAdmin()) {
            event.rerouteTo("login");
        }
    }
}