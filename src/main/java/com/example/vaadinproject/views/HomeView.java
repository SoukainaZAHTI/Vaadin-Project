package com.example.vaadinproject.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("")

public class HomeView extends VerticalLayout {
    public HomeView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        Button loginButton = new Button("Login");
        loginButton.addClickListener(e ->
                UI.getCurrent().navigate("login")
        );

        Button registerButton = new Button("Register");
        registerButton.addClickListener(e ->
                UI.getCurrent().navigate("register")
        );

        HorizontalLayout buttonLayout = new HorizontalLayout(loginButton, registerButton);

        add(
                new H1("Login To Reservations Space"),
                buttonLayout
        );
    }

}