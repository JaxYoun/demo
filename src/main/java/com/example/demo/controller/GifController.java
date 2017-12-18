package com.example.demo.controller;

import com.example.demo.entity.gif.FramePath;
import com.example.demo.service.IGifService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author：YangJx
 * @Description：图片处理
 * @DateTime：2017/12/7 20:07
 */
@Slf4j
@RestController
@RequestMapping("/image")
public class GifController {

    private static final ObjectMapper jacksonMapper = new ObjectMapper();

    @Autowired
    private IGifService gifService;

    /**
     * 将多帧图片编码为Gif，原图最好是png，因为jpg会导致生成的gif体积过于膨胀
     *
     * @param json
     * @return
     */
    @PostMapping("/transImageToGifNaive")
    public Object transImageToGifNaive(@RequestBody String json) {
        Map<String, Object> resultMap = new HashMap<>();
        log.info("transImageToGif");
        String arg = null;
        try {
            arg = URLDecoder.decode(json, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            resultMap.put("code", "500");
            resultMap.put("message", "请求参数格式非法！");
            resultMap.put("data", null);
            e1.printStackTrace();
        }

        FramePath framePath = null;
        try {
            framePath = jacksonMapper.readValue(arg, FramePath.class);
        } catch (IOException e) {
            resultMap.put("code", "500");
            resultMap.put("message", "请求参数格式非法！");
            resultMap.put("data", null);
            e.printStackTrace();
        }

        List<String> resultList = this.gifService.encodeFrameListToGif(framePath.getPathList());
        if (resultList == null) {
            resultMap.put("code", "500");
            resultMap.put("message", "请求参数非法！");
            resultMap.put("data", null);
        } else {
            resultMap.put("code", "200");
            resultMap.put("message", "转换成功！");
            resultMap.put("data", resultList);
        }
        return resultMap;
    }

    /**
     * 将多帧图片编码为Gif，原图最好是png，因为jpg会导致生成的gif体积过于膨胀
     *
     * @param json
     * @return
     */
    @PostMapping("/transImageToGif")
    public Object transImageToGif(@RequestBody String json) {
        Map<String, Object> resultMap = new HashMap<>();
        log.info("transImageToGif");
        String arg = null;
        try {
            arg = URLDecoder.decode(json, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            resultMap.put("code", "500");
            resultMap.put("message", "请求参数格式非法！");
            resultMap.put("data", null);
            e1.printStackTrace();
        }

        FramePath framePath = null;
        try {
            framePath = jacksonMapper.readValue(arg, FramePath.class);
        } catch (IOException e) {
            resultMap.put("code", "500");
            resultMap.put("message", "请求参数格式非法！");
            resultMap.put("data", null);
            e.printStackTrace();
        }

        List<String> resultList = this.gifService.encodeImageToGigWithMultiThread(framePath.getPathList());
        if (resultList == null) {
            resultMap.put("code", "500");
            resultMap.put("message", "请求参数非法！");
            resultMap.put("data", null);
        } else {
            resultMap.put("code", "200");
            resultMap.put("message", "转换成功！");
            resultMap.put("data", resultList);
        }
        return resultMap;
    }
}