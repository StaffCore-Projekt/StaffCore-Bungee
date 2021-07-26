package de.lacodev.staffbungee.objects;

import de.lacodev.staffbungee.enums.ReasonType;

public class Reason {

	private ReasonType type;
	private Integer id;
	private String name;
	private Long length;
	private boolean admin;
	
	public Reason(ReasonType type, Integer id, String name, Long length, boolean admin) {
		this.type = type;
		this.id = id;
		this.name = name;
		this.length = length;
		this.admin = admin;
	}
	
	public ReasonType getType() {
		return type;
	}

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Long getLength() {
		return length;
	}
	
	public boolean isAdmin() {
		return admin;
	}
	
}
