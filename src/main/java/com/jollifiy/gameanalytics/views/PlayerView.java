package com.jollifiy.gameanalytics.views;

import com.jollifiy.gameanalytics.entity.Player;
import com.jollifiy.gameanalytics.service.PlayerService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.PermitAll;

@PageTitle("Oyuncular | Jollify Game Analytics")
@Route(value = "players", layout = MainLayout.class)
@PermitAll // <-- Ekle
public class PlayerView extends VerticalLayout {

    private final PlayerService playerService;
    private final Grid<Player> grid = new Grid<>(Player.class);

    public PlayerView(PlayerService playerService) {
        this.playerService = playerService;
        setSizeFull();

        // 1. Üst Kontrol Paneli (Sadece Yenile - Ekleme butonu kaldırıldı)
        Button refreshButton = new Button("Yenile", VaadinIcon.REFRESH.create(), e -> refreshGrid());

        HorizontalLayout toolbar = new HorizontalLayout(refreshButton);
        toolbar.setWidthFull();

        // 2. Grid Yapısı (Read)
        grid.setColumns("id", "playerId", "deviceId", "country", "createdAt");

        // Sadece Silme işlemi (Delete)
        grid.addComponentColumn(player -> {
            Button deleteButton = new Button(VaadinIcon.TRASH.create(), e -> deletePlayer(player));
            deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
            return deleteButton;
        }).setHeader("İşlemler");

        add(toolbar, grid);
        refreshGrid();
    }

    // DELETE: Oyuncu silme
    private void deletePlayer(Player player) {
        playerService.delete(player);
        Notification.show("Oyuncu silindi.");
        refreshGrid(); // REFRESH
    }

    // READ & REFRESH: Listeyi güncelleme
    private void refreshGrid() {
        grid.setItems(playerService.findAll());
    }
}