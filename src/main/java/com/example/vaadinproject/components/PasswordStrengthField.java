package com.example.vaadinproject.components;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.data.value.ValueChangeMode;

/**
 * A custom password field component with visual strength indicator.
 * Displays a color-coded bar and text showing password strength in real-time.
 */
public class PasswordStrengthField extends VerticalLayout {

    private final PasswordField passwordField;
    private final Div strengthBar;
    private final Span strengthText;

    public PasswordStrengthField(String label) {
        setPadding(false);
        setSpacing(false);
        setWidthFull();

        // Create password field
        passwordField = new PasswordField(label);
        passwordField.setWidthFull();
        passwordField.setRequired(true);
        passwordField.setMinLength(8);
        passwordField.setErrorMessage("Password must be at least 8 characters");
        passwordField.setValueChangeMode(ValueChangeMode.EAGER);

        // Create strength indicator container
        Div strengthIndicator = new Div();
        strengthIndicator.setWidthFull();
        strengthIndicator.getStyle()
                .set("margin-top", "5px")
                .set("margin-bottom", "10px");

        // Create strength bar
        strengthBar = new Div();
        strengthBar.setHeight("4px");
        strengthBar.setWidthFull();
        strengthBar.getStyle()
                .set("background", "#e0e0e0")
                .set("border-radius", "2px")
                .set("overflow", "hidden")
                .set("transition", "all 0.3s ease");

        // Create strength text
        strengthText = new Span();
        strengthText.getStyle()
                .set("font-size", "12px")
                .set("margin-top", "5px")
                .set("display", "block");

        strengthIndicator.add(strengthBar, strengthText);

        // Add value change listener
        passwordField.addValueChangeListener(e -> updatePasswordStrength(e.getValue()));

        add(passwordField, strengthIndicator);
    }

    /**
     * Updates the visual strength indicator based on password value.
     */
    private void updatePasswordStrength(String password) {
        if (password == null || password.isEmpty()) {
            strengthBar.getStyle()
                    .set("background", "#e0e0e0")
                    .set("width", "0%");
            strengthText.setText("");
            return;
        }

        int strength = calculatePasswordStrength(password);
        String color;
        String label;
        String width;

        if (strength < 2) {
            color = "#f44336"; // Red
            label = "Weak";
            width = "25%";
        } else if (strength < 3) {
            color = "#ff9800"; // Orange
            label = "Fair";
            width = "50%";
        } else if (strength < 4) {
            color = "#ffc107"; // Yellow
            label = "Good";
            width = "75%";
        } else {
            color = "#4caf50"; // Green
            label = "Strong";
            width = "100%";
        }

        strengthBar.getStyle()
                .set("background", color)
                .set("width", width);

        strengthText.setText(label);
        strengthText.getStyle().set("color", color);
    }

    /**
     * Calculates password strength based on various criteria.
     * Returns a score from 0-5.
     */
    private int calculatePasswordStrength(String password) {
        int strength = 0;

        // Length check
        if (password.length() >= 8) strength++;
        if (password.length() >= 12) strength++;

        // Contains lowercase
        if (password.matches(".*[a-z].*")) strength++;

        // Contains uppercase
        if (password.matches(".*[A-Z].*")) strength++;

        // Contains digit
        if (password.matches(".*\\d.*")) strength++;

        // Contains special character
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) strength++;

        return Math.min(strength, 5);
    }

    /**
     * Gets the password value.
     */
    public String getValue() {
        return passwordField.getValue();
    }

    /**
     * Sets the password value.
     */
    public void setValue(String value) {
        passwordField.setValue(value);
    }

    /**
     * Clears the password field.
     */
    public void clear() {
        passwordField.clear();
    }

    /**
     * Gets the underlying PasswordField component.
     * Useful for binding or additional configuration.
     */
    public PasswordField getPasswordField() {
        return passwordField;
    }

    /**
     * Sets whether the field is required.
     */
    public void setRequired(boolean required) {
        passwordField.setRequired(required);
    }

    /**
     * Sets the error message.
     */
    public void setErrorMessage(String errorMessage) {
        passwordField.setErrorMessage(errorMessage);
    }

    /**
     * Sets the minimum length.
     */
    public void setMinLength(int minLength) {
        passwordField.setMinLength(minLength);
    }

    /**
     * Sets the helper text.
     */
    public void setHelperText(String helperText) {
        passwordField.setHelperText(helperText);
    }
}