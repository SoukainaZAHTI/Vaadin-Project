package com.example.vaadinproject.views;


import com.example.vaadinproject.services.SessionService;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("organizer")
@PageTitle("OrganizerDashboard")
public class OrganizerDashboardView extends VerticalLayout implements BeforeEnterObserver {

    private final SessionService sessionService;

    public OrganizerDashboardView(SessionService sessionService) {
        this.sessionService = sessionService;

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        add(
                new H1("Organizer Dashboard"),
                new Paragraph("Welcome, " + sessionService.getCurrentUser().getNomComplet() + "!"),
                new Paragraph("Manage your events here.")
        );
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Check if user is logged in and has organizer role
        if (!sessionService.isLoggedIn() || !sessionService.isOrganizer()) {
            event.rerouteTo("login");
        }
    }
}