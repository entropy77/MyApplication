package com.example.mkseo.myapplication.User.Fragments.userOrderFragmentPage;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.mkseo.myapplication.loading_dialog;
import com.example.mkseo.myapplication.orderListViewAdapter;
import com.example.mkseo.myapplication.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link user_order_fragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link user_order_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class user_order_fragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public user_order_fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment consumeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static user_order_fragment newInstance(String param1, String param2) {
        user_order_fragment fragment = new user_order_fragment();
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

    String TAG = this.getClass().getSimpleName();

    ArrayList<HashMap<String, String>> informations;
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

    ArrayList<ArrayList<HashMap<String, String>>> items;
    // items is like this below
    // name(1) :
    // count(1) :
    // name(2) :
    // count(2) :
    //  .
    //  .
    //  .

    ListView listView;
    private loading_dialog loading_dialog;
    private AlertDialog dialog;

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
            statusCode = 100;
        }

        String errorMessage;

        switch (statusCode) {
            case 100:
                errorMessage = "서버와 연결할 수 없습니다. 와이파이나 데이터를 켜시고 다시 시도해 주세요";
                break;
            case 400:
                errorMessage = "빈칸없이 작성해주시기 바랍니다";
                break;
            case 404:
                errorMessage = "아이디나 비밀번호가 일치하지 않습니다";
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
            items = new ArrayList<>();
            informations = new ArrayList<>();

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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    protected void refreshListview() {

        Log.d(TAG, "refreshing listview...");

        // init adapter
        orderListViewAdapter orderListViewAdapter = new orderListViewAdapter(getActivity(), informations, items);
        listView.setAdapter(orderListViewAdapter);
//        items.clear();
        orderListViewAdapter.notifyDataSetChanged();
//        orderListViewAdapter.refreshAdapter();
    }

    // it include server connection
    public void refreshRequest(String login_id, String password) {

        // request to server about data
        user_order_request user_order_request = new user_order_request(login_id, password, getResponseListener(), getErrorListener());
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(user_order_request);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_order, container, false);

        // loading dialog init
        loading_dialog = new loading_dialog(getActivity());
        loading_dialog.setup();

        // match XML file which is listview
        listView = (ListView) view.findViewById(R.id.orderListView_fragment_user_order_list);

        // bring-out id and password from preferences
        SharedPreferences preferences = getActivity().getApplicationContext().getSharedPreferences("IDPASSWORD", getActivity().getApplicationContext().MODE_PRIVATE);
        String login_id = preferences.getString("login_id", null);
        String password = preferences.getString("password", null);

        // start loading_dialog since we are connecting with server
        loading_dialog.show();

        // connect with server
        // it will automatically refresh listview
        // we need to connect with server in order to refresh listview
        refreshRequest(login_id, password);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
