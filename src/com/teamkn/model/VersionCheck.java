package com.teamkn.model;

public class VersionCheck {
	public static final class Action{
		public static final String NEWEST = "NEWEST";
		public static final String UPDATE = "UPDATE";
		public static final String EXPIRED = "EXPIRED";
	}
	public int id;
	public String action ;
	public String version ;
	public String change_log ;
	
	public static VersionCheck VERSIONCHECK = new VersionCheck();
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getChange_log() {
		return change_log;
	}
	public void setChange_log(String change_log) {
		this.change_log = change_log;
	}
	
	public VersionCheck() {
		super();
	}
	public VersionCheck(String action, String version, String change_log) {
		super();
		this.action = action;
		this.version = version;
		this.change_log = change_log;
	}
	public VersionCheck(int id, String action, String version, String change_log) {
		super();
		this.id = id;
		this.action = action;
		this.version = version;
		this.change_log = change_log;
	}
	
}
