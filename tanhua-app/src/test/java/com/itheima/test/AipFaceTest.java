package com.itheima.test;


import cn.hutool.core.io.FileUtil;
import com.itheima.autoconfig.face.AipFaceTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;

@SpringBootTest
public class AipFaceTest {

    @Autowired
    private AipFaceTemplate template;

    @Test
    public void testAip() throws IOException {
        String filename = "/Users/zhangchanggeng/Desktop/test.jpg";
        File file = new File(filename);
        byte[] bytes = FileUtil.readBytes(file);
        System.out.println(template.detect(bytes));
    }
}