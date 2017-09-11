package com.example.demo.service;

import com.example.demo.entity.font.FontJsonParameter;

import java.io.IOException;
import java.util.Map;

public interface IFontService {

    void extractFont(FontJsonParameter fontJsonParameter, String option, Map<String, Object> resultMap) throws IOException;

}
