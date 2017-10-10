package com.example.demo.controller;

import com.example.demo.util.RedisUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/redis")
public class RedisController {

    /**
     * 此接口用于重新加载redis数据（适用于redis数据更新后需要及时加载的情况）
     * 可在浏览器调用，也可用postman【GET】
     * @return
     */
    @GetMapping(value = "/reloadAllRedisKey")
    public String reloadAllRedisKey() {
        RedisUtils.loadAllValueFromRedis();
        return "Reload Successfully！";
    }
}
