package com.example.vaadinproject.views;

import com.example.vaadinproject.entities.Role;
import com.example.vaadinproject.entities.User;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import javax.swing.*;
import java.util.List;
@Route("register")
@PageTitle("Register")
@AnonymousAllowed

public class RegisterView  extends VerticalLayout {


    TextField nom = new TextField("First Name");
    TextField prenom = new TextField("Last Name");
    EmailField email = new EmailField("Email");
    ComboBox <Role> role = new ComboBox<>("Role");

    Button register = new Button("Register");

    public RegisterView(List<Role> roles) {

        addClassName("register-view");
        setSizeFull();
        setAlignItems(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        role.setItems(roles);
        role.setItemLabelGenerator(Role :: getLabel);

        add (
                new H1("Welcome to Reservations Space, Please fill the Registration Form"),
              nom,
              prenom,
                email,
                role,
                createButtonLayout()

        );
    }

    private Component createButtonLayout() {

        register.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        register.addClickShortcut(Key.ENTER);

        return new HorizontalLayout(register);
    }


}