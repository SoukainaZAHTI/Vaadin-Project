package com.example.vaadinproject.components;

import com.example.vaadinproject.entities.Reservation;
import com.example.vaadinproject.entities.ReservationStatus;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.List;

public class ReservationStatisticsPanel extends HorizontalLayout {

    public ReservationStatisticsPanel(List<Reservation> reservations) {
        setWidthFull();
        setSpacing(true);
        getStyle().set("margin-bottom", "20px");

        Statistics stats = calculateStatistics(reservations);

        add(
                createStatCard("Total", String.valueOf(stats.total), "#2196F3"),
                createStatCard("Confirmées", String.valueOf(stats.confirmed), "#4CAF50"),
                createStatCard("En attente", String.valueOf(stats.pending), "#FF9800"),
                createStatCard("Annulées", String.valueOf(stats.cancelled), "#F44336"),
                createStatCard("Revenu Total", String.format("%.2f MAD", stats.totalRevenue), "#9C27B0")
        );
    }

    private Statistics calculateStatistics(List<Reservation> reservations) {
        long total = reservations.size();
        long confirmed = reservations.stream()
                .filter(r -> r.getStatut() == ReservationStatus.CONFIRMEE)
                .count();
        long pending = reservations.stream()
                .filter(r -> r.getStatut() == ReservationStatus.EN_ATTENTE)
                .count();
        long cancelled = reservations.stream()
                .filter(r -> r.getStatut() == ReservationStatus.ANNULEE)
                .count();
        double totalRevenue = reservations.stream()
                .filter(r -> r.getStatut() == ReservationStatus.CONFIRMEE)
                .mapToDouble(Reservation::getMontantTotal)
                .sum();

        return new Statistics(total, confirmed, pending, cancelled, totalRevenue);
    }

    private VerticalLayout createStatCard(String label, String value, String color) {
        VerticalLayout card = new VerticalLayout();
        card.setPadding(true);
        card.setSpacing(false);
        card.getStyle()
                .set("background", "white")
                .set("border-left", "4px solid " + color)
                .set("border-radius", "8px")
                .set("box-shadow", "0 2px 4px rgba(0,0,0,0.1)")
                .set("flex", "1");

        H3 valueText = new H3(value);
        valueText.getStyle()
                .set("margin", "0")
                .set("color", color)
                .set("font-size", "28px");

        Span labelText = new Span(label);
        labelText.getStyle()
                .set("color", "#666")
                .set("font-size", "13px");

        card.add(valueText, labelText);
        return card;
    }

    private static class Statistics {
        final long total;
        final long confirmed;
        final long pending;
        final long cancelled;
        final double totalRevenue;

        Statistics(long total, long confirmed, long pending, long cancelled, double totalRevenue) {
            this.total = total;
            this.confirmed = confirmed;
            this.pending = pending;
            this.cancelled = cancelled;
            this.totalRevenue = totalRevenue;
        }
    }
}