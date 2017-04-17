package com.example.mkseo.myapplication.User.PayingPage;

import com.example.mkseo.myapplication.User.itemInfoForUser;

import java.util.ArrayList;

/**
 * Created by mkseo on 2017. 3. 10..
 */

public class totalPriceCalculating {

    private ArrayList<itemInfoForUser> infos;

    public totalPriceCalculating(ArrayList<itemInfoForUser> infos) {
        this.infos = infos;
    }

    public int calculating() {
        int tempTotalPrice = 0;
        for(itemInfoForUser itemInfo : infos) {
            tempTotalPrice += itemInfo.getPrice() * itemInfo.getCount();
        }

        return tempTotalPrice;
    }

}
