package com.example.demo.dao;

import com.example.demo.bean.BranchExpress;
import org.apache.ibatis.annotations.Param;

public interface BranchExpressDao {
    int insertExpress(String date,String name, String number, String code, String address, String company);
    int deleteExpressByCode(String code);
    BranchExpress[] findAll();
}
