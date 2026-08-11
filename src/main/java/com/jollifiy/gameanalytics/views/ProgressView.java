package com.jollifiy.gameanalytics.views;

import com.jollifiy.gameanalytics.entity.Progress;
import com.jollifiy.gameanalytics.service.ProgressService;
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
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.annotation.security.PermitAll;

@PageTitle("Oyuncu İlerleme Durumları | Jollify Game Analytics")
@Route(value = "progress", layout = MainLayout.class)
@PermitAll // <-- Ekle
public class ProgressView extends VerticalLayout {

    private final ProgressService progressService;
    private final Grid<Progress> grid = new Grid<>(Progress.class);

    @Autowired
    public ProgressView(ProgressService progressService) {
        this.progressService = progressService;

        setSizeFull();
        setPadding(true);

        add(new H2("Oyuncu İlerleme Durumları"));

        // 1. Üst Kontrol Paneli (Create + Refresh)
        Button addButton = new Button("Yeni İlerleme Ekle", VaadinIcon.PLUS.create(), e -> openAddDialog());
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button refreshButton = new Button("Yenile", VaadinIcon.REFRESH.create(), e -> loadData());

        HorizontalLayout toolbar = new HorizontalLayout(addButton, refreshButton);
        toolbar.setWidthFull();

        // 2. Grid Yapısı (Read)
        grid.setColumns("id", "playerId", "currentLevel", "totalCoins", "updatedAt");

        // Kolon başlıklarını Türkçeleştirelim
        grid.getColumnByKey("id").setHeader("ID");
        grid.getColumnByKey("playerId").setHeader("Oyuncu ID");
        grid.getColumnByKey("currentLevel").setHeader("Mevcut Seviye");
        grid.getColumnByKey("totalCoins").setHeader("Toplam Altın");
        grid.getColumnByKey("updatedAt").setHeader("Son Güncelleme");

        // Silme Butonu Kolonu (Delete)
        grid.addComponentColumn(progress -> {
            Button deleteButton = new Button(VaadinIcon.TRASH.create(), e -> deleteProgress(progress));
            deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
            return deleteButton;
        }).setHeader("İşlemler");

        grid.setSizeFull();

        add(toolbar, grid);
        loadData();
    }

    // CREATE: Yeni ilerleme ekleme diyaloğu
    private void openAddDialog() {
        Dialog dialog = new Dialog();
        TextField playerIdField = new TextField("Oyuncu ID");
        IntegerField levelField = new IntegerField("Mevcut Seviye");
        IntegerField coinsField = new IntegerField("Toplam Altın");

        Button saveButton = new Button("Kaydet", e -> {
            Progress progress = new Progress();
            progress.setPlayerId(playerIdField.getValue());
            progress.setCurrentLevel(levelField.getValue() != null ? levelField.getValue() : 1);
            progress.setTotalCoins(coinsField.getValue() != null ? coinsField.getValue() : 0);

            progressService.save(progress);
            Notification.show("İlerleme başarıyla eklendi!");
            dialog.close();
            loadData(); // REFRESH LIST
        });

        VerticalLayout dialogLayout = new VerticalLayout(
                playerIdField, levelField, coinsField, saveButton
        );
        dialog.add(dialogLayout);
        dialog.open();
    }

    // DELETE: Kayıt silme
    private void deleteProgress(Progress progress) {
        progressService.delete(progress);
        Notification.show("İlerleme kaydı silindi.");
        loadData(); // REFRESH LIST
    }

    // READ & REFRESH: Listeyi güncelleme
    private void loadData() {
        try {
            grid.setItems(progressService.findAll());
        } catch (Exception e) {
            Notification.show("İlerleme verileri yüklenirken hata: " + e.getMessage());
        }
    }
}