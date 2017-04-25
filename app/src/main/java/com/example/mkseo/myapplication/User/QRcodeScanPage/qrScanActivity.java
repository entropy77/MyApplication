package com.example.mkseo.myapplication.User.QRcodeScanPage;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.mkseo.myapplication.loading_dialog;
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

    private String TAG = this.getClass().getSimpleName();

    private int QRscanActivityID = 1;
    private DecoratedBarcodeView barcodeScannerView;
    private Button payingButton;
    private Dialog dialog;
    private loading_dialog loading_dialog;

    public ArrayList<itemInfoForUser> items = new ArrayList<>();
    private ListViewAdapter adapter;
    private ListView list;

    // local raw messages from qrscanning
    private ArrayList<String> rawMessages = new ArrayList<>();

    // kill other activity related var
    public static qrScanActivity qrScanActivity;

    // -----------------------
    // regular process methods
    // -----------------------

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

        loading_dialog.dismiss();
        try {
            JSONObject jsonObject = new JSONObject(response);

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
            items.add(item);

            // refresh listview
            adapter.refreshAdapter(items);

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

        loading_dialog.dismiss();

        int statusCode = error.networkResponse.statusCode;
        // error code 401 is unreadable. be aware of it.
        String errorMessage;

        Log.d(TAG, statusCode + " " + error.getMessage());

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
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void requestQRinformation(String result) {

        // crop raw text with specific rule -> AnalyzeRawTextFromQRcode class
        List<String> croppedText = AnalyzeRawTextFromQRcode.main(result);
        String localProductID = croppedText.get(0);
        String localTable_no = croppedText.get(1);

        // show loading_dialog
        loading_dialog.show();

        // request server about QRcode information
        qrScanRequest qrScanRequest = new qrScanRequest(localProductID, localTable_no, getResponseListener(), getErrorListener());
        RequestQueue queue = Volley.newRequestQueue(qrScanActivity.this);
        queue.add(qrScanRequest);
    }

    // called from external class: ListViewAdapter
    public void removeRawMessage(int position) {
        Log.d(TAG, "remove local raw message");
        Log.d(TAG, rawMessages.toString());
        rawMessages.remove(position);
    }

    // -----------------------
    // react methods
    // -----------------------

    // when PayingActivity has ended
    // this is related with intent communication
    // sort of callback method once sub activity has finished
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == 0) {
            // if the activity returned in any reason, got intent from that activity and retrive on the list(items)
            // this also include refresh action
            items = (ArrayList<itemInfoForUser>) intent.getSerializableExtra("selectedItemArrayFromPayingActivity");
            adapter = new ListViewAdapter(this, items, QRscanActivityID);
            list.setAdapter(adapter);
        }
    }

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(final BarcodeResult result) {

            boolean isThisRawMessageDuplicated = false;

            for (String rawMessage : rawMessages) {
                if (rawMessage.equals(result.getText())) {
                    isThisRawMessageDuplicated = true;
                    break;
                }
            }

            if (!isThisRawMessageDuplicated) {
                rawMessages.add(result.getText());
                Log.d(TAG, "add local raw message");
                Log.d(TAG, rawMessages.toString());
                requestQRinformation(result.getText());
            }



//            if (items.size() == 0) {
//                // include connect with server
//                // save items into local
//                // refresh adapter(listview)
//                requestQRinformation(result.getText());
//            } else {
//                List<String> croppedText = AnalyzeRawTextFromQRcode.main(result.getText());
//                String tempProduct_id = croppedText.get(0);
//
//                boolean isThisItemDuplicated = false;
//
//                // prevent same QR code scanning
//                for (itemInfoForUser row : items) {
//                    if (row.getProduct_id().equals(tempProduct_id)) {
//                        isThisItemDuplicated = true;
//                        break;
//                    }
//                }
//
//                if (!isThisItemDuplicated) {
//                    requestQRinformation(result.getText());
//                }
//            }

//            if (result != null) {
//                if (!duplicationCheckVar.equals(result.getText())) {
//                    Log.d(TAG, "found new item - " + result.getText());
//                    duplicationCheckVar = result.getText();
//                    final List<String> croppedText = AnalyzeRawTextFromQRcode.main(result.getText());
//
//                    // 1. 서버로 product_id, table_no 전송해서 jsonObject에 company_id, id, information, name, price, table_no 받아옴
//                    // 2. 필요한 name, price만 추출해서 사용
//
//                    // this is for Developer
//                    barcodeScannerView.setStatusText(result.getText());
//
//                    boolean isThisItemAlreadyExsist = false;
//                    if (items != null) {
//                        // prevent same QR code scanning
//                        for (itemInfoForUser row : items) {
//                            if (row.getName().equals(result)) {
//                                isThisItemAlreadyExsist = true;
//                            }
//                        }
//                    }
//                    if (!isThisItemAlreadyExsist) {
//
//                        loading_dialog.show();
//                        qrScanRequest qrScanRequest = new qrScanRequest(croppedText.get(0), croppedText.get(1), getResponseListener(), getErrorListener());
//                        RequestQueue queue = Volley.newRequestQueue(qrScanActivity.this);
//                        queue.add(qrScanRequest);
//
//                    }
//                }
//            }
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {

        }
    };

    // kill other activity related method
    public static qrScanActivity getInstance() {
        return qrScanActivity;
    }

    // -----------------------
    // main process
    // -----------------------

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

        // init loading_dialog
        loading_dialog = new loading_dialog(qrScanActivity);
        loading_dialog.setup();

        // QRscan related acts starts
        barcodeScannerView = (DecoratedBarcodeView) findViewById(R.id.zxing_barcode_scanner);
        barcodeScannerView.decodeContinuous(callback);
//        barcodeScannerView.setTorchListener(this);

        // list view activity below
        adapter = new ListViewAdapter(this, items, QRscanActivityID);
        list = (ListView) this.findViewById(R.id.selectedItemList);
        list.setAdapter(adapter);

        // put Button with XML
        payingButton = (Button) findViewById(R.id.payingButtonInQrScanTag);

        // 다 고르셨나요? button clicked event handler
        payingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View nowActivity) {
                // pass the data(items) into another page(PayingPage) activity
                Intent intent = new Intent(nowActivity.getContext(), payingActivity.class);
                intent.putExtra("selectedItemArrayFromqrScanActivity", items);
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

    }

    // QRscan methods for continuous scan 17. 03. 09

    // -----------------------
    // ETC
    // -----------------------

    //region activity control related methods

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

    //endregion
    //region barcodeScanner control related methods

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

    //endregion

}
