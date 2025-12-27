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
import com.vaadin.flow.component.icon.Icon;
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
        Image logo = new Image("https://github.com/SoukainaZAHTI/Resources/blob/main/logo.png?raw=true", "EventHub Logo");
        logo.setHeight("20%"); // Reduced height for better header proportions
        logo.getStyle()
                .set("margin", "0")
                .set("cursor", "pointer")
                .set("object-fit", "contain") // Keep aspect ratio
                .set("max-width", "20%"); // Limit maximum width

        logo.addClickListener(e -> UI.getCurrent().navigate("/"));

        // User info or login/register buttons
        HorizontalLayout headerRight = new HorizontalLayout();
        headerRight.setSpacing(true);
        headerRight.setAlignItems(FlexComponent.Alignment.CENTER);
        headerRight.getStyle().set("flex-shrink", "0"); // Prevent buttons from shrinking

        if (sessionService.isLoggedIn()) {
//            Span userName = new Span("👤 " + sessionService.getCurrentUser().getNomComplet());
//            userName.getStyle().set("color", "white");
            Button profileButton = new Button("My Profile", new Icon(VaadinIcon.USER));
            profileButton.addClickListener(e -> UI.getCurrent().navigate("profile"));
            Button logoutBtn = new Button("Logout", VaadinIcon.SIGN_OUT.create());
            logoutBtn.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
            logoutBtn.addClickListener(e -> {
                sessionService.logout();
                UI.getCurrent().getPage().setLocation("/login"); // Use setLocation for full page reload
            });

            headerRight.add(profileButton, logoutBtn);
        } else {
            Button loginBtn = new Button("Login", VaadinIcon.SIGN_IN.create());
            loginBtn.getStyle().set("color", "white")
                    .set("background-color", "#5E6E28");
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
                .set("flex-shrink", "0")
                .set("font-size", "32px")
                .set("cursor", "pointer");

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
               // .set("background-image", "linear-gradient(90deg, #000000, #737373)")
                .set("background-color", "#D8C9A7")
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