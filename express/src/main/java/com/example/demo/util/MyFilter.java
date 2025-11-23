package com.example.demo.util;

import com.example.demo.dao.ExpressDao;
import org.springframework.core.annotation.Order;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;


@Order(1)
@WebFilter(urlPatterns = "/testLogin",filterName = "myFilter")
public class MyFilter implements Filter {
    @Resource
    private ExpressDao expressDao;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("过滤器初始化");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        System.out.println("过滤请求！");
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpSession session = request.getSession(false);
        String servletPathInFact = request.getServletPath();
        String filetPath = "/toLogin";
        if(filetPath.equals(servletPathInFact)||session==null){
            servletResponse.setCharacterEncoding("utf-8");
            servletResponse.setContentType("text/html");
            PrintWriter writer = servletResponse.getWriter();
            writer.println("你还未登录！");
            return;
        }

        filterChain.doFilter(servletRequest,servletResponse);
        System.out.println("过滤响应");
    }

    @Override
    public void destroy() {
        System.out.println("过滤器销毁");
    }
}
