package com.example.mkseo.myapplication.Boss.Fragments.orderListFragmentPage;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.mkseo.myapplication.BusProvider;
import com.example.mkseo.myapplication.LoginPage.splashPage.splashActivity;
import com.example.mkseo.myapplication.R;
import com.example.mkseo.myapplication.orderListViewAdapter;
import com.example.mkseo.myapplication.loading_dialog;
import com.example.mkseo.myapplication.pushEvent;
import com.squareup.otto.Subscribe;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

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

    ArrayList<ArrayList<HashMap<String, String>>> informations;
    ArrayList<HashMap<String, String>> items;
    ListView listView;
    public orderListViewAdapter orderListViewAdapter;
    private loading_dialog loading_dialog;

    private SharedPreferences sharedPreferences;

    String login_id;
    String password;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // loading_dialog setting
        loading_dialog = new loading_dialog(getActivity());
        loading_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_boss_order_list, container, false);

        // bring-out id and password from preferences
        sharedPreferences = getActivity().getApplicationContext().getSharedPreferences("IDPASSWORD", getActivity().getApplicationContext().MODE_PRIVATE);
        login_id = sharedPreferences.getString("login_id", null);
        password = sharedPreferences.getString("password", null);

        // match XML file which is listview
        listView = (ListView) view.findViewById(R.id.orderListView_fragment_boss_order_list);

        // init data structures
        informations = new ArrayList<>();
        items = new ArrayList<>();

        // create Adapter
        orderListViewAdapter = new orderListViewAdapter(getActivity(), informations, items);
        listView.setAdapter(orderListViewAdapter);

        // setItemOnclickListener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                loading_dialog.show();

                System.out.println("login_id : " + login_id);
                System.out.println("password : " + password);

                String order_id = items.get(position).get("id");
                System.out.println(order_id);

                String status = items.get(position).get("status");
                System.out.println("status : " + status);

                // 2 implies order complete
                String requestStatus = "2";

                // status change request
                requestToServer(login_id, password, order_id, requestStatus);
            }
        });

        // receive refreshed information from server again
        requestToServer();

        return view;
    }

    // status change request
    public void requestToServer(String login_id, String password, String order_id, String status) {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    loading_dialog.dismiss();
                    requestToServer();
                    listView.setAdapter(orderListViewAdapter);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading_dialog.dismiss();
            }
        };

        boss_order_status_change_request boss_order_status_change_request = new boss_order_status_change_request(login_id, password, order_id, status, responseListener, errorListener);
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(boss_order_status_change_request);

    }

    // order list request
    public void requestToServer() {

        loading_dialog.show();

        informations.clear();
        items.clear();

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    loading_dialog.dismiss();

                    // get raw jsonMessage
                    JSONArray jsonMessage = new JSONArray(response);

                    System.out.println("jsonMessageLength : " + jsonMessage.length());

                    // first, get single json message from raw jason message
                    for (int i = 0; i < jsonMessage.length(); i++) {
                        JSONObject singleJsonMessage = jsonMessage.getJSONObject(i);

                        // we accept items which status is not 2
                        String status = singleJsonMessage.getString("status");

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

                        items.add(tempItemInfo);

                        // now, convert String into JSONArray
                        JSONArray information_jsonArray = new JSONArray(information_string);

                        // and get the items
                        // you can receive item as JSONObject - yes!! :D
                        // we need to make HashMap ArrayList

                        ArrayList<HashMap<String, String>> information = new ArrayList<>();

                        for (int j = 0; j < information_jsonArray.length(); j++) {
                            JSONObject item = information_jsonArray.getJSONObject(j);
                            HashMap<String, String> tempItem = new HashMap<>();

                            String name = item.getString("name");
                            String count = item.getString("count");

                            tempItem.put("name", name);
                            tempItem.put("count", count);

                            information.add(tempItem);
                            System.out.println(i + " order item name : " + name + " " + count);
                        }

                        informations.add(information);
                        orderListViewAdapter.refreshAdapter();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading_dialog.dismiss();
            }
        };

        boss_order_request orderListFragmentRequest = new boss_order_request(login_id, password, responseListener, errorListener);
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(orderListFragmentRequest);
    }

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

        System.out.println("It got an local push notification");

        this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                requestToServer();
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
