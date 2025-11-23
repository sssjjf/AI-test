package com.example.demo.dao;

import com.example.demo.bean.Express;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpressDao {
    public int deleteExpressByCode(@Param("code") String code); //下架、取快递
    public int insertExpress(@Param("date") String date,
                             @Param("name") String name,
                             @Param("number") String number,
                             @Param("code") String code,
                             @Param("address") String address,
                             @Param("company") String company,
                             @Param("belongTo") String belongTo); //上架、收快递
    public Express[] findAll();
}
