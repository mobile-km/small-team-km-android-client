package com.teamkn.model;

import java.io.Serializable;

public class QRCodeResult implements Serializable{

	private static final long serialVersionUID = 1L;
	
	public Class<?> result_activity;
	public String farmat;
	public String text;
	public QRCodeResult(String farmat, String text) {
		super();
		this.farmat = farmat;
		this.text = text;
	}
	public QRCodeResult(Class<?> result_activity) {
		super();
		this.result_activity = result_activity;
	}	
}
