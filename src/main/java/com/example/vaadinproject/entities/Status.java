package com.example.vaadinproject.entities;

public enum Status {
    BROUILLON("Brouillon", "#FFA500"),
    PUBLIE("Publié", "#28A745"),
    ANNULE("Annulé", "#DC3545"),
    TERMINE("Terminé", "#6C757D");

    private final String label;
    private final String color;

    Status(String label, String color) {
        this.label = label;
        this.color = color;
    }

    public String getLabel() {
        return label;
    }

    public String getColor() {
        return color;
    }
}