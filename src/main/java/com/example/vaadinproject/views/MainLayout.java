package com.example.vaadinproject.views;

import com.example.vaadinproject.components.Breadcrumb;
import com.example.vaadinproject.services.NavigationManager;
import com.example.vaadinproject.services.SessionService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.server.VaadinSession;

public class MainLayout extends AppLayout {

    private final SessionService sessionService;
    private final NavigationManager navigationManager;
    private Breadcrumb breadcrumb;

    public MainLayout(SessionService sessionService, NavigationManager navigationManager) {
        this.sessionService = sessionService;
        this.navigationManager = navigationManager;


        createHeader();
        createDrawer();
        setDrawerOpened(false);
        createBreadcrumb();

    }

    @Override
    public void setContent(Component content) {
        // Content wrapper
        Div contentArea = new Div(content);
        contentArea.getStyle()
                .set("flex", "0 0 auto")
                .set("width", "100%");

        // Footer - FIX: Use box-sizing to include padding in width calculation
        Div footer = new Div();
        footer.getStyle()
                .set("background-color", "#2c3e50")
                .set("color", "white")
                .set("padding", "20px")
                .set("text-align", "center")
                .set("width", "100%")
                .set("box-sizing", "border-box")
                .set("flex-shrink", "0")
                .set("margin-top", "auto");

        Span footerText = new Span("© 2025 EventHub - All Rights Reserved");
        footerText.getStyle()
                .set("font-size", "16px")
                .set("display", "block");

        footer.add(footerText);

        // Main wrapper - Also ensure no overflow
        VerticalLayout wrapper = new VerticalLayout();
        wrapper.setPadding(false);
        wrapper.setSpacing(false);
        wrapper.setMargin(false);
        wrapper.getStyle()
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("min-height", "100vh")
                .set("width", "100%")
                .set("overflow-x", "hidden");  // ADD THIS - prevents horizontal scroll

        wrapper.add(contentArea, footer);

        super.setContent(wrapper);
    }









    private void createBreadcrumb() {
        breadcrumb = new Breadcrumb();
        breadcrumb.setWidthFull();
    }

    public Breadcrumb getBreadcrumb() {
        return breadcrumb;
    }



    private void createHeader() {
        Image logo = new Image("https://github.com/SoukainaZAHTI/Resources/blob/main/logo.png?raw=true", "EventHub Logo");
        logo.setHeight("20%"); // Reduced height for better header proportions
        logo.getStyle()
                .set("margin", "0")
                .set("cursor", "pointer")
                .set("object-fit", "contain") // Keep aspect ratio
                .set("max-width", "20%"); // Limit maximum width

        logo.addClickListener(e -> navigationManager.navigateToHome());

        // User info or login/register buttons
        HorizontalLayout headerRight = new HorizontalLayout();
        headerRight.setSpacing(true);
        headerRight.setAlignItems(FlexComponent.Alignment.CENTER);
        headerRight.getStyle().set("flex-shrink", "0"); // Prevent buttons from shrinking

        if (sessionService.isLoggedIn()) {
//            Span userName = new Span("👤 " + sessionService.getCurrentUser().getNomComplet());
//            userName.getStyle().set("color", "white");
            Button profileButton = new Button("My Profile", new Icon(VaadinIcon.USER));
            profileButton.addClickListener(e -> navigationManager.navigateToProfile());
            Button logoutBtn = new Button("Logout", VaadinIcon.SIGN_OUT.create());
            logoutBtn.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
            logoutBtn.addClickListener(e -> {
                sessionService.logout();
                VaadinSession.getCurrent().close();
                navigationManager.logoutAndRedirect();
            });

            headerRight.add(profileButton, logoutBtn);
        } else {
            Button loginBtn = new Button("Login", VaadinIcon.SIGN_IN.create());
            loginBtn.getStyle().set("color", "white")
                    .set("background-color", "#2E3E51");
            loginBtn.addClickListener(e -> navigationManager.navigateToLogin());

            Button registerBtn = new Button("Register", VaadinIcon.USER.create());
            registerBtn.getStyle().set("color", "white")
                    .set("background-color", "#9C4C36");
            registerBtn.addClickListener(e -> navigationManager.navigateToRegister());

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
        drawerContent.setSpacing(false);

        // Common menu items for everyone
        SideNavItem homeItem = createMenuItem("Home", VaadinIcon.HOME, HomeView.class);
        drawerContent.add(homeItem);

        // Role-based menu items
        if (sessionService.isLoggedIn()) {
            if (sessionService.isAdmin()) {
                SideNavItem dashboardItem = createMenuItem("Admin Dashboard", VaadinIcon.DASHBOARD, AdminDashboardView.class);
                SideNavItem usersItem = createMenuItem("Manage Users", VaadinIcon.USERS, UserListView.class);
                SideNavItem allEventsItem = createMenuItem("All Events", VaadinIcon.CALENDAR, EventListView.class);
                SideNavItem allRessItem = createMenuItem("All Reservations", VaadinIcon.LIST, AllReservationsView.class);

                drawerContent.add(dashboardItem, usersItem, allEventsItem, allRessItem);
            } else if (sessionService.isOrganizer()) {
                SideNavItem dashboardItem = createMenuItem("Dashboard", VaadinIcon.DASHBOARD, OrganizerDashboardView.class);
                SideNavItem myEventsItem = createMenuItem("My Events", VaadinIcon.CALENDAR_USER, EventListView.class);
                drawerContent.add(dashboardItem, myEventsItem);
            } else if (sessionService.isClient()) {
                SideNavItem dashboardItem = createMenuItem("Dashboard", VaadinIcon.DASHBOARD, DashboardView.class);
                SideNavItem myBookingsItem = createMenuItem("My Bookings", VaadinIcon.BOOK, MyReservationsView.class);
                drawerContent.add(dashboardItem, myBookingsItem);
            }
        }

        addToDrawer(drawerContent);
    }

    private SideNavItem createMenuItem(String text, VaadinIcon icon, Class<? extends Component> navigationTarget) {
        SideNavItem menuItem = new SideNavItem(text, navigationTarget, icon.create());
        return menuItem;
    }
}