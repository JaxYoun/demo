package com.example.demo.service.impl;

import com.example.demo.caller.GifCaller;
import com.example.demo.service.IGifService;
import com.example.demo.util.gifUtil.AnimatedGifEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author：YangJx
 * @Description：
 * @DateTime：2017/12/18 11:46
 */
@Slf4j
@Service
public class GifServiceImpl implements IGifService {

    @Value("${gif.quality}")
    private int gifQuality;

    @Value("${gif.poolSize}")
    private static int gifPoolSize;

    private static final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(3, 6, 4, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(6), new ThreadPoolExecutor.DiscardOldestPolicy());

    @Override
    public List<String> encodeImageToGigWithMultiThread(List<String> framePathList) {
        List<String> targetPathList = new ArrayList<>(framePathList.size());
        for (String path : framePathList) {
            FutureTask<String> futureTask;
            try {
                futureTask = new FutureTask<>(new GifCaller(path));
                threadPool.submit(futureTask);
                targetPathList.add(futureTask.get());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }

        List<String> tempList = new ArrayList<>(targetPathList.size());
        tempList.addAll(targetPathList);
        while (tempList.size() != 0) {
            Iterator<String> it = tempList.iterator();
            if (it.hasNext() && ifFileExist(it.next())) {
                it.remove();
            }
        }

        return targetPathList;
    }

    @Override
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
            animatedGifEncoder.setQuality(gifQuality);
//            animatedGifEncoder.setQuality(50);

            int lastIndexOfDot = framePath.lastIndexOf('.');
            String targetGifPath = framePath.substring(0, lastIndexOfDot) + ".gif";
            animatedGifEncoder.start(targetGifPath);
            InputStream inputStream_1 = null;
            InputStream inputStream_2 = null;
            try {
                inputStream_1 = new FileInputStream(framePath);
                inputStream_2 = new FileInputStream(framePath);
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
    public static boolean ifFileExist(String filePath) {
        boolean isExist;
        File file = new File(filePath);
        isExist = file.exists();
        return isExist;
    }

    public static void main(String[] args) {
        String path = "E:\\TEST\\ChengJie\\imageToGif\\test\\png\\455-0.png";
        String path1 = "E:\\TEST\\ChengJie\\imageToGif\\test\\png\\455-1.png";
        String path2 = "E:\\TEST\\ChengJie\\imageToGif\\test\\png\\4556-1.png";

        List<String> list = new ArrayList<>();
        list.add(path);
        list.add(path1);
        list.add(path2);

//        encodeFrameListToGif(list);
//        transImageToGigWithMultThread(list);

       /*try {
            Thumbnails.of("E:\\TEST\\ChengJie\\imageToGif\\test\\500179127.png").scale(1F).outputQuality(1F).toFile("E:\\TEST\\ChengJie\\imageToGif\\test\\500179127kkk.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }*/

    }
}
