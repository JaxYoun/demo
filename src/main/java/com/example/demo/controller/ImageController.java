package com.example.demo.controller;

import com.example.demo.entity.gif.FramePath;
import com.example.demo.util.gifUtil.AnimatedGifEncoder;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${gif.quality}")
    private int gifQuality;

    /*@Autowired
    private AnimatedGifEncoder animatedGifEncoder;*/

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

        List<String> resultList = encodeFrameListToGif(framePath.getPathList());
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
     * 将多帧图片编码为Gif
     *
     * @param framePathList
     * @return
     */
    public List<String> encodeFrameListToGif(List<String> framePathList) {
        if (framePathList == null || framePathList.size() == 0) {
            return null;
        }

        AnimatedGifEncoder animatedGifEncoder;
        List<String> targetGifPathList = new ArrayList<>();
        for (String framePath : framePathList) {
            animatedGifEncoder = new AnimatedGifEncoder();
            animatedGifEncoder.setDelay(1);
            animatedGifEncoder.setRepeat(-1);
//        animatedGifEncoder.setQuality(gifQuality);
            animatedGifEncoder.setQuality(50);

            int lastIndexOfDot = framePath.lastIndexOf('.');
            String targetGifPath = framePath.substring(0, lastIndexOfDot) + ".gif";
            animatedGifEncoder.start(targetGifPath);
            InputStream inputStream_1 = null;
            InputStream inputStream_2 = null;
            try {
                inputStream_1 = new FileInputStream(framePath);
                inputStream_2 = new FileInputStream(framePath); //"E:\\TEST\\ChengJie\\imageToGif\\test\\422-0-副本.jpg");
                animatedGifEncoder.addFrame(ImageIO.read(inputStream_1));
                animatedGifEncoder.addFrame(ImageIO.read(inputStream_2));
                animatedGifEncoder.finish();
                targetGifPathList.add(targetGifPath);
            } catch (FileNotFoundException e) {
                log.error(e.getMessage(), e);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            } finally {
                if (inputStream_1 != null) {
                    try {
                        inputStream_1.close();
                    } catch (IOException e) {
                        log.error(e.getMessage(), e);
                    }
                }
                if (inputStream_2 != null) {
                    try {
                        inputStream_2.close();
                    } catch (IOException e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
        }

        List<String> tempList = new ArrayList<>(targetGifPathList.size());
        tempList.addAll(targetGifPathList);

        while (tempList.size() != 0) {
            Iterator<String> it = tempList.iterator();
            if (it.hasNext() && ifFileExist(it.next())) {
                it.remove();
            }
        }

        return targetGifPathList;
    }

    /**
     * 判断FileSystem上是否存在指定文件
     *
     * @param filePath
     * @return
     */
    public boolean ifFileExist(String filePath) {
        boolean isExist;
        File file = new File(filePath);
        isExist = file.exists();
        return isExist;
    }

    public static void main(String[] args) {
        /*String path = "E:\\TEST\\ChengJie\\imageToGif\\test\\png\\455-0.png";
        String path1 = "E:\\TEST\\ChengJie\\imageToGif\\test\\png\\455-1.png";
        String path2 = "E:\\TEST\\ChengJie\\imageToGif\\test\\png\\4556-1.png";

        List<String> list = new ArrayList<>();
        list.add(path);
        list.add(path1);
        list.add(path2);

        encodeFrameListToGif(list);*/

       /*try {
            Thumbnails.of("E:\\TEST\\ChengJie\\imageToGif\\test\\500179127.png").scale(1F).outputQuality(1F).toFile("E:\\TEST\\ChengJie\\imageToGif\\test\\500179127kkk.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }
}