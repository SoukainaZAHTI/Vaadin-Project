package com.example.vaadinproject.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("session-expired")
@PageTitle("Session Expirée")
@AnonymousAllowed
public class SessionExpiredView extends VerticalLayout {

    public SessionExpiredView() {
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setSizeFull();

        getStyle()
                .set("background", "linear-gradient(135deg, #333 0%, #f5f5f5 100%)")
                .set("color", "white");

        Icon icon = VaadinIcon.SIGN_OUT.create();
        icon.setSize("80px");
        icon.getStyle().set("color", "#333");

        H1 title = new H1("Session Expired");
        title.getStyle()
                .set("margin", "20px 0 10px 0")
                .set("font-size", "2.5em");

        Paragraph message = new Paragraph(
                "Your session has expired or you're not connected. " +
                        "Please login to continue."
        );
        message.getStyle()
                .set("font-size", "1.2em")
                .set("text-align", "center")
                .set("max-width", "500px")
                .set("opacity", "0.9");

        Button loginButton = new Button("Login", e -> {
            getUI().ifPresent(ui -> ui.navigate("login"));
        });
        loginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        loginButton.getStyle()
                .set("margin-top", "30px")
                .set("padding", "15px 40px")
                .set("font-size", "1.1em");

        add(icon, title, message, loginButton);
    }
}