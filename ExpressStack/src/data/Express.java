package data;


import java.io.Serializable;

/**
 * 快递类
 */
public class Express implements Serializable {
    private String num;//快递单号
    private int placeX;
    private int placeY;
    private int code;//取件码
    private String company;//快递公司

    public Express() {
    }

    public Express(String num, int placeX, int placeY, int code, String company) {
        this.num = num;
        this.placeX = placeX;
        this.placeY = placeY;
        this.code = code;
        this.company = company;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }


    public int getPlaceX() {
        return placeX;
    }

    public void setPlaceX(int placeX) {
        this.placeX = placeX;
    }

    public int getPlaceY() {
        return placeY;
    }

    public void setPlaceY(int placeY) {
        this.placeY = placeY;
    }
}
