package com.example.demo.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.demo.entity.ImageRatio;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Component(value="redisUtils")
public class RedisUtils {
	
	public static String redisImageValue = null;
	public static String redisMyImageValue = null;
	public static String redisPngValue = null;
	public static String redisSvgValue = null;
	public static String redisSvgIconValue = null;
	public static String redisTmpValue = null;
	public static String redisImageRatioValue = null;
	
	private static final JedisPool jedisPool;
	static {
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
		redisMyImageValue = getByKey("my_img_key");
		redisPngValue = getByKey("png_key");
		redisSvgValue = getByKey("svg_key");
		redisSvgIconValue = getByKey("svg_icon_key");
		redisTmpValue = getByKey("tmp_key");
		redisImageRatioValue = getByKey("img_ratio");
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
	public static String getByKey(String key) {
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

	/**
	 * 像redis中存key-value对
	 * @param key
	 * @param value
	 * @return
	 */
	public static String putByKey(String key, String value) {
		Jedis jedis = jedisPool.getResource();
		return jedis.set(key, value);
	}

	/**
	 * 根据key，从静态变量读取value
	 * @param key
	 * @return
	 */
	public static String getRedisValueByKey(String key) {
		String valueString = null;
		switch (key) {
			case "img_key" : {
				valueString = redisImageValue;
				break;
			}
			case "my_img_key" : {
				valueString = redisMyImageValue;
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

	/**
	 * 从redis读取所有key
	 */
	public static void loadAllValueFromRedis() {
		redisImageValue = getByKey("img_key");
		redisMyImageValue = getByKey("my_img_key");
		redisPngValue = getByKey("png_key");
		redisSvgValue = getByKey("svg_key");
		redisSvgIconValue = getByKey("svg_icon_key");
		redisTmpValue = getByKey("tmp_key");
		redisImageRatioValue = getByKey("img_ratio");
	}

	/**
	 * 定时调度
	 */
	@Scheduled(cron = "0 45 5 * * *")
	public void timer() {
	    System.out.println("当前时间为:" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
	    loadAllValueFromRedis();
	}

}
