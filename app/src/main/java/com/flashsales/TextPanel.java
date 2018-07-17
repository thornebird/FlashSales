package com.flashsales;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.flashsales.R;

public class TextPanel extends CollapsablePanelView {
    private TextView tvContent;

    public TextPanel(Context context) {
        this(context, null, 0);
    }

    public TextPanel(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextPanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public View onCreatePanelBody(Context context) {
        this.tvContent = new TextView(context);
        this.tvContent.setLayoutParams(new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

       // int padding = UIUtils.dpToPx(context, 12);
       // this.tvContent.setPadding(padding, padding, padding, padding);

        return tvContent;
    }

    @Override
    public int getPanelBackgroundColor() {
        return Color.WHITE;
    }

    @Override
    public int getTitleBackgroundColor() {
        return ContextCompat.getColor(getContext(), R.color.background_buy);
    }

    public void setContent(CharSequence content) {
        this.tvContent.setText(content);
    }


}
