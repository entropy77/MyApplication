package com.example.mkseo.myapplication.User.PayingPage;

import android.util.Log;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mkseo.myapplication.User.PayingCompletePage.payingCompleteActivity;
import com.example.mkseo.myapplication.User.itemInfoForUser;
import com.example.mkseo.myapplication.User.ListViewAdapter;
import com.example.mkseo.myapplication.R;
import com.example.mkseo.myapplication.User.decimalChange;
import com.example.mkseo.myapplication.loading_dialog;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class payingActivity extends AppCompatActivity {

    private String TAG = this.getClass().getSimpleName();

    final static private String url = "http://leafrog.iptime.org:20080/v1/order/create";

    private ArrayList<itemInfoForUser> items;
    private int PayingActivityID = 2;
    private TextView totalPrice;
    private Button payingButton;
    private Dialog dialog;
    private loading_dialog loading_dialog;

    private String login_id;
    private String password;
    private String table_no;

    // example for kill other activity
    static payingActivity payingActivity;

//    // good response
//    protected Response.Listener<String> getResponseListener() {
//        Response.Listener responseListener = new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                try {
//                    reactor(response);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//
//        return responseListener;
//    }
//    protected void reactor(String response) {
//
//        loading_dialog.dismiss();
//        try {
//            JSONObject jsonObject = new JSONObject(response);
//
//            Intent intent = new Intent(payingActivity.this, payingCompleteActivity.class);
//            intent.putExtra("selectedItemArrayFrompayingActivity", items);
//            startActivity(intent);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    // error response
//    protected Response.ErrorListener getErrorListener() {
//        com.android.volley.Response.ErrorListener errorListener = new com.android.volley.Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                try {
//                    errorReactor(error);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//
//        return errorListener;
//    }
//    protected void errorReactor(VolleyError error) {
//        loading_dialog.dismiss();
//
//        int statusCode;
//        // error code 401 is unreadable. be aware of it.
//
//        if (error.networkResponse != null) {
//            // 401 error code is unreadable so be aware of that
//            statusCode = error.networkResponse.statusCode;
//        } else {
//            // 000 - can't connect with server
//            statusCode = 100;
//        }
//
//        String errorMessage;
//
//        Log.d(TAG, statusCode + " " + error.getMessage());
//
//
////        400 : required 인자가 불충족됨
////        401 : table_no 가 숫자가 아닌 경우
////        404 : 사용자 계정이 존재하지 않음
////        405 : products 의 인자가 충분하지 않음 (product_id, count 둘중 하나 누락)
////        406 : product 들에 대한 회사가 존재하지 않거나, 2개 이상의 회사가 조회됨
////        407 : 제품 정보가 존재하지 않음 (하나라도 없을 경우, 주문되지 않음)
////        410 : 쿼리 실행 중 에러
////        200 : ok
//
//        switch (statusCode) {
//            case 400:
//                errorMessage = "인자가 충분하지 않습니다";
//                break;
//            case 401:
//                errorMessage = "table_no가 숫자가 아닙니다";
//                break;
//            case 404:
//                errorMessage = "사용자 게정이 존재하지 않습니다";
//                break;
//            case 405:
//                errorMessage = "product_id나 count 둘 중 하나가 누락되었습니다";
//                break;
//            case 406:
//                errorMessage = "product들에 대한 회사가 존재하지 않거나 2개 이상의 회사가 조회되었습니다";
//                break;
//            case 407:
//                errorMessage = "제품 정보가 존재하지 않습니다";
//                break;
//            case 410:
//                errorMessage = "쿼리 에러가 발생하였습니다";
//                break;
//            default:
//                errorMessage = "알수없는 에러가 발생하였습니다";
//                break;
//        }
//        AlertDialog.Builder builder = new AlertDialog.Builder(payingActivity.this);
//        dialog = builder.setMessage(errorMessage)
//                .setNegativeButton("ok", null)
//                .create();
//        dialog.setCanceledOnTouchOutside(false);
//        dialog.show();
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        payingActivity = this;

        loading_dialog = new loading_dialog(payingActivity.this);
        loading_dialog.setup();

        // make status-bar disappear
        // 사용보류
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_paying);

        // bring-out id and password from preferences
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("IDPASSWORD", getApplicationContext().MODE_PRIVATE);
        login_id = preferences.getString("login_id", null);
        password = preferences.getString("password", null);

        // assign XML objects
        ListView listView = (ListView) findViewById(R.id.selectedItemListOnPayingPageTag);
        totalPrice = (TextView) findViewById(R.id.totalPriceTag);
        payingButton = (Button) findViewById(R.id.payingButtonInPayingTag);

        // set payingbutton onclicklistener
        payingButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View nowActivity) {

                System.out.println("");
//                if there is no items in items, don't do this'
                if (items.size() != 0) {
                    loading_dialog.show();
                    table_no = String.valueOf(items.get(items.size() - 1).getTable_no());

                    // need to request paying related action from Server
                    for (itemInfoForUser item : items) {
                        Log.d(TAG, item.getName() + " - " + item.getCount());
                    }

                    JSONObject jsonObject = makeJsonTable();
                    requestToServer(jsonObject);
                } else {

                    String errorMessage = "결제할 메뉴가 없습니다. 먼저 메뉴를 선택해 주세요";

                    AlertDialog.Builder builder = new AlertDialog.Builder(payingActivity.this);
                    dialog = builder.setMessage(errorMessage)
                            .setNegativeButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    returnIntent();
                                    finish();
                                }
                            })
                            .create();
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                }

            }
        });

        // catch passed data from qrScanActivity which are items and tableNumber
        Intent intent = getIntent();
        items = (ArrayList<itemInfoForUser>) intent.getSerializableExtra("selectedItemArrayFromqrScanActivity");

        System.out.println("table_no on PayingActivity : " + table_no);

        // sum total price
        totalPriceCalculating totalPriceCalculating = new totalPriceCalculating(items);
        int tempTotalPrice = totalPriceCalculating.calculating();
        decimalChange decimalChange = new decimalChange(tempTotalPrice);
        totalPrice.setText(decimalChange.converting());

        // listview assign
        ListViewAdapter adapter = new ListViewAdapter(this, items, PayingActivityID);
        listView.setAdapter(adapter);

    }

