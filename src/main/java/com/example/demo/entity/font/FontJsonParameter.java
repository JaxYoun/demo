package com.example.demo.entity.font;

import java.io.Serializable;
import java.util.List;

public class FontJsonParameter implements Serializable {

    private List<FontParameter> font_info;

    public FontJsonParameter() {
        super();
    }
    public FontJsonParameter(List<FontParameter> font_info) {
        this.font_info = font_info;
    }

    public List<FontParameter> getFont_info() {
        return font_info;
    }

    public void setFont_info(List<FontParameter> font_info) {
        this.font_info = font_info;
    }
}
