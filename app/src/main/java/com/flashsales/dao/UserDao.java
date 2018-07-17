package com.flashsales.dao;

import android.content.Context;
import android.util.Log;

import com.flashsales.ProgressAlert;
import com.flashsales.R;
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

    public void setmDatabase(Context context, User user) {
        final ProgressAlert progressAlert = new ProgressAlert(context, context.getResources().getString(R.string.loading),
                context.getResources().getString(R.string.registeration_process));

        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        //need to save to prefs
        String userId = mDatabase.push().getKey();
        SharedPreferenceUtils prefs = new SharedPreferenceUtils(context);
        prefs.saveFirebaseId(userId);
        mDatabase.child(userId).setValue(user);
        mDatabase.child(userId).addValueEventListener(new ValueEventListener() {
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


    public void updateUser(final Context context, User user) {
        if (user == null)
            return;
        // g et i d from prefs set new values
        SharedPreferenceUtils prefs = new SharedPreferenceUtils(context);
        String id = prefs.getFirebaseId();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        mDatabase.child(id).child("name").setValue(user.getName());
        mDatabase.child(id).child("email").setValue(user.getEmail());
        mDatabase.child(id).child("password").setValue(user.getEmail());
        mDatabase.child(id).child("gender").setValue(user.getGender());

    }

}
