package com.flashsales.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
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

import com.flashsales.datamodel.OrderObject;
import com.flashsales.datamodel.PrizeOrderObject;
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

        private TextView tvName, tvDiscount, tvPrice, tvReview, tvBrand, tvSavePercentage;
        private TextView tvVariant, tvVariantPrice; /// R.layout.item_variant.xml
        private ImageView imProduct, imReview;
        private RatingBar rbReview;
        private ImageView ivRemove, ivView;
        private EditText etAmount;
        private int currentState;
        //////// Order history
        private TextView tvOrderDate, tvAmount, tvAmountSave, tvOrderCount;


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

            tvBrand = (TextView) view.findViewById(R.id.tv_brand);

            tvSavePercentage = (TextView) view.findViewById(R.id.tv_save_percentage);
            tvPriceBefore = (TextView) view.findViewById(R.id.tv_price_before);
            ivRemove = (ImageView) view.findViewById(R.id.tv_remove);
            //ivAdd = (ImageView)view.findViewById(R.id.iv_add);
            etAmount = (EditText) view.findViewById(R.id.et_amount);
            ivView = (ImageView) view.findViewById(R.id.iv_view);
            ///// order history items
            tvOrderDate = (TextView) view.findViewById(R.id.tv_order_date);
            tvAmount = (TextView) view.findViewById(R.id.tv_amount);
            tvAmountSave = (TextView) view.findViewById(R.id.tv_amount_saved);
            tvOrderCount = (TextView) view.findViewById(R.id.tv_count);
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
        } else if (object instanceof Product && layoutResId == R.layout.item_viewed_product) {
            setViewedProduct(holder, object, position);
        } else if (object instanceof OrderObject && layoutResId == R.layout.item_order_history) {
            setOrderHistoryItem(holder, object, position);
        } else if (object instanceof PrizeOrderObject && layoutResId == R.layout.item_order_history) {
            setPrizeOrderHistoryItem(holder, object, position);
        } else if (object instanceof Product && layoutResId == R.layout.item_product_orderinformation) {
            setOrderHistoryProducts(holder, object, position);
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private void setProductDisplayInfo(ViewHolder holder, Object object) {
        Log.i("display info", "set info");
        ProductDisplay productDisplay = ProductDisplay.class.cast(object);
        holder.tvName.setText(productDisplay.getShortName());
        String salePrice = context.getString(R.string.sale_price);
        salePrice += " " + context.getString(R.string.currency);
        holder.tvPrice.setText(salePrice + productDisplay.getPrice() + "");

        String retailPrice = context.getString(R.string.retail_price);
        retailPrice += " " + context.getString(R.string.currency);

        holder.tvDiscount.setText(retailPrice + productDisplay.getRetailPrice() + "");

        Double savePercentage = (productDisplay.getPrice() / productDisplay.getRetailPrice()) * 100;
        savePercentage = 100 - savePercentage;
        int i = savePercentage.intValue();

        holder.tvSavePercentage.setText(context.getString(R.string.save) + " " + i + "%");
        Log.d("savePercentage", savePercentage + "");
        // holder.tvDiscount.setText(productDisplay.getDiscount());
        //   holder.tvOrderCount.setText(productDisplay.getCartCount() + " " + context.getResources().getString(R.string.bought_this));
       if(productDisplay.getImage()!=null) {
           Picasso.with(context).load(productDisplay.getImage()).fit().centerCrop().
                   error(R.drawable.ic_hourglass_empty).placeholder(R.drawable.logo).into(holder.imProduct);
       }else{
           Drawable drawable = context.getDrawable(R.drawable.logo);
           holder.imProduct.setImageDrawable(drawable);
       }
    }

    private void setReviewInfo(ViewHolder holder, Object object) {
        Review review = Review.class.cast(object);
        holder.tvName.setText(review.getReviewerName());
        holder.tvReview.setText(review.getReview());
        if(review.getImageId()!= 0) {
            Picasso.with(context).load(review.getImageId()).fit().centerCrop().error(R.drawable.ic_hourglass_empty).placeholder(R.drawable.logo).into(holder.imReview);
        }else{
            Drawable drawable = context.getDrawable(R.drawable.logo);
            holder.imProduct.setImageDrawable(drawable);
        }
        // Double rating = review.getFakeRating();
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
        holder.tvName.setText(product.getName());
        holder.tvBrand.setText(product.getBrand());
        String salePrice = context.getResources().getString(R.string.sale_price);
        String retailPrice = context.getString(R.string.retail_price);
        holder.tvPrice.setText(salePrice + " " + product.getStock() + "x " + context.getString(R.string.currency) + product.getPrice());
        holder.tvPriceBefore.setText(retailPrice + " " + context.getString(R.string.currency) + (product.getRetailPrice()) + "");
        holder.tvPriceBefore.setPaintFlags(holder.tvPriceBefore.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        int price = Integer.parseInt(product.getPrice());
        int retailPriceCalc = Integer.parseInt(product.getRetailPrice());
        int percent = (price * 100) / retailPriceCalc;
        percent = 100 - percent;
        holder.tvSavePercentage.setText(context.getString(R.string.save) + " " + percent + "%");


        holder.ivRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null)
                    mClickListener.onCartItemDelted(product, position);
            }
        });

        int total = product.getStock() * Integer.valueOf(product.getPrice());

        holder.etAmount.setText(context.getString(R.string.currency) + total);
        if (product.getImage() != null) {
            Picasso.with(context).load(product.getImage()).fit().centerCrop().fit().error(R.drawable.ic_hourglass_empty)
                    .placeholder(R.drawable.logo).into(holder.imProduct);
        }else{
            Drawable drawable = context.getDrawable(R.drawable.logo);
            holder.imProduct.setImageDrawable(drawable);
        }
    }


    private void setViewedProduct(ViewHolder holder, final Object object, final int position) {
        final Product product = (Product) object;
        if (product.getImage() != null) {
            Picasso.with(context).load(product.getImage()).fit().centerCrop().error(R.drawable.ic_hourglass_empty)
                    .placeholder(R.drawable.logo).into(holder.imProduct);
        } else {
            Drawable drawable = context.getDrawable(R.drawable.logo);
            holder.imProduct.setImageDrawable(drawable);
        }
        String currency = context.getString(R.string.currency);
        holder.tvName.setText(product.getName());
        holder.tvBrand.setText(product.getBrand());
        String retail = context.getString(R.string.retail_price);
        String sale = context.getString(R.string.sale_price);
        holder.tvPriceBefore.setText(retail + " " + currency + product.getRetailPrice());
        holder.tvPrice.setText(sale + " " + currency + product.getPrice());

        int price = Integer.parseInt(product.getPrice());
        int retailPrice = Integer.parseInt(product.getRetailPrice());
        int percent = (price * 100) / retailPrice;
        percent = 100 - percent;
        holder.tvSavePercentage.setText(context.getString(R.string.save) + " " + percent + "%");
        holder.ivView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null)
                    mClickListener.onItemAdapterClick(object, position, v);
            }
        });
        holder.ivRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null)
                    mClickListener.onFavoruriteProductDelete(product);
            }
        });
    }

    private void setOrderHistoryItem(ViewHolder holder, final Object object, final int position) {
        final OrderObject order = (OrderObject) object;
        String date = dateFormatted(order.getPaymentObject().getPaypalResponse().getCreatedTime());
        holder.tvOrderDate.setText(date);
        holder.tvAmount.setText(context.getString(R.string.amount_paid) + " " + context.getString(R.string.currency) + order.getCartProduct().getSalePrice() + "");
        double amount = order.getCartProduct().getRetailPrice() - order.getCartProduct().getSalePrice();
        holder.tvAmountSave.setText(context.getString(R.string.amount_saved) + " " + context.getString(R.string.currency) + amount);
    }

    private void setPrizeOrderHistoryItem(ViewHolder holder, final Object object, final int position) {
        final PrizeOrderObject order = (PrizeOrderObject) object;
        String date = dateFormatted(order.getPaymentObject().getPaypalResponse().getCreatedTime());
        holder.tvOrderDate.setText(date);
        holder.tvAmount.setText(context.getString(R.string.amount_paid) + " " + context.getString(R.string.currency) + order.getFreeProduct().getShippingPrice() + "");
        holder.tvAmountSave.setText(context.getString(R.string.amount_saved) + " " + context.getString(R.string.currency) + order.getFreeProduct().getRetailPrice());
    }


    private void setOrderHistoryProducts(ViewHolder holder, final Object object, int position) {
        Product product = (Product) object;

        holder.tvName.setText(product.getName());
        holder.tvBrand.setText(product.getBrand());
        holder.tvPrice.setText(context.getString(R.string.currency) + product.getPrice() + "");
        holder.tvOrderCount.setText(context.getString(R.string.items_ordered) + " " + product.getStock() + "");
        if (product.getImage() != null) {
            Picasso.with(context).load(product.getImage()).fit().centerCrop().placeholder(R.drawable.logo).error(R.drawable.ic_hourglass_empty).into(holder.imProduct);
        } else {

            Drawable drawable = context.getDrawable(R.drawable.logo);
            holder.imProduct.setImageDrawable(drawable);
        }

    }

    public void updateList(ArrayList<T> updatedList) {
        this.list = updatedList;

    }

    private String dateFormatted(String date) {
        return date.replace("T", " ").replace("Z", "");
    }


    public interface ClickListener {
        public void onItemAdapterClick(Object object, int position, View view);

        public void onCartItemDelted(Product product, int layoutPosition);

        public void onCartItemAdded(Product product);

        public void onFavoruriteProductDelete(Product product);
    }

}
