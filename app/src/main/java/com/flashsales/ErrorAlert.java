package com.flashsales;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

public class ErrorAlert {

    private AlertDialog dialog;

    public ErrorAlert(final Context context, String title, String message, boolean isActive) {
        dialog = new AlertDialog.Builder(context).create();
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setIcon(R.drawable.ic_signal_cellular_connected_no_internet_4_bar);
        if (!isActive) {
            dialog.setButton(Dialog.BUTTON_POSITIVE, "Activate internet", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    context.startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                }
            });
            dialog.show();
        }
    }

    public AlertDialog getDialog() {
        return dialog;
    }

    public ErrorAlert(final Context context) {
        dialog = new AlertDialog.Builder(context).create();
        if (dialog != null && !dialog.isShowing()) {
            dialog.setTitle(context.getString(R.string.error_internet_occured));
            dialog.setMessage(context.getString(R.string.check_internet_settings));
            dialog.setIcon(R.drawable.ic_signal_cellular_connected_no_internet_4_bar);
            dialog.setButton(Dialog.BUTTON_POSITIVE, "Activate internet", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    context.startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                }
            });
            dialog.show();
        }
    }


    public void stopDialog() {
        dialog.dismiss();
    }
}
