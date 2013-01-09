package com.teamkn.model;

import java.io.Serializable;

public class QRCodeResult implements Serializable{

	private static final long serialVersionUID = 1L;
	
	public Class<?> from_activity;
	public Class<?> result_activity;
	public int farmat;
	public String code;
//	public QRCodeResult(String farmat, String text) {
//		super();
//		this.farmat = farmat;
//		this.text = text;
//	}
	
	
	public QRCodeResult(int farmat, String code) {
		super();
		this.farmat = farmat;
		this.code = code;
	}
	public QRCodeResult(Class<?> from_activity, Class<?> result_activity) {
		super();
		this.from_activity = from_activity;
		this.result_activity = result_activity;
	}
	public QRCodeResult(Class<?> result_activity) {
		super();
		this.result_activity = result_activity;
	}
}