//    private void requestPayment(JSONObject paymentItemList) {
//
//        loading_dialog.show();
//
//        payingRequest payingRequest = new payingRequest(paymentItemList, getResponseListener(), getErrorListener());
//
//        RequestQueue queue = Volley.newRequestQueue(payingActivity);
//        queue.add(payingRequest);
//    }

    private JSONObject makeJsonTable() {
        // reconstruct item list since server side item structure is not matched with it
        // client side item structure : name, product_id, price, count
        // server side item structure(in /v1/order/create) : product_id, number
        // 아 천재다 진짜 ㅋㅋㅋㅋ ArrayList를 String으로 변환한 다음 request에 넘겨주면 됩니다
        // 응 아니야 JSONObject로 새로 만들어 응
        JSONObject orderJSONMessage = new JSONObject();

        try {
            // make top JSONObject first
            // login_id:
            // passowrd:
            // table_no
            orderJSONMessage.put("login_id", login_id);
            orderJSONMessage.put("password", password);
            orderJSONMessage.put("table_no", table_no);

            // make products JSONObject
            // product_id:
            // count:
            JSONArray products = new JSONArray();
            for (itemInfoForUser nowItem : items) {
                JSONObject item = new JSONObject();

                //caution this converting action may cause problem
                // self warning
                item.put("count", String.valueOf(nowItem.getCount()));
                item.put("product_id", nowItem.getProduct_id());

                products.put(item);
            }

            // complete JSONMessage
            orderJSONMessage.put("products", products);

            Log.d(TAG, orderJSONMessage.toString());

            return orderJSONMessage;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // very important method(2017. 03. 10)
    public void setTotalPriceonTextView(String totalPrice) {
        this.totalPrice.setText(totalPrice);
    }

    @Override
    public void onBackPressed() {
        returnIntent();
    }

    public void returnIntent() {
        // return modified paying data
        Intent intent = new Intent();
        intent.putExtra("selectedItemArrayFromPayingActivity", items);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    public static payingActivity getInstance() {
        return payingActivity;
    }

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();

    String postRequest(String url, String json) throws IOException {

        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();

        return response.body().string();
    }

    public void requestToServer(final JSONObject orderJSONMessage) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String response = postRequest("http://leafrog.iptime.org:20080/v1/order/create", orderJSONMessage.toString());
                    Log.d("response", response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();


        loading_dialog.dismiss();
        Intent intent = new Intent(payingActivity.this, payingCompleteActivity.class);
        intent.putExtra("selectedItemArrayFrompayingActivity", items);
        startActivity(intent);


//        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
//            @Override
////            public void onResponse(JSONObject response) {
//                try {
//                    loading_dialog.dismiss();
//                    // Paying request has done
//                    // and server got the data
//                    // yet don't know weather paying has succeeded
//                    // for now, if you succeeded transporting payingArray and ETC
//                    // move to next Activity
//                    // 2017. 03. 17
//
//                    // make JSONTable
//                    makeJsonTable();
//
//                    // send items to payingCompleteActivity
//                    Intent intent = new Intent(payingActivity.this, payingCompleteActivity.class);
//                    intent.putExtra("selectedItemArrayFrompayingActivity", items);
//                    startActivity(intent);
////                    this is for debugging
////                    System.out.println(response.toString());
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//
////        400 : required 인자가 불충족됨
////        401 : table_no 가 숫자가 아닌 경우
////        404 : 사용자 계정이 존재하지 않음
////        405 : products 의 인자가 충분하지 않음 (product_id, count 둘중 하나 누락)
////        406 : product 들에 대한 회사가 존재하지 않거나, 2개 이상의 회사가 조회됨
////        407 : 제품 정보가 존재하지 않음 (하나라도 없을 경우, 주문되지 않음)
////        410 : 쿼리 실행 중 에러
////        200 : ok
//
//        Response.ErrorListener errorListener = new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                try {
//
//                    loading_dialog.dismiss();
//                    System.out.println(error.toString());
//                    int statusCode = 0;
//                    if (error.networkResponse != null) {
//                        statusCode = error.networkResponse.statusCode;
//                    }
//                    String errorMessage;
//
//
//                    switch (statusCode) {
//                        case 400:
//                            errorMessage = "required parameters need to be filled out";
//                            break;
//                        case 401:
//                            // 401 : limit, page 가 숫자가 아닌 경우
//                            errorMessage = "table number isn't int type";
//                            break;
//                        case 404:
//                            // 404 : user 아이디가 없음
//                            errorMessage = "user id doesn't exist";
//                            break;
//                        case 405:
//                            // 401 : 빈 값이 있을 경우
//                            errorMessage = "item parameters need to filled out";
//                            break;
//                        case 406:
//                            // 401 : 회사명과 일치하지 않는 아이템이 스캔됨
//                            errorMessage = "no matched company with scanned items or more than two company matched";
//                            break;
//                        case 407:
//                            // 401 : 하나 이상의 아이템을 스캔할 것
//                            errorMessage = "put more than one item";
//                            break;
//                        // 410 : 쿼리 에러
//                        case 410:
//                            errorMessage = "Query got wrong, sorry!";
//                            break;
//                        default:
//                            errorMessage = "unknown error";
//                            break;
//                    }
//                    AlertDialog.Builder builder = new AlertDialog.Builder(payingActivity);
//                    dialog = builder.setMessage(errorMessage)
//                            .setNegativeButton("ok", null)
//                            .create();
//                    dialog.show();
//
//                    System.out.println("error occurred : " + error.getMessage());
//                    System.out.println("error status code : " + statusCode);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

    }
}

// payingRequest param description
// ArrayList<HashMap<String, String>>, String login_id, String password, String table_no, responseListener, errorListener
//        payingRequest payingRequest = new payingRequest(orderJSONMessage, responseListener, errorListener);
//        RequestQueue queue = Volley.newRequestQueue(payingActivity);
//        queue.add(payingRequest);
//    }
//            }

