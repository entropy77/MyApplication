package com.example.mkseo.myapplication.Boss;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.mkseo.myapplication.R;
import com.example.mkseo.myapplication.User.itemInfoForUser;
import com.example.mkseo.myapplication.User.decimalChange;

import java.util.ArrayList;

/**
 * Created by mkseo on 2017. 3. 15..
 */

public class bossOrderListAdapter extends BaseAdapter{

    private Context context;
    private ArrayList<itemInfoForUser> itemList;
    private com.example.mkseo.myapplication.User.PayingPage.payingActivity payingActivity;

    public bossOrderListAdapter(Context context, ArrayList<itemInfoForUser> itemList, int id) {
        this.context = context;
        this.itemList = itemList;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void refreshAdapter() {
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {

        View row = convertView;
        // convertview is often not inserted. we need to check and fix it.
        if (row == null) {
            LayoutInflater layoutInflater = ((Activity)context).getLayoutInflater();

            // inflate is XML file(user_Item_listview_rowew_row.xml.xml
            row = layoutInflater.inflate(R.layout.user_item_listview_row, parent, false);
        }

        // findviewbyid works on View, row
        // matching with them
        TextView itemName = (TextView)row.findViewById(R.id.itemNameTag);
        final TextView itemPrice = (TextView)row.findViewById(R.id.itemPriceTag);
        final TextView itemNumber = (TextView)row.findViewById(R.id.itemNumberTag);
        Button increaseButton = (Button)row.findViewById(R.id.itemIncreaseButtonTag);
        Button decreaseButton = (Button)row.findViewById(R.id.itemDecreaseButtonTag);
        final TextView totalPrice = (TextView)row.findViewById(R.id.totalPriceTag);

        //find 해당 position의 아이템 정보 context
        final itemInfoForUser nowItemInfo = itemList.get(position);

        // item name assign
        itemName.setText(nowItemInfo.getName());

        // item price assign
        // use Integer method to change integer into String
        // since setText only use String
        // above methods applying comma for every 3 numbers of 0

        decimalChange decimalChange = new decimalChange(nowItemInfo.getPrice());
        itemPrice.setText(decimalChange.converting());

        // item number assign
        itemNumber.setText(Integer.toString(nowItemInfo.getCount()));

        // row is view
        return row;
    }
}
