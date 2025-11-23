package com.example.demo.dao;

import com.example.demo.bean.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao {
    public User getUserByMessage(@Param("username") String username,@Param("password") String password);
    public int register(@Param("username") String username,@Param("password") String password,@Param("role") String role);

    public User[] getUserList();

    public int deleteUser(@Param("username")String username);

    public int addUser(
            @Param("username")String username,
            @Param("password")String password,
            @Param("role")String role,
            @Param("e_mail")String e_mail,
            @Param("phone")String phone);

    public User getUser(@Param("username")String username);

    int updateUser(@Param("user") User user);
}
