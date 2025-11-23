package com.example.demo.controler;

import com.example.demo.bean.Managers;
import com.example.demo.bean.User;
import com.example.demo.dao.UserDao;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @Autowired
    UserDao userDao;

    @RequestMapping("/register")
    public String register(@RequestBody User user){
        user.setRole(Managers.commonUser);
        User[] userList = userDao.getUserList();
        for(User user1:userList){
            if(user.getUsername().equals(user1.getUsername())){
                return "error";
            }
        }
        int i= userDao.register(user.getUsername(),user.getPassword(),user.getRole());
        return i>0?"success":"error";
    }

    @RequestMapping("/addUser")
    public String addUser(@RequestBody User user){
        User[] userList = userDao.getUserList();
        for(User user1:userList){
            if(user1.getUsername().equals(user.getUsername())||user1.getPassword().equals(user.getPassword())){
                return "error";
            }
        }
        int i= userDao.addUser(user.getUsername(),user.getPassword(),user.getRole(),user.getE_mail(),user.getPhone());
        return i>0?"success":"error";
    }

    @RequestMapping("/getUserList")
    public String getUserList(){
        User[] userList = userDao.getUserList();
        Gson gson = new Gson();
        String gson_res = gson.toJson(userList);
        return gson_res;
    }


    @RequestMapping("/deleteUser")
    public int deleteUser(@RequestBody User user){
        if(user.getUsername().equals("admin")){
            return 0;
        }
       int result = userDao.deleteUser(user.getUsername());
       return result>0?1:-1;
    }

    @RequestMapping("/getUser")
    public User getUser(String username){
        User user = userDao.getUser(username);
        return user;
    }

    @RequestMapping("/updateUser")
    public int getUser(@RequestBody User user){
        if(user.getUsername().equals("admin")){
            return 0;
        }
        int res = userDao.updateUser(user);
        return res;
    }

}
