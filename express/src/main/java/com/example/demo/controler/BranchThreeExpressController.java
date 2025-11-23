package com.example.demo.controler;

import com.example.demo.bean.BranchExpress;
import com.example.demo.bean.Express;
import com.example.demo.dao.BranchOneExpressDao;
import com.example.demo.dao.BranchThreeExpressDao;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/branch3")
public class BranchThreeExpressController implements BranchExpressController{

    @Autowired
    public BranchThreeExpressDao branchThreeExpressDao;


    @Override
    @RequestMapping("/AddExpress")
    public int addExpress(@RequestBody BranchExpress branchExpress){
        BranchExpress[] branchExpressList = branchThreeExpressDao.findAll();
        for(BranchExpress e:branchExpressList){
            if(e.getCode().equals(branchExpress.getCode())){
                return -1;
            }
        }

        int result = branchThreeExpressDao.insertExpress(
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
        int result = branchThreeExpressDao.deleteExpressByCode(branchExpress.getCode());
        return result>0?1:-1;
    }

    @Override
    @RequestMapping("/FindAll")
    public String findAll(){
        BranchExpress[] branchExpressList = branchThreeExpressDao.findAll();
        Gson gson = new Gson();
        String gson_res = gson.toJson(branchExpressList);
        return gson_res;
    }
}
