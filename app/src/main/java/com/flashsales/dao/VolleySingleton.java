package com.flashsales.dao;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleySingleton {

    private static final String TAG = VolleySingleton.class.getSimpleName();
    private static VolleySingleton mInstance;
    private RequestQueue requestQueue;
    private Context context;



    private VolleySingleton(Context context){
        this.context = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized VolleySingleton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new VolleySingleton(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQue(Request<T> request){
        request.setTag(request);
        getRequestQueue().add(request);
    }

    public void cancelPendingRequests(Object tag){
        if(requestQueue != null)
            requestQueue.cancelAll(tag);
    }
}
