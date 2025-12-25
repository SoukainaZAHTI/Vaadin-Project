package com.example.vaadinproject.views;

import com.example.vaadinproject.entities.Event;
import com.example.vaadinproject.entities.Category;
import com.example.vaadinproject.entities.Status;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.shared.Registration;

import java.util.List;

@Route("event-form")
@PageTitle("Create Event")
@AnonymousAllowed
public class EventForm extends VerticalLayout {
    protected final Binder<Event> binder = new Binder<>(Event.class);

    private Event event;

    private final TextField titre = new TextField("Title");
    private final TextArea description = new TextArea("Description");
    private final ComboBox<Category> categorie = new ComboBox<>("Category");
    private final DateTimePicker dateDebut = new DateTimePicker("Start date");
    private final DateTimePicker dateFin = new DateTimePicker("End date");
    private final TextField lieu = new TextField("Location");
    private final TextField ville = new TextField("City");
    private final IntegerField capaciteMax = new IntegerField("Max capacity");
    private final NumberField prixUnitaire = new NumberField("Price");
    private final ComboBox<Status> statut = new ComboBox<>("Status");

    private final Button save = new Button("Save");
    private final Button add = new Button("Add");

    private final Button delete = new Button("Delete");
    private final Button cancel = new Button("Cancel");


    public EventForm(List<Event> events) {

        addClassName("event-form");
        setSizeFull();
        setAlignItems(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        categorie.setItems(Category.values());
        statut.setItems(Status.values());

        capaciteMax.setMin(1);
        capaciteMax.setStep(1);
        prixUnitaire.setMin(0);

        description.setMaxLength(1000);

        FormLayout formLayout = new FormLayout(
                titre,
                description,
                categorie,
                dateDebut,
                dateFin,
                lieu,
                ville,
                capaciteMax,
                prixUnitaire,
                statut
        );

        binder.bindInstanceFields(this);




        add(
                new H1("Create Event"),
                formLayout,
                createButtonLayout()
        );
    }
    public void setEvent(Event event) {
        this.event = event;
        binder.setBean(event);
    }

    private Component createButtonLayout() {

        save.addClickListener(e -> {
            if (binder.validate().isOk()) {
                fireEvent(new SaveEvent(this, binder.getBean()));
            }
        });

        add.addClickListener(e -> {
            System.out.println(">>> ADD CLICKED");

            if (binder.validate().isOk()) {
                System.out.println(">>> bean created");

                fireEvent(new CreateNewEvent(this, binder.getBean()));
            }
        });

        delete.addClickListener(e -> {
            if (event != null) {
                fireEvent(new DeleteEvent(this, event));
            }
        });

        cancel.addClickListener(e -> fireEvent(new CloseEvent(this)));





        return new HorizontalLayout(add, save, delete, cancel);
    }





    // Events
    public static abstract class OpFormEvent extends ComponentEvent<EventForm> {
        private Event event;

        protected OpFormEvent(EventForm source, Event event) {
            super(source, false);
            this.event = event;
        }

        public Event getEvent() {
            return event;
        }
    }
    public static class CreateNewEvent extends OpFormEvent {
        public CreateNewEvent(EventForm source, Event event) {
            super(source, event);
        }
    }

    public static class SaveEvent extends OpFormEvent {
        public SaveEvent(EventForm source, Event event) {
            super(source, event);
        }
    }


    public static class DeleteEvent extends OpFormEvent {
        DeleteEvent(EventForm source, Event event) {
            super(source, event);
        }

    }

    public static class CloseEvent extends OpFormEvent {
        CloseEvent(EventForm source) {
            super(source, null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}
