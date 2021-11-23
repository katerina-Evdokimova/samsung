package com.example.myapplication.dataBase;

import android.provider.BaseColumns;

public final class HotelContract {

    private HotelContract(){
    };

    public static final class Score implements BaseColumns{
        public final static String TABLE_NAME = "Score";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_STATUS = "status";
        public final static String COLUMN_SCORE = "score";
    }
}
