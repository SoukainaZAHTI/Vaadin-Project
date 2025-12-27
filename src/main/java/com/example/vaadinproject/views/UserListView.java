package com.example.vaadinproject.views;

import com.example.vaadinproject.entities.Role;
import com.example.vaadinproject.entities.User;
import com.example.vaadinproject.services.SessionService;
import com.example.vaadinproject.services.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "users", layout = MainLayout.class)
@PageTitle("Gestion des Utilisateurs")
public class UserListView extends VerticalLayout implements BeforeEnterObserver {

    private final SessionService sessionService;
    private final UserService userService;
    private final Grid<User> grid = new Grid<>(User.class, false);
    private final TextField filterText = new TextField();

    public UserListView(SessionService sessionService, UserService userService) {
        addClassName("user-list-view");
        this.sessionService = sessionService;
        this.userService = userService;

        setSizeFull();
        configureGrid();
        add(getToolbar(), grid);
        updateList();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        User currentUser = sessionService.getCurrentUser();
        if (currentUser == null || !currentUser.isAdmin()) {
            event.forwardTo("login");
        }
    }

    private void configureGrid() {
        grid.addClassName("user-grid");
        grid.setSizeFull();

        grid.addColumn(User::getId).setHeader("ID").setSortable(true);
        grid.addColumn(User::getNomComplet).setHeader("Nom Complet").setSortable(true);
        grid.addColumn(User::getEmail).setHeader("Email").setSortable(true);
        grid.addColumn(User::getTelephone).setHeader("Téléphone");
        grid.addColumn(user -> user.getRole().name()).setHeader("Rôle").setSortable(true);
        grid.addColumn(user -> user.getActif() ? "Actif" : "Inactif").setHeader("Statut");
        grid.addColumn(user -> user.getDateInscription().toLocalDate()).setHeader("Date d'inscription");

        grid.addComponentColumn(user -> {
            Button editButton = new Button("Modifier");
            editButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
            editButton.addClickListener(e -> openEditDialog(user));

            Button deleteButton = new Button("Supprimer");
            deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
            deleteButton.addClickListener(e -> deleteUser(user));

            HorizontalLayout actions = new HorizontalLayout(editButton, deleteButton);
            actions.setSpacing(true);
            return actions;
        }).setHeader("Actions").setAutoWidth(true);

        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private HorizontalLayout getToolbar() {
        filterText.setPlaceholder("Rechercher par nom ou email...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());

        HorizontalLayout toolbar = new HorizontalLayout(filterText);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void updateList() {
        grid.setItems(userService.findAllUsers(filterText.getValue()));
    }

    private void openEditDialog(User user) {
        Dialog dialog = new Dialog();
        dialog.setWidth("500px");

        H2 title = new H2("Modifier l'utilisateur");

        TextField nomField = new TextField("Nom");
        nomField.setValue(user.getNom());
        nomField.setRequired(true);

        TextField prenomField = new TextField("Prénom");
        prenomField.setValue(user.getPrenom());
        prenomField.setRequired(true);

        EmailField emailField = new EmailField("Email");
        emailField.setValue(user.getEmail());
        emailField.setRequired(true);

        TextField telephoneField = new TextField("Téléphone");
        if (user.getTelephone() != null) {
            telephoneField.setValue(user.getTelephone());
        }

        Select<Role> roleSelect = new Select<>();
        roleSelect.setLabel("Rôle");
        roleSelect.setItems(Role.values());
        roleSelect.setValue(user.getRole());
        roleSelect.setItemLabelGenerator(Role::name);

        Select<Boolean> actifSelect = new Select<>();
        actifSelect.setLabel("Statut");
        actifSelect.setItems(true, false);
        actifSelect.setValue(user.getActif());
        actifSelect.setItemLabelGenerator(actif -> actif ? "Actif" : "Inactif");

        FormLayout formLayout = new FormLayout();
        formLayout.add(nomField, prenomField, emailField, telephoneField, roleSelect, actifSelect);
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2)
        );

        Button saveButton = new Button("Enregistrer", e -> {
            user.setNom(nomField.getValue());
            user.setPrenom(prenomField.getValue());
            user.setEmail(emailField.getValue());
            user.setTelephone(telephoneField.getValue());
            user.setRole(roleSelect.getValue());
            user.setActif(actifSelect.getValue());

            try {
                userService.saveUser(user);
                updateList();
                dialog.close();
                Notification.show("Utilisateur modifié avec succès")
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } catch (Exception ex) {
                Notification.show("Erreur: " + ex.getMessage())
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Annuler", e -> dialog.close());

        HorizontalLayout buttons = new HorizontalLayout(saveButton, cancelButton);
        buttons.setJustifyContentMode(JustifyContentMode.END);

        VerticalLayout dialogLayout = new VerticalLayout(title, formLayout, buttons);
        dialogLayout.setPadding(true);
        dialogLayout.setSpacing(true);

        dialog.add(dialogLayout);
        dialog.open();
    }

    private void deleteUser(User user) {
        User currentUser = sessionService.getCurrentUser();
        if (user.getId().equals(currentUser.getId())) {
            Notification.show("Vous ne pouvez pas supprimer votre propre compte")
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        Dialog confirmDialog = new Dialog();
        confirmDialog.add(new H2("Confirmer la suppression"));
        confirmDialog.add("Êtes-vous sûr de vouloir supprimer " + user.getNomComplet() + " ?");

        Button confirmButton = new Button("Supprimer", e -> {
            try {
                userService.deleteUser(user);
                updateList();
                confirmDialog.close();
                Notification.show("Utilisateur supprimé")
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } catch (Exception ex) {
                Notification.show("Erreur: " + ex.getMessage())
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        confirmButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Annuler", e -> confirmDialog.close());

        HorizontalLayout buttons = new HorizontalLayout(confirmButton, cancelButton);
        confirmDialog.add(buttons);
        confirmDialog.open();
    }
}