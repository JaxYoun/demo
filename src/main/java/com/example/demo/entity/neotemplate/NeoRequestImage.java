package com.example.demo.entity.neotemplate;

import java.io.Serializable;
import java.util.List;

public class NeoRequestImage implements Serializable, Cloneable {

    private Float keywords_rate;
    private List<String> keyWordList;
    private List<Integer> pos;
    private Float pow;
    private Integer page_id;
    private String id;
    private Integer width;
    private Integer height;
    private List<List<Integer>> search_labels;
    private List<Integer> label;
    private List<Integer> position;

    public NeoRequestImage() {
        super();
    }
    public NeoRequestImage(Float keywords_rate, List<String> keyWordList, List<Integer> pos, Float pow, Integer page_id, String id, Integer width, Integer height, List<List<Integer>> search_labels, List<Integer> label, List<Integer> position) {
        super();
        this.keywords_rate = keywords_rate;
        this.keyWordList = keyWordList;
        this.pos = pos;
        this.pow = pow;
        this.page_id = page_id;
        this.id = id;
        this.width = width;
        this.height = height;
        this.search_labels = search_labels;
        this.label = label;
        this.position = position;
    }

    @Override
    public NeoRequestImage clone() throws CloneNotSupportedException {
        return (NeoRequestImage) super.clone();
    }

    public Float getKeywords_rate() {
        return keywords_rate;
    }
    public void setKeywords_rate(Float keywords_rate) {
        this.keywords_rate = keywords_rate;
    }
    public List<String> getKeyWordList() {
        return keyWordList;
    }
    public void setKeyWordList(List<String> keyWordList) {
        this.keyWordList = keyWordList;
    }
    public List<Integer> getPos() {
        return pos;
    }
    public void setPos(List<Integer> pos) {
        this.pos = pos;
    }
    public Float getPow() {
        return pow;
    }
    public void setPow(Float pow) {
        this.pow = pow;
    }
    public Integer getPage_id() {
        return page_id;
    }
    public void setPage_id(Integer page_id) {
        this.page_id = page_id;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
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
    public List<Integer> getLabel() {
        return label;
    }
    public void setLabel(List<Integer> label) {
        this.label = label;
    }
    public List<Integer> getPosition() {
        return position;
    }
    public void setPosition(List<Integer> position) {
        this.position = position;
    }
}
