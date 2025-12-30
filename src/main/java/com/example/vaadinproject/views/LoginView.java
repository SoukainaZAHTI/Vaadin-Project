package com.example.vaadinproject.views;

import com.example.vaadinproject.entities.User;
import com.example.vaadinproject.services.SessionService;
import com.example.vaadinproject.services.UserService;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import java.util.Optional;

@Route(value = "login")
@PageTitle("Login")
@AnonymousAllowed
public class LoginView extends VerticalLayout {

    private final UserService userService;
    private final SessionService sessionService;

    private EmailField email = new EmailField("Email");
    private PasswordField password = new PasswordField("Password");
    private Button loginButton = new Button("Login");

    public LoginView(UserService userService, SessionService sessionService) {

        VerticalLayout loginCard = new VerticalLayout();
        loginCard.setWidth("400px");
        loginCard.getStyle()
                .set("background", "white")
                .set("padding", "30px")
                .set("border-radius", "30px")
                .set("box-shadow", "0 2px 4px rgba(0, 0, 0, 0.1), 0 8px 16px rgba(0, 0, 0, 0.1)");

        this.userService = userService;
        this.sessionService = sessionService;


        addClassName("login-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        // Configure fields
        email.setWidthFull();
        email.setPlaceholder("Enter your email");
        email.setClearButtonVisible(true);

        password.setWidthFull();
        password.setPlaceholder("Enter your password");

        loginButton.setWidthFull();
        loginButton.addClickListener(e -> handleLogin());
        loginButton.getStyle()
                .set("background", "#9C4C36")
                .set("color", "white")
                .set("font-weight", "bold")
                .set("font-size", "16px")
                .set("padding", "12px")
                .set("border", "none")
                .set("border-radius", "6px")
                .set("cursor", "pointer")
                .set("margin-top", "10px");
        loginButton.addClickShortcut(Key.ENTER);

        Image appName = new Image("https://github.com/SoukainaZAHTI/Resources/blob/main/logo.png?raw=true", "EventHub Logo");
        appName.setHeight("200px"); // Reduced height for better header proportions
        appName.getStyle()
                .set("margin", "10px")
                .set("cursor", "pointer")
                .set("object-fit", "contain") // Keep aspect ratio
                .set("max-width", "300px"); // Limit maximum width

        appName.addClickListener(e -> UI.getCurrent().navigate("/"));




        // Registration link
        Anchor registerLink = new Anchor("/register", "Don't have an account? Register Now!");
        loginCard.add(email, password  , loginButton, registerLink);

        add(
                appName,
                loginCard
        );
    }


    private void handleLogin() {
        Optional<User> userOpt = userService.login(email.getValue(), password.getValue());

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // Store user in session
            sessionService.setCurrentUser(user);

            // Redirect based on role
            redirectBasedOnRole(user);
        } else {
            Notification.show("Invalid email or password", 3000, Notification.Position.MIDDLE);
        }
    }
    private void redirectBasedOnRole(User user) {
        switch (user.getRole()) {
            case ADMIN:
                UI.getCurrent().navigate("admin");
                break;
            case ORGANIZER:
                UI.getCurrent().navigate("organizer");
                break;
            case CLIENT:
                UI.getCurrent().navigate("client/dashboard");
                break;
        }
}
}
