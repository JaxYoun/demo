package com.example.demo.controller;

import com.example.demo.entity.gif.FramePath;
import com.example.demo.util.gifUtil.AnimatedGifEncoder;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.io.*;
import java.net.URLDecoder;
import java.util.*;

/**
 * @Author：YangJx
 * @Description：图片处理
 * @DateTime：2017/12/7 20:07
 */
@Slf4j
@RestController
@RequestMapping("/image")
public class ImageController {

    private static final ObjectMapper jacksonMapper = new ObjectMapper();

    @Autowired
    private AnimatedGifEncoder animatedGifEncoder;

    /**
     * 将多帧图片编码为Gif
     *
     * @param json
     * @return
     */
    @PostMapping("/transImageToGif")
    public Object transImageToGif(@RequestBody String json) {

        String arg = null;
        try {
            arg = URLDecoder.decode(json, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }

        FramePath framePath = null;
        try {
            framePath = jacksonMapper.readValue(arg, FramePath.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, Object> resultMap = new HashMap<>();
        List<String> resultList = encodeFrameListToGif(framePath.getPathList());

        if(resultList == null) {
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
     * 将多帧图片编码为Gif
     * @param framePathList
     * @return
     */
    public static List<String> encodeFrameListToGif(List<String> framePathList) {
        if(framePathList == null || framePathList.size() == 0) {
            return null;
        }

        List<String> targetGifPathList = new ArrayList<>();
        AnimatedGifEncoder animatedGifEncoder = new AnimatedGifEncoder();
        animatedGifEncoder.setDelay(10000);
        animatedGifEncoder.setRepeat(-1);
        animatedGifEncoder.setQuality(50);

        for (String framePath : framePathList) {
            int lastIndexOfDot = framePath.lastIndexOf('.');
            String targetGifPath = framePath.substring(0, lastIndexOfDot) + ".gif";
            animatedGifEncoder.start(targetGifPath);
            InputStream inputStream = null;
            try {
                inputStream = new FileInputStream(framePath);
                animatedGifEncoder.addFrame(ImageIO.read(inputStream));
                animatedGifEncoder.addFrame(ImageIO.read(inputStream));
                animatedGifEncoder.finish();
                targetGifPathList.add(targetGifPath);
            } catch (FileNotFoundException e) {
                log.error(e.getMessage(), e);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
        }
        return targetGifPathList;
    }

    public static void main(String[] args) {
        String path = "E:\\TEST\\ChengJie\\imageToGif\\test\\blue.jpg";
        int lastIndexOfDot = path.lastIndexOf('.');
        System.out.println(path.substring(0, lastIndexOfDot));
    }
}
