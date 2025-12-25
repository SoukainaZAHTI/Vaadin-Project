package com.example.vaadinproject.entities;


public enum Role {
    ADMIN("Administrator"),
    ORGANIZER("Organizer"),
    CLIENT("Client");

    private final String label;

    Role(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
