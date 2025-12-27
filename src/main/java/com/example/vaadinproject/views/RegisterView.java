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
    TextField telephone = new TextField("Phone Number");

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

        Image appName = new Image("https://github.com/SoukainaZAHTI/Resources/blob/main/logo.png?raw=true", "EventHub Logo");
        appName.setHeight("30%"); // Reduced height for better header proportions
        appName.getStyle()
                .set("margin", "0")
                .set("cursor", "pointer")
                .set("object-fit", "contain") // Keep aspect ratio
                .set("max-width", "350%"); // Limit maximum width

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
        register.addClickShortcut(Key.ENTER);
        register.addClickListener(event -> registerUser());



        card.add(subtitle, nom, prenom, email, password, telephone, register);
        rightSide.add(card);

        add(leftSide, rightSide);
    }
    private void registerUser() {
        try {
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

            clearForm(); // Clear the form before navigation

            // Redirect based on role
            UI.getCurrent().navigate("client/dashboard");

        } catch (ValidationException e) {
            Notification.show("Please fill all required fields correctly")
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

//    private void redirectBasedOnRole(User user) {
//        switch (user.getRole()) {
//            case ADMIN:
//                UI.getCurrent().navigate("admin");
//                break;
//            case ORGANIZER:
//                UI.getCurrent().navigate("organizer");
//                break;
//            case CLIENT:
//                UI.getCurrent().navigate("client/dashboard");
//                break;
//            default:
//                UI.getCurrent().navigate("login");
//                break;
//        }
//    }

    private void clearForm() {
        binder.readBean(null);
        nom.clear();
        prenom.clear();
        email.clear();
        password.clear();
        telephone.clear();
    }
}