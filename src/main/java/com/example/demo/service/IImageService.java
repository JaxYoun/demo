package com.example.demo.service;

import com.example.demo.entity.neotemplate.NeoRequestPacket;
import com.example.demo.entity.neotemplate.NeoResponsePacket;

public interface IImageService {

    NeoResponsePacket getImageByTemplate(final NeoRequestPacket neoRequestPacket);

}
