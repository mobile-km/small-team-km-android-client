package com.teamkn.model;

public class IsShow {
    private boolean isShow_value;
    private int x;
    private int y;
	public boolean isShow() {
		return isShow_value;
	}

	public void setShow(boolean isShow_value) {
		this.isShow_value = isShow_value;
	}

	public int getX() {
		return x;
	}

	public boolean isShow_value() {
		return isShow_value;
	}

	public void setShow_value(boolean isShow_value) {
		this.isShow_value = isShow_value;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public IsShow(int x, int y) {
		super();
		this.x = x;
		this.y = y;
	}
	public IsShow(){};
    
}
