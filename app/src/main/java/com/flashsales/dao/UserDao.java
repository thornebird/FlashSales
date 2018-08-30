package com.flashsales.dao;

import android.content.Context;
import android.util.Log;

import com.flashsales.ProgressAlert;
import com.flashsales.R;
import com.flashsales.Utils.Configs;
import com.flashsales.Utils.SharedPreferenceUtils;
import com.flashsales.datamodel.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserDao {
    private DatabaseReference mDatabase;
    private static UserDao mInstance;

    public static synchronized UserDao getInstance() {
        if (mInstance == null) {
            mInstance = new UserDao();
        }
        return mInstance;
    }

    public void addUserToDb(Context context, User user) {
        final ProgressAlert progressAlert = new ProgressAlert(context, context.getResources().getString(R.string.loading),
                context.getResources().getString(R.string.registeration_process));

        mDatabase = FirebaseDatabase.getInstance().getReference(Configs.KEY_USER_FIREBASE_TABLE);

        String key = mDatabase.push().getKey();
        mDatabase.child(key).setValue(user);
        mDatabase.child(key).addValueEventListener(new ValueEventListener() {
            @Override()
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Log.i("datasnap", "data");
                progressAlert.stopAlert();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i("DatabaseError", databaseError.getMessage());
                progressAlert.stopAlert();

            }
        });
    }


    private String encodeEmail(String toEncode) {
        return toEncode.replace(".", ",");
    }

    private String decodeEmail(String toDecode) {
        return toDecode.replace(",", ".");
    }

    public User getUser() {
        User user = new User();

        return user;
    }

}
