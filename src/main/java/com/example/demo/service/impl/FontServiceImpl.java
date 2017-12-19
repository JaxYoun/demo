package com.example.demo.service.impl;

import com.example.demo.entity.font.FontJsonParameter;
import com.example.demo.entity.font.FontParameter;
import com.example.demo.service.IFontService;
import com.google.typography.font.tools.sfnttool.SfntTool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Service
@Slf4j
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
    public List<String> extractFont(FontJsonParameter fontJsonParameter, String option, Map<String, Object> resultMap) throws IOException {

        List<String> destPathList = new ArrayList<>();
        List<FutureTask<String>> futureTaskList = new ArrayList<>();

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


                FutureTask<String> futureTask = new FutureTask<>(new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        SfntTool.main(paramArr);
                        return paramArr[3];
                    }
                });

                /*Thread thread = new Thread("fontThread") {
                    @Override
                    public void run() {
                        try {
                            SfntTool.main(paramArr);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                };*/

                threadPool.submit(futureTask);
                futureTaskList.add(futureTask);
            }
        }

        for (FutureTask<String> it : futureTaskList) {
            try {
                destPathList.add(it.get());
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            } catch (ExecutionException e) {
                log.error(e.getMessage(), e);
            }
        }

        return destPathList;
    }

}