package com.example.demo.controler;

import com.example.demo.bean.BranchExpress;
import com.example.demo.bean.Express;
import com.example.demo.dao.BranchOneExpressDao;
import com.google.gson.Gson;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Calendar;
import java.util.Date;

@RestController
@RequestMapping("/branch1")
public class BranchOneExpressController implements BranchExpressController {

    @Autowired
    public BranchOneExpressDao branchOneExpressDao;

    @Override
    @RequestMapping("/AddExpress")
    public int addExpress(@RequestBody BranchExpress branchExpress){
        BranchExpress[] branchExpressList = branchOneExpressDao.findAll();
        for(BranchExpress e:branchExpressList){
            if(e.getCode().equals(branchExpress.getCode())){
                return -1;
            }
        }
        int result = branchOneExpressDao.insertExpress(
                branchExpress.getDate().substring(0,10),
                branchExpress.getName(),
                branchExpress.getNumber(),
                branchExpress.getCode(),
                branchExpress.getAddress(),
                branchExpress.getCompany(),
                branchExpress.getWay());
        return result>0?1:-1;
    }

    @Override
    @RequestMapping("/DeleteExpress")
    public int deleteExpress(@RequestBody Express branchExpress){
        int result = branchOneExpressDao.deleteExpressByCode(branchExpress.getCode());
        return result>0?1:-1;
    }

    @Override
    @RequestMapping("/FindAll")
    public String findAll(){
        BranchExpress[] branchExpressList = branchOneExpressDao.findAll();
        Gson gson = new Gson();
        String gson_res = gson.toJson(branchExpressList);
        return gson_res;
    }

    public String getDate(Express express){
        String code = new String();
        Calendar calendar =Calendar.getInstance();
        return "3-4-4";
    }
}
