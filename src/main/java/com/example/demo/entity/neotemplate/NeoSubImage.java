package com.example.demo.entity.neotemplate;

import java.io.Serializable;

public class NeoSubImage implements Serializable, Cloneable {

    private Integer width;
    private Integer height;
    private Float xy;
    private Float position;
    private String id;

    public NeoSubImage() {
        super();
    }
    public NeoSubImage(Integer width, Integer height, Float xy, Float position, String id) {
       super();
        this.width = width;
        this.height = height;
        this.xy = xy;
        this.position = position;
        this.id = id;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
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
    public Float getXy() {
        return xy;
    }
    public void setXy(Float xy) {
        this.xy = xy;
    }
    public Float getPosition() {
        return position;
    }
    public void setPosition(Float position) {
        this.position = position;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
}
