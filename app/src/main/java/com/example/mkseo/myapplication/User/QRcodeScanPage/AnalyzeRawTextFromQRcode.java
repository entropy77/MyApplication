package com.example.mkseo.myapplication.User.QRcodeScanPage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by mkseo on 2017. 3. 9..
 */

public class AnalyzeRawTextFromQRcode {

    public static List<String> main(String rawText) {

        List<String> croppedList;

        if(rawText.contains(",")) {
            String temp = rawText.replace(" ", "");
            croppedList = Arrays.asList(temp.split(","));
        } else {
            croppedList = new ArrayList<>();
            croppedList.add("error");
            croppedList.add("0");
        }

        return croppedList;
    }

}
