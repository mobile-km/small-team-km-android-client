package com.teamkn.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;
import com.teamkn.R;
import com.teamkn.base.utils.BaseUtils;

public class TeamknProgressDialog extends Dialog {
    private String message;

    public TeamknProgressDialog(Context context, String message) {
        super(context, R.style.teamkn_progress_dialog);
        this.message = message;
    }

    public TeamknProgressDialog(Context context, int resid) {
        super(context, R.style.teamkn_progress_dialog);
        this.message = context.getString(resid);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM, WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        setContentView(R.layout.teamkn_progress_dialog);

        if (!BaseUtils.is_str_blank(this.message)) {
            TextView message_textview = (TextView) findViewById(R.id.teamkn_progress_dialog_message);
            message_textview.setText(this.message);
        }
    }

    public static TeamknProgressDialog show(Context context, String message) {
        TeamknProgressDialog dialog = new TeamknProgressDialog(context, message);
        dialog.show();
        return dialog;
    }

    public static TeamknProgressDialog show(Context context) {
        TeamknProgressDialog dialog = new TeamknProgressDialog(context, R.string.now_loading);
        dialog.show();
        return dialog;
    }

}
