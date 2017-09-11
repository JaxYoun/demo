package com.example.demo.util;

import com.example.demo.entity.Image;
import com.example.demo.entity.ImageRatio;
import com.example.demo.entity.KeyWord;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component(value="redisUtils")
public class RedisUtils {
	
	public static String redisImageValue = null;
	public static String redisPngValue = null;
	public static String redisSvgValue = null;
	public static String redisSvgIconValue = null;  //svg_icon_key
	public static String redisTmpValue = null;
	public static String redisImageRatioValue = null;
	
	private static final JedisPool jedisPool;
	static{
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(5);
		config.setMaxIdle(2);
		config.setMaxWaitMillis(100);
		config.setTestOnBorrow(true);  //获取连接实例前验证实例是否可用
		config.setTestOnReturn(true);  //回收连接实例钱验证实例是否可回收
		
		String redisIp = PropertiesUtil.getPropMap().get("redisIp");
		String redisPort = PropertiesUtil.getPropMap().get("redisPort");
		String connectTimeOut = PropertiesUtil.getPropMap().get("connectTimeOut");
		String redisPassword = PropertiesUtil.getPropMap().get("redisPassword");
		jedisPool = new JedisPool(config, redisIp, Integer.parseInt(redisPort), Integer.parseInt(connectTimeOut), redisPassword);
		
//		jedisPool = new JedisPool(config, redisIp, Integer.parseInt(redisPort), Integer.parseInt(connectTimeOut), null);
		
//		jedisPool = new JedisPool(config, "119.23.39.35", 6379, 100, "forget1230");
//		jedisPool = new JedisPool(config, "localhost", 6379, 100, null);
		
		redisImageValue = getByKey("img_key");
		redisPngValue = getByKey("png_key");
		redisSvgValue = getByKey("svg_key");
		redisSvgIconValue = getByKey("svg_icon_key");
		redisTmpValue = getByKey("tmp_key");
		redisImageRatioValue = getByKey("img_ratio");
	}
	
	public static void main(String[] args) {
//		Jedis jedis = jedisPool.getResource();
//		System.err.println(jedis.ping());
//		System.out.println(getByKey("img_ratio"));
//		System.out.println(getByKey("img_key"));
//		System.out.println(getByKey("imageList"));
		
		
//		System.out.println(putByKey("imageList", getJsonImageList()));
//		System.out.println(putByKey("imageRatioList", getJsonImageRatioList()));
		
//		System.out.println(putByKey("imageRatioList", getJsonImageRatioList()));

	}
	
	public static String getJsonImageList() {
		ObjectMapper jacksonMapper = new ObjectMapper();
		
		KeyWord k11 = new KeyWord("中文", "12-33-40", 1.1D);
		KeyWord k12 = new KeyWord("英文", "12-33-42", 1.2D);
		KeyWord k13 = new KeyWord("日文", "12-33-41", 1.5D);
		
		List<KeyWord> languageWordList = new ArrayList<KeyWord>();
		languageWordList.add(k11);
		languageWordList.add(k12);
		languageWordList.add(k13);
		
		KeyWord k21 = new KeyWord("手机", "12-12-42", 1.2D);
		KeyWord k22 = new KeyWord("电脑", "12-12-41", 1.5D);
		
		List<KeyWord> digitalKeyWordList = new ArrayList<KeyWord>();
		digitalKeyWordList.add(k21);
		digitalKeyWordList.add(k22);
		
//		Image languageImage = new Image("11", 3D, languageWordList);
//		Image digitalImage = new Image("33", 1D, digitalKeyWordList);
		
		List<Image> imageList = new ArrayList<Image>();
//		imageList.add(languageImage);
//		imageList.add(digitalImage);
		
		String json = null;
		try {
			json = jacksonMapper.writeValueAsString(imageList);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return json;
	}
	
	public static String getJsonImageRatioList() {
		ObjectMapper jacksonMapper = new ObjectMapper();
		
		ImageRatio r1 = new ImageRatio("11", 1D);
		ImageRatio r2 = new ImageRatio("33", 3D);
		
		List<ImageRatio> imageRatioList = new ArrayList<ImageRatio>();
		imageRatioList.add(r1);
		imageRatioList.add(r2);
		
		String json = null;
		try {
			json = jacksonMapper.writeValueAsString(imageRatioList);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return json;
	}
	
	@SuppressWarnings("deprecation")
	public static String getByKey(String key){
		System.err.println("---->从Redis获取了：" + key);
		Jedis jedis = null;
		String value = null;
		try {
			jedis = jedisPool.getResource();
			value = jedis.get(key);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			jedisPool.returnResourceObject(jedis);
		}
		return value;
	}
	
	public static String putByKey(String key, String value){
		Jedis jedis = jedisPool.getResource();
		return jedis.set(key, value);
	}
	
	public static String getRedisValueByKey(String key){
		String valueString = null;

		switch (key) {
			case "img_key" : {
				valueString = redisImageValue;
				break;
			}
			case "png_key" : {
				valueString = redisPngValue;
				break;
			}
			case "svg_key" : {
				valueString = redisSvgValue;
				break;
			}
			case "svg_icon_key" : {
				valueString = redisSvgIconValue;
				break;
			}
			case "tmp_key" : {
				valueString = redisTmpValue;
				break;
			}
			case "img_ratio" : {
				valueString = redisImageRatioValue;
				break;
			}

		}

		return valueString;
	}
	
	public static void loadAllValueFromRedis(){
		redisImageValue = getByKey("img_key");
		redisPngValue = getByKey("png_key");
		redisSvgValue = getByKey("svg_key");
		redisSvgIconValue = getByKey("svg_icon_key");
		redisTmpValue = getByKey("tmp_key");
		redisImageRatioValue = getByKey("img_ratio");
	}
	
	@Scheduled(cron = "0 45 5 * * *")
	public void timer(){
	    System.out.println("当前时间为:" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
	    loadAllValueFromRedis();
	}

}
