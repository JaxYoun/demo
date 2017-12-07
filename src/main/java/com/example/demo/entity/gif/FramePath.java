package com.example.demo.entity.gif;

import lombok.*;

import java.util.List;

/**
 * @Author：YangJx
 * @Description：帧传参实体
 * @DateTime：2017/12/7 22:38
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FramePath {
    private List<String> pathList;
}
