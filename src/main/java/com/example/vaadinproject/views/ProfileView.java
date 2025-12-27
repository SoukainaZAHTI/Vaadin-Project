package com.example.vaadinproject.views;

import com.example.vaadinproject.entities.User;
import com.example.vaadinproject.services.SessionService;
import com.example.vaadinproject.services.UserService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.transaction.annotation.Transactional;

@Route(value = "profile", layout = MainLayout.class)
@PageTitle("Profile")
@Transactional // Add this

public class ProfileView extends VerticalLayout implements BeforeEnterObserver {

    private final UserService userService;
    private final SessionService sessionService;

    // Personal Information Form
    private TextField nom = new TextField("First Name");
    private TextField prenom = new TextField("Last Name");
    private EmailField email = new EmailField("Email");
    private TextField telephone = new TextField("Phone Number");

    // Password Change Form
    private PasswordField currentPassword = new PasswordField("Current Password");
    private PasswordField newPassword = new PasswordField("New Password");
    private PasswordField confirmPassword = new PasswordField("Confirm New Password");

    // Statistics
    private Span totalEventsSpan = new Span();
    private Span totalReservationsSpan = new Span();
    private Span accountStatusSpan = new Span();

    private BeanValidationBinder<User> binder = new BeanValidationBinder<>(User.class);
    private User currentUser;

    public ProfileView(UserService userService, SessionService sessionService) {
        this.userService = userService;
        this.sessionService = sessionService;

        addClassName("profile-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setPadding(true);
        setSpacing(true);

        // Load current user
        currentUser = sessionService.getCurrentUser();
        if (currentUser == null) {
            return;
        }

        // Create main container
        VerticalLayout container = new VerticalLayout();
        container.setMaxWidth("900px");
        container.setWidthFull();
        container.getStyle()
                .set("background", "white")
                .set("padding", "30px")
                .set("border-radius", "12px")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.1)");

        // Add sections
        container.add(
                createHeader(),
                createStatisticsSection(),
                createPersonalInfoSection(),
                createPasswordChangeSection(),
                createDangerZone()
        );

        add(container);

        // Load user data
        loadUserData();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (!sessionService.isLoggedIn()) {
            event.rerouteTo("login");
        }
    }

    private VerticalLayout createHeader() {
        VerticalLayout header = new VerticalLayout();
        header.setPadding(false);
        header.setSpacing(false);

        H2 title = new H2("My Profile");
        title.getStyle()
                .set("margin", "0 0 10px 0")
                .set("color", "#A14C3A");

        Paragraph subtitle = new Paragraph("Manage your account settings and preferences");
        subtitle.getStyle()
                .set("margin", "0")
                .set("color", "#606770");

        header.add(title, subtitle);
        return header;
    }

    private VerticalLayout createStatisticsSection() {
        VerticalLayout section = new VerticalLayout();
        section.setPadding(true);
        section.setSpacing(true);
        section.getStyle()
                .set("background", "#F5F5F5")
                .set("border-radius", "8px")
                .set("margin-top", "20px");

        H3 sectionTitle = new H3("Account Statistics");
        sectionTitle.getStyle().set("margin", "0 0 15px 0");

        HorizontalLayout statsGrid = new HorizontalLayout();
        statsGrid.setWidthFull();
        statsGrid.setSpacing(true);

        // Role badge
        Span roleBadge = new Span(currentUser.getRole().getLabel());
        roleBadge.getStyle()
                .set("background", getRoleColor(currentUser.getRole()))
                .set("color", "white")
                .set("padding", "6px 16px")
                .set("border-radius", "20px")
                .set("font-weight", "bold")
                .set("font-size", "14px");

        // Account status
        accountStatusSpan.setText(currentUser.getActif() ? "Active" : "Inactive");
        accountStatusSpan.getStyle()
                .set("background", currentUser.getActif() ? "#4CAF50" : "#F44336")
                .set("color", "white")
                .set("padding", "6px 16px")
                .set("border-radius", "20px")
                .set("font-weight", "bold")
                .set("font-size", "14px");

        statsGrid.add(
                createStatCard("👤 Role", roleBadge),
                createStatCard("📊 Status", accountStatusSpan)
        );

        // Additional stats based on role
        if (currentUser.isOrganizer()) {
            totalEventsSpan.setText(String.valueOf(currentUser.getEvenementsOrganises().size()));
            totalEventsSpan.getStyle()
                    .set("font-size", "24px")
                    .set("font-weight", "bold")
                    .set("color", "#A14C3A");
            statsGrid.add(createStatCard("🎭 Events Organized", totalEventsSpan));
        }

        if (currentUser.isClient()) {
            totalReservationsSpan.setText(String.valueOf(currentUser.getReservations().size()));
            totalReservationsSpan.getStyle()
                    .set("font-size", "24px")
                    .set("font-weight", "bold")
                    .set("color", "#A14C3A");
            statsGrid.add(createStatCard("🎫 Total Reservations", totalReservationsSpan));
        }

        section.add(sectionTitle, statsGrid);
        return section;
    }

