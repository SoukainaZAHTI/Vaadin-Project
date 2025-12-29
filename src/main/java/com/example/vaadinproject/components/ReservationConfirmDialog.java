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

public class ReservationConfirmDialog extends Dialog {

    public ReservationConfirmDialog(Reservation reservation,
                                    Consumer<Reservation> onConfirm,
                                    Runnable onError) {
        setWidth("400px");

        VerticalLayout content = new VerticalLayout();
        content.setPadding(true);

        H3 title = new H3("Confirmer la réservation");
        title.getStyle().set("color", "#4CAF50");

        Paragraph message = new Paragraph(
                "Confirmer la réservation " + reservation.getCodeReservation() + " ?"
        );

        HorizontalLayout buttons = createButtons(reservation, onConfirm, onError);

        content.add(title, message, buttons);
        add(content);
    }

    private HorizontalLayout createButtons(Reservation reservation,
                                           Consumer<Reservation> onConfirm,
                                           Runnable onError) {
        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setWidthFull();
        buttons.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        Button cancelBtn = new Button("Annuler", e -> close());

        Button confirmBtn = new Button("Confirmer", e -> {
            try {
                onConfirm.accept(reservation);
                close();
            } catch (Exception ex) {
                onError.run();
            }
        });
        confirmBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);

        buttons.add(cancelBtn, confirmBtn);
        return buttons;
    }
}