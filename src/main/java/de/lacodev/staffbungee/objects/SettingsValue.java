package de.lacodev.staffbungee.objects;

public class SettingsValue {

    private String value;
    private long expires;

    public SettingsValue(String value) {
        this.value = value;
        this.expires = System.currentTimeMillis() + 1000 * 30;
    }

    public String getValue() {
        return value;
    }

    public long getExpires() {
        return expires;
    }
}
