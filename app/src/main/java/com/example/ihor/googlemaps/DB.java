package com.example.ihor.googlemaps;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

class DB extends SQLiteOpenHelper {
    private static DB mInstance = null;
    public static DB getInstance(Context context) {

        if (mInstance == null) {
            mInstance = new DB(context.getApplicationContext());}
        return mInstance;
    }

    public DB(Context context) {

        super(context, "myDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("test", "--- onCreate database ---");
        // создаем таблицу с полями
        db.execSQL("create table mytable (" + "id integer primary key autoincrement," + "name text," + "latitude REAL," + "longitude REAL" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
