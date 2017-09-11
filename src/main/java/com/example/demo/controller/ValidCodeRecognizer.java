package com.example.demo.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import com.example.demo.util.PropertiesUtil;

public class ValidCodeRecognizer {

	private static final Logger logger = Logger.getLogger(ColorMapController.class);

	private static final String LINUX_X64_SO_PATH = "libopencv_java320.so";
	private static final String LINUX_X86_SO_PATH = "libopencv_java320.so";

	private static final String WIN_X64_DLL_PATH = "opencv_java320_x64.dll";
	private static final String WIN_X86_DLL_PATH = "opencv_java320_x86.dll";

	private static String srcDir;
	private static String destDir;

	private static String post;

	static {
		try {
			String libFilePaht = null;
			String osName = System.getProperty("os.name").toLowerCase();
			String osArch = System.getProperty("os.arch").toLowerCase();

			if (osName.indexOf("win") >= 0) {
				post = ".dll";
				if (osArch.indexOf("64") >= 0) {
					libFilePaht = WIN_X64_DLL_PATH;
				} else if (osArch.indexOf("86") >= 0) {
					libFilePaht = WIN_X86_DLL_PATH;
				}
			} else if (osName.indexOf("linux") >= 0) {
				post = ".so";
				if (osArch.indexOf("64") >= 0) {
					libFilePaht = LINUX_X64_SO_PATH;
				} else if (osArch.indexOf("86") >= 0) {
					libFilePaht = LINUX_X86_SO_PATH;
				}
			}

			InputStream inputStream = ColorMapController.class.getClassLoader().getResourceAsStream(libFilePaht);
			String tempDir = System.getProperty("java.io.tmpdir");
			tempDir = tempDir + File.separator + "opencv320" + post;

			File tempDll = new File(tempDir);
			FileOutputStream fileOutputStream = new FileOutputStream(tempDll);

			int i;
			byte[] buffer = new byte[1024];
			try {
				while ((i = inputStream.read(buffer)) != -1) {
					fileOutputStream.write(buffer, 0, i);
				}
			} finally {
				inputStream.close();
				fileOutputStream.close();
			}

			System.load(tempDll.getAbsolutePath()); // jvm加载动态链接库
			tempDll.deleteOnExit();

			srcDir = PropertiesUtil.getPropMap().get("srcDir");
			destDir = PropertiesUtil.getPropMap().get("destDir");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Mat originMat = Imgcodecs.imread("D:\\card.png"); // 读取原图像
		Mat grayMat = new Mat(originMat.rows(), originMat.cols(), CvType.CV_8UC3); // 创建一个输出图像对象
		Imgproc.cvtColor(originMat, grayMat, Imgproc.COLOR_RGB2GRAY); // 彩色转灰度图
		// Imgcodecs.imwrite("D:\\grayMat.png", grayMat); //输出灰度图

		Mat binaryMat = new Mat(grayMat.height(), grayMat.width(), CvType.CV_8UC1); // 创建一个二值化图像对象
		Imgproc.threshold(grayMat, binaryMat, 30, 255, Imgproc.THRESH_BINARY);
		// Imgcodecs.imwrite("D:\\binaryMat.png", binaryMat); // 将黑白输出

		Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3)); // 腐蚀模板
		Mat erodedMat = new Mat(originMat.rows(), originMat.cols(), CvType.CV_8UC3); // 新建目标输出图像
		Imgproc.erode(binaryMat, erodedMat, element);
		// Imgcodecs.imwrite("D:\\erodedMat.png", erodedMat); // 将腐蚀图输出
//		Highgui.imwrite("erosion.jpg", destination);

		/*
		 * for (int y = 0; y < erodedMat.height(); y++) { for (int x = 0; x <
		 * erodedMat.width(); x++) { // 得到该行像素点的值 double[] data =
		 * erodedMat.get(y, x); for (int i1 = 0; i1 < data.length; i1++) {
		 * data[i1] = 255;// 像素点都改为白色 } erodedMat.put(x, y, data); } }
		 * Imgcodecs.imwrite("D:\\binaryMat7.png", erodedMat); // 将黑白输出
		 */

		/*for (int y = 0; y < erodedMat.height(); y++) {
			int count = 0;
			for (int x = 0; x < erodedMat.width(); x++) {
				// 得到该行像素点的值
				byte[] data = new byte[1];
				erodedMat.get(y, x, data);
				if (data[0] == 0)
					count = count + 1;
			}
			if (state == 0)// 还未到有效行
			{
				if (count >= 150)// 找到了有效行
				{// 有效行允许十个像素点的噪声
					a = y;
					state = 1;
				}
			} else if (state == 1) {
				if (count <= 150)// 找到了有效行
				{// 有效行允许十个像素点的噪声
					b = y;
					state = 2;
				}
			}
		}
		System.out.println("过滤下界" + Integer.toString(a));
		System.out.println("过滤上界" + Integer.toString(b));*/

	}

}
