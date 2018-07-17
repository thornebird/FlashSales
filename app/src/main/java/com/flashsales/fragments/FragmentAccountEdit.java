package com.flashsales.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;

import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.flashsales.R;
import com.flashsales.Utils.Utils;
import com.flashsales.datamodel.User;

import java.util.ArrayList;

public class FragmentAccountEdit extends Fragment implements View.OnClickListener {

    private OnAccountEditSaved mListener;
    private User user;
    private final static String KEY_USER = "keyUser";
    private ArrayList<String> genders;
    private EditText etName, etEmail;
    private EditText etPass;
    private String genderSelcted = "";
    private ConstraintLayout contenFrame;

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
        mListener = (OnAccountEditSaved) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = getArguments().getParcelable(KEY_USER);

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
        etName = (EditText) rootView.findViewById(R.id.et_name);
        etEmail = (EditText) rootView.findViewById(R.id.et_email);
        etPass = (EditText) rootView.findViewById(R.id.et_password);
        Button btnSave = (Button) rootView.findViewById(R.id.btn_save);
        ImageView ivClose = (ImageView) rootView.findViewById(R.id.iv_cancel);
        btnSave.setOnClickListener(this);
        ivClose.setOnClickListener(this);
        // EditText etPass = (EditText)rootView.findViewById(R.id.et_password);

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
                }
            }
        });

        etPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isPassValid()) {
                    etPass.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_circle, 0, 0, 0);
                } else {
                    etPass.setError(getActivity().getResources().getString(R.string.error_pass_length));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isPassValid()) {
                    etPass.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_circle, 0, 0, 0);
                } else {
                    etPass.setError(getActivity().getResources().getString(R.string.error_pass_length));
                }
            }
        });

        Spinner spinnerGender = (Spinner) rootView.findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, genders);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(adapter);
        spinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                genderSelcted = genders.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        if (user.getGender() != null) {
            if (isMale()) {
                spinnerGender.setSelection(1);
            } else {
                spinnerGender.setSelection(2);
            }
        }

        return rootView;
    }

    private boolean isMale() {
        return user.getGender().equals(Utils.male);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save:
                /// need to valdiate information first
                if (mListener != null && isEmailValid() && isNameValid() && isPassValid() && isGenderValid()) {
                    mListener.onSaveAccount(user);
                } else if (!isPassValid()) {
                    etPass.setError(getString(R.string.error_pass_length));
                } else if (!isEmailValid()) {
                    etEmail.setError(getString(R.string.error_invalid_email));
                } else if (!isGenderValid()) {
                    Snackbar.make(contenFrame, R.string.select_gender, Snackbar.LENGTH_SHORT).show();
                }
                break;
            case R.id.iv_cancel:
                if (mListener != null)
                    mListener.onCancel();
                break;
        }
    }

    private boolean isNameValid() {
        return etName.getText().length() > 3;
    }

    private boolean isEmailValid() {
        return etEmail.getText().toString().contains("@") && etEmail.getText().length() > 6;
    }

    private boolean isPassValid() {
        return etPass.getText().length() > 7;
    }

    private boolean isGenderValid() {
        return !genderSelcted.equals("") &&!genderSelcted.contains("select") && !genderSelcted.contentEquals("Select");
    }

    public interface OnAccountEditSaved {
        public void onSaveAccount(User user);
        public void onCancel();
    }
}
