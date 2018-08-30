package com.flashsales.fragments;

import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.flashsales.Utils.FBEvents;
import com.flashsales.MyApplication;
import com.flashsales.Utils.Utils;
import com.flashsales.dao.DBFavouriteProducts;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.flashsales.CollapsablePanelView;
import com.flashsales.R;
import com.flashsales.TextPanel;
import com.flashsales.ViewpagerHeightWrapping;
import com.flashsales.YoutubeConfig;
import com.flashsales.adapters.AdapterImageSlider;
import com.flashsales.datamodel.Product;

public class FragmentProductProfile extends Fragment {

    private OnProductEvent mFragListener;
    private Product product;
    private int dotsCount;
    private int currentDot;
    private ImageView[] imDots;
    private ArrayList<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    //private FlashSaleTimer timerCount;
    private YouTubePlayerSupportFragment youTubePlayerFragment;
    private YouTubePlayer yPlayer;
    private YouTubePlayer youTubePlayer;
    private YoutubeConfig youtubeConfig;
    private int timerCount;
    private Handler handlerTimer;
    private Runnable runnable;
    // private Timer timer;
    private Thread t;
    private CharSequence shipping, features;
    private CharSequence[] contents;
    private TextView tvVarColor, tvVarSize;
    private ViewpagerHeightWrapping vpInfo;
    private LinearLayout layoutMain;
    private ImageView ivFav, ivShare;
    private boolean isFree;

    public static FragmentProductProfile newInstance(Product product,boolean  isFree) {
        FragmentProductProfile fragmentProductProfile = new FragmentProductProfile();
        Bundle args = new Bundle();
        args.putParcelable(Utils.KEY_PRODUCT, product);
        args.putBoolean(Utils.KEY_FREE_PRUDUCT,isFree);
        fragmentProductProfile.setArguments(args);
        return fragmentProductProfile;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        product = getArguments().getParcelable(Utils.KEY_PRODUCT);
        isFree = getArguments().getBoolean(Utils.KEY_FREE_PRUDUCT);
        contents = new CharSequence[3];
        features = buildFeatures(product.getFeatures()/* + " " + stripHtml(product.getDescription()*/);
        Spanned spanned = Html.fromHtml(getString(R.string.shipping));

        contents[0] = features;
        contents[1] = spanned;
        //whileRun = true;
        //timerCount = new FlashSaleTimer();
        // createLists();
        Log.d("FragmentProductProfile", "onCreate");
    }

    /**
     * Called when a fragment_dialog_video is first attached to its context.
     * {@link #onCreate(Bundle)} will be called after this.
     *
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mFragListener = (OnProductEvent) context;
        } catch (ClassCastException ex) {
            Log.e("ClassCastException", ex.toString());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.d("FragmentProductProfile", "onCreateView");

        View view = inflater.inflate(R.layout.fragment_product_profile_v2, container, false);
        layoutMain = (LinearLayout) view.findViewById(R.id.layout_main);

        final NestedScrollView ns = (NestedScrollView) view.findViewById(R.id.ns);
        ViewPager vpImages = (ViewPager) view.findViewById(R.id.vp);
        vpImages.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels){

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
        if (product.getImagePaths().size() != 0 && product.getImagePaths() != null) {
            AdapterImageSlider adapterImageSlider = new AdapterImageSlider(getContext(), product.getImagePaths(), new AdapterImageSlider.OnImageClick() {
                @Override
                public void onImageClick() {
                    if (mFragListener != null) {
                        mFragListener.onExpandImage();
                    }
                }
            });
            vpImages.setAdapter(adapterImageSlider);

            dotsCount = adapterImageSlider.getCount();
            currentDot = vpImages.getCurrentItem();
            imDots = new ImageView[dotsCount];

        }
        LinearLayout sliderLayout = (LinearLayout) view.findViewById(R.id.layout_slider_dots);
        addDotsView(sliderLayout);

        TextView tvName = (TextView) view.findViewById(R.id.tv_name);
        TextView tvBrand = (TextView) view.findViewById(R.id.tv_brand);
        TextView tvPrice = (TextView) view.findViewById(R.id.tv_price);
        TextView tvViewing = (TextView) view.findViewById(R.id.tv_viewing);
        TextView tvSave = (TextView) view.findViewById(R.id.tv_save);
        TextView tvRetailPrice = (TextView) view.findViewById(R.id.tv_retail_price);

        RatingBar ratingBar = (RatingBar) view.findViewById(R.id.rb);
        float ratingFlaot = Float.valueOf(String.valueOf(product.getFakeRating()));
        ratingBar.setRating(ratingFlaot);

        tvName.setText(product.getName());
        tvBrand.setText(product.getBrand());

        String retailPrice = getContext().getString(R.string.retail_price);
        String currency = getContext().getString(R.string.currency);
        tvSave.setText(retailPrice);
        tvRetailPrice.setPaintFlags(tvRetailPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        tvRetailPrice.setText(currency + product.getRetailPrice());
        String only = getContext().getString(R.string.only);

        String priceCta ="";
        if(!isFree) {
             priceCta = only + " " + currency + product.getPrice();
        }else{
            priceCta = getString(R.string.free);
        }
        tvPrice.setText(priceCta);


      /*  final TextPanel tvPanelShippingInfo = (TextPanel) view.findViewById(R.id.panel_shipping);

        tvPanelShippingInfo.setPanelTitle("Shipping information");
        tvPanelShippingInfo.setPanelTitleIcon(R.drawable.ic_local_shipping);
        tvPanelShippingInfo.setContent(contents[1]);
        tvPanelShippingInfo.setPanelListener(new CollapsablePanelView.PanelViewListener() {
            @Override
            public void onPanelOpened(CollapsablePanelView panel) {
                ns.scrollTo(tvPanelShippingInfo.getScrollX(),tvPanelShippingInfo.getScrollY());

            }

            @Override
            public void onPanelClosed(CollapsablePanelView panel) {

            }
        });*/

