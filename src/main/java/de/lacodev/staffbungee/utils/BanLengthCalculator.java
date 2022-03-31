package de.lacodev.staffbungee.utils;

import de.lacodev.staffbungee.Main;

public class BanLengthCalculator {

    public static String calculate(long reasonlength) {

        long millis = reasonlength;

        if (reasonlength != -1) {

            long sekunden = 0L;
            long minuten = 0L;
            long stunden = 0L;
            long tage = 0L;

            while (millis >= 1000L) {
                millis -= 1000L;
                sekunden += 1L;
            }
            while (sekunden >= 60L) {
                sekunden -= 60L;
                minuten += 1L;
            }
            while (minuten >= 60L) {
                minuten -= 60L;
                stunden += 1L;
            }
            while (stunden >= 24L) {
                stunden -= 24L;
                tage += 1L;
            }

            String strtage = String.valueOf(tage);
            String strstunden = String.valueOf(stunden);
            String strminuten = String.valueOf(minuten);
            String strsekunden = String.valueOf(sekunden);

            if (tage < 10) {
                strtage = "0" + String.valueOf(tage);
            }

            if (stunden < 10) {
                strstunden = "0" + String.valueOf(stunden);
            }

            if (minuten < 10) {
                strminuten = "0" + String.valueOf(minuten);
            }

            if (sekunden < 10) {
                strsekunden = "0" + String.valueOf(sekunden);
            }

            if (tage != 0L) {
                return "§a" + strtage + "§7 " + Main.getMSG("Messages.Layouts.Ban.Remaining.Days") + " §a" + strstunden + "§7 " +
                    Main.getMSG("Messages.Layouts.Ban.Remaining.Hours") + " §a" + strminuten + "§7 " +
                    Main.getMSG("Messages.Layouts.Ban.Remaining.Minutes");
            }
            if ((tage == 0L) && (stunden != 0L)) {
                return "§a" + strstunden + "§7 " + Main.getMSG("Messages.Layouts.Ban.Remaining.Hours") + " §a" + strminuten + "§7 " +
                    Main.getMSG("Messages.Layouts.Ban.Remaining.Minutes") + " §a" + strsekunden + "§7 " +
                    Main.getMSG("Messages.Layouts.Ban.Remaining.Seconds");
            }
            if ((tage == 0L) && (stunden == 0L) && (minuten != 0L)) {
                return "§a" + strminuten + "§7 " + Main.getMSG("Messages.Layouts.Ban.Remaining.Minutes") + " §a" + strsekunden + " §7" +
                    Main.getMSG("Messages.Layouts.Ban.Remaining.Seconds");
            }
            if ((tage == 0L) && (stunden == 0L) && (minuten == 0L) && (sekunden != 0L)) {
                return "§a" + strsekunden + " §7" + Main.getMSG("Messages.Layouts.Ban.Remaining.Seconds");
            }
            return "§a" + strsekunden + " §7" + Main.getMSG("Messages.Layouts.Ban.Remaining.Seconds");
        } else {
            return Main.getMSG("Messages.Layouts.Ban.Length-Values.Permanently");
        }

    }

}
