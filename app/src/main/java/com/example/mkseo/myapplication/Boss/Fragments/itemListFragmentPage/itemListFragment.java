package com.example.mkseo.myapplication.Boss.Fragments.itemListFragmentPage;

import android.app.Dialog;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.mkseo.myapplication.Boss.Fragments.itemListFragmentPage.itemAddPage.itemAddActivity;
import com.example.mkseo.myapplication.Boss.bossMainActivity;
import com.example.mkseo.myapplication.Boss.itemInfoForBoss;
import com.example.mkseo.myapplication.LoginPage.loginActivity;
import com.example.mkseo.myapplication.R;
import com.example.mkseo.myapplication.User.userMainActivity;
import com.example.mkseo.myapplication.loading_dialog;
import com.example.mkseo.myapplication.orderListViewAdapter;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link itemListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link itemListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class itemListFragment extends Fragment {

    private static String url = "http://leafrog.iptime.org:20080/v1/product/list";
    public ArrayList<itemInfoForBoss> itemInfo = new ArrayList<>();
    private Dialog dialog;
    private ListView listView;
    private loading_dialog loading_dialog;
    private String TAG = getClass().getSimpleName();
    private String ID;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;

    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public itemListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment productListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static itemListFragment newInstance(String param1, String param2) {
        itemListFragment fragment = new itemListFragment();
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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // init loading_dialog
        loading_dialog = new loading_dialog(getActivity());
        loading_dialog.setup();

        // get View first from inflater
        View view = inflater.inflate(R.layout.fragment_boss_item_list, container, false);

        // XML matching
        Button registerItemButton = (Button) view.findViewById(R.id.addProductButtonTag);
        listView = (ListView) view.findViewById(R.id.itemListViewOnItemListFragmentTag);

        // bring ID into local var
        SharedPreferences preferences = getActivity().getApplicationContext().getSharedPreferences("IDPASSWORD", getActivity().getApplicationContext().MODE_PRIVATE);
        ID = preferences.getString("id", null);

        // listview itemClickListener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "itemID - " + itemInfo.get(position).getProduct_id());
            }
        });


        // registerButton clickListener
        registerItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //currently move to itemAddActivity
                Intent intent = new Intent(getActivity(), itemAddActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        loading_dialog.show();
        // refresh
        requestToServer();

        System.out.println("itemListFragment is back on!");

    }

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
            JSONArray jsonArray = new JSONArray(response);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String information = jsonObject.getString("information");
                String name = jsonObject.getString("name");
                String price = jsonObject.getString("price");
                String item_ID = jsonObject.getString("product_id");
                itemInfo.add(new itemInfoForBoss(information, name, price, item_ID));
            }

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
            // for now we are using this null networkResponse as 401 error code
            statusCode = error.networkResponse.statusCode;
        } else {
            statusCode = 401;
        }

        String errorMessage;

        switch (statusCode) {
            case 400:
                errorMessage = "업체 아이디가 잘못되었습니";
                break;
            case 401:
                errorMessage = "limit이나 page가 숫자가 아닙니다";
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

        Log.d(TAG, "error occurred(" + statusCode + ") - " + error.getMessage());
    }

    protected void refreshListview() {
        Log.d(TAG, "refreshing listview...");

        itemListViewAdapter itemListViewAdapter = new itemListViewAdapter(getActivity(), itemInfo);
        listView.setAdapter(itemListViewAdapter);
        itemListViewAdapter.notifyDataSetChanged();
    }

    public void requestToServer() {
        //formal refresh source code -> will delete soon
//        final itemListViewAdapter itemListViewAdapter = new itemListViewAdapter(getActivity(), itemInfo);
//        listView.setAdapter(itemListViewAdapter);
//        itemInfo.clear();

        String getMessage = new StringBuilder().append(url).append("?id=").append(ID).toString();
        Log.d(TAG, "get_message - " + getMessage);

        itemListFragmentRequest itemListFragmentRequest = new itemListFragmentRequest(getMessage, getResponseListener(), getErrorListener());
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(itemListFragmentRequest);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

//    @Override
//    public void onAttach(final Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

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
