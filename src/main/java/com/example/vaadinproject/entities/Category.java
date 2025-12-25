package com.example.vaadinproject.entities;

public enum Category {
    CONCERT("Concert", "🎵"),
    THEATRE("Théâtre", "🎭"),
    CONFERENCE("Conférence", "🎤"),
    SPORT("Sport", "⚽"),
    AUTRE("Autre", "📌");

    private final String label;
    private final String icon;

    Category(String label, String icon) {
        this.label = label;
        this.icon = icon;
    }

    public String getLabel() {
        return label;
    }


    public String getIcon() {
        return icon;
    }

    public String getDisplayName() {
        return icon + " " + label;
    }
}