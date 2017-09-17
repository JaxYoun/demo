package com.example.demo.entity.neotemplate;

import java.io.Serializable;
import java.util.List;

public class NeoRedisParentImage implements Serializable, Cloneable {

    private String id;
    private List<String> keyWordList;
    private List<Integer> station;
    private Float weight;
    private List<NeoSubImage> img_info;
    private List<Integer> label_info;
    private Integer price;

    public NeoRedisParentImage() {
        super();
    }

    public NeoRedisParentImage(String id, List<String> keyWordList, List<Integer> station, Float weight, List<NeoSubImage> img_info, List<Integer> label_info, Integer price) {
        super();
        this.id = id;
        this.keyWordList = keyWordList;
        this.station = station;
        this.weight = weight;
        this.img_info = img_info;
        this.label_info = label_info;
        this.price = price;
    }

    @Override
    public NeoRedisParentImage clone() throws CloneNotSupportedException {
        return (NeoRedisParentImage) super.clone();
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public List<String> getKeyWordList() {
        return keyWordList;
    }
    public void setKeyWordList(List<String> keyWordList) {
        this.keyWordList = keyWordList;
    }
    public List<Integer> getStation() {
        return station;
    }
    public void setStation(List<Integer> station) {
        this.station = station;
    }
    public Float getWeight() {
        return weight;
    }
    public void setWeight(Float weight) {
        this.weight = weight;
    }
    public List<NeoSubImage> getImg_info() {
        return img_info;
    }
    public void setImg_info(List<NeoSubImage> img_info) {
        this.img_info = img_info;
    }
    public List<Integer> getLabel_info() {
        return label_info;
    }
    public void setLabel_info(List<Integer> label_info) {
        this.label_info = label_info;
    }
    public Integer getPrice() {
        return price;
    }
    public void setPrice(Integer price) {
        this.price = price;
    }
}
