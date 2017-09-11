package com.example.demo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

import static java.lang.System.out;

public class JacksonTest {

	private static final ObjectMapper objectMapper = new ObjectMapper();
	
	public static void getObjectFromString() {
		String jsonString = "{\"id\":1, \"name\":\"youn\", \"age\":12}";
		try {
			User user = objectMapper.readValue(jsonString, User.class);
			System.err.println(user.name);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void getJsonStringFromObject(){
		User user = new User(75, "yang", 24);
		try {
			out.println(objectMapper.writeValueAsString(user));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}
	
	public static void getListFromString(){
		String arr = "[{\"id\": 1, \"name\": \"youn\"},{\"id\": 1, \"name\": \"youn2\"}]";
		try {
			List<Object> userList = objectMapper.readValue(arr, List.class);
			out.println(userList.size());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void getUserFromNotFullStr(){
		String json = "{\"id\": \"75\", \"name\": 'youn'}";
		try {
			User user = objectMapper.readValue(json, User.class);
			System.out.println(user.getId() + ">>" + user.getId() + ">>" + user.getAge());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
//		getObjectFromString();
//		getJsonStringFromObject();
//		getListFromString();
		getUserFromNotFullStr();
	}
}
