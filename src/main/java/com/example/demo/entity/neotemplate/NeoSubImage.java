package com.example.demo.entity.neotemplate;

import java.io.Serializable;

public class NeoSubImage implements Serializable, Cloneable {

    private Integer weight;
    private Integer height;
    private Float xy;
    private Integer position;
    private String id;

    public NeoSubImage() {
        super();
    }
    public NeoSubImage(Integer weight, Integer height, Float xy, Integer position, String id) {
       super();
        this.weight = weight;
        this.height = height;
        this.xy = xy;
        this.position = position;
        this.id = id;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public Integer getWeight() {
        return weight;
    }
    public void setWeight(Integer weight) {
        this.weight = weight;
    }
    public Integer getHeight() {
        return height;
    }
    public void setHeight(Integer height) {
        this.height = height;
    }
    public Float getXy() {
        return xy;
    }
    public void setXy(Float xy) {
        this.xy = xy;
    }
    public Integer getPosition() {
        return position;
    }
    public void setPosition(Integer position) {
        this.position = position;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
}
