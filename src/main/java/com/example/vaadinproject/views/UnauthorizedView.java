package com.example.vaadinproject.views;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import jakarta.servlet.http.HttpServletResponse;

@Route("unauthorized")
@PageTitle("Unauthorized Access")
@AnonymousAllowed
public class UnauthorizedView extends VerticalLayout {

    public UnauthorizedView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        getStyle()
                .set("background", "linear-gradient(135deg, #667eea 0%, #764ba2 100%)")
                .set("color", "white");

        H1 title = new H1("🚫 Unauthorized Access");
        title.getStyle()
                .set("margin", "0")
                .set("font-size", "48px")
                .set("color", "white");

        Paragraph message = new Paragraph(
                "You don't have permission to access this page."
        );
        message.getStyle()
                .set("font-size", "20px")
                .set("color", "rgba(255, 255, 255, 0.9)")
                .set("margin-top", "20px");

        Paragraph instruction = new Paragraph(
                "Please contact an administrator if you believe this is an error."
        );
        instruction.getStyle()
                .set("font-size", "16px")
                .set("color", "rgba(255, 255, 255, 0.7)");

        add(title, message, instruction);
    }
}