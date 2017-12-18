package com.example.demo.service.impl;

import com.example.demo.caller.NeoImageCaller;
import com.example.demo.entity.neotemplate.*;
import com.example.demo.service.IImageService;
import com.example.demo.util.RedisUtils;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

@Service
public class ImageService implements IImageService {

    @Autowired
    private static RedisUtils redisUtils;

    private static final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
            6,
            8,
            60,
            TimeUnit.SECONDS,
            new LinkedBlockingDeque<>(16),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.DiscardOldestPolicy()
    );

    private static final ObjectMapper jacksonMapper = new ObjectMapper();

    volatile Set<String> idSet;

    @Override
    public NeoResponsePacket getImageByTemplate(final NeoRequestPacket neoRequestPacket) {
        String type = neoRequestPacket.getType();
        List<NeoRedisParentImage> neoRedisParentImageList = null;
        idSet = new HashSet<>();
        try {
            neoRedisParentImageList = jacksonMapper.readValue(redisUtils.getRedisValueByKey(type), new TypeReference<List<NeoRedisParentImage>>() {
            });
//            Map<String, Float> imageRatioMap = getImageRatioMapFromRedis("img_ratio");
        } catch (IOException e) {
            e.printStackTrace();
        }

        NeoResponsePacket neoResponsePacket = new NeoResponsePacket();
        neoResponsePacket.setType(type);
        List<NeoResponseImage> neoResponseImageList = new ArrayList<>();

        /**
         *图片搜索
         */
        List<NeoRequestImage> requestImageList = neoRequestPacket.getImageList();
        List<FutureTask<NeoResponseImage>> futureTaskList = new ArrayList<>(requestImageList.size());
        for (NeoRequestImage neoRequestImage : requestImageList) {
            FutureTask<NeoResponseImage> futureTask = new FutureTask<>(new NeoImageCaller(type, neoRequestImage, neoRedisParentImageList, getImageRatioMapFromRedis("img_ratio"), idSet));
            threadPool.submit(futureTask);
            futureTaskList.add(futureTask);

            /*try {
                NeoResponseImage responseImage = futureTask.get();
                if (responseImage != null) {
                    neoResponseImageList.add(responseImage);
                    idSet.add(responseImage.getImg_id());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }*/
        }

        //多线程优化 START
        for(FutureTask<NeoResponseImage> it : futureTaskList) {
            try {
                NeoResponseImage responseImage = it.get();
                neoResponseImageList.add(responseImage);
                idSet.add(responseImage.getImg_id());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        //多线程优化 END

        neoResponsePacket.setImageList(neoResponseImageList);
        return neoResponsePacket;
    }

    /**
     * 从Redis获取“图片-增益率”集合，并封装为 “图片id-增益率”map格式
     *
     * @param key
     * @return
     */
    public Map<String, Float> getImageRatioMapFromRedis(String key) {
        Map<String, Float> ratioMap = new HashMap<>();
        try {
            ArrayList<NeoImageRatio> ratioImageList = jacksonMapper.readValue(redisUtils.getRedisValueByKey(key), new TypeReference<List<NeoImageRatio>>() {
            });
            for (NeoImageRatio it : ratioImageList) {
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

}
