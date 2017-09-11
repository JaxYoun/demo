package com.example.demo.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public class Util {
	public static void main(String[] args) {
		ObjectMapper jacksonMapper = new ObjectMapper();
		
		KeyWord k11 = new KeyWord("中文", "12-33-40", 1.1D);
		KeyWord k12 = new KeyWord("英文", "12-33-42", 1.2D);
		KeyWord k13 = new KeyWord("日文", "12-33-41", 1.5D);
		
		List<KeyWord> languageWordList = new ArrayList<KeyWord>();
		languageWordList.add(k11);
		languageWordList.add(k12);
		languageWordList.add(k13);
		
		KeyWord k21 = new KeyWord("手机", "11-12-42", 1.2D);
		KeyWord k22 = new KeyWord("电脑", "11-12-41", 1.5D);
		
		List<KeyWord> digitalKeyWordList = new ArrayList<KeyWord>();
		digitalKeyWordList.add(k21);
		digitalKeyWordList.add(k22);
		
		Image languageImage = new Image("11", 3D, null, null, null, null,null);
		Image digitalImage = new Image("33", 1D, null, null, null, null,null);
		
		List<Image> imageList = new ArrayList<Image>();
		imageList.add(languageImage);
		imageList.add(digitalImage);
		
		Packet p = new Packet("imageList", 2, languageWordList, imageList);
		try {
			String json = jacksonMapper.writeValueAsString(p);
			System.out.println(json);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
	}
}
