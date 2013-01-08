package com.teamkn.model;

import java.io.Serializable;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

//"server_created_time":1357527393,
//"name":"\u534e\u8054\u4fdd\u9c9c\u819c",
//"unit":"\u5377",
//"server_updated_time":1357527427,
//"origin":"\u4e0a\u6d77\r",
//"vendor":null,
//"id":4,
//"kind":"28\u5398\u7c73*20",
//"code":"6900000000038"
public class Product implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Expose
	public int id ;
	@Expose
	public String name;
	@Expose
	public String unit;
	@Expose
	public String origin;
	@Expose
	public String vendor;
	@Expose
	public String kind;
	@Expose
	public String code;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getOrigin() {
		return origin;
	}
	public void setOrigin(String origin) {
		this.origin = origin;
	}
	public String getVendor() {
		return vendor;
	}
	public void setVendor(String vendor) {
		this.vendor = vendor;
	}
	public String getKind() {
		return kind;
	}
	public void setKind(String kind) {
		this.kind = kind;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	@Override
	public String toString() {
		return name+ " : " + kind + " : " + code;
	}
	public static Product get_product_by_json(JSONObject product_json){
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		Product item= gson.fromJson(product_json.toString(), Product.class);
		return item;
	}
	
	
}
