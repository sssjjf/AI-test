package com.example.demo.controler;

import com.example.demo.bean.BranchExpress;
import com.example.demo.bean.Express;
import org.springframework.web.bind.annotation.RequestBody;

public interface BranchExpressController {
    int addExpress(BranchExpress branchExpress);
    int deleteExpress(Express branchExpress);
    String findAll();

}
