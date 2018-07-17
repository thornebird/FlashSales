package com.flashsales.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flashsales.R;
import com.flashsales.Utils.Utils;
import com.flashsales.adapters.AdapterImageSlider;
import com.flashsales.datamodel.Product;

public class FragmentExpandSwipe extends Fragment {

    private Product product;
    private OnSwipeEvent mListener;
    private TextView tvCount;
    private ImageView[] imDots;
    private int dotsCount;
    private int currentDot;

    public static FragmentExpandSwipe newInstance(Product product) {
        Bundle args = new Bundle();
        FragmentExpandSwipe fragment = new FragmentExpandSwipe();
        args.putParcelable(Utils.KEY_PRODUCT, product);
        fragment.setArguments(args);
        return fragment;
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
       try{
           mListener = (OnSwipeEvent) context;
       }catch (ClassCastException ex){

       }
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        product = getArguments().getParcelable(Utils.KEY_PRODUCT);
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     * This is optional, and non-graphical fragments can return null (which
     * is the default implementation).  This will be called between
     * {@link #onCreate(Bundle)} and {@link #onActivityCreated(Bundle)}.
     * <p>
     * <p>If you return a View from here, you will later be called in
     * {@link #onDestroyView} when the view is being released.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragmnet_expand_swipe, container, false);

        ViewPager vp = (ViewPager) view.findViewById(R.id.vp);
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                int currentPosition = position + 1;
                tvCount.setText(currentPosition + "/" + product.getImagePaths().size());
            }

            @Override
            public void onPageSelected(int position) {
                for (ImageView im : imDots) {
                    im.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.non_active_dot));
                }
                imDots[position].setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.active_dot));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        AdapterImageSlider adapterImageSlider = new AdapterImageSlider(getContext(), product.getImagePaths(), new AdapterImageSlider.OnImageClick() {
            @Override
            public void onImageClick() {

            }
        });
        vp.setAdapter(adapterImageSlider);

        ImageView ivClose = (ImageView) view.findViewById(R.id.iv_close);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onSwipeClosed();
                }
            }
        });

        tvCount = (TextView) view.findViewById(R.id.tv_count);
        tvCount.setText("0/0");

      /*  TextView tvName = (TextView) view.findViewById(R.id.tv_name);
        tvName.setText(product.getName());*/
        dotsCount = adapterImageSlider.getCount();
        imDots = new ImageView[dotsCount];
        currentDot = vp.getCurrentItem();
        LinearLayout layoutDots = (LinearLayout) view.findViewById(R.id.layout_dots);
        addDotsView(layoutDots);
        return view;
    }

    private void addDotsView(LinearLayout sliderLayout) {
        for (int i = 0; i < dotsCount; i++) {
            imDots[i] = new ImageView(getContext());
            if (i == currentDot) {
                imDots[i].setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.active_dot));
            } else {
                imDots[i].setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.non_active_dot));
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 0, 8, 0);

            sliderLayout.addView(imDots[i], params);
        }
    }

    public interface OnSwipeEvent {
        public void onSwipeClosed();
    }
}
