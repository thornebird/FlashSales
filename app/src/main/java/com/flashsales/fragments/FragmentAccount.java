package com.flashsales.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flashsales.R;
import com.flashsales.datamodel.User;
import com.squareup.picasso.Picasso;

public class FragmentAccount extends Fragment implements View.OnClickListener {

    private OnAccountUpdate mListener;
    private final static String KEY_USER = "keyUser";
    private User user;
    private RelativeLayout frameAccountInfo;

    public static FragmentAccount newInstance(User user) {
        FragmentAccount fragment = new FragmentAccount();
        Bundle args = new Bundle();
        args.putParcelable(KEY_USER, user);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        user = args.getParcelable(KEY_USER);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (OnAccountUpdate)context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        ImageView iv = (ImageView) view.findViewById(R.id.iv);
        TextView tvName = (TextView) view.findViewById(R.id.tv_name);
        frameAccountInfo = (RelativeLayout) view.findViewById(R.id.frame_account_info);

        tvName.setOnClickListener(this);
        if (!user.getImageFb().equals("")) {
            Picasso.with(getContext()).load(user.getImageFb()).into(iv);
        }
        if (user.getName() != null)
            tvName.setText(user.getName());
        return view;
    }


    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_name:
                if(mListener!=null)
                    mListener.onAccountEdit();
                break;
        }
    }

    public interface OnAccountUpdate{
        public void onAccountEdit();
    }
}
