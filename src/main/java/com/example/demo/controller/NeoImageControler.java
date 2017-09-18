package com.example.demo.controller;

import com.example.demo.entity.neotemplate.NeoRequestPacket;
import com.example.demo.entity.neotemplate.NeoResponsePacket;
import com.example.demo.service.IImageService;
import com.example.demo.util.RedisUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
//@RequestMapping(value = "/image")
@RequestMapping(value = "/template")
public class NeoImageControler {

    @Autowired
    private static RedisUtils redisUtils;

    @Autowired
    private IImageService imageService;

    private static final ObjectMapper jacksonMapper = new ObjectMapper();

    @RequestMapping(value = "/getImageByTemplate", method = RequestMethod.POST)
    public NeoResponsePacket getImageByTemplate(@RequestBody String json) {

        System.out.println("| | |NeoImageControler.getImageByTemplate 当前时间为:" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        NeoResponsePacket neoResponsePacket = null;
        String arg = null;
        try {
            arg = URLDecoder.decode(json,"UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }

        try {
            NeoRequestPacket neoRequestPacket = jacksonMapper.readValue(arg, NeoRequestPacket.class);
            neoResponsePacket = imageService.getImageByTemplate(neoRequestPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return neoResponsePacket;
    }
}
