package com.example.demo.entity;

import com.example.demo.entity.template.SubImage;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author youn
 *
 */
public class Image implements Serializable, Cloneable {

	private static final long serialVersionUID = -5072236791880950876L;
	
	private String id;
	private Double weight;
	private List<KeyWord> keyWordList;
	
	private Double keywords_rate;
	
	private List<SubImage> img_info;
	private List<Integer> label_info;

	private Integer price;
	
	@Override
	public Image clone() throws CloneNotSupportedException {
		return (Image) super.clone();
	}
	
	public Image() {
		super();
	}
	public Image(String id, Double weight, List<KeyWord> keyWordList, Double keywords_rate, List<SubImage> img_info, List<Integer> label_info, Integer price) {
		super();
		this.id = id;
		this.weight = weight;
		this.keyWordList = keyWordList;
		this.img_info = img_info;
		this.label_info = label_info;
		this.keywords_rate = keywords_rate;
		this.price = price;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Double getWeight() {
		return weight;
	}
	public void setWeight(Double weight) {
		this.weight = weight;
	}
	public List<KeyWord> getKeyWordList() {
		return keyWordList;
	}
	public void setKeyWordList(List<KeyWord> keyWordList) {
		this.keyWordList = keyWordList;
	}
	public List<SubImage> getImg_info() {
		return img_info;
	}
	public void setImg_info(List<SubImage> img_info) {
		this.img_info = img_info;
	}
	public List<Integer> getLabel_info() {
		return label_info;
	}
	public void setLabel_info(List<Integer> label_info) {
		this.label_info = label_info;
	}
	public static void main(String[] args) {
		Image k = new Image();
	}
	public Double getKeywords_rate() {
		return keywords_rate;
	}
	public void setKeywords_rate(Double keywords_rate) {
		this.keywords_rate = keywords_rate;
	}
	public Integer getPrice() {
		return price;
	}
	public void setPrice(Integer price) {
		this.price = price;
	}
}
