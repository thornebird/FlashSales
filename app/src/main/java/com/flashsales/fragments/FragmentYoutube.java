package com.flashsales.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import com.flashsales.R;
import com.flashsales.YoutubeConfig;

public class FragmentYoutube extends Fragment  implements View.OnClickListener{
    private OnVideoEvent mListener;
    private YouTubePlayer yPlayer;
    private YouTubePlayerSupportFragment youTubePlayerFragment;


    public FragmentYoutube() {
    }

    public static FragmentYoutube newInstance() {
        FragmentYoutube fragmentYoutube = new FragmentYoutube();
        Bundle args = new Bundle();
        fragmentYoutube.setArguments(args);
        return fragmentYoutube;
    }

    /**
     * Called when a fragment is first attached to its context.
     * {@link #onCreate(Bundle)} will be called after this.
     *
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("atach","atach");
        try{
            mListener = (OnVideoEvent) context;
        }catch (ClassCastException ex){
            Log.e("error",ex.toString());
        }
    }

    /**
     * Called when the fragment is no longer attached to its activity.  This
     * is called after {@link #onDestroy()}.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        Log.d("detach","detached");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (yPlayer != null)
            yPlayer.release();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_youtube, container, false);

        youTubePlayerFragment = (YouTubePlayerSupportFragment) getChildFragmentManager().findFragmentById(R.id.youtube_player_fragment);
        YoutubeConfig youtubeConfig = new YoutubeConfig();
        youTubePlayerFragment.initialize(youtubeConfig.getApiKey(), new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
                if (!wasRestored) {
                    if (player != null)
                        yPlayer = player;
                    yPlayer.setShowFullscreenButton(false);
                    yPlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
                    yPlayer.cueVideo("2Vv-BfVoq4g");
                    //  yPlayer.loadVideo("2Vv-BfVoq4g");
                }
/*
                if (b) {
                    yPlayer.play();
                } else {
                    yPlayer.loadVideo("2Vv-BfVoq4g");
                }*/

            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        });

        Button btnClose = (Button)view.findViewById(R.id.btn_close);
        btnClose.setOnClickListener(this);

        return view;
    }


    public void setListener(OnVideoEvent mListener){
        this.mListener = mListener;
    }
    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_close:
                if(mListener != null)
                    mListener.onVideoClosed();
                break;
        }
    }



    public interface OnVideoEvent {
        public void onVideoClosed();
    }
}

