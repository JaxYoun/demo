/**
 * Project Name:demo
 * File Name:ColorSpaceMapController.java
 * Package Name:com.example.demo
 * Date:2017年7月19日下午6:29:31
 * Copyright (c) 2017, chenzhou1025@126.com All Rights Reserved.
 *
*/

package com.example.demo.controller;

import com.example.demo.service.IColorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

/**
 * ClassName:ColorSpaceMapController <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2017年7月19日 下午6:29:31 <br/>
 * 
 * @author Yang Jx
 * @version
 * @since JDK 1.8
 * @see
 */
@RestController
@RequestMapping(value="/color")
public class ColorMapController {

	@Autowired
	IColorService colorService;

	private static final Logger logger = Logger.getLogger(ColorMapController.class);
	private static final ObjectMapper jacksonMapper = new ObjectMapper();

	private static final String LINUX_X64_SO_PATH = "libopencv_java320.so";
	private static final String LINUX_X86_SO_PATH = "libopencv_java320.so";
	
	private static final String WIN_X64_DLL_PATH = "opencv_java320_x64.dll";
	private static final String WIN_X86_DLL_PATH = "opencv_java320_x86.dll";
	
//	private static String srcDir;
//	private static String destDir;
	
	private static String post;
	
	static {
			try {
				String libFilePaht = null;
				String osName = System.getProperty("os.name").toLowerCase();
				String osArch = System.getProperty("os.arch").toLowerCase();
				
				if(osName.indexOf("win") >= 0){
					post = ".dll";
					if(osArch.indexOf("64") >= 0){
						libFilePaht = WIN_X64_DLL_PATH;
					}else if(osArch.indexOf("86") >= 0){
						libFilePaht = WIN_X86_DLL_PATH;
					}
				}else if(osName.indexOf("linux") >= 0){
					post = ".so";
					if(osArch.indexOf("64") >= 0){
						libFilePaht = LINUX_X64_SO_PATH;
					}else if(osArch.indexOf("86") >= 0){
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
				
				System.load(tempDll.getAbsolutePath());  //jvm加载动态链接库
				tempDll.deleteOnExit();
				
//				srcDir = PropertiesUtil.getPropMap().get("srcDir");
//				destDir = PropertiesUtil.getPropMap().get("destDir");
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	@RequestMapping(value = "/colorMapping", method = RequestMethod.POST)
		public Object colorMapping(String originImagePath, String new_path, Integer colorMapCode) {

		HashMap<String, String> resultMap = new HashMap<>();
		if(StringUtils.isBlank(originImagePath)){
			resultMap.put("status", "error");
			resultMap.put("message", "原始路径无效！");
		} else if(StringUtils.isBlank(new_path)){
			resultMap.put("status", "error");
			resultMap.put("message", "输出路径无效！");
		} else if(colorMapCode == null || colorMapCode < 0){
			resultMap.put("status", "error");
			resultMap.put("message", "色彩编码无效！");
		}else {
			try {
				colorService.colorMapping(originImagePath, new_path, colorMapCode);
				resultMap.put("status", "success");
				resultMap.put("message", "成功！");
			} catch (Exception e) {
				e.printStackTrace();
				resultMap.put("status", "error");
				resultMap.put("message", "服务端转换异常！");
			}
		}
		System.out.println("| | |colorMapping---当前时间:" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + this.getClass().getSimpleName());
		return resultMap;
	}

	@RequestMapping(value="/hello", method = RequestMethod.POST)
	public String hello(HttpServletRequest request) throws IOException {
		request.setCharacterEncoding("utf-8");
		System.out.println(request.getParameterNames());
		
		System.err.println(request.getParameter("originImagePath"));
		
//		StringBuilder stringBuilder = new StringBuilder();
//		BufferedReader reader = request.getReader();
//		String line = null;
//		while((line = reader.readLine()) != null){
//			stringBuilder.append(line);
//		}
//		System.out.println(stringBuilder.toString());
		return "<b>Hello World!</b>";
	}
	
	@ResponseBody
	@PostMapping(value="/postTest")  //限定POST请求
	public String postTest(@RequestBody String json) {
//		ObjectMapper jacksonMapper = new ObjectMapper();
		try {
			HashMap mapObj = jacksonMapper.readValue(json, HashMap.class);
			System.err.println(mapObj.get("originImagePath"));
			System.err.println(mapObj.get("id"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "postTest";
	}
	
	public static String convert(String originImagePath, String new_path, int colorMapCode) {
		String result = null;
		Mat originMat = null;
		Mat destinMat = null;
		try {
			originMat = Imgcodecs.imread(originImagePath); // 读取原图像
			destinMat = new Mat(originMat.rows(), originMat.cols(), CvType.CV_8UC3); // 新建目标输出图像

			Imgproc.applyColorMap(originMat, destinMat, colorMapCode); // 执行图像彩色空间映射
			Imgcodecs.imwrite(new_path, destinMat); // 将转换结果写入到磁盘
			
			result = "{'status': 'ok', 'imgPath': '" + new_path + "'}";
			logger.info("END**********" + originImagePath + " SUCCESS**********");
		} catch (Exception e) {
			result = "{'status': 'error', 'imgPath': ''}";
			logger.error("END**********" + originImagePath + " FAILED**********");
			e.printStackTrace();
		}finally{
			if (destinMat != null) {
				originMat.release();
			}
			if (destinMat != null) {
				destinMat.release();
			}
		}
		
		return result;
	}

}
