package com.example.demo.service;

import java.util.List;

/**
 * @Author：YangJx
 * @Description：
 * @DateTime：2017/12/18 11:43
 */
public interface IGifService {

    /**
     * 多线程方式 将图片转为Gif
     * @param framePathList
     * @return
     */
    List<String> encodeImageToGigWithMultiThread(List<String> framePathList);

    /**
     * 单线程方式 将图片转为Gif
     * @param framePathList
     * @return
     */
    List<String> encodeFrameListToGif(List<String> framePathList);

}
