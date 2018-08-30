package com.flashsales;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.flashsales.R;

public abstract class CollapsablePanelView extends LinearLayout {
    private TextView tvTitle;
    private ImageView ivArrow;
    private boolean isCollapsed = false;
    private PanelViewListener mListener;
    private View panelBody;
    private LinearLayout headerContainer;

    public CollapsablePanelView(Context context) {
        this(context, null, 0);
    }

    public CollapsablePanelView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CollapsablePanelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setOrientation(VERTICAL);

        addView(createPanelHeader(context));

        panelBody = onCreatePanelBody(context);
        panelBody.setBackgroundColor(getPanelBackgroundColor());
        panelBody.setPadding(5,5,5,5);
     //   addView(panelBody);
    }

    public int getPanelBackgroundColor() {
        return ContextCompat.getColor(getContext(), R.color.orange);
    }

    public int getTitleBackgroundColor() {
        return ContextCompat.getColor(getContext(), R.color.background_buy);
    }

    private View createPanelHeader(final Context context) {
        Resources res = getResources();
        int padding = res.getDimensionPixelSize(R.dimen.padding);
        int paddingTop = res.getDimensionPixelOffset(R.dimen.paddintgTop);

         headerContainer = new LinearLayout(context);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(padding,padding,padding,padding);
        headerContainer.setLayoutParams(layoutParams);

        headerContainer.setOrientation(HORIZONTAL);
        headerContainer.setBackgroundColor(getTitleBackgroundColor());
        headerContainer.setPadding(padding,padding,padding,padding);


        tvTitle = new TextView(context);
        LayoutParams tvLp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        tvLp.gravity = Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL;
        tvTitle.setLayoutParams(tvLp);
        tvTitle.setPadding(padding,paddingTop,padding,padding);
        tvTitle.setTextColor(Color.WHITE);
        tvTitle.setTypeface(tvTitle.getTypeface(),Typeface.BOLD);
       // tvTitle.setTextAppearance(context,android.R.style.TextAppearance_Large);


        ivArrow = new ImageView(context);
        LayoutParams ivLp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ivLp.gravity = Gravity.LEFT|Gravity.CENTER_VERTICAL;
        ivArrow.setLayoutParams(ivLp);
        ivArrow.setPadding(padding,padding,padding,padding);

        if (!isCollapsed) {
            ivArrow.setImageResource(R.drawable.ic_keyboard_arrow_down);
        } else {
            ivArrow.setImageResource(R.drawable.ic_keyboard_arrow_up);
        }
        headerContainer.addView(ivArrow);
        headerContainer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle(context);
            }
        });
        /*if(collapsed) {
            ivArrow.setImageResource(R.drawable.ic_orange_arrow_down);
        } else {
            ivArrow.setImageResource(R.drawable.ic_orange_arrow_up);
        }*/
        headerContainer.addView(tvTitle);

        return headerContainer;
    }

    public View onCreatePanelBody(Context context) {
        View view = new View(context);
        int padding = context.getResources().getDimensionPixelOffset(R.dimen.paddintgTop);
        LayoutParams vLp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        vLp.gravity = Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL;
        vLp.setMargins(padding,padding,padding,padding);
        view.setPadding(padding,padding,padding,padding);
        view.setBackgroundColor(Color.WHITE);
        view.setLayoutParams(vLp);
        return new View(context);

    }

    /*protected void setPanelBody(View panelBody) {
        removeViewAt(1);
        panelBody.setBackgroundColor(Color.TRANSPARENT);
        addView(panelBody);
    }*/

    public void setPanelListener(PanelViewListener listener) {
        this.mListener = listener;
    }

    public void setPanelTitle(String title) {
        tvTitle.setText(title);
    }

   public void setPanelTitleIcon(@DrawableRes int iconRes) {
        tvTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(iconRes, 0, 0, 0);
    }

    public void toggle(Context context) {
        if (!this.isCollapsed) {
            //expand(getChildAt(1), 1, this.ivArrow);
            int padding = context.getResources().getDimensionPixelOffset(R.dimen.padding_child);
            panelBody.setPadding(padding,padding,padding,padding);
            addView(panelBody);
            Animation animation = AnimationUtils.loadAnimation(context,R.anim.slide_up);
            panelBody.startAnimation(animation);
            panelBody.getParent().requestChildFocus(panelBody,panelBody);

             ivArrow.setImageResource(R.drawable.ic_keyboard_arrow_up);
        } else if (this.isCollapsed) {
            // UIUtils.collapse(getChildAt(1), 1, this.ivArrow);
            Log.d("panel", "remove view");
            if (panelBody != null)
                removeView(panelBody);
                ivArrow.setImageResource(R.drawable.ic_keyboard_arrow_down);
            /// removeViewAt(1);

        }

        this.isCollapsed = !this.isCollapsed;

        if (mListener != null) {
            if (isCollapsed) {
                mListener.onPanelClosed(this);
            } else {
                mListener.onPanelOpened(this);
            }
        }
    }

    public View getPanelBody(){
        return panelBody;
    }

    public interface PanelViewListener {
        void onPanelOpened(CollapsablePanelView panel);
        void onPanelClosed(CollapsablePanelView panel);
    }
}
