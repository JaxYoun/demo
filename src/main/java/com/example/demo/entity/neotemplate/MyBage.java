package com.example.demo.entity.neotemplate;

import java.io.Serializable;

/**
 * 用于封装关键字匹配过程中计算所得分数与redis大图的对应关系
 */
public class MyBage implements Serializable {

    private String id;
    private Float score;
    private NeoRedisParentImage neoRedisParentImage;


    public MyBage() {
        super();
    }
    public MyBage(String id, Float score, NeoRedisParentImage neoRedisParentImage) {
        super();
        this.id = id;
        this.score = score;
        this.neoRedisParentImage = neoRedisParentImage;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public Float getScore() {
        return score;
    }
    public void setScore(Float score) {
        this.score = score;
    }
    public NeoRedisParentImage getNeoRedisParentImage() {
        return neoRedisParentImage;
    }
    public void setNeoRedisParentImage(NeoRedisParentImage neoRedisParentImage) {
        this.neoRedisParentImage = neoRedisParentImage;
    }
}
