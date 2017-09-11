package com.example.demo.caller;

import com.example.demo.entity.Image;
import com.example.demo.entity.template.SubImage;
import com.example.demo.entity.template.TemplateImage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

public class TemplateAdaptor implements Callable<List<Image>> {

	private String type;
	private TemplateImage tamplateImage;
	private List<Image> redisImageList;

	public TemplateAdaptor() {
		super();
	}

	public TemplateAdaptor(String type, TemplateImage tamplateImage, List<Image> redisImageList) {
		super();
		this.type = type;
		this.tamplateImage = tamplateImage;
		this.redisImageList = redisImageList;
	}

	@Override
	public List<Image> call() throws Exception {
		return fit(tamplateImage, redisImageList);
	}

	public List<Image> fit(TemplateImage tamplateImage, List<Image> redisImageList) {

		// 第一步：标签筛选
		List<Image> rImageList = new ArrayList<Image>();
		int leng = tamplateImage.getSearch_labels().size();
		for (Image redisImage : redisImageList) {
			int count = 0;
			for (List<Integer> labelArrList : tamplateImage.getSearch_labels()) {
				for (Integer lable : labelArrList) {
					if (redisImage.getLabel_info().contains(lable)) {
						count++;
						break;
					}
				}
			}

			if (leng <= count) {
                rImageList.add(redisImage);
			}
		}

		// 第二步：小图筛选
		if(rImageList.size() == 0){
			rImageList = redisImageList;  //如果通过search_label从redis匹配不到图片，就将整个redis表作为下一步的源数据集
		}

        float rate = 0F;
		if(tamplateImage.getWidth() != null && tamplateImage.getHeight() != null){
            float kk = (float) (tamplateImage.getWidth() / tamplateImage.getHeight());  //yjx?
            rate = getTop(kk);
        }
		List<Double> positionList = tamplateImage.getPosition();

		List<Image> iList = new ArrayList<>();
		if(positionList == null || positionList.size() <= 0){
			for (Image rImage : rImageList) {
				Image imgIt;
				try {
					imgIt = rImage.clone();
					iList.add(imgIt);
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
			}
		}else{
			for (Image rImage : rImageList) {
				List<SubImage> subImageList0 = adaptSubImageOfSingleImage(type, rate, positionList, tamplateImage.getWidth(), tamplateImage.getHeight(), rImage);
				if (subImageList0.size() > 0) {
					Image imgIt;
					try {
						imgIt = rImage.clone();
						imgIt.setImg_info(subImageList0);
						iList.add(imgIt);
					} catch (CloneNotSupportedException e) {
						e.printStackTrace();
					}
				}
			}
		}

		// 第三步：标签权重计算
		List<Image> imageWithIdAndWeight = new ArrayList<>();
		for (Image getIdAndWeight : iList) {
			Image tempImage = null;
			try {
				tempImage = getIdAndWeight.clone();
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
			tempImage.setWeight(getLabelWeight(tamplateImage.getLabel(), getIdAndWeight.getLabel_info()));
			imageWithIdAndWeight.add(tempImage);
		}

		return imageWithIdAndWeight;
	}

    /**
     * 通过求传入图片与redis中筛选出的图片的label的交集，来确定权重，策略如下：
     * 1.如果传入图片label数组为null或没有交集，权重为0
     * 2.如果有交集，则每个公共元素贡献2个权重值
     *
     * @param guestLabel 传入图片的label数组
     * @param hostLabelInfo redis图片的label数组
     * @return
     */
	public static Double getLabelWeight(List<Double> guestLabel, List<Integer> hostLabelInfo) {
		Double count = 0D;
		if(guestLabel != null){
            for (Integer hostLabel : hostLabelInfo) {
                Double db = Double.valueOf(hostLabel.intValue());
                if (guestLabel.contains(db)) {
                    count += 2D;
                }
            }
        }
		return count;
	}

	public List<SubImage> adaptSubImageOfSingleImage(String type, float rate, List<Double> positionList, Integer width, Integer height, Image redisImage) {
		List<SubImage> subimageList = new ArrayList<SubImage>();
		SubImage subImg = null;
		for (SubImage sub : redisImage.getImg_info()) {
//			System.err.println(rate + "||" + positionList.toString() + "||" + width + "||" + height);
//			System.out.println(sub.getPosition() + "||" + sub.getXy() + "||" + sub.getWidth() + "||" + sub.getHeight());

			switch (type) {
				case "img_key" : {
					if (positionList.contains(sub.getPosition()) && rate == sub.getXy() && width <= sub.getWidth() && height <= sub.getHeight()) {
						subimageList.add(sub);
					}else if(rate == sub.getXy() && width <= sub.getWidth() && height <= sub.getHeight()){
						subimageList.add(sub);
					}
					break;
				}
				case "tmp_key" : {
					if (positionList.contains(sub.getPosition()) && rate == sub.getXy() && width <= sub.getWidth() && height <= sub.getHeight()) {
						subimageList.add(sub);
					}else if(rate == sub.getXy() && width <= sub.getWidth() && height <= sub.getHeight()){
						subimageList.add(sub);
					}
					break;
				}
				case "png_key" : {
					if (positionList.contains(sub.getXy()) && width <= sub.getWidth() && height <= sub.getHeight()) {
						subimageList.add(sub);
					}
					break;
				}
				case "svg_key" : {
					if (positionList.contains(sub.getXy())) {
						subimageList.add(sub);
					}
					break;
				}

			}

		}
		if (subimageList.size() > 0) {
			subImg = subimageList.get(0);
		}
		return subimageList;
	}

	public float getTop(float input) {
		float result = 0F;
		List<Float> floatList = Arrays.asList(0.1F, 0.14F, 0.2F, 0.25F, 0.33F, 0.5F, 0.67F, 1F, 1.5F, 2F, 3F, 4F, 5F, 7F, 10F, 12F);
		int leng = floatList.size();

		if (!floatList.contains(input)) {
            if (input >= floatList.get(leng - 1)) {
				result = floatList.get(leng - 1);
			} else {
				for (int i = 0; i < leng; i++) {
					if (input < floatList.get(i)) {
						result = floatList.get(i);
						break;
					}
				}
			}
		} else {
			result = input;
		}
		return result;
	}

	public TemplateImage getTamplateImage() {
		return tamplateImage;
	}
	public void setTamplateImage(TemplateImage tamplateImage) {
		this.tamplateImage = tamplateImage;
	}
	public List<Image> getRedisImageList() {
		return redisImageList;
	}
	public void setRedisImageList(List<Image> redisImageList) {
		this.redisImageList = redisImageList;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	public static void testFor() {
		outter: for (int i = 0; i < 10; i++) {
			inner: for (int j = 0; j < 10; j++) {
				if (j == 5) {
					break inner;
				}
				System.err.println("inner | |" + j);
			}
			System.out.println("outter | |" + i);
		}
	}

	public static void breakTest() {
		List<Integer> list01 = Arrays.asList(1, 2, 3);
		List<Integer> list02 = Arrays.asList(4, 5);
		List<List<Integer>> list = new ArrayList<>();

		list.add(list01);
		list.add(list02);

		int count = 0;
		int leng = list.size();

		List<Integer> list03 = Arrays.asList(1, 2, 3);
		for (List<Integer> it : list) {
			for (Integer i : it) {
				if (list03.contains(i)) {
					count++;
					break;
				}
			}
		}
		System.out.println(count == leng);
		System.out.println(count + "| |" + leng);
	}

	public static void main(String[] args) {
		// System.out.println(getTop(0.6F));

		/*
		 * List<Integer> integerList = Arrays.asList(1, 4, 5); List<Double>
		 * doubleList = Arrays.asList(1D, 4D, 5D);
		 * System.out.println(getLabelWeight(doubleList, integerList));
		 */

		// testFor();

		// breakTest();

		List<Integer> list01 = Arrays.asList(1, 2, 3);
		System.out.println(list01.toString());

	}
}
