package com.example.vaadinproject.services;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.QueryParameters;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NavigationManager {

    // Navigation methods for main views
    public void navigateToHome() {
        UI.getCurrent().navigate("");
    }

    public void navigateToLogin() {
        UI.getCurrent().navigate("login");
    }

    public void navigateToRegister() {
        UI.getCurrent().navigate("register");
    }

    public void navigateToProfile() {
        UI.getCurrent().navigate("profile");
    }

    // Admin navigation
    public void navigateToAdminDashboard() {
        UI.getCurrent().navigate("admin");
    }

    public void navigateToUserList() {
        UI.getCurrent().navigate("users");
    }



    // Organizer navigation
    public void navigateToOrganizerDashboard() {
        UI.getCurrent().navigate("organizer");
    }

    public void navigateToMyEvents() {
        UI.getCurrent().navigate("events");
    }

    public void navigateToCreateEvent() {
        UI.getCurrent().navigate("event-form");
    }

    // Client navigation
    public void navigateToClientDashboard() {
        UI.getCurrent().navigate("client/dashboard");
    }

    public void navigateToMyBookings() {
        UI.getCurrent().navigate("bookings");
    }

    // Event navigation with parameters
    public void navigateToAllEvents() {
        UI.getCurrent().navigate("all-events");

    }

    public void navigateToEditEvent(Long eventId) {
        Map<String, String> params = new HashMap<>();
        params.put("id", eventId.toString());
        UI.getCurrent().navigate("event-form", QueryParameters.simple(params));
    }

    // Reservation navigation
    public void navigateToReservationDetails(Long reservationId) {
        Map<String, String> params = new HashMap<>();
        params.put("id", reservationId.toString());
        UI.getCurrent().navigate("reservation-details", QueryParameters.simple(params));
    }

    // Navigation with multiple parameters
    public void navigateWithParams(String route, Map<String, String> params) {
        UI.getCurrent().navigate(route, QueryParameters.simple(params));
    }

    // Back navigation
    public void navigateBack() {
        UI.getCurrent().getPage().getHistory().back();
    }

    // Role-based navigation
    public void navigateToDashboardByRole(String role) {
        switch (role) {
            case "ADMIN":
                navigateToAdminDashboard();
                break;
            case "ORGANIZER":
                navigateToOrganizerDashboard();
                break;
            case "CLIENT":
                navigateToClientDashboard();
                break;
            default:
                navigateToHome();
        }
    }

    // Logout and redirect
    public void logoutAndRedirect() {
        UI.getCurrent().getPage().setLocation("/login");
    }
}