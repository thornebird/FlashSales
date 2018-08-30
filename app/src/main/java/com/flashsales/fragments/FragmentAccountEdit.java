package com.flashsales.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;

import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.flashsales.R;
import com.flashsales.SplashActivity;
import com.flashsales.Utils.SharedPreferenceUtils;
import com.flashsales.Utils.Utils;
import com.flashsales.dao.FreeOrderDao;
import com.flashsales.dao.OrderDao;
import com.flashsales.datamodel.OrderObject;
import com.flashsales.datamodel.PrizeOrderObject;
import com.flashsales.datamodel.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLDisplay;

public class FragmentAccountEdit extends Fragment implements View.OnClickListener {

    private OnAccountEditSaved mListener;
    private User user;
    private final static String KEY_USER = "keyUser";
    private ArrayList<String> genders;
    private EditText etName, etEmail;
    /*    private EditText etPass;*/
    private String genderSelcted = "";
    private ConstraintLayout contenFrame;
    private final static int ACTION_EMAIL_UPDATE = 0;
    private final static int ACTION_PASS_UPDATE = 1;
    private int action = 0;
    boolean loggedInFb = false;

    public static FragmentAccountEdit newInstance(User user) {
        FragmentAccountEdit fragment = new FragmentAccountEdit();
        Bundle args = new Bundle();
        args.putParcelable(KEY_USER, user);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnAccountEditSaved) context;
        } catch (ClassCastException ex) {

        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = getArguments().getParcelable(KEY_USER);
        if (!user.getImageFb().equals(""))
            loggedInFb = true;
        genders = new ArrayList<>();
        genders.add(getContext().getString(R.string.select_gender));
        genders.add(getContext().getString(R.string.male));
        genders.add(getContext().getString(R.string.female));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit_acc, container, false);

        TextInputLayout ilName = (TextInputLayout) rootView.findViewById(R.id.il_name);
        final TextInputLayout ilEmail = (TextInputLayout) rootView.findViewById(R.id.il_email);
        //TextInputLayout ilPassword= (TextInputLayout)rootView.findViewById(R.id.il_password);

        contenFrame = (ConstraintLayout) rootView.findViewById(R.id.content_frame);

        ImageView iv = (ImageView) rootView.findViewById(R.id.iv);

        if (!user.getImageFb().equals("")) {
            Picasso.with(getContext()).load(user.getImageFb()).into(iv);
        }

        etName = (EditText) rootView.findViewById(R.id.et_name);
        etEmail = (EditText) rootView.findViewById(R.id.et_email);

        etName.setText(user.getName());
        etEmail.setText(user.getEmail());
        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isEmailValid()) {
                    etEmail.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_circle, 0, 0, 0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isEmailValid()) {
                    etEmail.setError(getString(R.string.error_invalid_email));
                } else {
                    etEmail.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_circle, 0, 0, 0);
                }
            }
        });

        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isNameValid()) {
                    etName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_circle, 0, 0, 0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isNameValid()) {
                    etName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_circle, 0, 0, 0);
                }else{
                    etName.setError(getString(R.string.name_required));
                }
            }
        });

        //  FrameLayout frameFbLogin = (FrameLayout)rootView.findViewById(R.id.frmae_loginfb);

        TextView tvChangeEmail = (TextView) rootView.findViewById(R.id.tv_change_email);
        TextView tvChangePass = (TextView) rootView.findViewById(R.id.tv_change_password);
        TextView tvForgotPass = (TextView) rootView.findViewById(R.id.tv_forgot_password);
        TextView tvLogout = (TextView) rootView.findViewById(R.id.tv_log_out);
        tvLogout.setOnClickListener(this);

        if (loggedInFb) {
            tvChangeEmail.setVisibility(View.INVISIBLE);
            tvChangePass.setVisibility(View.INVISIBLE);
            tvForgotPass.setVisibility(View.INVISIBLE);
           // tvLogout.setVisibility(View.INVISIBLE);
        } else {
            //   frameFbLogin.setVisibility(View.INVISIBLE);
            tvChangeEmail.setOnClickListener(this);
            tvChangePass.setOnClickListener(this);
            tvForgotPass.setOnClickListener(this);
        }

        return rootView;
    }

 /*   private boolean isMale() {
        return user.getGender().equals(Utils.male);
    }
*/
    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_change_email:
                changeEmailDialog(user.getEmail());
                break;

            case R.id.tv_change_password:
                changePassDialog(user.getEmail());
                break;

            case R.id.tv_forgot_password:
                sendForgotPass();
                break;
            case R.id.tv_log_out:
                if (loggedInFb) {
                    facebookLogout();
                } else {
                    fireLogout();
                }
                break;
        }
    }

    private void changePassDialog(String email) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setCancelable(true);

        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_edit_password, null);
        dialog.setContentView(view);

        final ConstraintLayout framePass = (ConstraintLayout) view.findViewById(R.id.frame_pass_dialog);

        ImageView ivClose = (ImageView) view.findViewById(R.id.im_close);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        final TextInputEditText etEmail = (TextInputEditText) view.findViewById(R.id.et_email_current);
        etEmail.setText(email);
        etEmail.setFocusable(false);
        etEmail.setFocusableInTouchMode(false);
        etEmail.setClickable(false);

        final TextInputEditText etCurrentPass = (TextInputEditText) view.findViewById(R.id.et_old_pass);
        etCurrentPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!isPasswordSet(etCurrentPass.getText().toString())) {
                    etCurrentPass.setError(getActivity().getResources().getString(R.string.error_pass_length));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isPasswordSet(etCurrentPass.getText().toString())) {
                    etCurrentPass.setError(getActivity().getResources().getString(R.string.error_pass_length));
                }
            }
        });

        final TextInputEditText etNewPass = (TextInputEditText) view.findViewById(R.id.et_new_pass);
        etNewPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!isPasswordSet(etNewPass.getText().toString())) {
                    etNewPass.setError(getActivity().getResources().getString(R.string.error_pass_length));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isPasswordSet(etNewPass.getText().toString())) {
                    etNewPass.setError(getActivity().getResources().getString(R.string.error_pass_length));
                }
            }
        });
        Button btnSave = (Button) view.findViewById(R.id.btn_save_pass);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_save_pass:
                        if (isPasswordSet(etCurrentPass.getText().toString().trim()) &&
                                isPasswordSet(etNewPass.getText().toString().trim())) {
                            reauthenticateCredentials(ACTION_PASS_UPDATE,
                                    etEmail.getText().toString()
                                    , etCurrentPass.getText().toString()
                                    , etNewPass.getText().toString().trim(), framePass);
                        } else if (!isPasswordSet(etCurrentPass.getText().toString().trim())) {
                            /// snack current pass to short
                            notifyUser(getString(R.string.incorrect_password), framePass);
                        } else if (!isPasswordSet(etNewPass.getText().toString().trim())) {
                            /// snack new pass  to short
                            notifyUser(getString(R.string.new_password_to_short), framePass);
                        }
                        break;
                }
            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void changeEmailDialog(String email) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setCancelable(true);

        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_edit_email, null);
        dialog.setContentView(view);

        final ConstraintLayout frameDialogEmail = (ConstraintLayout) view.findViewById(R.id.frame_email_dialog);

        ImageView ivClose = (ImageView) view.findViewById(R.id.im_close);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        final TextInputEditText etCurrentEmail = (TextInputEditText) view.findViewById(R.id.et_email_current);
        etCurrentEmail.setText(user.getEmail());
        etCurrentEmail.setText(email);
        etCurrentEmail.setFocusable(false);
        etCurrentEmail.setFocusableInTouchMode(false);
        etCurrentEmail.setClickable(false);

        final TextInputEditText etNewEmail = (TextInputEditText) view.findViewById(R.id.et_new_email);
        etNewEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isEmailValid(s.toString())) {
                    etNewEmail.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_circle, 0, 0, 0);
                } else {
                    etNewEmail.setError(getActivity().getResources().getString(R.string.error_invalid_email));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isEmailValid(s.toString())) {
                    etNewEmail.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_circle, 0, 0, 0);
                } else {
                    etNewEmail.setError(getActivity().getResources().getString(R.string.error_invalid_email));
                }
            }
        });

        final TextInputEditText etPass = (TextInputEditText) view.findViewById(R.id.et_pass);
        etPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!isPasswordSet(etPass.getText().toString())) {
                    etPass.setError(getActivity().getResources().getString(R.string.error_pass_length));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isPasswordSet(etPass.getText().toString())) {
                    etPass.setError(getActivity().getResources().getString(R.string.error_pass_length));
                }
            }
        });

        Button btnSave = (Button) view.findViewById(R.id.btn_save_email);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEmailValid(etCurrentEmail.getText().toString().trim()) &&
                        isEmailValid(etNewEmail.getText().toString().trim()) && isPasswordSet(etPass.getText().toString().trim())) {
                    reauthenticateCredentials(ACTION_EMAIL_UPDATE, etCurrentEmail.getText().toString().trim(),
                            etPass.getText().toString().trim(), etNewEmail.getText().toString().trim(), frameDialogEmail);
                }
            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private boolean isPasswordSet(String passWord) {
        return passWord.length() > 7;
    }

    private void reauthenticateCredentials(final int action, final String currentEmail, String password, final String newValue,
                                           final ConstraintLayout contenFrame) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential credential = EmailAuthProvider
                .getCredential(currentEmail, password);

        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    if (action == ACTION_EMAIL_UPDATE) {
                        updateEmail(user,currentEmail, newValue, contenFrame);
                    } else if (action == ACTION_PASS_UPDATE) {
                        updatePass(user, newValue, contenFrame);
                    }
                } else {
                    notifyUser(getString(R.string.incorrect_password), contenFrame);

                }
            }
        });
    }

    private void sendForgotPass() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.sendPasswordResetEmail(etEmail.getText().toString().trim())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String message = "";
                        if (task.isSuccessful()) {
                            message = getString(R.string.forgot_password_sent);
                            message += " " + etEmail.getText().toString().trim();
                        } else {
                            message = getString(R.string.forgotten_pass_not_sent);
                        }
                        notifyUser(message, contenFrame);
                    }
                });
    }

    private void updateEmail(final FirebaseUser firebaseuser, final String currentEmail, final String newEmail, final ConstraintLayout contenFrame) {
        firebaseuser.updateEmail(newEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    //Snackbar email updated succesfully
                    // Snackbar.make(contenFrame,"Email was updated succesfully", Snackbar.LENGTH_SHORT).show();
                    notifyUser(getString(R.string.email_updated), contenFrame);
                    SharedPreferenceUtils sharedPreferenceUtils = new SharedPreferenceUtils(getContext());
                    user.setEmail(newEmail);
                    sharedPreferenceUtils.saveLogins(user);
                    user = sharedPreferenceUtils.getUser();
                    etEmail.setText(user.getEmail());
                    OrderDao orderDao = new OrderDao();
                    orderDao.updateOrderHistory(new OrderDao.OrdersListener() {
                        @Override
                        public void OnOrderLoaded(ArrayList<OrderObject> orderObjects) {

                        }
                    },true,currentEmail,newEmail);

                    FreeOrderDao freeOrderDao = new FreeOrderDao();
                    freeOrderDao.updateFreeOrders(new FreeOrderDao.FreeOrdersListener() {
                        @Override
                        public void onFreeOrdersLoaded(ArrayList<PrizeOrderObject> freeOrders) {

                        }
                    },true,currentEmail,newEmail);
                }
            }
        });
    }

    private void updatePass(FirebaseUser user, String newPass, final ConstraintLayout frame) {
        user.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    //  Snackbar.make(frame,"Password was updated succesfully", Snackbar.LENGTH_SHORT).show();
                    notifyUser(getString(R.string.password_updated), frame);
                } else {
                    //Snackbar.make(frame,"Password update failed", Snackbar.LENGTH_SHORT).show();
                    notifyUser(getString(R.string.password_update_failed), frame);
                }
            }
        });
    }

    private void facebookLogout() {
        LoginManager.getInstance().logOut();
        redirectToLogin();
    }

    private void fireLogout() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
        redirectToLogin();
    }

    private void redirectToLogin() {
        user.setLoggedIn(false);
        SharedPreferenceUtils prefs = new SharedPreferenceUtils(getActivity());
        prefs.saveLogins(user);

        Intent intent = new Intent(getActivity(), SplashActivity.class);
        startActivity(intent);

    }

    private void notifyUser(String message, ConstraintLayout layout) {
        Snackbar.make(layout, message, Snackbar.LENGTH_SHORT).show();
    }

    private boolean isNameValid() {
        return etName.getText().length() > 3;
    }

    private boolean isEmailValid() {
        return etEmail.getText().toString().contains("@") && etEmail.getText().length() > 6;
    }

    private boolean isEmailValid(String email) {
        return email.contains("@") && email.length() > 6;
    }

/*
    private boolean isPassValid() {
        return etPass.getText().length() > 7;
    }
*/

    private boolean isGenderValid() {
        return !genderSelcted.equals("") && !genderSelcted.contains("select") && !genderSelcted.contentEquals("Select");
    }

    public interface OnAccountEditSaved {
        public void onSaveAccount(User user);
    }
}