        final TextPanel tvPanelFeaturesInfo = (TextPanel) view.findViewById(R.id.panel_features);
        tvPanelFeaturesInfo.setPanelTitleIcon(R.drawable.ic_info);
        tvPanelFeaturesInfo.setPanelTitle("Product details");
        tvPanelFeaturesInfo.setContent(contents[0]+" "+contents[1]);
        tvPanelFeaturesInfo.setPanelListener(new CollapsablePanelView.PanelViewListener() {
            @Override
            public void onPanelOpened(CollapsablePanelView panel) {
                ns.scrollTo(tvPanelFeaturesInfo.getScrollX(),tvPanelFeaturesInfo.getScrollY());
            }

            @Override
            public void onPanelClosed(CollapsablePanelView panel) {

            }
        });

        ivFav = (ImageView) view.findViewById(R.id.iv_favourite);
        if (isFavourite()) {
            ivFav.setImageResource(R.drawable.ic_favorite_border);
        }
        ivFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToFavourites();
            }
        });




        randomViewing(tvViewing);
        return view;
    }



    private boolean isFavourite() {
        DBFavouriteProducts db = new DBFavouriteProducts(getContext());
        return db.ifExists(product);
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    private void inflateRecommended(FragmentTransaction transaction) {
        FragmentTopSales fragmentTopSales = FragmentTopSales.newInstance();
        transaction.add(R.id.frame_top_sales, fragmentTopSales).commit();

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

    private void randomViewing(final TextView textView) {
        t = new Thread() {

            @Override
            public void run() {

                while (!isInterrupted()) {
                    Random randomTimer = new Random();
                    int timer = randomTimer.nextInt(9500 - 5100) + 1;
                    try {
                        Thread.sleep(timer);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                final Random random = new Random();
                                int i = random.nextInt(10) + 1;
                                textView.setText(i + " Viewing Now");
                                textView.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                }

            }
        };
        t.start();
    }


    private String buildFeatures(List<String> items) {
        String value = "";
        for (int i = 0; i < items.size(); i++) {
            value += " " + items.get(i).toString() + System.getProperty("line.separator");
        }
        value += System.getProperty("line.separator");
        return value;
    }

    private String stripHtml(String html) {
        return Html.fromHtml(html).toString();
    }

    private void saveToFavourites() {
        if (product.getImage() == null)
            product.setImage(product.getImagePaths().get(0));
        String message = "";
        DBFavouriteProducts db = new DBFavouriteProducts(getContext());
        if (!db.ifExists(product)) {
            db.addProduct(product);
            message = getString(R.string.adding_product_favouries);
            ivFav.setImageResource(R.drawable.ic_favorite_border);
            FBEvents fbEvents = new FBEvents(MyApplication.getInstance());
            fbEvents.logAddedToWishlistEvent(product.getName(), product.getBrand(), product.getRetailPrice(), getString(R.string.currency),
                    Double.parseDouble(product.getPrice()));

            if (mFragListener != null)
                mFragListener.itemSaved(message, true);

        } else {
            db.upDateProduct(product);
            message = getString(R.string.product_exists_favouries);
            if (mFragListener != null)
                mFragListener.itemSaved(message, false);
        }


    }

    public interface OnProductEvent {
        public void onExpandImage();
        public void itemSaved(String message, boolean isSuccess);
    }

}

