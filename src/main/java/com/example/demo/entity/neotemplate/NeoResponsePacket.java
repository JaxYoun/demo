package com.example.demo.entity.neotemplate;

import java.io.Serializable;
import java.util.List;

public class NeoResponsePacket implements Serializable, Cloneable {

    private String type;
    private List<NeoResponseImage> imageList;

    public NeoResponsePacket() {
        super();
    }
    public NeoResponsePacket(String type, List<NeoResponseImage> imageList) {
        super();
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
    public List<NeoResponseImage> getImageList() {
        return imageList;
    }
    public void setImageList(List<NeoResponseImage> imageList) {
        this.imageList = imageList;
    }
}
