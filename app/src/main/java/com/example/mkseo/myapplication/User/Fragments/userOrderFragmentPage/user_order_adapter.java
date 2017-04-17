package com.example.mkseo.myapplication.User.Fragments.userOrderFragmentPage;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mkseo.myapplication.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by mkseo on 2017. 3. 22..
 */

public class user_order_adapter extends BaseAdapter {

    private Context context;
    private ArrayList<ArrayList<HashMap<String, String>>> itemList;
    private int[] table_no;
    private ViewGroup parent;

    public user_order_adapter(Context context, ArrayList<ArrayList<HashMap<String, String>>> itemList, int[] table_no) {
        this.context = context;
        this.itemList = itemList;
        this.table_no = table_no;
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

        View row = convertView;
        // convertview is often not inserted. we need to check and fix it.
        if (row == null) {
            LayoutInflater layoutInflater = ((Activity) context).getLayoutInflater();
            // inflate is XML file(user_Item_listview_rowew_row.xml.xml
            row = layoutInflater.inflate(R.layout.user_order_listview_row, parent, false);
        }

        // findviewbyid works on View, row
        // matching with them
        TextView table_noText = (TextView) row.findViewById(R.id.tableNumber_user_order_listview_row_text);
        LinearLayout itemBoard = (LinearLayout) row.findViewById(R.id.itemBoard_user_order_listview_row_text);

        //find 해당 position의 아이템 정보 context
        final ArrayList<HashMap<String, String>> nowItemInfo = itemList.get(position);


        if (itemBoard.getChildCount() == 0) {
            for (HashMap<String, String> itemArray : nowItemInfo) {
                View tempView = addView(itemArray.get("name"), itemArray.get("count"));
                itemBoard.addView(tempView);
            }
        }

        System.out.println(position + " itemBoard's child count : " + itemBoard.getChildCount());

        // table_no update
        table_noText.setText(String.valueOf(table_no[position]));

        // row is view
        return row;
    }

    public View addView(String name, String count) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View layout = layoutInflater.inflate(R.layout.user_order_listview_row_item, parent, false);

        TextView itemName = (TextView) layout.findViewById(R.id.nameText_user_order_listview_row_item);
        TextView itemCount = (TextView) layout.findViewById(R.id.countText_user_order_listview_row_item);

        itemName.setText(name);
        itemCount.setText(count);

        return layout;
    }

}
