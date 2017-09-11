package com.example.demo.entity.template;

import java.io.Serializable;

public class SubImage implements Serializable {
	
	private static final long serialVersionUID = 7790806821173200137L;
	
	private Double width;
	private Double height;
	private Double xy;
	private Integer position;
	private String path;
	
	public SubImage() {
		super();
	}
	public SubImage(Double width, Double height, Double xy, Integer position, String path) {
		super();
		this.width = width;
		this.height = height;
		this.xy = xy;
		this.position = position;
		this.path = path;
	}
	
	public Double getWidth() {
		return width;
	}
	public void setWidth(Double width) {
		this.width = width;
	}
	public Double getHeight() {
		return height;
	}
	public void setHeight(Double height) {
		this.height = height;
	}
	public Double getXy() {
		return xy;
	}
	public void setXy(Double xy) {
		this.xy = xy;
	}
	public Integer getPosition() {
		return position;
	}
	public void setPosition(Integer position) {
		this.position = position;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	
}
