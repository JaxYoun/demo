package com.example.demo.controller;

import com.example.demo.caller.TemplateAdaptor;
import com.example.demo.entity.Image;
import com.example.demo.entity.Packet;
import com.example.demo.entity.template.Template;
import com.example.demo.entity.template.TemplateImage;
import com.example.demo.entity.template.TemplatePacket;
import com.example.demo.util.PropertiesUtil;
import com.example.demo.util.RedisUtils;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

@RestController
@RequestMapping(value = "/template")
public class TemplateController {

	@Autowired
	private static RedisUtils redisUtils;

	@Autowired
	private NewKeyWordController newKeyWordController;

	static final ExecutorService fixedPool;
	private static final ObjectMapper jacksonMapper = new ObjectMapper();
	private static final Logger logger = Logger.getLogger(NewKeyWordController.class);

	static {
		String threadPoolSize = PropertiesUtil.getPropMap().get("threadPoolSize");
		fixedPool = Executors.newFixedThreadPool(Integer.parseInt(threadPoolSize));
	}

	@SuppressWarnings("static-access")
	@RequestMapping(value = "/getImageByTemplate", method = RequestMethod.POST)
	public HashMap<String, Object> getImageByTemplate(@RequestBody String json) {

		System.out.println("| | |getImageByTemplate 当前时间为:" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

		HashMap<String, Object> returnMap = new HashMap<String, Object>();
		TemplatePacket resultTemplatePacket = new TemplatePacket();
		String arg = null;
		try {
			arg = URLDecoder.decode(json, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

		try {
			TemplatePacket templatePacket = jacksonMapper.readValue(arg, TemplatePacket.class);
			if (StringUtils.isBlank(templatePacket.getType())) {
				returnMap.put("status", "failed");
				returnMap.put("message", "类型不能为空！");
			} else if (templatePacket.getTmp_list().size() <= 0) {
				returnMap.put("status", "failed");
				returnMap.put("message", "模板不能为空！");
			} else {
				String type = templatePacket.getType();

				List<Image> redisImageList = jacksonMapper.readValue(redisUtils.getRedisValueByKey(type), new TypeReference<List<Image>>() {});
				resultTemplatePacket.setType(type);  //返回的最外层模板框架对象，其type与请求的type一致
				List<Template> resultTemplateList = new ArrayList<Template>();  //返回的内层模板组

				for (Template tmp : templatePacket.getTmp_list()) {
					Set<String> imageIdSet = new HashSet<>();  //避免同一模板中，不同图片匹配相同的redis大图

					Template resultTemplate = new Template();  //返回的单个内层模板
					List<TemplateImage> templateImageList0 = new ArrayList<TemplateImage>();  //返回的单个模板内的大图片组

					List<Image> sortedSelectedImageList = new ArrayList<Image>(); // 封装每一套下n张图片所对应的，经过关键词匹配后的n张图

//					System.out.println(templatePacket.getTmp_list().size() + "======================================");
					for (TemplateImage tmpImg : tmp.getImageList()) {
						FutureTask<List<Image>> futureTask = new FutureTask<List<Image>>(new TemplateAdaptor(type, tmpImg, redisImageList));
						fixedPool.submit(futureTask);
						try {
							List<Image> imgList = futureTask.get();  //每张传来的大图会到redis里匹配N张大图
							Map<String, Image> backImageMap = new HashMap<>();
							for (Image img0 : imgList) {
								img0.setKeywords_rate(tmpImg.getKeywords_rate());
								backImageMap.put(img0.getId(), img0);
							}


							Packet packet = new Packet();
							packet.setImageList(imgList);
							packet.setKeywordList(tmpImg.getKeyWordList());
							packet.setType(templatePacket.getType());
							packet.setNum(imgList.size());

							List<Image> imgList_1 = newKeyWordController.neoAdaptKeyWordAndImage(packet);
							int len = imgList_1.size();

							if (len > 0) {
								// resultTemplateList.add(imgList);
								for(int ij = 0; ij < len; ij++){
									if(imageIdSet.contains(imgList.get(ij).getId())){
										continue;
									}else{
										Image selectedImg = backImageMap.get(imgList_1.get(ij).getId());
										TemplateImage templateImage0 = new TemplateImage();

										templateImage0.setId(tmpImg.getId());
										templateImage0.setBackId(selectedImg.getId());
										templateImage0.setPage_id(tmpImg.getPage_id());
										templateImage0.setHeight(tmpImg.getHeight());
										templateImage0.setWidth(tmpImg.getWidth());
										templateImage0.setSubImagePath(selectedImg.getImg_info().get(0).getPath());

										templateImageList0.add(templateImage0);
										imageIdSet.add(imgList.get(ij).getId());
										break;
									}
								}

							}
						} catch (InterruptedException e) {
							e.printStackTrace();
						} catch (ExecutionException e) {
							e.printStackTrace();
						}
						resultTemplate.setImageList(templateImageList0);
					}
					resultTemplateList.add(resultTemplate);
				}
				resultTemplatePacket.setTmp_list(resultTemplateList);
				returnMap.put("status", "success");
				returnMap.put("message", "成功！");
				returnMap.put("template", resultTemplatePacket);
			}
		} catch (JsonParseException e) {
			returnMap.put("status", "failed");
			returnMap.put("message", "数据结构错误！");
			e.printStackTrace();
		} catch (JsonMappingException e) {
			returnMap.put("status", "failed");
			returnMap.put("message", "数据结构错误！");
			e.printStackTrace();
		} catch (IOException e) {
			returnMap.put("status", "failed");
			returnMap.put("message", "数据读取错误！");
			e.printStackTrace();
		}
		return returnMap;
	}

}
