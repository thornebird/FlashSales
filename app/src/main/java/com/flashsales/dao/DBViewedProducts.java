package com.flashsales.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.flashsales.datamodel.Product;

import java.util.ArrayList;

public class DBViewedProducts extends SQLiteOpenHelper {

    private static final int DB_VERSION = 2;
    private static final String DB_NAME = "dbViewedProducts";
    private static final String TABLE_VIEWED_PRODUCTS = "tableViewedProducts";

    private static final String KEY_ID = "keyId";
    private static final String KEY_NAME = "keyName";
    private static final String KEY_BRAND = "keyBrand";
    private static final String KEY_RETAIL_PRICE = "keyRetailPrice";
    private static final String KEY_PRICE = "keyPrice";
    private static final String KEY_IMAGE = "keyImage";
    private static final String KEY_RATING = "keyRating";


    private static final String CREATE_TABLE = " CREATE TABLE " + TABLE_VIEWED_PRODUCTS + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_NAME + " TEXT,"
            + KEY_BRAND + " TEXT,"
            + KEY_RETAIL_PRICE + " TEXT,"
            + KEY_PRICE + " TEXT,"
            + KEY_RATING + " TEXT,"
            + KEY_IMAGE + " TEXT)";


    public DBViewedProducts(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     * <p>
     * <p>
     * The SQLite ALTER TABLE documentation can be found
     * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
     * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
     * you can use ALTER TABLE to rename the old table, then create the new table and then
     * populate the new table with the contents of the old table.
     * </p><p>
     * This method executes within a transaction.  If an exception is thrown, all changes
     * will automatically be rolled back.
     * </p>
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
       db.execSQL("DROP TABLE IF EXISTS "+TABLE_VIEWED_PRODUCTS);
       onCreate(db);
    }

    public boolean addProduct(Product product){
        ///// need to check if exists first
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = contentValues(product);
        boolean  rowInserted =  db.insert(TABLE_VIEWED_PRODUCTS,null,values)>0;
        db.close();
        return rowInserted;
    }

    public  boolean deleteProduct(Product product){
        SQLiteDatabase db = this.getWritableDatabase();
        boolean deleted = db.delete(TABLE_VIEWED_PRODUCTS,
                KEY_NAME + "=?" + " and "  +
                        KEY_BRAND + "=?",
                new String[] {
                        product.getName(),
                        product.getBrand()}) > 0;
       db.close();
       return deleted;
    }

    public boolean upDateProduct(Product product){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = contentValues(product);

        boolean updated =   db.update(TABLE_VIEWED_PRODUCTS, values,
                KEY_NAME + "=?" + " AND "  +
                        KEY_BRAND + "=?",
                new String[] {
                        product.getName(),
                        product.getBrand()}) > 0;
        db.close();
        return updated;
    }

    public ArrayList<Product> getViewedProducts(){
        ArrayList<Product> products = new ArrayList<>();
        String query  = " SELECT * FROM "+TABLE_VIEWED_PRODUCTS;
        SQLiteDatabase  db = this.getReadableDatabase();
        Cursor cursor  = db.rawQuery(query,null);
        if (cursor.moveToFirst()){
            while (cursor.moveToNext()) {
                Product product = new Product();
                product.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                product.setBrand(cursor.getString(cursor.getColumnIndex(KEY_BRAND)));
                product.setPrice(cursor.getString(cursor.getColumnIndex(KEY_PRICE)));
                product.setRetailPrice(cursor.getString(cursor.getColumnIndex(KEY_RETAIL_PRICE)));
                product.setImage(cursor.getString(cursor.getColumnIndex(KEY_IMAGE)));
                product.setFakeRating(Double.valueOf(cursor.getString(cursor.getColumnIndex(KEY_RATING))));
                products.add(product);
            }
        }
        cursor.close();
        db.close();
        return products;
    }

    public  boolean ifExists(Product product) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = " SELECT * FROM "+TABLE_VIEWED_PRODUCTS+" WHERE "+KEY_NAME+"=? AND "+KEY_BRAND+"=?";
        Cursor cursor = db.rawQuery(sql,new String[] {product.getName(),product.getBrand()});

        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        db.close();
        return true;
    }

    private ContentValues contentValues(Product product){
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_NAME,product.getName());
        contentValues.put(KEY_BRAND,product.getBrand());
        contentValues.put(KEY_RETAIL_PRICE,product.getRetailPrice());
        contentValues.put(KEY_PRICE,product.getPrice());
        contentValues.put(KEY_IMAGE,product.getImage());
        contentValues.put(KEY_RATING,product.getFakeRating()+"");
        return contentValues;
    }



}
