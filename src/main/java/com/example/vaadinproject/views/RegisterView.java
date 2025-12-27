package com.example.vaadinproject.views;

import com.example.vaadinproject.components.PasswordStrengthField; // ADD THIS IMPORT
import com.example.vaadinproject.entities.Role;
import com.example.vaadinproject.entities.User;
import com.example.vaadinproject.services.SessionService;
import com.example.vaadinproject.services.UserService;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("register")
@PageTitle("Register")
@AnonymousAllowed
public class RegisterView extends HorizontalLayout {

    private final UserService userService;
    private final SessionService sessionService;

    TextField nom = new TextField("First Name");
    TextField prenom = new TextField("Last Name");
    EmailField email = new EmailField("Email");

    // REPLACE: PasswordField password = new PasswordField("Password");
    // WITH:
    PasswordStrengthField password = new PasswordStrengthField("Password");

    PasswordField confirmPassword = new PasswordField("Confirm Password");
    TextField telephone = new TextField("Phone Number");
    Button register = new Button("Sign Up");

    BeanValidationBinder<User> binder = new BeanValidationBinder<>(User.class);

    public RegisterView(UserService userService, SessionService sessionService) {
        this.userService = userService;
        this.sessionService = sessionService;

        addClassName("register-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setPadding(true);
        setSpacing(true);

        // Configure binder - BIND TO THE UNDERLYING PASSWORD FIELD
        binder.forField(nom).bind(User::getNom, User::setNom);
        binder.forField(prenom).bind(User::getPrenom, User::setPrenom);
        binder.forField(email).bind(User::getEmail, User::setEmail);
        binder.forField(password.getPasswordField()).bind(User::getPassword, User::setPassword);
        binder.forField(telephone).bind(User::getTelephone, User::setTelephone);

        // Left side - Branding
        VerticalLayout leftSide = new VerticalLayout();
        leftSide.setAlignItems(Alignment.CENTER);
        leftSide.setJustifyContentMode(JustifyContentMode.CENTER);
        leftSide.setWidth("50%");

        Image appName = new Image("https://github.com/SoukainaZAHTI/Resources/blob/main/logo.png?raw=true", "EventHub Logo");
        appName.setHeight("30%");
        appName.getStyle()
                .set("margin", "0")
                .set("cursor", "pointer")
                .set("object-fit", "contain")
                .set("max-width", "350%");

        appName.addClickListener(e -> UI.getCurrent().navigate("/"));

        Paragraph tagline = new Paragraph("Discover and manage amazing events");
        tagline.getStyle()
                .set("color", "#606770")
                .set("font-size", "20px")
                .set("font-family", "Candara")
                .set("margin-top", "10px");

        Image illustration = new Image("https://github.com/SoukainaZAHTI/Resources/blob/main/illustrationEventHUB.png?raw=true", "Events illustration");
        illustration.setWidth("600px");
        illustration.setHeight("600px");

        leftSide.add(appName, tagline, illustration);

        // Right side - Form card
        VerticalLayout rightSide = new VerticalLayout();
        rightSide.setAlignItems(Alignment.CENTER);
        rightSide.setJustifyContentMode(JustifyContentMode.CENTER);
        rightSide.setWidth("50%");

        VerticalLayout card = new VerticalLayout();
        card.setWidth("70%");
        card.getStyle()
                .set("background", "white")
                .set("padding", "30px")
                .set("border-radius", "30px")
                .set("box-shadow", "0 2px 4px rgba(0, 0, 0, 0.1), 0 8px 16px rgba(0, 0, 0, 0.1)");

        H2 subtitle = new H2("Create a new account");
        subtitle.getStyle()
                .set("margin", "0 0 20px 0")
                .set("font-size", "24px")
                .set("font-weight", "normal")
                .set("color", "#1c1e21");

        // Configure form fields
        nom.setWidthFull();
        nom.setRequired(true);

        prenom.setWidthFull();
        prenom.setRequired(true);

        email.setWidthFull();
        email.setRequired(true);
        email.setErrorMessage("Please enter a valid email");

        // Password is already configured by PasswordStrengthField
        // Just set it to full width
        password.setWidthFull();

        confirmPassword.setWidthFull();
        confirmPassword.setRequired(true);
        confirmPassword.setErrorMessage("Passwords must match");

        telephone.setWidthFull();
        telephone.setPlaceholder("+212 612-345-678");
        telephone.setPattern("^\\+?[0-9]{10,15}$");
        telephone.setHelperText("Enter a valid phone number (10-15 digits)");
        telephone.setErrorMessage("Invalid phone number format");

        register.setWidthFull();
        register.addClickShortcut(Key.ENTER);
        register.getStyle()
                .set("background", "#A14C3A")
                .set("color", "white")
                .set("font-weight", "bold")
                .set("font-size", "16px")
                .set("padding", "12px")
                .set("border", "none")
                .set("border-radius", "6px")
                .set("cursor", "pointer")
                .set("margin-top", "10px");
        register.addClickListener(event -> registerUser());

        card.add(subtitle, nom, prenom, email, password, confirmPassword, telephone, register);
        rightSide.add(card);

        add(leftSide, rightSide);
    }

    private void registerUser() {
        try {
            // Validate passwords match
            if (!password.getValue().equals(confirmPassword.getValue())) {
                Notification.show("Passwords do not match!")
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            // Validate password length
            if (password.getValue().length() < 8) {
                Notification.show("Password must be at least 8 characters")
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            // Create new user object
            User user = new User();

            // Write form values to the user object
            binder.writeBean(user);
            user.setRole(Role.CLIENT);

            // Check if email already exists
            if (userService.emailExists(user.getEmail())) {
                Notification.show("Email already exists!")
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            // Save user to database
            User savedUser = userService.saveUser(user);

            // Auto-login: Store user in session
            sessionService.setCurrentUser(savedUser);

            // Show success message
            Notification.show("Registration successful! Welcome to EventHub!")
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

            clearForm();

            // Redirect based on role
            UI.getCurrent().navigate("client/dashboard");

        } catch (ValidationException e) {
            Notification.show("Please fill all required fields correctly")
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void clearForm() {
        binder.readBean(null);
        nom.clear();
        prenom.clear();
        email.clear();
        password.clear();
        confirmPassword.clear();
        telephone.clear();
    }
}