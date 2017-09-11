package com.example.demo.entity;

import java.io.Serializable;

/**
 * @author youn
 *
 */
public class KeyWord implements Serializable {

	private static final long serialVersionUID = 5519935520461369233L;

	private String value;
	private String position;
	private Double pow;

	public KeyWord() {
		super();
	}
	public KeyWord(String value, String position, Double pow) {
		super();
		this.value = value;
		this.position = position;
		this.pow = pow;
	}
	
	public Double getPow() {
		return pow;
	}
	public void setPow(Double pow) {
		this.pow = pow;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	
}
