package com.example.demo.bean;

public class Express {
    private String date;
    private String name;
    private String number;
    private String code;
    private String address;
    private String company;
    private String belongTo;


    public Express() {

    }

    public Express(String date,String name, String number, String code, String address, String company, String belongTo) {
        this.date = date;
        this.name = name;
        this.number = number;
        this.code = code;
        this.address = address;
        this.company = company;
        this.belongTo = belongTo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBelongTo() {
        return belongTo;
    }

    public void setBelongTo(String belongTo) {
        this.belongTo = belongTo;
    }

    @Override
    public String toString() {
        return "Express{" +
                "date='" + date + '\'' +
                ", name='" + name + '\'' +
                ", number='" + number + '\'' +
                ", code='" + code + '\'' +
                ", address='" + address + '\'' +
                ", company='" + company + '\'' +
                ", belongTo='" + belongTo + '\'' +
                '}';
    }
}
