package com.example.vaadinproject.components;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import java.util.ArrayList;
import java.util.List;

public class Breadcrumb extends HorizontalLayout {

    private List<BreadcrumbItem> items = new ArrayList<>();

    public Breadcrumb() {
        setSpacing(false);
        getStyle()
                .set("padding", "10px 20px")
                .set("background-color", "#f5f5f5")
                .set("border-radius", "4px")
                .set("margin-bottom", "20px");
    }

    public void addItem(String label, String route) {
        items.add(new BreadcrumbItem(label, route));
        refresh();
    }

    public void addItem(String label) {
        items.add(new BreadcrumbItem(label, null));
        refresh();
    }

    public void clear() {
        items.clear();
        removeAll();
    }

    private void refresh() {
        removeAll();

        for (int i = 0; i < items.size(); i++) {
            BreadcrumbItem item = items.get(i);
            boolean isLast = (i == items.size() - 1);

            if (isLast || item.route == null) {
                // Last item or no route - show as plain text
                Span span = new Span(item.label);
                span.getStyle()
                        .set("color", "#333")
                        .set("font-weight", "bold");
                add(span);
            } else {
                // Clickable link
                Anchor link = new Anchor(item.route, item.label);
                link.getStyle()
                        .set("color", "#2196F3")
                        .set("text-decoration", "none");
                add(link);
            }

            // Add separator
            if (!isLast) {
                Span separator = new Span(" / ");
                separator.getStyle()
                        .set("color", "#999")
                        .set("margin", "0 8px");
                add(separator);
            }
        }
    }

    private static class BreadcrumbItem {
        String label;
        String route;

        BreadcrumbItem(String label, String route) {
            this.label = label;
            this.route = route;
        }
    }
}