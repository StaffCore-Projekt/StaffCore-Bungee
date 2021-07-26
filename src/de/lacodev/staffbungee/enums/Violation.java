package de.lacodev.staffbungee.enums;

public enum Violation {
	
	CONFIRMED_REPORT(3),WARN(5),KICK(7),MUTE(14),BAN(21),SILENT_MUTE(28),SILENT_BAN(35),IP_BAN(60);
	
	int vl;
	
	private Violation(int vl) {
		this.vl = vl;
	}

	public int getVL() {
		return vl;
	}
	
}
