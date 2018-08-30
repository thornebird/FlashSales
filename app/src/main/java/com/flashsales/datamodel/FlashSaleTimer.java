package com.flashsales.datamodel;

import android.os.CountDownTimer;
import android.util.Log;
import android.widget.TextView;

import java.util.Calendar;

public class FlashSaleTimer {

    public FlashSaleTimer() {
    }

    public void updateTimerMidnight(final TextView textView) {

        Calendar calendar = Calendar.getInstance();
        long currentMills = calendar.get(Calendar.MILLISECOND);


        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        long howMany = (c.getTimeInMillis() - System.currentTimeMillis());
        Log.i("sec", howMany - currentMills + "");

        long endMills = howMany - currentMills;
        startTimer(endMills, textView, "");

    }




    private String formatLong(long toFormat) {
        if (toFormat > 9) {
            return Long.toString(toFormat);
        }
        String format = Long.toString(toFormat);
        format = "0" + format;
        return format;
    }

    public void cartTimer(TextView tv, long mills, String message) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(mills);
        long endMills = (c.getTimeInMillis() - System.currentTimeMillis());
        startTimer(endMills, tv, message);
    }

    public void startTimer(long endMills, final TextView tv, final String message) {
        new CountDownTimer(endMills, 1000) {

            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                long minutes = seconds / 60;
                long hour = minutes / 60;

                seconds = seconds % 60;
                minutes = minutes % 60;
                hour = hour % 60;

                String secondsTyped = formatLong(seconds);
                String minutesTyped = formatLong(minutes);
                String hoursTyped = formatLong(hour);

                tv.setText(message + " " + hoursTyped + ":" + minutesTyped + ":" + secondsTyped);
                Log.i("sec2", millisUntilFinished + "");
            }

            public void onFinish() {
                /*    mTextField.setText("done!");*/
            }

        }.start();
    }


}
