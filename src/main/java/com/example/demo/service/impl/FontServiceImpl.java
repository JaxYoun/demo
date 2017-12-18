package com.example.demo.service.impl;

import com.example.demo.entity.font.FontJsonParameter;
import com.example.demo.entity.font.FontParameter;
import com.example.demo.service.IFontService;
import com.google.typography.font.tools.sfnttool.SfntTool;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service(value = "fontService")
public class FontServiceImpl implements IFontService {

    private static final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
            6,
            8,
            60,
            TimeUnit.SECONDS,
            new LinkedBlockingDeque<>(16),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.DiscardOldestPolicy()
    );

    @Override
    public void extractFont(FontJsonParameter fontJsonParameter, String option, Map<String, Object> resultMap) throws IOException {

        for (FontParameter parm : fontJsonParameter.getFont_info()) {
            if (StringUtils.isBlank(parm.getExtract_str())) {
                resultMap.put("status", "error");
                resultMap.put("message", "待抽取字符 不能为空！");
            } else if (StringUtils.isBlank(parm.getFont_ori_path())) {
                resultMap.put("status", "error");
                resultMap.put("message", "原始字体库路径 不能为空！");
            } else if (StringUtils.isBlank(parm.getFont_path())) {
                resultMap.put("status", "error");
                resultMap.put("message", "抽取结果路径 不能为空！");
            } else {
                String[] paramArr = new String[4];
                paramArr[0] = option;
                paramArr[1] = parm.getExtract_str();
                paramArr[2] = parm.getFont_ori_path();
                paramArr[3] = parm.getFont_path();

                Thread t = new Thread("fontThread") {
                    @Override
                    public void run() {
                        try {
                            SfntTool.main(paramArr);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                };
                threadPool.submit(t);
            }
        }
    }

}