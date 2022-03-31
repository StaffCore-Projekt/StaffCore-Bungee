package de.lacodev.staffbungee.handlers;

import de.lacodev.staffbungee.enums.Settings;
import de.lacodev.staffbungee.managers.SettingsManager;

public class MaintenanceHandler {

    public MaintenanceHandler() {
        super();
    }

    public boolean isMaintenance() {
        return Boolean.valueOf(SettingsManager.getValue(Settings.MAINTENANCE_ENABLE));
    }

}
