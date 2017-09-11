package com.example.demo.entity.template;

import java.io.Serializable;
import java.util.List;

public class Template implements Serializable {

	private static final long serialVersionUID = 3665598512879992699L;

	private List<TemplateImage> imageList;

	public Template() {
		super();
	}
	public Template(List<TemplateImage> imageList) {
		super();
		this.imageList = imageList;
	}
	
	public List<TemplateImage> getImageList() {
		return imageList;
	}
	public void setImageList(List<TemplateImage> imageList) {
		this.imageList = imageList;
	}
	
}
