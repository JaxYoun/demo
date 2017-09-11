package com.example.demo.entity.template;

import com.example.demo.entity.KeyWord;

import java.io.Serializable;
import java.util.List;

public class TemplateImage implements Serializable {

	private static final long serialVersionUID = 4681477076837866846L;

	private String id;
	private String backId;
	private Double keywords_rate;
	private List<KeyWord> keyWordList;
	private String page_id;
	private Integer width;
	private Integer height;
	private List<List<Integer>> search_labels;
	private List<Double> position;
	private List<Double> label;
	private String subImagePath;

	public TemplateImage() {
		super();
	}
	public TemplateImage(String id, String backId, Double keywords_rate, List<KeyWord> keyWordList, String page_id, Integer width,
			Integer height, List<List<Integer>> search_labels, List<Double> position, List<Double> label, String subImagePath) {
		super();
		this.id = id;
		this.backId = backId;
		this.keywords_rate = keywords_rate;
		this.keyWordList = keyWordList;
		this.page_id = page_id;
		this.width = width;
		this.height = height;
		this.search_labels = search_labels;
		this.position = position;
		this.label = label;
		this.subImagePath = subImagePath;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Double getKeywords_rate() {
		return keywords_rate;
	}
	public void setKeywords_rate(Double keywords_rate) {
		this.keywords_rate = keywords_rate;
	}
	public List<KeyWord> getKeyWordList() {
		return keyWordList;
	}
	public void setKeyWordList(List<KeyWord> keyWordList) {
		this.keyWordList = keyWordList;
	}
	public String getPage_id() {
		return page_id;
	}
	public void setPage_id(String page_id) {
		this.page_id = page_id;
	}
	public Integer getWidth() {
		return width;
	}
	public void setWidth(Integer width) {
		this.width = width;
	}
	public Integer getHeight() {
		return height;
	}
	public void setHeight(Integer height) {
		this.height = height;
	}
	public List<List<Integer>> getSearch_labels() {
		return search_labels;
	}
	public void setSearch_labels(List<List<Integer>> search_labels) {
		this.search_labels = search_labels;
	}
	public List<Double> getPosition() {
		return position;
	}
	public void setPosition(List<Double> position) {
		this.position = position;
	}
	public List<Double> getLabel() {
		return label;
	}
	public void setLabel(List<Double> label) {
		this.label = label;
	}
	public String getBackId() {
		return backId;
	}
	public void setBackId(String backId) {
		this.backId = backId;
	}
	public String getSubImagePath() {
		return subImagePath;
	}
	public void setSubImagePath(String subImagePath) {
		this.subImagePath = subImagePath;
	}

}
