package com.jollifiy.gameanalytics.views;

import com.jollifiy.gameanalytics.entity.Analytics;
import com.jollifiy.gameanalytics.service.AnalyticsService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.annotation.security.PermitAll;

@PageTitle("Oyun Analitikleri | Jollify Game Analytics")
@Route(value = "analytics", layout = MainLayout.class)
@PermitAll // <-- Ekle
public class AnalyticsView extends VerticalLayout {

    private final AnalyticsService analyticsService;
    private final Grid<Analytics> grid = new Grid<>(Analytics.class);

    @Autowired
    public AnalyticsView(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;

        setSizeFull();
        setPadding(true);

        // Başlık
        add(new H2("Oyun Analitikleri ve Olaylar"));

        // 1. Üst Kontrol Paneli (Create + Refresh)
        Button addButton = new Button("Yeni Analitik Ekle", VaadinIcon.PLUS.create(), e -> openAddDialog());
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button refreshButton = new Button("Yenile", VaadinIcon.REFRESH.create(), e -> loadData());

        HorizontalLayout toolbar = new HorizontalLayout(addButton, refreshButton);
        toolbar.setWidthFull();

        // 2. Grid Yapısı (Read)
        grid.setColumns("id", "playerId", "eventName", "level", "score", "coinCount", "playTime", "createdAt");

        // Kolon başlıklarını Türkçeleştirelim
        grid.getColumnByKey("id").setHeader("ID");
        grid.getColumnByKey("playerId").setHeader("Oyuncu ID");
        grid.getColumnByKey("eventName").setHeader("Olay Adı");
        grid.getColumnByKey("level").setHeader("Seviye");
        grid.getColumnByKey("score").setHeader("Skor");
        grid.getColumnByKey("coinCount").setHeader("Altın");
        grid.getColumnByKey("playTime").setHeader("Oynama Süresi");
        grid.getColumnByKey("createdAt").setHeader("Zaman");

        // Silme Butonu Kolonu (Delete)
        grid.addComponentColumn(analytics -> {
            Button deleteButton = new Button(VaadinIcon.TRASH.create(), e -> deleteAnalytics(analytics));
            deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
            return deleteButton;
        }).setHeader("İşlemler");

        grid.setSizeFull();

        add(toolbar, grid);
        loadData();
    }

    // CREATE: Yeni analitik kayıt diyaloğu
    private void openAddDialog() {
        Dialog dialog = new Dialog();
        TextField playerIdField = new TextField("Oyuncu ID");
        TextField eventNameField = new TextField("Olay Adı (Event Name)");
        IntegerField levelField = new IntegerField("Seviye");
        IntegerField scoreField = new IntegerField("Skor");
        IntegerField coinField = new IntegerField("Altın");
        NumberField playTimeField = new NumberField("Oynama Süresi (sn)");

        Button saveButton = new Button("Kaydet", e -> {
            Analytics analytics = new Analytics();
            analytics.setPlayerId(playerIdField.getValue());
            analytics.setEventName(eventNameField.getValue());
            analytics.setLevel(levelField.getValue());
            analytics.setScore(scoreField.getValue());
            analytics.setCoinCount(coinField.getValue());
            analytics.setPlayTime(playTimeField.getValue());

            analyticsService.save(analytics);
            Notification.show("Analitik başarıyla eklendi!");
            dialog.close();
            loadData(); // REFRESH LIST
        });

        VerticalLayout dialogLayout = new VerticalLayout(
                playerIdField, eventNameField, levelField, scoreField, coinField, playTimeField, saveButton
        );
        dialog.add(dialogLayout);
        dialog.open();
    }

    // DELETE: Kayıt silme
    private void deleteAnalytics(Analytics analytics) {
        analyticsService.delete(analytics);
        Notification.show("Analitik kaydı silindi.");
        loadData(); // REFRESH LIST
    }

    // READ & REFRESH: Listeyi güncelleme
    private void loadData() {
        try {
            grid.setItems(analyticsService.getAllEvents());
        } catch (Exception e) {
            Notification.show("Analitik verileri yüklenirken hata: " + e.getMessage());
        }
    }
}