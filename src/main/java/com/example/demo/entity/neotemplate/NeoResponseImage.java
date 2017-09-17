package com.example.demo.entity.neotemplate;

import java.io.Serializable;

public class NeoResponseImage implements Serializable, Cloneable {

    private String img_id;
    private String small_id;
    private Integer price;
    private Integer page_id;
    private String el_id;
    private Integer width;
    private Integer height;

    public NeoResponseImage() {
        super();
    }
    public NeoResponseImage(String img_id, String small_id, Integer price, Integer page_id, String el_id, Integer width, Integer height) {
        super();
        this.img_id = img_id;
        this.small_id = small_id;
        this.price = price;
        this.page_id = page_id;
        this.el_id = el_id;
        this.width = width;
        this.height = height;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public String getImg_id() {
        return img_id;
    }
    public void setImg_id(String img_id) {
        this.img_id = img_id;
    }
    public String getSmall_id() {
        return small_id;
    }
    public void setSmall_id(String small_id) {
        this.small_id = small_id;
    }
    public Integer getPrice() {
        return price;
    }
    public void setPrice(Integer price) {
        this.price = price;
    }
    public Integer getPage_id() {
        return page_id;
    }
    public void setPage_id(Integer page_id) {
        this.page_id = page_id;
    }
    public String getEl_id() {
        return el_id;
    }
    public void setEl_id(String el_id) {
        this.el_id = el_id;
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
}
