package com.example.mkseo.myapplication.Boss.Fragments.itemListFragmentPage;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.mkseo.myapplication.Boss.itemInfoForBoss;
import com.example.mkseo.myapplication.R;
import com.example.mkseo.myapplication.User.decimalChange;

import java.util.ArrayList;

/**
 * Created by mkseo on 2017. 3. 16..
 */

public class itemListViewAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<itemInfoForBoss> itemList;

    public itemListViewAdapter(Context context, ArrayList<itemInfoForBoss> itemList) {
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
            LayoutInflater layoutInflater = ((Activity) context).getLayoutInflater();

            // inflate is XML file(user_Item_listview_rowew_row.xml.xml
            row = layoutInflater.inflate(R.layout.boss_item_listview_row, parent, false);
        }

        // findviewbyid works on View, row
        // matching with them
        TextView itemNameText = (TextView) row.findViewById(R.id.itemNameOnBossItemListviewRowTag);
        TextView itemInformationText = (TextView) row.findViewById(R.id.itemInformationTextOnBossItemListviewRowTag);
        TextView itemPriceText = (TextView) row.findViewById(R.id.itemPriceOnBossItemListviewRowTag);

        //find 해당 position의 아이템 정보 context
        final itemInfoForBoss nowItemInfo = itemList.get(position);

        // item name assign, item information assign
        itemNameText.setText(nowItemInfo.getName());
        itemInformationText.setText(nowItemInfo.getInformation());

        // item price assign
        // use Integer method to change integer into String
        // since setText only use String
        // above methods applying comma for every 3 numbers of 0
        decimalChange decimalChange = new decimalChange(nowItemInfo.getPrice());
        itemPriceText.setText(decimalChange.converting());

        // row is view
        return row;
    }
}
