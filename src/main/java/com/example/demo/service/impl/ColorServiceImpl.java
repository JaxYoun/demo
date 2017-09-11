package com.example.demo.service.impl;

import com.example.demo.service.IColorService;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Service;

@Service(value = "colorService")
public class ColorServiceImpl implements IColorService {

    @Override
    public void colorMapping(String srcImagePath, String destImagePath, int colorMapCode) throws Exception {

        Mat originMat = null;
        Mat destinMat = null;

        try {
            originMat = Imgcodecs.imread(srcImagePath); // 读取原图像
            destinMat = new Mat(originMat.rows(), originMat.cols(), CvType.CV_8UC3); // 新建目标输出图像

            Imgproc.applyColorMap(originMat, destinMat, colorMapCode); // 执行图像彩色空间映射
            Imgcodecs.imwrite(destImagePath, destinMat); // 将转换结果写入到磁盘
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }finally{
            if (originMat != null){
                originMat.release();
            }
            if (destinMat != null) {
                destinMat.release();
            }
        }
    }

}
