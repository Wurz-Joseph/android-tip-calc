package com.my.tipjar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Restaurant_Expenses.db";
    public static final String TABLE_NAME = "expenses_table";
    public static final String COL_2 = "BASE_CHECK_AMOUNT";
    public static final String COL_3 = "TIP_AMOUNT";
    public static final String COL_4 = "TOTAL";


    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);

    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, BASE_CHECK_AMOUNT DECIMAL(5,2), TIP_AMOUNT DECIMAL(4,2), TOTAL DECIMAL(5,2)," +
                " date DATE DEFAULT (datetime('now','localtime')))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(double base_check_amount, double tip_amount, double total) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, base_check_amount);
        contentValues.put(COL_3, tip_amount);
        contentValues.put(COL_4, total);
        long result = db.insert(TABLE_NAME, null, contentValues);
        if (result == -1) {
            return false;
        } else
            return true;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        return result;
    }

    public Cursor getRow(int position) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT BASE_CHECK_AMOUNT, TIP_AMOUNT, TOTAL FROM " + TABLE_NAME + " LIMIT 1 OFFSET " + position, null);
        return result;
    }

    public Cursor getTotal(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT TOTAL FROM " + TABLE_NAME , null);
        return result;
    }

    public Cursor dateQuery() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT strftime('%m-%d-%Y %H:%M', date) FROM " + TABLE_NAME, null);
        return result;
    }

    public Cursor totalSum() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT SUM(TOTAL) FROM " + TABLE_NAME, null);
        return result;
    }
    public Cursor getId(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT ID FROM " + TABLE_NAME, null);
        return result;
    }

    public boolean deleteRow(Long id) {
        SQLiteDatabase database = this.getWritableDatabase();
        boolean databaseDelete = database.delete(TABLE_NAME, "ID" + "=?", new String[]{Long.toString(id)}) > 0;
        return databaseDelete;
    }
}
