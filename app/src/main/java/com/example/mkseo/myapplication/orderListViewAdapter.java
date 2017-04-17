package com.example.mkseo.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mkseo.myapplication.Boss.itemInfoForBoss;
import com.example.mkseo.myapplication.R;
import com.example.mkseo.myapplication.User.decimalChange;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by mkseo on 2017. 3. 18..
 */

public class orderListViewAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<ArrayList<HashMap<String, String>>> itemList;
    private ArrayList<HashMap<String, String>> items;
    private ViewGroup parent;

    public orderListViewAdapter(Context context, ArrayList<ArrayList<HashMap<String, String>>> itemList, ArrayList<HashMap<String, String>> items) {
        this.context = context;
        this.itemList = itemList;
        this.items = items;
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

        this.parent = parent;

        boolean isRowInitWasNull = false;

        View row = convertView;
        // convertview is often not inserted. we need to check and fix it.
        if (row == null) {
            LayoutInflater layoutInflater = ((Activity) context).getLayoutInflater();
            // inflate is XML file(user_Item_listview_rowew_row.xml.xml
            row = layoutInflater.inflate(R.layout.boss_order_listview_row, parent, false);
            isRowInitWasNull = true;
        }

        // findviewbyid works on View, row
        // matching with them
        TextView table_noText = (TextView) row.findViewById(R.id.tableNumber_boss_order_listview_row_text);
        TextView itemId = (TextView) row.findViewById(R.id.order_idText_boss_order_listview_row);
        TextView status = (TextView) row.findViewById(R.id.statusText_boss_order_listview_row);
        TextView accountIdText = (TextView) row.findViewById(R.id.account_idText_boss_order_listview_row);
        TextView compnayIdText = (TextView) row.findViewById(R.id.company_idText_boss_order_listview_row);
        TextView phoneText = (TextView) row.findViewById(R.id.phoneText_boss_order_listview_row);
        LinearLayout itemBoard = (LinearLayout) row.findViewById(R.id.itemBoard_boss_order_listview_row_text);

        //find 해당 position의 아이템 정보 context
        final ArrayList<HashMap<String, String>> nowItemInfo = itemList.get(position);

        if (!isRowInitWasNull) {
            itemBoard.removeAllViews();
        }
        for (HashMap<String, String> itemArray : nowItemInfo) {
            View tempView = addView(itemArray.get("name"), itemArray.get("count"));
            itemBoard.addView(tempView);
        }

//        System.out.println(position + " itemBoard's child count : " + itemBoard.getChildCount());

        // table_no update
        table_noText.setText(items.get(position).get("table_no"));
        itemId.setText("orderID : " + items.get(position).get("id"));
        status.setText("status : " + items.get(position).get("status"));
        accountIdText.setText("account_ID : " + items.get(position).get("account_id"));
        compnayIdText.setText("company_ID : " + items.get(position).get("company_id"));
        phoneText.setText("phone : " + items.get(position).get("phone"));

        // row is view
        return row;
    }

    public View addView(String name, String count) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View layout = layoutInflater.inflate(R.layout.boss_order_listview_row_item, parent, false);

        TextView itemName = (TextView) layout.findViewById(R.id.nameText_boss_order_listview_row_item);
        TextView itemCount = (TextView) layout.findViewById(R.id.countText_boss_order_listview_row_item);

        itemName.setText(name);
        itemCount.setText(count);

        return layout;
    }
}