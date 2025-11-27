package com.example.vaadinproject;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Accueil")
@Route("")
public class MyView extends VerticalLayout {

    public MyView() {
        add(new H1("Bonjour Vaadin Flow ✨"));
        add(new Button("Dire bonjour", e -> Notification.show("Hello!")));
    }
}
