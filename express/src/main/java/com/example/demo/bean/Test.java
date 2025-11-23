package com.example.demo.bean;

import org.springframework.boot.context.properties.ConfigurationProperties;

import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "test")
public class Test {
    private String name;
    private float[] ages ;

    public Test() {
        System.out.println("创建Test bean——————————————————————————");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float[] getAges() {
        return ages;
    }

    public void setAges(float[] ages) {
        this.ages = ages;
    }
}
