package com.example.demo.controller;

import com.huaban.analysis.jieba.JiebaSegmenter;

public class JieBaTest {

	public static void main(String[] args) {
		String text = "北风网已获得某国际著名风投公司的A轮投资。这也是目前国内IT在线企业中首家获得A轮以上规模投资的企业。国际著名投资机构的专业眼光，令人信服地证明了北风网在IT在线教育行业内无可争议的主导地位！";
        JiebaSegmenter segmenter = new JiebaSegmenter();
        System.out.println(segmenter.sentenceProcess(text));
	}

}
