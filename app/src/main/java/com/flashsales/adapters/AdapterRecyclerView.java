package com.flashsales.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import com.flashsales.datamodel.Product;
import com.flashsales.R;
import com.flashsales.datamodel.ProductDisplay;
import com.flashsales.datamodel.Review;

public class AdapterRecyclerView<T> extends RecyclerView.Adapter<AdapterRecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<T> list;
    private int layoutResId;
    private ClickListener mClickListener;


    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvName, tvDiscount, tvPrice, tvReview, tvBrand/*, tvTotalPrice*/;
        private TextView tvVariant, tvVariantPrice; /// R.layout.item_variant.xml
        private ImageView imProduct, imReview;
        private RatingBar rbReview;
        private ImageView ivRemove,ivView;
        private EditText etAmount;
        private int currentState;


        private TextView tvVariantChosen, tvShippingCost, tvShipping, tvPriceBefore;

        public ViewHolder(View view) {
            super(view);
            ///Product layout related
            tvName = (TextView) view.findViewById(R.id.tv_name);
            tvDiscount = (TextView) view.findViewById(R.id.tv_discount);
            imProduct = (ImageView) view.findViewById(R.id.im_product);
            tvPrice = (TextView) view.findViewById(R.id.tv_price);
            /////////////////// Product review related
            tvName = (TextView) view.findViewById(R.id.tv_name);
            tvReview = (TextView) view.findViewById(R.id.tvreview);
            imReview = (ImageView) view.findViewById(R.id.im_review);
            rbReview = (RatingBar) view.findViewById(R.id.rb_review);
            /////////////// Product varaint related
            tvVariant = (TextView) view.findViewById(R.id.tv_variant);
            // tvVariantPrice = (TextView) view.findViewById(R.id.tv_variant_price);
            /// Cart product
            tvBrand = (TextView) view.findViewById(R.id.tv_brand);

            //tvVariantChosen = (TextView) view.findViewById(R.id.tv_variant_chosen);
            // tvShippingCost = (TextView) view.findViewById(R.id.tv_shipping_cost);
            //tvShipping = (TextView) view.findViewById(R.id.tv_shipping);
            tvPriceBefore = (TextView) view.findViewById(R.id.tv_price_before);
        //    tvTotalPrice = (TextView) view.findViewById(R.id.tv_total_price);
            ivRemove = (ImageView) view.findViewById(R.id.tv_remove);
            //ivAdd = (ImageView)view.findViewById(R.id.iv_add);
            etAmount = (EditText) view.findViewById(R.id.et_amount);
            ivView =(ImageView)view.findViewById(R.id.iv_view);
        }

    }

    public AdapterRecyclerView(Context context, int layoutResId, ArrayList<T> list, ClickListener clickListener) {
        this.context = context;
        this.layoutResId = layoutResId;
        this.list = list;
        this.mClickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AdapterRecyclerView.ViewHolder holder, final int position) {
      final Object object = list.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            //    Object object = list.get(position);
                if (mClickListener != null)
                    mClickListener.onItemAdapterClick(object, position, v);
            }
        });


        if (object instanceof ProductDisplay && layoutResId == R.layout.row_product) {
            Log.i("display info onbin", "set info bing");
            setProductDisplayInfo(holder, object);
        } else if (object instanceof Review && layoutResId == R.layout.item_review) {
            setReviewInfo(holder, object);
        } else if (object instanceof String && layoutResId == R.layout.item_variant) {
            setProductVaritants(holder, object, position);
        } else if (object instanceof Product && layoutResId == R.layout.item_cart) {
            setCartlistItem(holder, object, position);
        } else  if(object instanceof Product && layoutResId == R.layout.item_viewed_product){
            setViewedProduct(holder,object,position);
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    private void setProductDisplayInfo(ViewHolder holder, Object object) {
        Log.i("display info", "set info");
        ProductDisplay productDisplay = ProductDisplay.class.cast(object);
        holder.tvName.setText(productDisplay.getShortName());
        String salePrice = context.getString(R.string.sale_price);
        salePrice += " " + context.getString(R.string.currency);
        holder.tvPrice.setText(salePrice + " " + productDisplay.getPrice() + "");

        String retailPrice = context.getString(R.string.retail_price);
        retailPrice += " " + context.getString(R.string.currency);

        holder.tvDiscount.setText(retailPrice + "" + productDisplay.getRetailPrice() + "");
        // holder.tvDiscount.setText(productDisplay.getDiscount());
        //   holder.tvOrderCount.setText(productDisplay.getCartCount() + " " + context.getResources().getString(R.string.bought_this));
        Picasso.with(context).load(productDisplay.getImage()).into(holder.imProduct);
    }

    private void setReviewInfo(ViewHolder holder, Object object) {
        Review review = Review.class.cast(object);
        holder.tvName.setText(review.getReviewerName());
        holder.tvReview.setText(review.getReview());
        Picasso.with(context).load(review.getImageId()).into(holder.imReview);
        // Double rating = review.getRating();
        Float rating = (float) review.getRating();
        holder.rbReview.setRating(rating);
    }

    private void setProductVaritants(ViewHolder holder, Object ob, int position) {
        String variant = "";
        try {
            variant = String.valueOf(ob);
        } catch (ClassCastException ex) {

        }
        holder.tvVariant.setText(variant);
    }

    private void setCartlistItem(ViewHolder holder, Object object, final int position) {
        final Product product = Product.class.cast(object);
        //holder.tvVariantChosen.setText("Tv variant chosen");
        holder.tvName.setText(product.getName());
        holder.tvBrand.setText(product.getBrand());
        //  holder.tvShipping.setText("shiping");
        // holder.tvShippingCost.setText("tv shipping cost");
        String salePrice = context.getResources().getString(R.string.sale_price);
        String retailPrice =  context.getString(R.string.retail_price);
        holder.tvPrice.setText(salePrice+" "+product.getStock()+"x R" + product.getPrice());
        holder.tvPriceBefore.setText(retailPrice+ " R" + (product.getRetailPrice()) + "");
        holder.tvPriceBefore.setPaintFlags(holder.tvPriceBefore.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.ivRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null)
                    mClickListener.onCartItemDelted(product, position);
            }
        });

        int total = product.getStock() * Integer.valueOf(product.getPrice());
        holder.etAmount.setText("R" +total);
        Picasso.with(context).load(product.getImage()).into(holder.imProduct);
    }


    private void setViewedProduct(ViewHolder holder, final Object object, final int position){
        Product product = (Product)object;
        Picasso.with(context).load(product.getImage()).into(holder.imProduct);
        holder.tvName.setText(product.getName());
        holder.tvBrand.setText(product.getBrand());
        holder.tvPriceBefore.setText(product.getRetailPrice());
        holder.tvPrice.setText(product.getPrice());
        holder.ivView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mClickListener!=null)
                    mClickListener.onItemAdapterClick(object,position,v);
            }
        });
    }

    public void updateList(ArrayList<T> updatedList) {
        /*list.clear();
        list.addAll(updatedList);*/
        this.list = updatedList;

    }


    public interface ClickListener {
        public void onItemAdapterClick(Object object, int position, View view);

        public void onCartItemDelted(Product product, int layoutPosition);

        public void onCartItemAdded(Product product);
    }

}
