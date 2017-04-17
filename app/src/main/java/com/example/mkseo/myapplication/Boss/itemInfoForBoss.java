package com.example.mkseo.myapplication.Boss;

/**
 * Created by mkseo on 2017. 3. 16..
 */

public class itemInfoForBoss {

    private String information;
    private String name;
    private String price;
    private String product_id;

    public itemInfoForBoss(String information, String name, String price, String product_id) {
        this.information = information;
        this.name = name;
        this.price = price;
        this.product_id = product_id;
    }

    public String getInformation() {
        return information;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getProduct_id() {
        return product_id;
    }
}
