package com.example.vaadinproject.utils;

import com.example.vaadinproject.entities.Reservation;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CSVExporter {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public void export(List<Reservation> reservations) {
        StringBuilder csv = buildCSV(reservations);
        downloadCSV(csv.toString());
        showSuccessNotification();
    }

    private StringBuilder buildCSV(List<Reservation> reservations) {
        StringBuilder csv = new StringBuilder();
        csv.append("Code,Utilisateur,Email,Événement,Date Événement,Places,Montant,Statut,Date Réservation\n");

        reservations.forEach(r -> appendReservationRow(csv, r));

        return csv;
    }

    private void appendReservationRow(StringBuilder csv, Reservation r) {
        csv.append(escapeCSV(r.getCodeReservation())).append(",");
        csv.append(escapeCSV(r.getUtilisateur().getNom() + " " +
                r.getUtilisateur().getPrenom())).append(",");
        csv.append(escapeCSV(r.getUtilisateur().getEmail())).append(",");
        csv.append(escapeCSV(r.getEvenement().getTitre())).append(",");
        csv.append(formatDateTime(r.getEvenement().getDateDebut())).append(",");
        csv.append(r.getNombrePlaces()).append(",");
        csv.append(r.getMontantTotal()).append(",");
        csv.append(escapeCSV(r.getStatutLabel())).append(",");
        csv.append(formatDateTime(r.getDateReservation())).append("\n");
    }

    private String escapeCSV(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return "N/A";
        try {
            return dateTime.format(DATE_FORMATTER);
        } catch (Exception e) {
            return dateTime.toString();
        }
    }

    private void downloadCSV(String csvContent) {
        UI.getCurrent().getPage().executeJs(
                "const blob = new Blob([$0], {type: 'text/csv;charset=utf-8'});" +
                        "const url = window.URL.createObjectURL(blob);" +
                        "const a = document.createElement('a');" +
                        "a.href = url;" +
                        "a.download = 'reservations_' + new Date().toISOString().split('T')[0] + '.csv';" +
                        "a.click();" +
                        "window.URL.revokeObjectURL(url);",
                csvContent
        );
    }

    private void showSuccessNotification() {
        Notification.show("Export CSV généré avec succès", 3000, Notification.Position.MIDDLE)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }
}