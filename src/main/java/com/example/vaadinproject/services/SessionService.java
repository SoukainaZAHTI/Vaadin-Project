package com.example.vaadinproject.services;

import com.example.vaadinproject.entities.User;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.stereotype.Service;

@Service
public class SessionService {

    private static final String USER_SESSION_KEY = "current_user";

    public void setCurrentUser(User user) {
        VaadinSession.getCurrent().setAttribute(USER_SESSION_KEY, user);
    }

    public User getCurrentUser() {
        return (User) VaadinSession.getCurrent().getAttribute(USER_SESSION_KEY);
    }

    public boolean isLoggedIn() {
        return getCurrentUser() != null;
    }

    public boolean isAdmin() {
        User user = getCurrentUser();
        return user != null && user.isAdmin();
    }

    public boolean isOrganizer() {
        User user = getCurrentUser();
        return user != null && user.isOrganizer();
    }

    public boolean isClient() {
        User user = getCurrentUser();
        return user != null && user.isClient();
    }

    public void logout() {
        VaadinSession.getCurrent().getSession().invalidate();
    }
}