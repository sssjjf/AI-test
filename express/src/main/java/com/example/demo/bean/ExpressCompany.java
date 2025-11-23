package com.example.demo.bean;

public enum ExpressCompany {
    ST(1,"申通"),
    JD(2,"京东"),
    YT(3,"圆通"),
    EMS(4,"邮政"),
    SF(5,"顺丰"),
    YD(6,"韵达"),
    TT(7,"天天"),
    BS(8,"百世"),
    ZT(9,"中通");

    private int id;
    private String name;

    ExpressCompany(int id, String name) {
        this.id=id;
        this.name=name;
    }
}
