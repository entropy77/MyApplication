package com.example.mkseo.myapplication.User;

import java.text.DecimalFormat;

/**
 * Created by mkseo on 2017. 3. 10..
 */

public class decimalChange {

    private String price;

    public decimalChange(int price) {
        this.price = Integer.toString(price);
    }

    public decimalChange(String price) {
        this.price = price;
    }

    public String converting() {
        String temp = price;
        DecimalFormat decimalFormat = new DecimalFormat("##,###,###");
        temp = decimalFormat.format(Long.valueOf(temp));
        return temp;
    }
}