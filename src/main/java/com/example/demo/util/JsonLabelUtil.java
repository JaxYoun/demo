package com.example.demo.util;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonLabelUtil {
	
	private static final ObjectMapper jacksonMapper = new ObjectMapper();

	public static void main(String[] args) {
		int leng = 100;
		char[] arr = new char[leng];
		arr[0] = '[';
		for(int i = 1; i < leng - 1; i++){
			arr[i] = ' ';
		}
		arr[leng - 1] = ']';
		
		for(char c : arr){
			System.out.printf("\r" + c);
		}
		
//		for(int k = 0; k < leng; k++){
//			arr[k] = '*';
//		}
		
	}

	public static void bb(){
		
		class Label{
			public String name;
			public boolean necessary;
			
			public String getName() {
				return name;
			}
			public void setName(String name) {
				this.name = name;
			}
			public boolean getNecessary() {
				return necessary;
			}
			public void setNecessary(boolean necessary) {
				this.necessary = necessary;
			}
		}
		
		Map<String, Label> map = new HashMap<String, Label>();
		Label label = null;
		for(int i = 0; i < 10; i++){
			label = new Label();
			label.setName("" + i);
			label.setNecessary(i % 2 == 0 ? true : false);
			map.put("" + i, label);
		}
		
		try {
			String kk = jacksonMapper.writeValueAsString(map);
			System.out.println(kk);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}
}


