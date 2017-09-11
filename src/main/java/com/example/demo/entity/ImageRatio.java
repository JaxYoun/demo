package com.example.demo.entity;

import java.io.Serializable;

/**
 * @author youn
 *
 */
public class ImageRatio implements Serializable {

	private static final long serialVersionUID = -6787691799926872488L;
	
	private String id;
	private Double ratio;
	
	public ImageRatio() {
		super();
	}
	public ImageRatio(String id, Double ratio) {
		super();
		this.id = id;
		this.ratio = ratio;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Double getRatio() {
		return ratio;
	}
	public void setRatio(Double ratio) {
		this.ratio = ratio;
	}
	
}
