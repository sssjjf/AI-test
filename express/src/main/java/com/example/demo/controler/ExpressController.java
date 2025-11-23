package com.example.demo.controler;

import com.example.demo.bean.Express;
import com.example.demo.dao.ExpressDao;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class ExpressController {
    @Autowired
    ExpressDao expressDao;

    @RequestMapping("/addExpress")
    public int addExpress(@RequestBody Express express){
        Express[] expressList = expressDao.findAll();
        for(Express e:expressList){
            if(e.getCode().equals(express.getCode())){
                return -1;
            }
        }
        int result = expressDao.insertExpress(
                express.getDate().substring(0,10),
                express.getName(),
                express.getNumber(),
                express.getCode(),
                express.getAddress(),
                express.getCompany(),
                express.getBelongTo());
        return result>0?1:-1;
    }

    @RequestMapping("/deleteExpress")
    public int deleteExpress(@RequestBody Express express){
        int result = expressDao.deleteExpressByCode(express.getCode());
        return result>0?1:-1;
    }

    @RequestMapping("/findAll")
    public String findAll(){
        Express[] expressList = expressDao.findAll();
        Gson gson = new Gson();
        String gson_res = gson.toJson(expressList);
        return gson_res;
    }
}
