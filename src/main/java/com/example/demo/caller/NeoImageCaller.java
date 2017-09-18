package com.example.demo.caller;

import com.example.demo.entity.neotemplate.*;

import java.util.*;
import java.util.concurrent.Callable;

public class NeoImageCaller implements Callable<NeoResponseImage> {

    private Set<String> idSet;
    private String type;
    private NeoRequestImage neoRequestImage;
    private List<NeoRedisParentImage> neoRedisParentImageList;
    private Map<String, Float> ratioMap;
    private static final List<Float> floatList = Arrays.asList(0.1F, 0.14F, 0.2F, 0.25F, 0.33F, 0.5F, 0.67F, 1F, 1.5F, 2F, 3F, 4F, 5F, 7F, 10F, 12F);

    public NeoImageCaller(String type, NeoRequestImage neoRequestImage, List<NeoRedisParentImage> neoRedisParentImageList, Map<String, Float> ratioMap, Set<String> idSet) {
        super();
        this.type = type;
        this.neoRequestImage = neoRequestImage;
        this.neoRedisParentImageList = neoRedisParentImageList;
        this.ratioMap = ratioMap;
        this.idSet = idSet;
    }

    @Override
    public NeoResponseImage call() throws Exception {
        List<NeoRedisParentImage> filtedRedisImageList = filteNeoRedisParentImage();
        return filtedRedisImageList.size() > 0 ? picOneImageFromFiltedListByKeyword(filtedRedisImageList) : null;
    }

    /**
     * 根据关键词从筛选图片中挑选出得分最高的一张
     * @param filtedRedisImageList
     * @return
     */
    public NeoResponseImage picOneImageFromFiltedListByKeyword(final List<NeoRedisParentImage> filtedRedisImageList) {

        List<MyBage> myBageList = new ArrayList<>();
        for(NeoRedisParentImage filtedRedisParentImage : filtedRedisImageList) {
            MyBage myBage = new MyBage();
            myBage.setId(filtedRedisParentImage.getId());
            myBage.setNeoRedisParentImage(filtedRedisParentImage);

            /**
             * 用请求关键字keyWordList的字面值和上一步结果列表中的keyWordsList字面值取交集，需考虑单个关键词字面值内部包含关系，相当于like模糊查询，
             * 相等加10，包含加5，累加，得出该请求图片和某个搜索结果图片的总得分。
             */
            Float keywordScore = getKeywordJiaoJi(neoRequestImage.getKeyWordList(), filtedRedisParentImage.getKeyWordList());

            /**
             * 用请求数据的pos字段与搜索结果列表中的station字段求差集，用差集查询redis中的ratio，累加求距离，
             * （1-距离/100） * 请求数据的pow  * 结果集大图的weight。
             */
            Set<Integer> diffSet = getDiffCount(neoRequestImage.getPos(), filtedRedisParentImage.getStation());

            float distance = 0F;
            for(Integer inte : diffSet){
                distance += ratioMap.get(inte.toString());
            }
            float positionScore = (1 - distance / 100) * neoRequestImage.getPow() * filtedRedisParentImage.getWeight();

            /**
             * 以上两步结果相加乘以请求数据的keywords_rate, 再加上，【图片搜索】中第6步计算的【H】值*(1 - keywords_rate)。
             */
            float finalScore = (keywordScore + positionScore) * neoRequestImage.getKeywords_rate() + (filtedRedisParentImage.getLabelScore() * (1 - neoRequestImage.getKeywords_rate()));
            myBage.setScore(finalScore);

            myBageList.add(myBage);
        }

        /**
         *挑出得分最高的一张大图
         */
        List<MyBage> sortedMyBageList = sortList(myBageList, false);
        int myBageListSize = sortedMyBageList.size();
        NeoRedisParentImage finalNeoRedisParentImage = null;
        for (int i = 0; i < myBageListSize; i++) {
            String imageId = sortedMyBageList.get(i).getId();
            if (!idSet.contains(imageId)){
                finalNeoRedisParentImage = sortedMyBageList.get(i).getNeoRedisParentImage();
                idSet.add(imageId);
                break;
            }
        }

        NeoResponseImage kk = new NeoResponseImage();
        kk.setImg_id(finalNeoRedisParentImage.getId());
        kk.setSmall_id(finalNeoRedisParentImage.getImg_info().get(0).getId());
        kk.setPrice(finalNeoRedisParentImage.getPrice());
        kk.setPage_id(neoRequestImage.getPage_id());
        kk.setEl_id(neoRequestImage.getId());
        kk.setWidth(neoRequestImage.getWidth());
        kk.setHeight(neoRequestImage.getHeight());

        return kk;
    }


