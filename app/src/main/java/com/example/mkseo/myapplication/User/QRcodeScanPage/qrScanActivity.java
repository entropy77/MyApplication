package com.example.mkseo.myapplication.User.QRcodeScanPage;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.mkseo.myapplication.LoginPage.loginActivity;
import com.example.mkseo.myapplication.LoginPage.splashPage.splashActivity;
import com.example.mkseo.myapplication.User.itemInfoForUser;
import com.example.mkseo.myapplication.User.ListViewAdapter;
import com.example.mkseo.myapplication.User.PayingPage.payingActivity;
import com.example.mkseo.myapplication.R;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class qrScanActivity extends Activity implements DecoratedBarcodeView.TorchListener {

    private int QRscanActivityID = 1;
    private DecoratedBarcodeView barcodeScannerView;
    private Button payingButton;
    private Dialog dialog;

    public ArrayList<itemInfoForUser> infos = new ArrayList<>();
    private ListViewAdapter adapter;
    private ListView list;

    // kill other activity related var
    public static qrScanActivity qrScanActivity;

    // when PayingActivity has ended
    // this is related with intent communication
    // sort of callback method once sub activity has finished
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == 0) {
            // if the activity returned in any reason, got intent from that activity and retrive on the list(infos)
            // this also include refresh action
            infos = (ArrayList<itemInfoForUser>) intent.getSerializableExtra("selectedItemArrayFromPayingActivity");
            adapter = new ListViewAdapter(this, infos, QRscanActivityID);
            list.setAdapter(adapter);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // kill other activity related assign
        qrScanActivity = this;

        // make status-bar disappear
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // setContentView must assign after status-bar disappearing
        setContentView(R.layout.activity_qr_scan);

        // QRscan related acts starts
        barcodeScannerView = (DecoratedBarcodeView) findViewById(R.id.zxing_barcode_scanner);
        barcodeScannerView.decodeContinuous(callback);
//        barcodeScannerView.setTorchListener(this);
//        barcodeScannerView.


        // put Button with XML
        payingButton = (Button) findViewById(R.id.payingButtonInQrScanTag);

        // 다 고르셨나요? button clicked event handler
        payingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View nowActivity) {
                // pass the data(infos) into another page(PayingPage) activity
                Intent intent = new Intent(nowActivity.getContext(), payingActivity.class);
                intent.putExtra("selectedItemArrayFromqrScanActivity", infos);
//                intent.putExtra("selectedTableNumber", public_table_no);
                startActivityForResult(intent, 0);
            }
        });

//        you can use flash light turn on/off button(id:switch_flashlight)
//        switchFlashlightButton = (Button)findViewById(R.id.switch_flashlight);

//        if the device does not have flashlight in its camera,
//        then remove the switch flashlight button...
//        if (!hasFlash()) {
//            switchFlashlightButton.setVisibility(View.GONE);
//        }

        // list view activity below
        ListViewAdapter adapter = new ListViewAdapter(this, infos, QRscanActivityID);
        list = (ListView) this.findViewById(R.id.selectedItemList);
        list.setAdapter(adapter);
    }

    // QRscan methods for continuous scan 17. 03. 09

    // good response
    protected Response.Listener<String> getResponseListener() {
        Response.Listener responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    reactor(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        return responseListener;
    }

    protected void reactor(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);

            // prevent same QR code scanning
            boolean isThisItemAlreadyExsist = false;
            for (itemInfoForUser row : infos) {
                if (row.getName().equals(jsonObject.getString("name"))) {
                    isThisItemAlreadyExsist = true;
                }
            }

            if (!isThisItemAlreadyExsist) {
                // server side data structure
                // v1/product/get_item
                String company_id = jsonObject.getString("company_id");
                String product_id = jsonObject.getString("id");
                String information = jsonObject.getString("information");
                String name = jsonObject.getString("name");
                int price = Integer.parseInt(jsonObject.getString("price"));
                int table_no = Integer.parseInt(jsonObject.getString("table_no"));

                // init count is 1
                itemInfoForUser item = new itemInfoForUser(company_id, product_id, information, name, price, table_no, 1);
                infos.add(item);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // error response
    protected Response.ErrorListener getErrorListener() {
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    errorReactor(error);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        return errorListener;
    }

    protected void errorReactor(VolleyError error) {
        barcodeScannerView.pause();

        int statusCode = error.networkResponse.statusCode;
        // error code 401 is unreadable. be aware of it.
        String errorMessage;

        System.out.println(this.getClass().getSimpleName());
        System.out.println("error occurred : " + error.getMessage());
        System.out.println("error status code : " + statusCode);

        switch (statusCode) {
            case 400:
                errorMessage = "테이블번호나 제품 아이디가 없습니다";
                break;
            case 410:
                errorMessage = "쿼리 에러가 발생하였습니다";
                break;
            default:
                errorMessage = "알수없는 에러가 발생하였습니다";
                break;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(qrScanActivity.this);
        dialog = builder.setMessage(errorMessage)
                .setNegativeButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        barcodeScannerView.resume();
                    }
                })
                .create();
        dialog.show();
    }


    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(final BarcodeResult result) {

            if (result != null) {
                final List<String> croppedText = AnalyzeRawTextFromQRcode.main(result.getText());


                // 1. 서버로 product_id, table_no 전송해서 jsonObject에 company_id, id, information, name, price, table_no 받아옴
                // 2. 필요한 name, price만 추출해서 사용

                qrScanRequest qrScanRequest = new qrScanRequest(croppedText.get(0), croppedText.get(1), getResponseListener(), getErrorListener());
                RequestQueue queue = Volley.newRequestQueue(qrScanActivity.this);
                queue.add(qrScanRequest);

                // this is for Developer
                barcodeScannerView.setStatusText(result.getText());
            }
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {

        }
    };

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        barcodeScannerView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        barcodeScannerView.pause();
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        captureManager.onDestroy();
//    }
//
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        captureManager.onSaveInstanceState(outState);
//    }

    public void pause(View view) {
        barcodeScannerView.pause();
    }

    public void resume(View view) {
        barcodeScannerView.resume();
    }

    public void triggerScan(View view) {
        barcodeScannerView.decodeSingle(callback);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    // check if the device's camera has a Flashlight.
    private boolean hasFlash() {
        return getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    @Override
    public void onTorchOn() {
//        switchFlashlightButton.setText("turn off flashlight");
    }

    @Override
    public void onTorchOff() {
//        switchFlashlightButton.setText("Turn on Flashlight");
    }

    // listview related method starts

    // kill other activity related method
    public static qrScanActivity getInstance() {
        return qrScanActivity;
    }

}
