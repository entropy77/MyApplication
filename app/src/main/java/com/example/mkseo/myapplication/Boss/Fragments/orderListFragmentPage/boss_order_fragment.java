package com.example.mkseo.myapplication.Boss.Fragments.orderListFragmentPage;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.mkseo.myapplication.Boss.bossMainActivity;
import com.example.mkseo.myapplication.BusProvider;
import com.example.mkseo.myapplication.PushWakeLock;
import com.example.mkseo.myapplication.R;
import com.example.mkseo.myapplication.orderListViewAdapter;
import com.example.mkseo.myapplication.loading_dialog;
import com.example.mkseo.myapplication.pushEvent;
import com.squareup.otto.Subscribe;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link boss_order_fragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link boss_order_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class boss_order_fragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public boss_order_fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment orderList.
     */
    // TODO: Rename and change types and number of parameters
    public static boss_order_fragment newInstance(String param1, String param2) {
        boss_order_fragment fragment = new boss_order_fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private String TAG = this.getClass().getSimpleName();

    private String login_id;
    private String password;

    private ArrayList<HashMap<String, String>> informations;
    // informations is like this below
    // table_no(1) :
    // id(1) :
    // status(1) :
    // account_id(1) :
    // company_id(1) :
    // phone(1) :
    //  .
    //  .
    //  .

    private ArrayList<ArrayList<HashMap<String, String>>> items;
    // items is like this below
    // name(1) :
    // count(1) :
    // name(2) :
    // count(2) :
    //  .
    //  .
    //  .

    private ListView listView;
    private loading_dialog loading_dialog;
    private AlertDialog dialog;

    // boss_order_request
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
            loading_dialog.dismiss();
            // get raw jsonMessage
            JSONArray jsonMessage = new JSONArray(response);

            // convert JSON into LOCAL data
            // which is informations and items
            put_JSONdata_into_local_data(jsonMessage);

            // refreshListview
            refreshListview();

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

        // make loading_dialog gone
        loading_dialog.dismiss();

        // error string logging
        Log.d(TAG, error.toString());

        int statusCode;
        if (error.networkResponse != null) {
            // 401 error code is unreadable so be aware of that
            statusCode = error.networkResponse.statusCode;
        } else {
            // 000 - can't connect with server
            statusCode = 401;
        }

        String errorMessage;

        switch (statusCode) {
            case 100:
                errorMessage = "서버와 연결할 수 없습니다. 와이파이나 데이터를 켜시고 다시 시도해 주세요";
                break;
            case 400:
                errorMessage = "필요한 인자가 불충족되었습니다";
                break;
            case 401:
                errorMessage = "테이블 번호가 숫자가 아닙니다(nullable)";
                break;
            case 402:
                errorMessage = "주문 상태가 숫자가 아닙니다(nullable)";
                break;
            case 403:
                errorMessage = "page나 limit이 숫자가 아닙니다";
                break;
            case 404:
                errorMessage = "회사 계정이 존재하지 않습니다(nullable)";
                break;
            case 405:
                errorMessage = "사용자 게정이 존재하지 않습니다(nullable)";
                break;
            case 410:
                errorMessage = "쿼리 에러가 발생하였습니다";
                break;
            default:
                errorMessage = "알수없는 에러가 발생하였습니다";
                break;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        dialog = builder.setMessage(errorMessage)
                .setNegativeButton("확인", null)
                .create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    // good response
    protected Response.Listener<String> getResponseListener(final String message_title, final String message_body) {
        Response.Listener responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    reactor(response, message_title, message_body);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        return responseListener;
    }
    protected void reactor(String response, String message_title, String message_body) {
        try {
            loading_dialog.dismiss();
            // get raw jsonMessage
//            JSONArray jsonMessage = new JSONArray(response);

            // convert JSON into LOCAL data
            // which is informations and items
//            put_JSONdata_into_local_data(jsonMessage);

            Log.d(TAG, "Succeeded in pushing");

            // refreshListview
            refreshListview();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // error response
    protected Response.ErrorListener getErrorListener(final String message_title, final String message_body) {
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    errorReactor(error, message_title, message_body);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        return errorListener;
    }
    protected void errorReactor(VolleyError error, String message_title, String message_body) {

        // make loading_dialog gone
        loading_dialog.dismiss();

        // error string logging
        Log.d(TAG, error.toString());

        int statusCode;
        if (error.networkResponse != null) {
            // 401 error code is unreadable so be aware of that
            statusCode = error.networkResponse.statusCode;
        } else {
            // 000 - can't connect with server
            statusCode = 401;
        }

        String errorMessage;

        switch (statusCode) {
            case 100:
                errorMessage = "서버와 연결할 수 없습니다. 와이파이나 데이터를 켜시고 다시 시도해 주세요";
                break;
            case 400:
                errorMessage = "필요한 인자가 불충족되었습니다";
                break;
            case 401:
                errorMessage = "테이블 번호가 숫자가 아닙니다(nullable)";
                break;
            case 402:
                errorMessage = "주문 상태가 숫자가 아닙니다(nullable)";
                break;
            case 403:
                errorMessage = "page나 limit이 숫자가 아닙니다";
                break;
            case 404:
                errorMessage = "회사 계정이 존재하지 않습니다(nullable)";
                break;
            case 405:
                errorMessage = "사용자 계정이 존재하지 않습니다(nullable)";
                break;
            case 410:
                errorMessage = "쿼리 에러가 발생하였습니다";
                break;
            default:
                errorMessage = "알수없는 에러가 발생하였습니다";
                break;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        dialog = builder.setMessage(errorMessage)
                .setNegativeButton("확인", null)
                .create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }


    // boss_order_status_change_request
    // good response
    protected Response.Listener<String> getResponseListener(final String order_id) {
        Response.Listener responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    reactor(response, order_id);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        return responseListener;
    }

    protected void reactor(String response, String order_id) {
        try {
            // get raw jsonMessage
            JSONObject jsonMessage = new JSONObject(response);

            // refresh listview
            refreshRequest(login_id, password);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // error response
    protected Response.ErrorListener getErrorListener(final String order_id) {
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    errorReactor(error, order_id);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        return errorListener;
    }

    protected void errorReactor(VolleyError error, String order_id) {

        // make loading_dialog gone
        loading_dialog.dismiss();

        // error string logging
        Log.d(TAG, error.toString());

        int statusCode;
        if (error.networkResponse != null) {
            // 401 error code is unreadable so be aware of that
            statusCode = error.networkResponse.statusCode;
        } else {
            // 000 - can't connect with server
            statusCode = 401;
        }

//        400 : required 인자가 불충족됨
//        401 : status가 숫자가 아닌 경우 (입력했을 때 에러 체크를 함)
//        404 : 회사 계정이 존재하지 않음
//        410 : 쿼리 실행 중 에러
//        200 : ok

        String errorMessage;

        switch (statusCode) {
            case 400:
                errorMessage = "필요한 인자가 불충족되었습니다";
                break;
            case 401:
                errorMessage = "주문 상태가 숫자가 아닙니다(nullable)";
                break;
            case 404:
                errorMessage = "회사 계정이 존재하지 않습니다";
                break;
            case 410:
                errorMessage = "쿼리 에러가 발생하였습니다";
                break;
            default:
                errorMessage = "알수없는 에러가 발생하였습니다";
                break;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        dialog = builder.setMessage(errorMessage)
                .setNegativeButton("확인", null)
                .create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }


    protected void put_JSONdata_into_local_data(JSONArray jsonMessage) {
        try {

            // init local vars
            informations = new ArrayList<>();
            items = new ArrayList<>();

            // first, get single json message from raw jason message
            for (int i = 0; i < jsonMessage.length(); i++) {
                JSONObject singleJsonMessage = jsonMessage.getJSONObject(i);

                // bring out information from single jason message
                // NOTICE!!
                // You must use "String" format, not JSONObject or JSONArray
                // http://stackoverflow.com/questions/14041698/jsonexception-java-lang-string-cannot-be-converted-to-jsonobject
                String information_string = singleJsonMessage.getString("information");

                // make item info without information table
                HashMap<String, String> tempItemInfo = new HashMap<>();
                tempItemInfo.put("table_no", singleJsonMessage.getString("table_no"));
                tempItemInfo.put("id", singleJsonMessage.getString("id"));
                tempItemInfo.put("status", singleJsonMessage.getString("status"));
                tempItemInfo.put("account_id", singleJsonMessage.getString("account_id"));
                tempItemInfo.put("company_id", singleJsonMessage.getString("company_id"));
                tempItemInfo.put("phone", singleJsonMessage.getString("phone"));

                informations.add(tempItemInfo);

                // now, convert String into JSONArray
                JSONArray information_jsonArray = new JSONArray(information_string);

                // and get the informations
                // you can receive item as JSONObject - yes!! :D
                // we need to make HashMap ArrayList

                ArrayList<HashMap<String, String>> smallArray = new ArrayList<>();

                for (int j = 0; j < information_jsonArray.length(); j++) {
                    JSONObject item = information_jsonArray.getJSONObject(j);
                    HashMap<String, String> tempItem = new HashMap<>();

                    String name = item.getString("name");
                    String count = item.getString("count");

                    tempItem.put("name", name);
                    tempItem.put("count", count);

                    smallArray.add(tempItem);
                }

                items.add(smallArray);
            }

            Log.d(TAG, informations.toString());
            Log.d(TAG, items.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // this is used after server communication
    protected void refreshListview() {

        Log.d(TAG, "refreshing listview...");

        // init adapter
        orderListViewAdapter orderListViewAdapter = new orderListViewAdapter(getActivity(), informations, items);
        listView.setAdapter(orderListViewAdapter);
        orderListViewAdapter.refresh(informations, items);
    }

    // it include server connection
    public void refreshRequest(String login_id, String password) {
        // request to server about data
        boss_order_request boss_order_request = new boss_order_request(login_id, password, getResponseListener(), getErrorListener());
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(boss_order_request);
    }


    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();

    String postRequest(String url, String json) throws IOException {

        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        okhttp3.Response response = client.newCall(request).execute();

        return response.body().string();
    }

    public void pushRequest(String account_id, String msg) {

        Log.d(TAG, "pushRequest starts");

        String testTitle = "testTitle";
        String testBody = "testBody";

        boss_order_push_request boss_order_push_request = new boss_order_push_request(account_id, msg, getResponseListener(testTitle, testBody), getErrorListener(testTitle, testBody));
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(boss_order_push_request);


//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    String response = postRequest("http://leafrog.iptime.org:20080/message/send_data", orderJSONMessage.toString());
//                    Log.d(TAG, response);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_boss_order_list, container, false);

        // loading dialog init
        loading_dialog = new loading_dialog(getActivity());
        loading_dialog.setup();

        // match XML file which is listview
        listView = (ListView) view.findViewById(R.id.orderListView_fragment_boss_order_list);

        // bring-out id and password from preferences
        SharedPreferences preferences = getActivity().getApplicationContext().getSharedPreferences("IDPASSWORD", getActivity().getApplicationContext().MODE_PRIVATE);
        login_id = preferences.getString("login_id", null);
        password = preferences.getString("password", null);

        // setItemOnclickListener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                String order_id = informations.get(position).get("id");
                String status = informations.get(position).get("status");
                String account_id = informations.get(position).get("account_id");
                String company_id = informations.get(position).get("company_id");

                Log.d(TAG, "order completing..");
                Log.d(TAG, "order_id - " + order_id);
                Log.d(TAG, "status - " + status);
                Log.d(TAG, "account_id - " + account_id);
                Log.d(TAG, "company_id - " + company_id);

                // 2 implies order complete
                String requestStatus = "2";

                // status change request
                changeStatusRequest(login_id, password, order_id, requestStatus);

                HashMap<String, String> pushMessage = new HashMap<String, String>();
                pushMessage.put("login_id", login_id);
                pushMessage.put("password", password);
                pushMessage.put("message_title", "testTitle");
                pushMessage.put("message_body", "testBody");

                JSONObject jsonObject = new JSONObject(pushMessage);

                pushRequest(account_id, "test msg");
//                pushRequest(company_id, "test msg for company");
            }
        });

        // start loading_dialog since we are connecting with server
        loading_dialog.show();

        // connect with server
        // it will automatically refresh listview
        // we need to connect with server in order to refresh listview
        refreshRequest(login_id, password);

        // receive refreshed information from server again
//        requestToServer();

        return view;
    }

    private void changeStatusRequest(final String login_id, final String password, String order_id, String requestStatus) {

        loading_dialog.show();

        boss_order_status_change_request boss_order_status_change_request = new boss_order_status_change_request(login_id, password, order_id, requestStatus, getResponseListener(order_id), getErrorListener(order_id));
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(boss_order_status_change_request);

    }

//    // status change request
//    public void requestToServer(String login_id, String password, String order_id, String status) {
//        Response.Listener<String> responseListener = new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                try {
//                    loading_dialog.dismiss();
//                    refreshRequest(login_id, password);
//                    listView.setAdapter(orderListViewAdapter);
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//
//        Response.ErrorListener errorListener = new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                loading_dialog.dismiss();
//            }
//        };
//
//        boss_order_status_change_request boss_order_status_change_request = new boss_order_status_change_request(login_id, password, order_id, status, responseListener, errorListener);
//        RequestQueue queue = Volley.newRequestQueue(getActivity());
//        queue.add(boss_order_status_change_request);
//
//    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // otto using related method - reigster
        Log.d(TAG, "otto register succeeded");
        BusProvider.getInstance().register(this);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // otto using related method - unregister
        BusProvider.getInstance().unregister(this);

        mListener = null;
    }

    // this is one of otto protocol. don't delete
    @Subscribe
    public void gotPushNotification(pushEvent event) {
        // receive refreshed information from server again

        Log.d(TAG, "It got an local push notification - otto");

        this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // after got notification, we need to refresh Listview
//                refreshRequest(login_id, password);

                bossMainActivity.getInstance().orderListButtonClickEvent();

            }
        });

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
