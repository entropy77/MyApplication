package com.example.mkseo.myapplication.User;

import android.app.Application;

import java.io.Serializable;

/**
 * Created by mkseo on 2017. 3. 8..
 */

public class itemInfoForUser implements Serializable {

    private String company_id;
    private String product_id;
    private String information;
    private String name;
    private int price;
    private int table_no;
    private int count;

    public itemInfoForUser(String company_id, String product_id, String information, String name, int price, int table_no, int count) {
        this.company_id = company_id;
        this.product_id = product_id;
        this.information = information;
        this.name = name;
        this.price = price;
        this.table_no = table_no;
        this.count = count;
    }

    public String getCompany_id() {
        return company_id;
    }
    public String getProduct_id() {
        return product_id;
    }
    public String getInformation() {
        return information;
    }
    public String getName() {
        return name;
    }
    public int getPrice() {
        return price;
    }
    public int getTable_no() {
        return table_no;
    }
    public int getCount() {
        return count;
    }

    public int setCount(int count) {
        this.count = count;
        return count;
    }
}
