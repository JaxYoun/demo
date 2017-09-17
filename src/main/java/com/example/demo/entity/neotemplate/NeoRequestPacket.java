package com.example.demo.entity.neotemplate;

import java.io.Serializable;
import java.util.List;

public class NeoRequestPacket implements Serializable, Cloneable {

    private String type;
    private List<NeoRequestImage> imageList;

    public NeoRequestPacket() {
    }
    public NeoRequestPacket(String type, List<NeoRequestImage> imageList) {
        this.type = type;
        this.imageList = imageList;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public List<NeoRequestImage> getImageList() {
        return imageList;
    }
    public void setImageList(List<NeoRequestImage> imageList) {
        this.imageList = imageList;
    }
}
