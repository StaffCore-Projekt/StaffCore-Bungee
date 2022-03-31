package de.lacodev.staffbungee.enums;

public enum ReportStatus {

    CANCELLED(-1),
    CREATED(0),
    CLAIMED(1),
    CONFIRMED(2);

    int status;

    ReportStatus(int i) {
        status = i;
    }

    public int getStatus() {
        return status;
    }


}
