package com.flashsales.datamodel;

import java.util.ArrayList;

public class Cart {
    private static  Cart cart;
    private ArrayList<Product> products;
    private double cartValue;
    private double discount;

    private Cart(){}
    public static Cart getInstance(){
        if(cart == null){
            cart = new Cart();
        }
        return cart;
    }

    public ArrayList<Product> getProducts(){
        return products;
    }

    public void setProducts(ArrayList<Product> products){
        this.products = products;
    }

    public void addProductCart(Product product){
        if(this.products == null){
            this.products =  new ArrayList<>();
        }
        products.add(product);
    }

    public void removeProduct(Product product){
        for(int i=0;i<this.products.size();i++){
            Product currentProduct = this.products.get(i);
            if(product.getName().equals(currentProduct.getName())){
                this.products.remove(currentProduct);
            }
        }
    }
}
