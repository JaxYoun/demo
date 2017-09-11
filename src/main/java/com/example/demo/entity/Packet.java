package com.example.demo.entity;

import java.io.Serializable;
import java.util.List;

/**
 * @author youn
 *
 */
public class Packet implements Serializable {

	private static final long serialVersionUID = 7417298750591294338L;

	private String type;
	private Integer num;
	private List<KeyWord> keywordList;
	private List<Image> imageList;
	
	public Packet() {
		super();
	}
	public Packet(String type, Integer num, List<KeyWord> keywordList, List<Image> imageList) {
		super();
		this.type = type;
		this.num = num;
		this.keywordList = keywordList;
		this.imageList = imageList;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Integer getNum() {
		return num;
	}
	public void setNum(Integer num) {
		this.num = num;
	}
	public List<KeyWord> getKeywordList() {
		return keywordList;
	}
	public void setKeywordList(List<KeyWord> keywordList) {
		this.keywordList = keywordList;
	}
	public List<Image> getImageList() {
		return imageList;
	}
	public void setImageList(List<Image> imageList) {
		this.imageList = imageList;
	}
	
}
