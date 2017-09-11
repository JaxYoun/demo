package com.example.demo.caller;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import com.example.demo.entity.Image;
import com.example.demo.entity.KeyWord;

public class Adaptor implements Callable<Double> {

	private Map<String, Double> ratioMap;
	private Image image;
	private List<KeyWord> keyWordList;
	private Image dbImage;

	public Adaptor() {
		super();
	}

	public Adaptor(Map<String, Double> ratioMap, Image image, List<KeyWord> keyWordList, Image dbImage) {
		super();
		this.ratioMap = ratioMap;
		this.image = image;
		this.keyWordList = keyWordList;
		this.dbImage = dbImage;
	}

	@Override
	public Double call() throws Exception {
		Double score = 0D;
		Double sum = 0D;
		for (KeyWord keyWord : keyWordList) {
			sum += fit(keyWord, dbImage);
		}
//		return sum + image.getWeight();
		Double keyRat = image.getKeywords_rate();
		if(keyRat != null){
			score = sum * keyRat + image.getWeight() * (1 - keyRat);
		}else{
			score = sum;
		}
		return score;
	}

	/**
	 * 
	 * @param keyWord
	 * @param imageCode
	 * @return
	 */
	public Double fit(KeyWord keyWord, Image dbImage) {

		double score = 0D;
		// Image imageFromMap = inputImageMap.get(image.getId());
		if (dbImage != null) {
			for (KeyWord it : dbImage.getKeyWordList()) {
				try {
					if (keyWord.getValue().equals(it.getValue())) {
						score = keyWord.getPow() + it.getPow();
					} else if ("0".equals(keyWord.getPosition().trim()) || "0".equals(it.getPosition().trim())) {
						score = 0.4 * it.getPow();
					} else {
						double sum = 0D;
						HashSet<String> diffSet = getDiff(keyWord.getPosition(), it.getPosition(), "-");
						for (String cod : diffSet) {
							sum += ratioMap.get(cod);
						}
						score = (1 - (sum / 10)) * it.getPow();
					}
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
			}
		}
		return score * keyWord.getPow();
	}

	/**
	 * 
	 * @param frontString
	 * @param backString
	 * @param separator
	 * @return
	 */
	public HashSet<String> getDiff(String frontString, String backString, String separator) {

		HashSet<String> diff1 = new HashSet<String>();
		HashSet<String> diff2 = new HashSet<String>();
		HashSet<String> diff3 = new HashSet<String>();

		String[] frontPostArr = frontString.split(separator);
		String[] backPostArr = backString.split(separator);

		diff1.clear();
		diff1.addAll(new HashSet<String>(Arrays.asList(backPostArr)));
		diff1.removeAll(new HashSet<String>(Arrays.asList(frontPostArr)));

		diff2.clear();
		diff2.addAll(new HashSet<String>(Arrays.asList(frontPostArr)));
		diff2.removeAll(new HashSet<String>(Arrays.asList(backPostArr)));

		diff3.clear();
		diff3.addAll(diff1);
		diff3.addAll(diff2);

		return diff3;
	}

}
