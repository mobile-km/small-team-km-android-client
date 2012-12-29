package com.teamkn.model;

import java.io.Serializable;

public class QRCodeResult implements Serializable{

	private static final long serialVersionUID = 1L;
	
	public String farmat;
	public String text;
	public QRCodeResult(String farmat, String text) {
		super();
		this.farmat = farmat;
		this.text = text;
	}		
}
