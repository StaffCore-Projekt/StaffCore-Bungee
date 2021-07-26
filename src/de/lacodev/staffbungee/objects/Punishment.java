package de.lacodev.staffbungee.objects;

import de.lacodev.staffbungee.enums.PunishmentType;
import de.lacodev.staffbungee.managers.PlayerManager;

public class Punishment {

	int id;
	PunishmentType type;
	String reason;
	String punished;
	String punisher;
	long punishment_start;
	long punishment_end;
	String server;
	
	public Punishment(int id, PunishmentType type, String reason, String punished, String punisher, long punishment_start, long punishment_end, String sub_server) {
		this.id = id;
		this.type = type;
		this.reason = reason;
		this.punished = punished;
		this.punisher = punisher;
		this.punishment_start = punishment_start;
		this.punishment_end = punishment_end;
		this.server = sub_server;
	}

	public PunishmentType getType() {
		return type;
	}

	public void setType(PunishmentType type) {
		this.type = type;
	}

	public String getReason() {
		if(reason != null) {
			return reason;
		} else {
			return type.toString();
		}
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
	
	public String getPunished() {
		return PlayerManager.getUsernamebyUUID(punished);
	}

	public String getPunisher() {
		if(punisher == "Console") {
			return "Console";
		} else {
			return PlayerManager.getUsernamebyUUID(punisher);
		}
	}

	public void setPunisher(String punisher) {
		this.punisher = punisher;
	}

	public long getPunishment_start() {
		return punishment_start;
	}

	public void setPunishment_start(long punishment_start) {
		this.punishment_start = punishment_start;
	}

	public long getPunishment_end() {
		return punishment_end;
	}

	public void setPunishment_end(long punishment_end) {
		this.punishment_end = punishment_end;
	}

	public int getId() {
		return id;
	}

	public String getServer() {
		return server;
	}
	
	
}
