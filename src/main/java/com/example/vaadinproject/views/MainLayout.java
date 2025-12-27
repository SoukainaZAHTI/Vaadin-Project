package com.example.vaadinproject.views;

import com.example.vaadinproject.services.SessionService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;

public class MainLayout extends AppLayout {

    private final SessionService sessionService;

    public MainLayout(SessionService sessionService) {
        this.sessionService = sessionService;

        createHeader();
        createDrawer();
    }

    private void createHeader() {
        Image logo = new Image("https://private-user-images.githubusercontent.com/214033788/530433296-10326aaa-7231-4cb6-b43e-8e0ae2f58e4a.png?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NjY3OTMxMDAsIm5iZiI6MTc2Njc5MjgwMCwicGF0aCI6Ii8yMTQwMzM3ODgvNTMwNDMzMjk2LTEwMzI2YWFhLTcyMzEtNGNiNi1iNDNlLThlMGFlMmY1OGU0YS5wbmc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjUxMjI2JTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI1MTIyNlQyMzQ2NDBaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT0yODRiODA0ZDhjMzcwNWUwNTZlNDMxMWE0NWEwYTFkZjczZjliZDQyYjJkMzYzNDVmM2MxZTFiYjVhMjI3MzdlJlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCJ9.HlfezVVFRWDIfF5z1zH_id0rqzVjrRawqGXCyJdZdkw", "EventHub Logo");
        logo.setHeight("20%"); // Reduced height for better header proportions
        logo.getStyle()
                .set("margin", "0")
                .set("cursor", "pointer")
                .set("object-fit", "contain") // Keep aspect ratio
                .set("max-width", "20%"); // Limit maximum width

        logo.addClickListener(e -> UI.getCurrent().navigate("home"));

        // User info or login/register buttons
        HorizontalLayout headerRight = new HorizontalLayout();
        headerRight.setSpacing(true);
        headerRight.setAlignItems(FlexComponent.Alignment.CENTER);
        headerRight.getStyle().set("flex-shrink", "0"); // Prevent buttons from shrinking

        if (sessionService.isLoggedIn()) {
            Span userName = new Span("👤 " + sessionService.getCurrentUser().getNomComplet());
            userName.getStyle().set("color", "white");

            Button logoutBtn = new Button("Logout", VaadinIcon.SIGN_OUT.create());
            logoutBtn.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
            logoutBtn.addClickListener(e -> {
                sessionService.logout();
                UI.getCurrent().navigate("home");
                UI.getCurrent().getPage().reload();
            });

            headerRight.add(userName, logoutBtn);
        } else {
            Button loginBtn = new Button("Login", VaadinIcon.SIGN_IN.create());
            loginBtn.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
            loginBtn.addClickListener(e -> UI.getCurrent().navigate("login"));

            Button registerBtn = new Button("Register", VaadinIcon.USER.create());
            registerBtn.getStyle().set("color", "white")
                    .set("background-color", "#9C4C36");
            registerBtn.addClickListener(e -> UI.getCurrent().navigate("register"));

            headerRight.add(loginBtn, registerBtn);
        }

        // Hamburger toggle
        DrawerToggle toggle = new DrawerToggle();
        toggle.getStyle()
                .set("color", "white")
                .set("flex-shrink", "0");

        // Create a spacer to push headerRight to the right
        Div spacer = new Div();
        spacer.getStyle().set("flex-grow", "1");

        HorizontalLayout header = new HorizontalLayout(toggle, logo, spacer, headerRight);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidthFull();
        header.setSpacing(true);
        header.setPadding(false);
        header.addClassName("header");
        header.getStyle()
                .set("background-image", "linear-gradient(90deg, #000000, #737373)")
                .set("padding", "10px 20px")
                .set("gap", "15px");

        addToNavbar(header);
    }
    private void createDrawer() {
        VerticalLayout drawerContent = new VerticalLayout();
        drawerContent.setPadding(true);
        drawerContent.setSpacing(true);

        // Common menu items for everyone
        RouterLink homeLink = new RouterLink("🏠 Home", HomeView.class);
        drawerContent.add(homeLink);

        // Role-based menu items
        if (sessionService.isLoggedIn()) {
            if (sessionService.isAdmin()) {
                RouterLink dashboardLink = new RouterLink("📊 Admin Dashboard", AdminDashboardView.class);
                RouterLink usersLink = new RouterLink("👥 Manage Users", UserListView.class);
                RouterLink allEventsLink = new RouterLink("📅 All Events", EventListView.class);
                drawerContent.add(dashboardLink, usersLink, allEventsLink);
            } else if (sessionService.isOrganizer()) {
                RouterLink dashboardLink = new RouterLink("📊 Dashboard", OrganizerDashboardView.class);
                RouterLink myEventsLink = new RouterLink("📅 My Events", EventListView.class);
                drawerContent.add(dashboardLink, myEventsLink);
            } else if (sessionService.isClient()) {
                RouterLink dashboardLink = new RouterLink("📊 Dashboard", DashboardView.class);
                RouterLink myBookingsLink = new RouterLink("🎫 My Bookings", HomeView.class); // TODO: Create BookingsView
                drawerContent.add(dashboardLink, myBookingsLink);
            }
        }

        addToDrawer(drawerContent);
    }
}