    /**
     * list排序
     *
     * @param list
     * @param isAsc 是否升序，默认为升序
     *
     * @return
     */
    public List<MyBage> sortList(List<MyBage> list, boolean isAsc) {
        if (list != null && list.size() > 0) {
            if (isAsc) {
                list.sort((h1, h2) -> h1.getScore().compareTo(h2.getScore()));
            } else {
                Comparator<MyBage> comparator = (h1, h2) -> h1.getScore().compareTo(h2.getScore());
                list.sort(comparator.reversed());
            }
        }
        return list;
    }


    /**
     * 关键字，字面值求交集
     * @param requestKeywordList
     * @param redisKeywordList
     * @return
     */
    public Float getKeywordJiaoJi(List<String> requestKeywordList, List<String> redisKeywordList) {
        Float cou = 0F;
        for(String request : requestKeywordList) {
            for(String redis : redisKeywordList){
                if(request.equals(redis)){
                    cou += 10F;
                }else if(request.indexOf(redis) >= 0 || redis.indexOf(request) >= 0){
                    cou += 5F;
                }
            }

        }
        return cou;
    }


    /**
     * 求差集
     * @return
     */
    public Set<Integer> getDiffCount(List<Integer> requestPos, List<Integer> redisStation) {

        HashSet<Integer> diff1 = new HashSet<>();
        HashSet<Integer> diff2 = new HashSet<>();
        HashSet<Integer> diff3 = new HashSet<>();

        diff1.clear();
        diff1.addAll(new HashSet<Integer>(redisStation));
        diff1.removeAll(new HashSet<Integer>(requestPos));

        diff2.clear();
        diff2.addAll(new HashSet<Integer>(requestPos));
        diff2.removeAll(new HashSet<Integer>(redisStation));

        diff3.clear();
        diff3.addAll(diff1);
        diff3.addAll(diff2);

        return diff3;
    }

