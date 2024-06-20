package com.customapi.fmkpl.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.customapi.fmkpl.R;


public class PrivacyDialog extends Dialog {
    private String content;
    private View.OnClickListener confirmClickListener;
    private View.OnClickListener cancelClickListener;

    public PrivacyDialog(Context context,
                         View.OnClickListener confirmClickListener, View.OnClickListener cancelClickListener) {
        super(context, R.style.Dialog);
        this.content = content;
        this.confirmClickListener = confirmClickListener;
        this.cancelClickListener = cancelClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.privacy_dialog);
        TextView dialog_title = (TextView) findViewById(R.id.dialog_title);
        TextPaint paint = dialog_title.getPaint();
        paint.setFakeBoldText(true);

        TextView dialog_confirm = (TextView) findViewById(R.id.dialog_confirm);
        TextView dialog_cancel = (TextView) findViewById(R.id.dialog_cancel);
        if (null != confirmClickListener) {
            dialog_confirm.setOnClickListener(confirmClickListener);
        }
        if (null != cancelClickListener) {
            dialog_cancel.setOnClickListener(cancelClickListener);
        } else {
            dialog_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PrivacyDialog.this.dismiss();
                }
            });
        }
    }

    public void setCanotBackPress() {
        this.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    return true;
                }
                return false;
            }
        });
    }
}
