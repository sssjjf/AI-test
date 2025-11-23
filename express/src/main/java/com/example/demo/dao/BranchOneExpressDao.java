package com.example.demo.dao;

import com.example.demo.bean.BranchExpress;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface BranchOneExpressDao extends BranchExpressDao{

    int insertExpress(@Param("date") String date,
                      @Param("name") String name,
                      @Param("number") String number,
                      @Param("code") String code,
                      @Param("address") String address,
                      @Param("company") String company,
                      @Param("way") String way);

    int deleteExpressByCode(@Param("code") String code);

    BranchExpress[] findAll();
}
