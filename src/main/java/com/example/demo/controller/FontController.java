package com.example.demo.controller;

import com.example.demo.entity.font.FontJsonParameter;
import com.example.demo.entity.font.FontParameter;
import com.example.demo.service.IFontService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/font")
public class FontController {

    @Autowired
    private IFontService fontService;

    private static final String OPTION = "-s";  //google包之命令选项

    private static final ObjectMapper jacksonMapper = new ObjectMapper();

    /**
     * 字体抽取
     * @return 返回消息
     */
    @PostMapping("/extractFont")
    public Map<String, Object> extractFont(@RequestBody String json) {

        Map<String, Object> resultMap = new HashMap<>();
        String arg = null;
        try {
            arg = URLDecoder.decode(json, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        try {
            FontJsonParameter fontJsonParameter = jacksonMapper.readValue(arg, FontJsonParameter.class);
            List<String> destPathList = fontService.extractFont(fontJsonParameter, OPTION, resultMap);

            while(destPathList.size() != 0) {
                Iterator<String> it = destPathList.iterator();
                if(it.hasNext() && ifFileExist(it.next())) {
                    it.remove();
                }
            }

            resultMap.put("status", "success");
            resultMap.put("message", "抽取成功！");
        } catch (IOException e) {
            e.printStackTrace();
            resultMap.put("status", "error");
            resultMap.put("message", "服务端抽取异常！");
        }finally {
            System.out.println("| | |extractFont---当前时间:" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + this.getClass().getSimpleName());
        }
        return resultMap;
    }

    public boolean ifFileExist(String filePath) {
        boolean isExist;
        File file = new File(filePath);
        isExist = file.exists();
        return isExist;
    }

}
