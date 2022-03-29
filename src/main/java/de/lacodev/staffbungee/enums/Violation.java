package de.lacodev.staffbungee.enums;

import de.lacodev.staffbungee.Main;

public enum Violation {

    CONFIRMED_REPORT, WARN, KICK, MUTE, BAN, SILENT_MUTE, SILENT_BAN, IP_BAN;

    public int getVL(Violation vl) {
        return Main.getInstance().getConfig().getInt("ViolationLevelSystem.Adding-Points." + vl.toString());
    }

}
