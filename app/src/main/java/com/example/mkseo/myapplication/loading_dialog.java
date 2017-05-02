package com.example.mkseo.myapplication;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;

/**
 * Created by mkseo on 2017. 3. 23..
 */

public class loading_dialog extends Dialog {

    loading_dialog self = this;

    public loading_dialog(Context context) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.loading_dialog_view);
    }

    public void setup() {
        // make dialog background transpart
        self.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // make dialog background unselectable
        self.setCanceledOnTouchOutside(false);
    }

}