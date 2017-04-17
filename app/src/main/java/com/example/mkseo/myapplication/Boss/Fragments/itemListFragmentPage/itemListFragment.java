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
import com.example.mkseo.myapplication.Boss.itemInfoForBoss;
import com.example.mkseo.myapplication.R;
import com.example.mkseo.myapplication.loading_dialog;

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

    ListView listView;
    private loading_dialog loading_dialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // init loading_dialog
        loading_dialog = new loading_dialog(getActivity());
        loading_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // get View first from inflater
        View view = inflater.inflate(R.layout.fragment_boss_item_list, container, false);

        // XML matching
        Button registerItemButton = (Button) view.findViewById(R.id.addProductButtonTag);
        listView = (ListView) view.findViewById(R.id.itemListViewOnItemListFragmentTag);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("itemID : " + itemInfo.get(position).getProduct_id());
            }
        });


        registerItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    public void requestToServer() {
        final itemListViewAdapter itemListViewAdapter = new itemListViewAdapter(getActivity(), itemInfo);
        listView.setAdapter(itemListViewAdapter);
        itemInfo.clear();

        //region response
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
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

                    itemListViewAdapter.refreshAdapter();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        //endregion
        //region errorResponse
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                int statusCode = error.networkResponse.statusCode;
                String errorMessage;
                loading_dialog.dismiss();

                switch (statusCode) {
                    case 400:
                        errorMessage = "업체 ID가 잘못되었습니다";
                        break;
                    case 401:
                        // 401 : limit, page 가 숫자가 아닌 경우
                        errorMessage = "limit, page가 숫자가 아닌 경우";
                        break;
                    case 410:
                        errorMessage = "Query got wrong, sorry!";
                        break;
                    default:
                        errorMessage = "unknown error";
                        break;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                dialog = builder.setMessage(errorMessage)
                        .setNegativeButton("ok", null)
                        .create();
                dialog.show();

                System.out.println("error occurred : " + error.getMessage());
                System.out.println("error status code : " + statusCode);
            }
        };
        //endregion

        SharedPreferences preferences = getActivity().getApplicationContext().getSharedPreferences("IDPASSWORD", getActivity().getApplicationContext().MODE_PRIVATE);
        String id = preferences.getString("id", null);

        String getMessage = new StringBuilder().append(url).append("?id=").append(id).toString();
        System.out.println("getMessage: " + getMessage);

        itemListFragmentRequest itemListFragmentRequest = new itemListFragmentRequest(getMessage, responseListener, errorListener);
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
