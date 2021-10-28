package com.itheima.test;

import com.itheima.autoconfig.oss.OssTemplate;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;


@SpringBootTest
class OssTest {

    @Autowired
    private OssTemplate ossTemplate;

    @Test
    public void testUploadFile() throws FileNotFoundException {
        String file = "/Users/zhangchanggeng/Desktop/109951163510210613.jpeg";
        String url = ossTemplate.upload(file, new FileInputStream(file));
        System.out.println(url);
    }
    
}