package com.example.demo.caller;

import com.example.demo.util.gifUtil.AnimatedGifEncoder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Callable;

/**
 * @Author：YangJx
 * @Description：Gif图片转换线程类
 * @DateTime：2017/12/18 10:36
 */
@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GifCaller implements Callable<String> {

    /*@Value("${gif.quality}")
    private static int gifQuality;*/

    /**
     * 原图路径
     */
    private String sourceFramePath;

    @Override
    public String call() throws Exception {
        return transImageToGif(this.sourceFramePath);
    }

    /**
     * 根据图片在OS上的存储路径，将图片转为gif
     *
     * @param sourcePath
     * @return
     */
    public String transImageToGif(String sourcePath) {
        AnimatedGifEncoder animatedGifEncoder = new AnimatedGifEncoder();
        animatedGifEncoder.setDelay(1);
        animatedGifEncoder.setRepeat(-1);
        animatedGifEncoder.setQuality(50);

        int lastIndexOfDot = sourcePath.lastIndexOf('.');
        String targetGifPath = sourcePath.substring(0, lastIndexOfDot) + ".gif";
        animatedGifEncoder.start(targetGifPath);
        InputStream inputStream_1 = null;
        InputStream inputStream_2 = null;
        try {
            inputStream_1 = new FileInputStream(sourcePath);
            inputStream_2 = new FileInputStream(sourcePath);
            animatedGifEncoder.addFrame(ImageIO.read(inputStream_1));
            animatedGifEncoder.addFrame(ImageIO.read(inputStream_2));
            animatedGifEncoder.finish();
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
        return targetGifPath;
    }
}