package com.example.demo.bean;

import java.util.Scanner;

public class User {
    private String username;
    private String password;
    private String role;
    private String e_mail;
    private String phone;

    public User(String username, String password,String role, String e_mail, String phone) {
        this.username = username;
        this.password = password;
        this.role=role;
        this.e_mail = e_mail;
        this.phone = phone;
    }

    public User() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(String role){
        this.role=role;
    }
    public String getRole(){
        return this.role;
    }

    public String getE_mail() {
        return e_mail;
    }

    public void setE_mail(String e_mail) {
        this.e_mail = e_mail;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {

        Scanner input = new Scanner(System.in);
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                ", e_mail='" + e_mail + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
