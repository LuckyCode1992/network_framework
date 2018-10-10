package com.justcode.hxl.networkframework.loading;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.justcode.hxl.networkframework.R;


public class Dialog_loading extends Dialog {

    public Dialog_loading(Context context) {
            super(context, R.style.MyDialog);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.my_dialog_loading);
            //按空白处不能取消动画
            setCanceledOnTouchOutside(false);
        }


}
