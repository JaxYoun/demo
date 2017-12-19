package com.example.demo.service;

import com.example.demo.entity.font.FontJsonParameter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface IFontService {

    List<String> extractFont(FontJsonParameter fontJsonParameter, String option, Map<String, Object> resultMap) throws IOException;

}
