package com.jollifiy.gameanalytics.views;

import com.jollifiy.gameanalytics.entity.AppConfig;
import com.jollifiy.gameanalytics.service.ConfigService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.annotation.security.PermitAll;

@PageTitle("Uygulama Konfigürasyonları | Jollify Game Analytics")
@Route(value = "config", layout = MainLayout.class)
@PermitAll // <-- Ekle
public class ConfigView extends VerticalLayout {
    // Mevcut kodların aynen kalacak...

    private final ConfigService configService;
    private final Grid<AppConfig> grid = new Grid<>(AppConfig.class);

    @Autowired
    public ConfigView(ConfigService configService) {
        this.configService = configService;

        setSizeFull();
        setPadding(true);

        add(new H2("Uygulama Konfigürasyonları"));

        // 1. Üst Kontrol Paneli (Create + Refresh)
        Button addButton = new Button("Yeni Konfigürasyon Ekle", VaadinIcon.PLUS.create(), e -> openAddDialog());
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button refreshButton = new Button("Yenile", VaadinIcon.REFRESH.create(), e -> loadData());

        HorizontalLayout toolbar = new HorizontalLayout(addButton, refreshButton);
        toolbar.setWidthFull();

        // 2. Grid Yapısı (Read)
        grid.setColumns("id", "configKey", "configValue");

        // Kolon başlıklarını Türkçeleştirelim
        grid.getColumnByKey("id").setHeader("ID");
        grid.getColumnByKey("configKey").setHeader("Ayar Anahtarı (Key)");
        grid.getColumnByKey("configValue").setHeader("Ayar Değeri (Value)");

        // Silme Butonu Kolonu (Delete)
        grid.addComponentColumn(config -> {
            Button deleteButton = new Button(VaadinIcon.TRASH.create(), e -> deleteConfig(config));
            deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
            return deleteButton;
        }).setHeader("İşlemler");

        grid.setSizeFull();

        add(toolbar, grid);
        loadData();
    }

    // CREATE: Yeni konfigürasyon ekleme diyaloğu
    private void openAddDialog() {
        Dialog dialog = new Dialog();
        TextField keyField = new TextField("Ayar Anahtarı (Key, örn: MIN_APP_VERSION)");
        TextField valueField = new TextField("Ayar Değeri (Value, örn: 1.0.0)");

        Button saveButton = new Button("Kaydet", e -> {
            AppConfig config = new AppConfig();
            config.setConfigKey(keyField.getValue());
            config.setConfigValue(valueField.getValue());

            configService.save(config);
            Notification.show("Konfigürasyon başarıyla eklendi!");
            dialog.close();
            loadData(); // REFRESH LIST
        });

        VerticalLayout dialogLayout = new VerticalLayout(keyField, valueField, saveButton);
        dialog.add(dialogLayout);
        dialog.open();
    }

    // DELETE: Kayıt silme
    private void deleteConfig(AppConfig config) {
        configService.delete(config);
        Notification.show("Konfigürasyon silindi.");
        loadData(); // REFRESH LIST
    }

    // READ & REFRESH: Listeyi güncelleme
    private void loadData() {
        try {
            grid.setItems(configService.findAll());
        } catch (Exception e) {
            Notification.show("Konfigürasyon verileri yüklenirken hata: " + e.getMessage());
        }
    }
}