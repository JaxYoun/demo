package com.example.demo.entity.font;

import java.io.Serializable;

public class FontParameter implements Serializable {

    /**
     * 原始字体库路径
     */
    private String font_ori_path;

    /**
     * 抽出字体路径
     */
    private String font_path;

    /**
     * 待抽取字串
     */
    private String extract_str;

    public FontParameter() {
        super();
    }

    public FontParameter(String font_ori_path, String font_path, String extract_str) {
        this.font_ori_path = font_ori_path;
        this.font_path = font_path;
        this.extract_str = extract_str;
    }

    public String getFont_ori_path() {
        return font_ori_path;
    }
    public void setFont_ori_path(String font_ori_path) {
        this.font_ori_path = font_ori_path;
    }
    public String getFont_path() {
        return font_path;
    }
    public void setFont_path(String font_path) {
        this.font_path = font_path;
    }
    public String getExtract_str() {
        return extract_str;
    }
    public void setExtract_str(String extract_str) {
        this.extract_str = extract_str;
    }
}