    /**
     * 步骤1：用单张请求图片从redis中过滤筛选出 少量大图列表
     * @return
     */
    public List<NeoRedisParentImage> filteNeoRedisParentImage() {
        List<NeoRedisParentImage> neoRParentImageList = new ArrayList<>();

        //1：标签筛选
        int inputLeng = neoRequestImage.getSearch_labels().size();
        for (NeoRedisParentImage neoRedisParentImage : neoRedisParentImageList){
            int count = 0;
            for(List<Integer> subLabelList : neoRequestImage.getSearch_labels()){
                for(Integer lable : subLabelList) {
                    if (neoRedisParentImage.getLabel_info().contains(lable)) {
                        count++;
                        break;
                    }
                }
            }
            if(inputLeng == count){
                neoRParentImageList.add(neoRedisParentImage);
            }
        }

        if(neoRParentImageList.size() == 0){  //如果通过search_label从redis匹配不到图片，就将整个redis表作为下一步的源数据集
            neoRParentImageList = neoRedisParentImageList;
        }

        //2：小图筛选
        float rate = 0F;
        if(neoRequestImage.getWidth() != null && neoRequestImage.getHeight() != null) {
            float kk = 1F * neoRequestImage.getWidth() / neoRequestImage.getHeight();
            rate = getConst(getFloatRemainTwoDot(kk));
        }

        List<NeoRedisParentImage> filtedRedisParentImageList = new ArrayList<>();
        for (NeoRedisParentImage rImage : neoRParentImageList) {
            List<NeoSubImage> subImageList0 = adaptSubImageOfSingleImage(type, rate, neoRequestImage.getPosition(), neoRequestImage.getWidth(), neoRequestImage.getHeight(), rImage);
            if (subImageList0.size() > 0) {
                NeoRedisParentImage imgIt;
                try {
                    imgIt = rImage.clone();
                    imgIt.setImg_info(subImageList0);
                    filtedRedisParentImageList.add(imgIt);
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
            }
        }

        //对于svg型图片，如果position未匹配到任何大图，就返回粗筛结果
        if(("svg_key".equals(type) || "svg_icon_key".equals(type)) && filtedRedisParentImageList.size() == 0){
            filtedRedisParentImageList = neoRParentImageList;
        }

        //3：标签权重计算
        List<NeoRedisParentImage> weightedRedisParentImageList = new ArrayList<>();
        for (NeoRedisParentImage getIdAndWeight : filtedRedisParentImageList) {
            NeoRedisParentImage tempRImage = null;
            try {
                tempRImage = getIdAndWeight.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            tempRImage.setLabelScore(getLabelScore(neoRequestImage.getLabel(), getIdAndWeight.getLabel_info()));
            weightedRedisParentImageList.add(tempRImage);
        }



        return weightedRedisParentImageList;
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
    public static Float getLabelScore(List<Integer> guestLabel, List<Integer> hostLabelInfo) {
        Float count = 0F;
        if(guestLabel != null){
            for (Integer hostLabel : hostLabelInfo) {
                if (guestLabel.contains(hostLabel)) {
                    count += 10F;
                }
            }
        }
        return count;
    }

    /**
     * 宽高比常量查询函数
     * @param input
     * @return
     */
    public float getConst(float input) {
        float result = 0F;
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

    /**
     * 小图筛选函数
     * @param type
     * @param rate
     * @param positionList
     * @param width
     * @param height
     * @param redisImage
     * @return
     */
    public List<NeoSubImage> adaptSubImageOfSingleImage(String type, float rate, List<Float> positionList, Integer width, Integer height, NeoRedisParentImage redisImage) {
        List<NeoSubImage> subimageList = new ArrayList<>();
        NeoSubImage subImg = null;
        for (NeoSubImage sub : redisImage.getImg_info()) {
            boolean isPositionListBlank = (positionList == null || positionList.size() <= 0);

            switch (type) {
                case "img_key" : {
                    if (!isPositionListBlank && positionList.contains(sub.getPosition()) && rate == sub.getXy() && width <= sub.getWidth() && height <= sub.getHeight()) {
                        subimageList.add(sub);
                    }else if(rate == sub.getXy() && width <= sub.getWidth() && height <= sub.getHeight()){
                        subimageList.add(sub);
                    }
                    break;
                }
                case "my_img_key" : {
                    if (!isPositionListBlank && positionList.contains(sub.getPosition()) && rate == sub.getXy() && width <= sub.getWidth() && height <= sub.getHeight()) {
                        subimageList.add(sub);
                    }else if(rate == sub.getXy() && width <= sub.getWidth() && height <= sub.getHeight()){
                        subimageList.add(sub);
                    }
                    break;
                }
                case "tmp_key" : {
                    if (!isPositionListBlank && positionList.contains(sub.getPosition()) && rate == sub.getXy() && width <= sub.getWidth() && height <= sub.getHeight()) {
                        subimageList.add(sub);
                    }else if(rate == sub.getXy() && width <= sub.getWidth() && height <= sub.getHeight()){
                        subimageList.add(sub);
                    }
                    break;
                }
                case "png_key" : {
                    if (!isPositionListBlank && positionList.contains(sub.getPosition()) && width <= sub.getWidth() && height <= sub.getHeight()) {
                        subimageList.add(sub);
                    }else if(width <= sub.getWidth() && height <= sub.getHeight()){
                        subimageList.add(sub);
                    }
                    break;
                }
                case "svg_key" : {
                    if (!isPositionListBlank && positionList.contains(sub.getPosition())) {
                        subimageList.add(sub);
                    }
                    break;
                }
                case "svg_icon_key" : {
                    if (!isPositionListBlank && positionList.contains(sub.getPosition())) {
                        subimageList.add(sub);
                    }
                    break;
                }
            }
        }

        int sise = subimageList.size();
        if (sise > 0) {
            subImg = subimageList.get(new Random().nextInt(sise));  //随机取出一张小图
        }
        return subimageList;
    }

    /**
     * 保留两位小数【不四舍五入】
     * @param originFloat
     * @return
     */
    public float getFloatRemainTwoDot(final float originFloat){
       return ((float)((int) (originFloat * 100))) / 100;
    }

//    public static void main(String[] args) { }

}
