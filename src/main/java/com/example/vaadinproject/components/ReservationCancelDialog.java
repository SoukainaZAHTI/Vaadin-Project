package com.example.vaadinproject.components;

import com.example.vaadinproject.entities.Reservation;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.function.Consumer;

public class ReservationCancelDialog extends Dialog {

    public ReservationCancelDialog(Reservation reservation,
                                   Consumer<Reservation> onCancel,
                                   Runnable onError) {
        setWidth("400px");

        VerticalLayout content = new VerticalLayout();
        content.setPadding(true);

        H3 title = new H3("Annuler la réservation");
        title.getStyle().set("color", "#F44336");

        Paragraph message = new Paragraph(
                "Êtes-vous sûr de vouloir annuler la réservation " +
                        reservation.getCodeReservation() + " ?"
        );

        HorizontalLayout buttons = createButtons(reservation, onCancel, onError);

        content.add(title, message, buttons);
        add(content);
    }

    private HorizontalLayout createButtons(Reservation reservation,
                                           Consumer<Reservation> onCancel,
                                           Runnable onError) {
        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setWidthFull();
        buttons.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        Button noBtn = new Button("Non", e -> close());

        Button yesBtn = new Button("Oui, annuler", e -> {
            try {
                onCancel.accept(reservation);
                close();
            } catch (Exception ex) {
                onError.run();
            }
        });
        yesBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        buttons.add(noBtn, yesBtn);
        return buttons;
    }
}