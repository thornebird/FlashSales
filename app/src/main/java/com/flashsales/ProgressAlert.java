package com.flashsales;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.widget.ProgressBar;

public class ProgressAlert {

    private ProgressDialog progressDialog;

    public ProgressAlert(Context context, String title, String message) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        progressDialog.setIcon(R.drawable.ic_account_circle);
        progressDialog.setCancelable(false);
    }

    public void stopAlert(){
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }

}
