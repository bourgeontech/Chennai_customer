package com.online.estore.localdb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.online.estore.models.ShopDomain;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "estore.db";

    public static final String TABLE_NAME_FAV = "fav";
    public static final String FAV_SHOP_ID = "shop_id";
    public static final String FAV_SHOP_NAME = "shop_name";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_FAV_TABLE = "CREATE TABLE " + TABLE_NAME_FAV + "("
                + FAV_SHOP_ID + " INTEGER PRIMARY KEY,"
                + FAV_SHOP_NAME + " TEXT" + ")";
        db.execSQL(CREATE_FAV_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_FAV);
        onCreate(db);
    }


   public void addtoFav(ShopDomain shopDomain) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FAV_SHOP_ID, shopDomain.getShop_id());
        values.put(FAV_SHOP_NAME, shopDomain.getShop_id());
        db.insert(TABLE_NAME_FAV, null, values);
        db.close();
    }


    public void deleteContact(ShopDomain shopDomain) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME_FAV, FAV_SHOP_ID + " = ?",
                new String[]{String.valueOf(shopDomain.getShop_id())});
        db.close();
    }

    public boolean checkIfExists(String shopId) {

        SQLiteDatabase sqldb = this.getReadableDatabase();
        String Query = "Select * from " + TABLE_NAME_FAV + " where " + FAV_SHOP_ID + " = " + shopId;
        Cursor cursor = sqldb.rawQuery(Query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }
}
