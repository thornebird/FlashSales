package com.flashsales.dao;

import android.util.Log;

import com.flashsales.Utils.Configs;
import com.flashsales.datamodel.OrderObject;
import com.flashsales.datamodel.PrizeOrderObject;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FreeOrderDao {
    private static final FreeOrderDao ourInstance = new FreeOrderDao();

    public static FreeOrderDao getInstance() {
        return ourInstance;
    }

    public void uploadFreeOrder(PrizeOrderObject prizeOrderObject) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference(Configs.KEY_USER_FIREBASE_FREEORDERS);

        String key = db.push().getKey();
        db.child(key).setValue(prizeOrderObject);
        db.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                PrizeOrderObject prizeOrderObject1 = dataSnapshot.getValue(PrizeOrderObject.class);

                Log.i("orderObject", prizeOrderObject1.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void updateFreeOrders(final FreeOrdersListener freeOrdersListener, final boolean upDateEmail, final String email, final String newEmail) {
        final ArrayList<PrizeOrderObject> freePrizes = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Configs.KEY_USER_FIREBASE_FREEORDERS);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot orderItem : dataSnapshot.getChildren()) {
                    String  key = orderItem.getKey();
                    PrizeOrderObject prizeOrderObject = orderItem.getValue(PrizeOrderObject.class);
                    if (prizeOrderObject.getEmailIdentifier().equals(email)) {
                        freePrizes.add(prizeOrderObject);
                        if (upDateEmail && !newEmail.equals("")) {
                            updateEmailForOrders(key,newEmail);
                        }
                    }
                }
                if (freeOrdersListener != null)
                    freeOrdersListener.onFreeOrdersLoaded(freePrizes);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void updateEmailForOrders(String userId, String newEmail) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Configs.KEY_USER_FIREBASE_FREEORDERS);
        reference.child(userId).child("emailIdentifier").setValue(newEmail);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot orderItem : dataSnapshot.getChildren()) {
                    OrderObject orderObject = orderItem.getValue(OrderObject.class);
                    Log.d("orderObject", orderObject.toString());

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public interface FreeOrdersListener {
        public void onFreeOrdersLoaded(ArrayList<PrizeOrderObject> freeOrders);
    }
}
