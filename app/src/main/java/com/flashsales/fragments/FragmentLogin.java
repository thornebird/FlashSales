package com.flashsales.fragments;


import android.content.Context;
import android.content.Intent;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;

import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.flashsales.ProgressAlert;
import com.flashsales.Utils.Utils;
import com.flashsales.dao.UserDao;
import com.flashsales.datamodel.User;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.flashsales.R;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentLogin extends Fragment implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {


    private ConstraintLayout contentFrame;
    private CallbackManager callbackManager;
    private LoginButton fbLoginButton;
    private EventLogin mListener;
    private TextInputEditText etName, etEmail, etPassword;
    private TextView tvOr, tvPromotional;
    private ImageView imAccount;
    private Spinner spinner;
    private Drawable drawablError, drawablePass;
    private RelativeLayout frameButtonLogin;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String gender;
    private List<String> genders;

    private TextInputLayout nameInputLayout, passInputLayout, /*confirmpassInputLayout,*/
            emailLayout, phoneLayout;

    public static FragmentLogin newInstance() {
        Bundle args = new Bundle();
        FragmentLogin fragmentLogin = new FragmentLogin();
        fragmentLogin.setArguments(args);
        return fragmentLogin;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        genders = new ArrayList<>();
        genders.add(getContext().getString(R.string.select_gender));
        genders.add(getContext().getString(R.string.male));
        genders.add(getContext().getString(R.string.female));

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

            }
        };

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (EventLogin) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_login, container, false);

        contentFrame = (ConstraintLayout) view.findViewById(R.id.content_frame);
        callbackManager = CallbackManager.Factory.create();
        fbLoginButton = (LoginButton) view.findViewById(R.id.login_button);
        fbLoginButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_birthday", "user_gender"));
        fbLoginButton.setFragment(this);

        fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            gender = response.getJSONObject().getString("gender");
                        } catch (JSONException ex) {

                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "gender");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();
                firebaseAuthWithFacebook(loginResult.getAccessToken());
            }


            @Override
            public void onCancel() {
                notifyUser(getContext().getString(R.string.cancelled_registerion));
            }

            @Override
            public void onError(FacebookException e) {
                notifyUser(getContext().getString(R.string.failed_registeration));
            }
        });


        Button btnRegister = (Button) view.findViewById(R.id.btn_register);
        btnRegister.setOnClickListener(this);
        frameButtonLogin = (RelativeLayout) view.findViewById(R.id.frame_login_button);
        imAccount = (ImageView) view.findViewById(R.id.im_logo);
        tvOr = (TextView) view.findViewById(R.id.tv_or);
        tvPromotional = (TextView) view.findViewById(R.id.tv_promotional);
        etName = (TextInputEditText) view.findViewById(R.id.et_firstname);
        etEmail = (TextInputEditText) view.findViewById(R.id.et_email);
        etPassword = (TextInputEditText) view.findViewById(R.id.et_password);


        nameInputLayout = (TextInputLayout) view.findViewById(R.id.name_input_layout);
        passInputLayout = (TextInputLayout) view.findViewById(R.id.password_input_layout);
        emailLayout = (TextInputLayout) view.findViewById(R.id.email_input_layout);

        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (checkName(s.toString())) {
                    etName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_circle, 0, 0, 0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (checkName(s.toString())) {
                    etName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_circle, 0, 0, 0);
                }
            }
        });

        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isEmailValid(s.toString())) {
                    emailLayout.setErrorEnabled(false);
                    etEmail.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_circle, 0, 0, 0);
                } else {
                    emailLayout.setErrorEnabled(true);
                    emailLayout.setError(getActivity().getResources().getString(R.string.error_invalid_email));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isEmailValid(s.toString())) {
                    etEmail.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_circle, 0, 0, 0);
                } else {
                    emailLayout.setErrorEnabled(true);
                    emailLayout.setError(getActivity().getResources().getString(R.string.error_invalid_email));
                }
            }
        });


        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isPasswordSet(etPassword.getText().toString())) {
                    etPassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_circle, 0, 0, 0);
                }else {
                    etPassword.setError(getActivity().getResources().getString(R.string.error_pass_length));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isPasswordSet(etPassword.getText().toString())) {
                    etPassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_circle, 0, 0, 0);
                } else {
                    etPassword.setError(getActivity().getResources().getString(R.string.error_pass_length));
                }
            }
        });
        spinner = (Spinner) view.findViewById(R.id.spinner);
        ArrayAdapter<String> spinnerAdaper = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, genders);
        spinnerAdaper.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdaper);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0)
                    gender = genders.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return view;
    }

    private void firebaseAuthWithFacebook(AccessToken accessToken) {
        final AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!task.isSuccessful()) {
                            notifyUser(getContext().getString(R.string.failed_registeration));

                        } else {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                // User is signed in
                                String displayName = user.getDisplayName();
                                Uri photoUrl = user.getPhotoUrl();
                                String imageUrl = String.valueOf(photoUrl);
                                String email = user.getEmail();
                                user.getProviderData();
                                saveUserDB(displayName, email, "", imageUrl);


                                Log.d("TAG", "onAuthStateChanged:signed_in:" + "id");

                            } else {
                                // User is signed out
                                notifyUser(getContext().getString(R.string.signed_out));
                                Log.d("TAG", "onAuthStateChanged:signed_out");
                            }

                        }
                    }
                });
    }


    private boolean checkName(String name) {
        return name.length() >= 4;
    }


    private boolean isEmailValid(String email) {
        return email.length() > 0 && email.contains("@") && !email.contains("test");
    }


    private boolean isPasswordSet(String passWord) {
        return passWord.length() > 7;
    }

    private boolean isGender() {
        return gender.equals(Utils.male) || genders.equals(Utils.female);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register:
                if (isPasswordSet(etPassword.getText().toString()) && isGender()) {
                    verifyUserEmail();

                } else if (!isPasswordSet(etPassword.getText().toString())) {
                    etPassword.setError(getActivity().getResources().getString(R.string.error_pass_length));
                }
                break;

            default:
                break;
        }
    }

    private void verifyUserEmail() {
        final ProgressAlert progressAlert = new ProgressAlert(getContext(), getString(R.string.registeration_process), getString(R.string.registering_account));
        mAuth.createUserWithEmailAndPassword(etEmail.getText().toString(), etPassword.getText().toString())
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            notifyUser(getContext().getString(R.string.succesfully_registered));
                            saveUserDB(etName.getText().toString(),
                                    etEmail.getText().toString(), etPassword.getText().toString(), "");
                            progressAlert.stopAlert();
                        } else if (task.getException().toString().contains("email address is already in use")) {
                            progressAlert.stopAlert();
                            Snackbar.make(contentFrame, getString(R.string.failed_registeration), Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });

    }


    private void saveUserDB(String name, String email, String pass, String imagePath) {
        UserDao userDao = UserDao.getInstance();
        User user = setUser(name, email, pass, imagePath);
        userDao.setmDatabase(getContext(), user);
        if (mListener != null)
            mListener.OnLogin(user);
    }


    private User setUser(String name, String email, String pass, String imgPath) {
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setPassword(pass);
        user.setImageFb(imgPath);
        user.setGender(gender);
        return user;
    }

    private void notifyUser(String message) {
        Snackbar bar = Snackbar.make(contentFrame, message, Snackbar.LENGTH_SHORT);
        bar.show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public interface EventLogin {
        public void OnLogin(User user);
    }
}
