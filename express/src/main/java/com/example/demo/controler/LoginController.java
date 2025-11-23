package com.example.demo.controler;

import com.example.demo.bean.Test;
import com.example.demo.bean.User;
import com.example.demo.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@RestController
public class LoginController {
    private int a=0;

    @Resource
    UserDao userDao;

    @Resource
    private Test test;

    @RequestMapping("/login")
    public String login(@RequestBody User user) {
        User us = userDao.getUserByMessage(user.getUsername(), user.getPassword());
        if (us == null) {
            return null;
        }
        String s = us.getRole().substring(0, 7);
        if (s.equals("midManager")) {
            return s;
        }
        return us.getRole();
    }

    @RequestMapping(value = "/toLogin", method = RequestMethod.GET)
    public String login(HttpServletRequest request, HttpServletResponse response,String username, String password) throws InterruptedException {
        HttpSession session = request.getSession();
        session.setMaxInactiveInterval(30*60);
        String sessionId = session.getId();
        session.setAttribute("username",username);
        session.setAttribute("password",password);
        Cookie cookie = new Cookie("JSESSIONID",sessionId);
        cookie.setPath(request.getContextPath());
        response.addCookie(cookie);
        return "success";
    }

    @RequestMapping("/testLogin")
    public String testLogin() throws InterruptedException {
        System.out.println(test.getName());
        if(test.getAges()!=null) {
            for (float i : test.getAges()) {
                System.out.println(i);
            }
        }
        return "success";
    }

}
