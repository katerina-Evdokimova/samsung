package com.example.myapplication.dataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class HotelDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = HotelDbHelper.class.getSimpleName();
    // Имя файла базы данных
    private static final String DATABASE_NAME = "ScoreData2.db";
    // Версия бд
    private static final int DATABASE_VERSION = 1;

    // Конструктор
    public HotelDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Создание таблиц
        String SQL_CREATE_FINANCE_TABLE = "CREATE TABLE " + HotelContract.Score.TABLE_NAME
                + " (" + HotelContract.Score._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + HotelContract.Score.COLUMN_STATUS + " TEXT NOT NULL, "
                + HotelContract.Score.COLUMN_SCORE + " TEXT NOT NULL DEFAULT 0);";

        db.execSQL(SQL_CREATE_FINANCE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}

