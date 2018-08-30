package com.flashsales;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.widget.ProgressBar;

public class ProgressAlert {

    private ProgressDialog progressDialog;

    public ProgressAlert(Context context, String title, String message) {
        progressDialog = new ProgressDialog(context);
        if (!title.equals("null") || title != null || title.equals(""))
            progressDialog.setTitle(title);
        if (!message.equals("null") || message != null || message.equals(""))
            progressDialog.setMessage(message);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIcon(R.drawable.logo);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void stopAlert() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

}
