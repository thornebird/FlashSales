package com.flashsales.dao;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.firebase.client.FirebaseError;
import com.flashsales.Utils.Configs;
import com.flashsales.Utils.SharedPreferenceUtils;
import com.flashsales.datamodel.CartProduct;
import com.flashsales.datamodel.OrderObject;
import com.flashsales.datamodel.PaymentObject;
import com.flashsales.datamodel.ShippingAddress;
import com.flashsales.datamodel.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OrderDao {
    private static OrderDao mInstance;
    private OrdersListener mListener;

    public static synchronized OrderDao getInstance() {
        if (mInstance == null) {
            mInstance = new OrderDao();
        }
        return mInstance;
    }

    public void uploadOrder(final Context context, OrderObject orderObject) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference(Configs.KEY_USER_FIREBASE_ORDERS);

        String key = db.push().getKey();
        db.child(key).setValue(orderObject);
        db.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                OrderObject orderObject = dataSnapshot.getValue(OrderObject.class);


                Log.i("orderObject", orderObject.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void updateOrderHistory(final  OrdersListener mListener, final boolean upDateEmail,final String email,  final String newEmail) {
        final ArrayList<OrderObject> orders = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Configs.KEY_USER_FIREBASE_ORDERS);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot orderItem : dataSnapshot.getChildren()) {
                    String key = orderItem.getKey();
                    Log.d("key",key);
                    OrderObject orderObject = orderItem.getValue(OrderObject.class);

                    if(orderObject.getEmailIdentifier().equals(email)){
                        orders.add(orderObject);
                        if(upDateEmail && !newEmail.equals("")){
                            updateEmailForOrders(key,newEmail);
                        }
                    }
                }
                if(mListener!=null)
                    mListener.OnOrderLoaded(orders);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
               Log.e("DatabaseError",databaseError.toString());
            }
        });


    }

    private void updateEmailForOrders(String userId,String newEmail){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Configs.KEY_USER_FIREBASE_ORDERS);
        reference.child(userId).child("emailIdentifier").setValue(newEmail);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot orderItem : dataSnapshot.getChildren()) {
                    OrderObject orderObject = orderItem.getValue(OrderObject.class);
                    Log.d("orderObject",orderObject.toString());

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private String encodeEmail(String toEncode) {
        return toEncode.replace(".", ",");
    }

    private String decodeEmail(String toDecode) {
        return toDecode.replace(",", ".");
    }

    public interface OrdersListener{
        public void OnOrderLoaded(ArrayList<OrderObject> orderObjects);
    }
}
