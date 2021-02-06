package com.yjz.entity;

import java.util.Date;

public class BuyRecord {

    private String uname;
    private int id;
    private Date bdate;
    private double allmoney;
    private int allbook;

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getBdate() {
        return bdate;
    }

    public void setBdate(Date bdate) {
        this.bdate = bdate;
    }

    public double getAllmoney() {
        return allmoney;
    }

    public void setAllmoney(double allmoney) {
        this.allmoney = allmoney;
    }

    public int getAllbook() {
        return allbook;
    }

    public void setAllbook(int allbook) {
        this.allbook = allbook;
    }
}
