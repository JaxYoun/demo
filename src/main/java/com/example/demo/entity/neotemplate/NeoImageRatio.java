package com.example.demo.entity.neotemplate;

import java.io.Serializable;

public class NeoImageRatio implements Serializable {

    private String id;
    private Float ratio;

    public NeoImageRatio() {
        super();
    }
    public NeoImageRatio(String id, Float ratio) {
        super();
        this.id = id;
        this.ratio = ratio;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public Float getRatio() {
        return ratio;
    }
    public void setRatio(Float ratio) {
        this.ratio = ratio;
    }
}
