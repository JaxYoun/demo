package com.example.demo.controller;

import com.example.demo.caller.Adaptor;
import com.example.demo.entity.Image;
import com.example.demo.entity.ImageRatio;
import com.example.demo.entity.KeyWord;
import com.example.demo.entity.Packet;
import com.example.demo.util.PropertiesUtil;
import com.example.demo.util.RedisUtils;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * @author youn
 *
 */
@RestController
// @RequestMapping(value = "/key")
@RequestMapping(value = "/keyword")
public class NewKeyWordController {

	@Autowired
	private static RedisUtils redisUtils;

	static final ExecutorService fixedPool;
	private static final ObjectMapper jacksonMapper = new ObjectMapper();
	private static final Logger logger = Logger.getLogger(NewKeyWordController.class);

	static {
		String threadPoolSize = PropertiesUtil.getPropMap().get("threadPoolSize");
		fixedPool = Executors.newFixedThreadPool(Integer.parseInt(threadPoolSize));
	}

	/**
	 * 
	 * @param json
	 * @return
	 */
	// @RequestMapping(value = "/adaptKeyWordAndImage", method =
	// RequestMethod.POST)
	@RequestMapping(value = "/getMostFitImageForDocByKeyWord", method = RequestMethod.POST)
	public HashMap<String, Object> adaptKeyWordAndImage(@RequestBody String json) {

		String arg = null;
		try {
			arg = URLDecoder.decode(json, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

		// System.out.println(arg.substring(0, 100));
		HashMap<String, Object> returnMap = new HashMap<String, Object>();
		try {
			Packet packet = jacksonMapper.readValue(arg, Packet.class);
			String type = packet.getType();
			Integer imageCount = packet.getNum();
			List<KeyWord> keywordList = packet.getKeywordList();
			List<Image> imageList = packet.getImageList();

			System.out.println("返回图片数| | | | " + imageCount);
			System.out.println("关键字个数| | | | " + keywordList.size());
			System.out.println("传进图片数| | | | " + imageList.size());

			if (keywordList.size() <= 0) {
				returnMap.put("status", "failed");
				returnMap.put("message", "关键字不能为空！");
			} else if (imageList.size() <= 0) {
				returnMap.put("status", "failed");
				returnMap.put("message", "图片或模板ID不能为空！");
			} else if (imageCount < 1) {
				returnMap.put("status", "failed");
				returnMap.put("message", "返回结果数量不能小于1！");
			} else if (StringUtils.isBlank(type.trim())) {
				returnMap.put("status", "failed");
				returnMap.put("message", "图片类型非法！");
			} else {
				ArrayList<Image> resultList = new ArrayList<Image>();
				Map<String, Image> imageMap = getImageMapFromRedis(type);
				Map<String, Double> imageRatioMap = getImageRatioMapFromRedis("img_ratio");

//				System.err.println("redis_img_key_size | | | | | | " + imageMap.size());
//				System.err.println("redis_img_ratio_size | | | | | " + imageRatioMap.size());

				for (Image image : imageList) {
					FutureTask<Double> futureTask = null;
					Image dbImage = imageMap.get(image.getId());

					try {
						if (dbImage != null) {
							futureTask = new FutureTask<Double>(new Adaptor(imageRatioMap, image, keywordList, dbImage));
							fixedPool.submit(futureTask);
							resultList.add(new Image(image.getId(), futureTask.get(), null, null, null, null,null));
						} else {
							resultList.add(new Image(image.getId(), image.getWeight(), null, null, null, null,null));
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (ExecutionException e) {
						e.printStackTrace();
					}
				}

				ArrayList<Image> sortedResultList = sortList(resultList, false);

				returnMap.put("status", "success");
				returnMap.put("message", "成功！");
				if (imageCount < sortedResultList.size()) {
					returnMap.put("fitedImageList", getTopElment(sortedResultList, imageCount));
				} else {
					returnMap.put("fitedImageList", sortedResultList);
				}
			}
		} catch (JsonParseException e) {
			e.printStackTrace();
			returnMap.put("status", "failed");
			returnMap.put("message", "请求异常！");
		} catch (JsonMappingException e) {
			e.printStackTrace();
			returnMap.put("status", "failed");
			returnMap.put("message", "请求异常！");
		} catch (IOException e) {
			e.printStackTrace();
			returnMap.put("status", "failed");
			returnMap.put("message", "请求异常！");
		}

		return returnMap;
	}

	public List<Image> neoAdaptKeyWordAndImage(Packet packet) {

		String type = packet.getType();
		Integer imageCount = packet.getNum();
		List<KeyWord> keywordList = packet.getKeywordList();
		List<Image> imageList = packet.getImageList();

		ArrayList<Image> resultList = new ArrayList<Image>();
		Map<String, Image> imageMap = getImageMapFromRedis(type);
		Map<String, Double> imageRatioMap = getImageRatioMapFromRedis("img_ratio");

//		System.err.println("redis_img_key_size | | | | | | " + imageMap.size());
//		System.err.println("redis_img_ratio_size | | | | | " + imageRatioMap.size());

		for (Image image : imageList) {
			FutureTask<Double> futureTask = null;
			Image dbImage = imageMap.get(image.getId());

			try {
				if (dbImage != null) {
					futureTask = new FutureTask<Double>(new Adaptor(imageRatioMap, image, keywordList, dbImage));
					fixedPool.submit(futureTask);
					resultList.add(new Image(image.getId(), futureTask.get(), null, null, null, null, null));
				} else {
					resultList.add(new Image(image.getId(), image.getWeight(), null, null, null, null, null));
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}

		ArrayList<Image> sortedResultList = sortList(resultList, false);

		List<Image> reList = null;
		if (imageCount < sortedResultList.size()) {
			reList = getTopElment(sortedResultList, imageCount);
		} else {
			reList = sortedResultList;
		}
		
		return reList;
	}

	/**
	 * 获取前n个图片
	 * 
	 * @param sortedResultList
	 * @param count
	 * @return
	 */
	public List<Image> getTopElment(List<Image> sortedResultList, int count) {
		ArrayList<Image> returnList = new ArrayList<Image>();
		for (int i = 0; i < count; i++) {
			returnList.add(sortedResultList.get(i));
		}
		return returnList;
	}

	public static void main(
			String[] args) {/*
							 * String redisImageStr = getJsonImageFromRedis();
							 * try { List<Image> redisImageList =
							 * jacksonMapper.readValue(redisImageStr, new
							 * TypeReference<List<Image>>(){}); //
							 * System.out.println(redisImageTable.
							 * getRedisImageList().size()); for (Image
							 * redisImage : redisImageList) { for (KeyWord
							 * redisKeyWord : redisImage.getKeyWordList()) {
							 * System.out.println(redisKeyWord.getVlaue() +
							 * "*******" + redisKeyWord.getPosition() +
							 * "*******" + redisKeyWord.getPow()); } }
							 * 
							 * } catch (JsonParseException e) {
							 * e.printStackTrace(); } catch
							 * (JsonMappingException e) { e.printStackTrace(); }
							 * catch (IOException e) { e.printStackTrace(); }
							 * 
							 */
		/*
		 * System.out.println(RedisUtils.getRedisValueByKey("img_key"));
		 * System.out.println(RedisUtils.getRedisValueByKey("img_ratio"));
		 * System.out.println(RedisUtils.getRedisValueByKey("img_key"));
		 */

		/*
		 * try { String kk = URLDecoder.decode(
		 * "7%7D%2C%7B%22vlaue%22%3A%22%5Cu4e09%5Cu5b9d%22%2C%22position%22%3A%2210-31-39%22%2C%22",
		 * "utf-8"); System.out.println(kk); } catch
		 * (UnsupportedEncodingException e) { e.printStackTrace(); }
		 */

	}

	/**
	 * 从Redis获取“图片-增益率”集合，并封装为 “图片id-增益率”map格式
	 * 
	 * @param key
	 * @return
	 */
	public Map<String, Double> getImageRatioMapFromRedis(String key) {
		Map<String, Double> ratioMap = new HashMap<String, Double>();
		try {
			ArrayList<ImageRatio> ratioImageList = jacksonMapper.readValue(redisUtils.getRedisValueByKey("img_ratio"),
					new TypeReference<List<ImageRatio>>() {
					});
			for (ImageRatio it : ratioImageList) {
				ratioMap.put(it.getId(), it.getRatio());
			}
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ratioMap;
	}

	/**
	 * 从Redis获取“图片”集合，并封装为 “图片id-图片”的map格式
	 * 
	 * @param key
	 * @return
	 */
	public static Map<String, Image> getImageMapFromRedis(String key) {
		Map<String, Image> imageMap = new HashMap<String, Image>();
		String redisImageStr = redisUtils.getRedisValueByKey(key);

		// System.err.println("yjx" + redisImageStr + "yjx");
		List<Image> redisImageList = null;
		try {
			redisImageList = jacksonMapper.readValue(redisImageStr, new TypeReference<List<Image>>() {
			});
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (Image im : redisImageList) {
			imageMap.put(im.getId(), im);
		}
		return imageMap;
	}

	@Deprecated
	public static String getJsonImageFromRedis() {
		KeyWord key1 = new KeyWord("\\u53d1\\u5e03\\u4f1a\\", "10-31-38", 1.0);
		KeyWord key2 = new KeyWord("\\u53d1\\u5e03\\u4f1a\\", "10-31-39", 6.0);
		KeyWord key3 = new KeyWord("\\u53d1\\u5e03\\u4f1a\\", "10-31-40", 4.0);
		KeyWord key4 = new KeyWord("\\u53d1\\u5e03\\u4f1a\\", "10-31-41", 5.0);

		ArrayList<KeyWord> redisKeyWordList1 = new ArrayList<KeyWord>();
		redisKeyWordList1.add(key1);
		redisKeyWordList1.add(key2);

		ArrayList<KeyWord> redisKeyWordList2 = new ArrayList<KeyWord>();
		redisKeyWordList2.add(key4);
		redisKeyWordList2.add(key3);

		Image imag1 = new Image("90", null, redisKeyWordList1, null, null, null,null);
		Image imag2 = new Image("80", null, redisKeyWordList2, null, null, null,null);

		ArrayList<Image> redisImageList = new ArrayList<Image>();
		redisImageList.add(imag1);
		redisImageList.add(imag2);

		String gu = null;
		try {
			gu = jacksonMapper.writeValueAsString(redisImageList);
			System.out.println(gu);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return gu;
	}

	/**
	 * list排序
	 * 
	 * @param list
	 * @param isAsc 是否升序，默认为升序
	 * 
	 * @return
	 */
	public static ArrayList<Image> sortList(ArrayList<Image> list, boolean isAsc) {
		if (list != null && list.size() > 0) {
			if (isAsc) {
				list.sort((h1, h2) -> h1.getWeight().compareTo(h2.getWeight()));
			} else {
				Comparator<Image> comparator = (h1, h2) -> h1.getWeight().compareTo(h2.getWeight());
				list.sort(comparator.reversed());
			}
		}
		return list;
	}

}