    private VerticalLayout createStatCard(String label, Span value) {
        VerticalLayout card = new VerticalLayout();
        card.setPadding(true);
        card.setSpacing(false);
        card.getStyle()
                .set("background", "white")
                .set("border-radius", "8px")
                .set("padding", "15px")
                .set("flex", "1");

        Paragraph labelText = new Paragraph(label);
        labelText.getStyle()
                .set("margin", "0 0 8px 0")
                .set("color", "#606770")
                .set("font-size", "14px");

        card.add(labelText, value);
        return card;
    }

    private VerticalLayout createPersonalInfoSection() {
        VerticalLayout section = new VerticalLayout();
        section.setPadding(true);
        section.setSpacing(true);
        section.getStyle()
                .set("margin-top", "20px");

        H3 sectionTitle = new H3("Personal Information");
        sectionTitle.getStyle().set("margin", "0 0 15px 0");

        // Configure form
        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2)
        );

        // Configure fields
        nom.setRequired(true);
        prenom.setRequired(true);
        email.setRequired(true);
        email.setReadOnly(true); // Email cannot be changed
        email.setHelperText("Email cannot be changed");
        telephone.setPattern("^(\\+212|0)[5-7]\\d{8}$");
        telephone.setHelperText("Format: +212612345678 or 0612345678");

        // Bind fields
        binder.forField(nom).bind(User::getNom, User::setNom);
        binder.forField(prenom).bind(User::getPrenom, User::setPrenom);
        binder.forField(email).bind(User::getEmail, User::setEmail);
        binder.forField(telephone).bind(User::getTelephone, User::setTelephone);

        formLayout.add(prenom, nom, email, telephone);

        // Buttons
        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setSpacing(true);
        buttons.getStyle().set("margin-top", "15px");

        Button saveButton = new Button("Save Changes");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.getStyle().set("background", "#A14C3A");
        saveButton.addClickListener(e -> savePersonalInfo());

        Button cancelButton = new Button("Cancel");
        cancelButton.addClickListener(e -> loadUserData());

        buttons.add(saveButton, cancelButton);

        section.add(sectionTitle, formLayout, buttons);
        return section;
    }

    private VerticalLayout createPasswordChangeSection() {
        VerticalLayout section = new VerticalLayout();
        section.setPadding(true);
        section.setSpacing(true);
        section.getStyle()
                .set("margin-top", "20px")
                .set("background", "#FFF9E6")
                .set("border-radius", "8px")
                .set("border", "1px solid #FFE082");

        H3 sectionTitle = new H3("Change Password");
        sectionTitle.getStyle().set("margin", "0 0 15px 0");

        Paragraph warning = new Paragraph("⚠️ Make sure to use a strong password");
        warning.getStyle()
                .set("margin", "0 0 15px 0")
                .set("color", "#F57C00")
                .set("font-size", "14px");

        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));

        currentPassword.setRequired(true);
        currentPassword.setWidthFull();

        newPassword.setRequired(true);
        newPassword.setWidthFull();
        newPassword.setMinLength(8);
        newPassword.setHelperText("At least 8 characters");

        confirmPassword.setRequired(true);
        confirmPassword.setWidthFull();

        formLayout.add(currentPassword, newPassword, confirmPassword);

        Button changePasswordButton = new Button("Change Password");
        changePasswordButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        changePasswordButton.getStyle()
                .set("background", "#FF9800")
                .set("margin-top", "15px");
        changePasswordButton.addClickListener(e -> changePassword());

        section.add(sectionTitle, warning, formLayout, changePasswordButton);
        return section;
    }

    private VerticalLayout createDangerZone() {
        VerticalLayout section = new VerticalLayout();
        section.setPadding(true);
        section.setSpacing(true);
        section.getStyle()
                .set("margin-top", "20px")
                .set("background", "#FFEBEE")
                .set("border-radius", "8px")
                .set("border", "1px solid #FFCDD2");

        H3 sectionTitle = new H3("Danger Zone");
        sectionTitle.getStyle()
                .set("margin", "0 0 10px 0")
                .set("color", "#D32F2F");

        Paragraph warning = new Paragraph(
                "⚠️ Deactivating your account will prevent you from logging in. " +
                        "Your data will be preserved but inaccessible."
        );
        warning.getStyle()
                .set("margin", "0 0 15px 0")
                .set("color", "#D32F2F")
                .set("font-size", "14px");

        Button deactivateButton = new Button("Deactivate Account");
        deactivateButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        deactivateButton.addClickListener(e -> confirmDeactivation());

        section.add(sectionTitle, warning, deactivateButton);
        return section;
    }

    private void loadUserData() {
        currentUser = sessionService.getCurrentUser();
        if (currentUser != null) {
            binder.readBean(currentUser);
        }
    }

    private void savePersonalInfo() {
        try {
            binder.writeBean(currentUser);
            User updatedUser = userService.saveUser(currentUser);
            sessionService.setCurrentUser(updatedUser);

            Notification.show("Profile updated successfully!")
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

            loadUserData();
        } catch (ValidationException e) {
            Notification.show("Please check your information")
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void changePassword() {
        String current = currentPassword.getValue();
        String newPwd = newPassword.getValue();
        String confirm = confirmPassword.getValue();

        // Validation
        if (current.isEmpty() || newPwd.isEmpty() || confirm.isEmpty()) {
            Notification.show("Please fill all password fields")
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        if (!currentUser.getPassword().equals(current)) {
            Notification.show("Current password is incorrect")
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        if (newPwd.length() < 8) {
            Notification.show("New password must be at least 8 characters")
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        if (!newPwd.equals(confirm)) {
            Notification.show("New passwords do not match")
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        // Update password
        currentUser.setPassword(newPwd);
        userService.saveUser(currentUser);

        Notification.show("Password changed successfully!")
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

        // Clear password fields
        currentPassword.clear();
        newPassword.clear();
        confirmPassword.clear();
    }

    private void confirmDeactivation() {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Deactivate Account?");
        dialog.setText(
                "Are you sure you want to deactivate your account? " +
                        "You will be logged out and won't be able to log in again until an admin reactivates your account."
        );

        dialog.setCancelable(true);
        dialog.setConfirmText("Deactivate");
        dialog.setConfirmButtonTheme("error primary");

        dialog.addConfirmListener(e -> deactivateAccount());

        dialog.open();
    }

    private void deactivateAccount() {
        currentUser.setActif(false);
        userService.saveUser(currentUser);

        Notification.show("Account deactivated. You will be logged out.")
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

        // Logout and redirect
        sessionService.logout();
        UI.getCurrent().navigate("login");
    }

    private String getRoleColor(com.example.vaadinproject.entities.Role role) {
        return switch (role) {
            case ADMIN -> "#673AB7";
            case ORGANIZER -> "#FF9800";
            case CLIENT -> "#2196F3";
        };
    }
}