package com.example.vaadinproject.views;

import com.example.vaadinproject.entities.Role;
import com.example.vaadinproject.entities.User;
import com.example.vaadinproject.services.SessionService;
import com.example.vaadinproject.services.UserService;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
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

    TextField nom = new TextField("First Name");
    TextField prenom = new TextField("Last Name");

    EmailField email = new EmailField("Email");
    PasswordField password = new PasswordField("Password");
    TextField phoneNumber = new TextField("Phone Number");
    ComboBox<Role> role = new ComboBox<>("Role");

    Button register = new Button("Sign Up");
    private final SessionService sessionService; // ADD THIS




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
        // Configure binder
        binder.bindInstanceFields(this);

        // Left side - Branding
        VerticalLayout leftSide = new VerticalLayout();
        leftSide.setAlignItems(Alignment.CENTER);
        leftSide.setJustifyContentMode(JustifyContentMode.CENTER);
        leftSide.setWidth("50%");

        Image appName = new Image("https://private-user-images.githubusercontent.com/214033788/530433296-10326aaa-7231-4cb6-b43e-8e0ae2f58e4a.png?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NjY3OTMxMDAsIm5iZiI6MTc2Njc5MjgwMCwicGF0aCI6Ii8yMTQwMzM3ODgvNTMwNDMzMjk2LTEwMzI2YWFhLTcyMzEtNGNiNi1iNDNlLThlMGFlMmY1OGU0YS5wbmc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjUxMjI2JTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI1MTIyNlQyMzQ2NDBaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT0yODRiODA0ZDhjMzcwNWUwNTZlNDMxMWE0NWEwYTFkZjczZjliZDQyYjJkMzYzNDVmM2MxZTFiYjVhMjI3MzdlJlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCJ9.HlfezVVFRWDIfF5z1zH_id0rqzVjrRawqGXCyJdZdkw", "EventHub Logo");
        appName.setHeight("30%"); // Reduced height for better header proportions
        appName.getStyle()
                .set("margin", "0")
                .set("cursor", "pointer")
                .set("object-fit", "contain") // Keep aspect ratio
                .set("max-width", "350%"); // Limit maximum width

        appName.addClickListener(e -> UI.getCurrent().navigate("home"));

        Paragraph tagline = new Paragraph("Discover and manage amazing events");
        tagline.getStyle()
                .set("color", "#606770")
                .set("font-size", "20px")
                .set("font-family", "Candara")
                .set("margin-top", "10px");

        Image illustration = new Image("https://private-user-images.githubusercontent.com/214033788/530430484-bcb4b5e6-4db8-487f-a381-53597f35c100.png?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NjY3OTM4MzIsIm5iZiI6MTc2Njc5MzUzMiwicGF0aCI6Ii8yMTQwMzM3ODgvNTMwNDMwNDg0LWJjYjRiNWU2LTRkYjgtNDg3Zi1hMzgxLTUzNTk3ZjM1YzEwMC5wbmc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjUxMjI2JTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI1MTIyNlQyMzU4NTJaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT04ODUzZmNhNmQwY2MxNTEyY2YxYTMxNDA4N2E5MjRlMWE2MjYyZGEyNmE1Njc3MDExNTc2ODYyMDc1NGZlMWM4JlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCJ9.uMuICdbYWVeh8oY5nrG1w9K3dv8yV-vM6e3WK2HtJpQ", "Events illustration");
        illustration.setWidth("600px");
        illustration.setHeight("600px");
        //illustration.getStyle().set("margin-top", "10px");

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

        password.setWidthFull();
        password.setRequired(true);
        password.setMinLength(8);
        password.setErrorMessage("Password must be at least 8 characters");

        phoneNumber.setWidthFull();
        phoneNumber.setPlaceholder("+212 612-345-678");
        phoneNumber.setPattern("^\\+?[0-9]{10,15}$");
        phoneNumber.setHelperText("Enter a valid phone number (10-15 digits)");
        phoneNumber.setErrorMessage("Invalid phone number format");

        role.setWidthFull();
        role.setItems(Role.values());
        role.setItemLabelGenerator(Role::getLabel);
        role.setAllowCustomValue(false);
        role.setRequired(true);

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
        register.addClickShortcut(Key.ENTER);
        register.addClickListener(event -> registerUser());



        card.add(subtitle, nom, prenom, email, password, phoneNumber, role, register);
        rightSide.add(card);

        add(leftSide, rightSide);
    }
    private void registerUser() {
        try {
            // Create new user object
            User user = new User();

            // Write form values to the user object
            binder.writeBean(user);

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

            // Redirect based on role
            redirectBasedOnRole(savedUser);

        } catch (ValidationException e) {
            Notification.show("Please fill all required fields correctly")
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
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
            default:
                UI.getCurrent().navigate("login");
                break;
        }
    }

    private void clearForm() {
        binder.readBean(null);
        nom.clear();
        prenom.clear();
        email.clear();
        password.clear();
        role.clear();
        phoneNumber.clear();
    }
}