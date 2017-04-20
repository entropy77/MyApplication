package com.example.mkseo.myapplication.User;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.mkseo.myapplication.R;
import com.example.mkseo.myapplication.User.PayingPage.totalPriceCalculating;
import com.example.mkseo.myapplication.User.QRcodeScanPage.qrScanActivity;

import java.util.ArrayList;

/**
 * Created by mkseo on 2017. 3. 8..
 */

public class ListViewAdapter extends BaseAdapter {

    private String TAG = this.getClass().getSimpleName();

    private Context context;
    private ArrayList<itemInfoForUser> itemList;
    private int id;
    private com.example.mkseo.myapplication.User.PayingPage.payingActivity payingActivity;

    public ListViewAdapter(Context context, ArrayList<itemInfoForUser> itemList, int id) {
        this.context = context;
        this.itemList = itemList;
        this.id = id;
        // id 1 = QRscanActivity
        // id 2 = PayingActivity
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

    public void refreshAdapter(ArrayList<itemInfoForUser> itemList) {
        Log.d(TAG, "refreshing..");
        this.itemList = itemList;
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {

        View row = convertView;
        // convertview is often not inserted. we need to check and fix it.
        if (row == null) {
            LayoutInflater layoutInflater = ((Activity) context).getLayoutInflater();

            // inflate is XML file(user_Item_listview_rowew_row.xml.xml
            row = layoutInflater.inflate(R.layout.user_item_listview_row, parent, false);
        }

        // findviewbyid works on View, row
        // matching with them
        TextView itemName = (TextView) row.findViewById(R.id.itemNameTag);
        final TextView itemPrice = (TextView) row.findViewById(R.id.itemPriceTag);
        final TextView itemNumber = (TextView) row.findViewById(R.id.itemNumberTag);
        Button increaseButton = (Button) row.findViewById(R.id.itemIncreaseButtonTag);
        Button decreaseButton = (Button) row.findViewById(R.id.itemDecreaseButtonTag);
        final TextView totalPrice = (TextView) row.findViewById(R.id.totalPriceTag);

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

        // assign onclicklistener on buttons
        increaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                listViewClass.increaseButtonClicked(position);
                itemList.get(position).setCount(itemList.get(position).getCount() + 1);
                itemPrice.setText(Integer.toString(nowItemInfo.getPrice() * nowItemInfo.getCount()));

                if (id == 2) {

                    // total price calculating
                    totalPriceCalculating totalPriceCalculating = new totalPriceCalculating(itemList);
                    int tempTotalPrice = totalPriceCalculating.calculating();
                    final decimalChange decimalChange1 = new decimalChange(tempTotalPrice);

                    // put decimal format on total price
                    // find method on PayingActivity this is soooooo important!!!(2017. 03. 10)
                    if (context instanceof com.example.mkseo.myapplication.User.PayingPage.payingActivity) {
                        ((com.example.mkseo.myapplication.User.PayingPage.payingActivity) context).setTotalPriceonTextView(decimalChange1.converting());
                    }
                }

                refreshAdapter(itemList);
            }
        });


        decreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (itemList.get(position).getCount() > 1) {
                    itemList.get(position).setCount(itemList.get(position).getCount() - 1);
                    itemPrice.setText(Integer.toString(nowItemInfo.getPrice() * nowItemInfo.getCount()));
                } else if (itemList.get(position).getCount() == 1) {
                    itemList.remove(position);

                    // remove local raw message in qrScanActivity as well
                    if (context instanceof qrScanActivity) {
                        ((qrScanActivity) context).removeRawMessage(position);
                    }
                }

                if (itemList.size() - 1 > -1)
                    System.out.println("last item table_no : " + itemList.get(itemList.size() - 1).getTable_no());


                if (id == 2) {

                    // total price calculating
                    totalPriceCalculating totalPriceCalculating = new totalPriceCalculating(itemList);
                    int tempTotalPrice = totalPriceCalculating.calculating();
                    final decimalChange decimalChange1 = new decimalChange(tempTotalPrice);

                    // put decimal format on total price
                    // find method on PayingActivity this is soooooo important!!!(2017. 03. 10)
                    if (context instanceof com.example.mkseo.myapplication.User.PayingPage.payingActivity) {
                        ((com.example.mkseo.myapplication.User.PayingPage.payingActivity) context).setTotalPriceonTextView(decimalChange1.converting());
                    }
                }

                refreshAdapter(itemList);

            }
        });

        // row is view
        return row;
    }

}
