package com.example.demo.entity.template;

import java.io.Serializable;
import java.util.List;

public class TemplatePacket implements Serializable, Cloneable {

	private static final long serialVersionUID = -6234063910939913921L;
	
	private String type;
	private List<Template> tmp_list;
	
	public TemplatePacket() {
		super();
	}
	public TemplatePacket(String type, List<Template> tmp_list) {
		super();
		this.type = type;
		this.tmp_list = tmp_list;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public List<Template> getTmp_list() {
		return tmp_list;
	}
	public void setTmp_list(List<Template> tmp_list) {
		this.tmp_list = tmp_list;
	}
	
	@Override
	public TemplatePacket clone() throws CloneNotSupportedException {
		return (TemplatePacket) super.clone();
	}
	
	public static void main(String[] args) {
		TemplatePacket t = new TemplatePacket();
	}
}
