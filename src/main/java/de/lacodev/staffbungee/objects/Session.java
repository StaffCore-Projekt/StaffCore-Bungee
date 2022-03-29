package de.lacodev.staffbungee.objects;

import de.lacodev.staffbungee.Main;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Session {

    String uniqueid;
    long start;
    long end;
    String mcversion;
    String ip;
    String uuid;
    String virtual;

    public Session(String uniqueid, long start, long end) {
        this.uniqueid = uniqueid;
        this.start = start;
        this.end = end;
    }

    public Session(String uniqueid, long start, long end, String mcversion, String ip, String uuid, String virtual) {
        this.uniqueid = uniqueid;
        this.start = start;
        this.end = end;
        this.mcversion = mcversion;
        this.ip = ip;
        this.uuid = uuid;
        this.virtual = virtual;
    }

    /**
     * @return the uniqueid
     */
    public String getUniqueid() {
        return uniqueid;
    }

    /**
     * @param uniqueid the uniqueid to set
     */
    public void setUniqueid(String uniqueid) {
        this.uniqueid = uniqueid;
    }

    /**
     * @return the start
     */
    public long getStart() {
        return start;
    }

    /**
     * @param start the start to set
     */
    public void setStart(long start) {
        this.start = start;
    }

    /**
     * @return the end
     */
    public long getEnd() {
        return end;
    }

    /**
     * @param end the end to set
     */
    public void setEnd(long end) {
        this.end = end;
    }

    public String getMcversion() {
        return mcversion;
    }

    public void setMcversion(String mcversion) {
        this.mcversion = mcversion;
    }

    public String getIp(ProxiedPlayer player) {
        if (player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) ||
            player.hasPermission(Main.getPermissionNotice("Permissions.System.Show-IP"))) {
            return ip;
        } else {
            return "§kUnknown";
        }
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUuid() {
        return uuid;
    }

    public String getVirtualHost() {
        return virtual;
    }

}
