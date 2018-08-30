package com.flashsales.Utils;

import com.crashlytics.android.answers.AddToCartEvent;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.crashlytics.android.answers.LoginEvent;
import com.crashlytics.android.answers.PurchaseEvent;
import com.crashlytics.android.answers.ShareEvent;
import com.crashlytics.android.answers.StartCheckoutEvent;

import java.math.BigDecimal;
import java.util.Currency;

public class AnswersEvents {

    /// /purchase/share
    public AnswersEvents(){}

    public void loginEvent(String method){
        Answers.getInstance().logLogin(new LoginEvent()
                .putMethod(method)
                .putSuccess(true));
    }

    public void checkoutStartedEvent(double amount,int itemCountAdded){
        Answers.getInstance().logStartCheckout(new StartCheckoutEvent()
                .putTotalPrice(BigDecimal.valueOf(amount))
                .putCurrency(Currency.getInstance("USD"))
                .putItemCount(itemCountAdded));
    }

    public void atdEvent(double amount,String productName,String productBrand){
        Answers.getInstance().logAddToCart(new AddToCartEvent()
                .putItemPrice(BigDecimal.valueOf(amount))
                .putCurrency(Currency.getInstance("USD"))
                .putItemName(productName)
                .putItemType(productBrand)
                .putItemId("sku-"+productName+"/"+productBrand));
    }

    public void purchaseEvent(double amount,String cartId,int itemsCount){
        Answers.getInstance().logPurchase(new PurchaseEvent()
                .putItemPrice(BigDecimal.valueOf(amount))
                .putCurrency(Currency.getInstance("USD"))
                .putItemName(cartId)
                .putItemType(itemsCount+"")
                .putItemId("sku-"+cartId)
                .putSuccess(true));
    }


    public void vcEvent(String productName,String productBrand,String price){
        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentName(productName)
                .putContentType(productBrand)
                .putContentId(price));
    }

    public void shareEvent(String productName,String brandName){
        Answers.getInstance().logShare(new ShareEvent()
                .putMethod("FB")
                .putContentName(productName)
                .putContentType("post")
                .putContentId(brandName));
    }
}
