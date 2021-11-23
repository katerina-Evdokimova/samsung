package com.example.myapplication;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Message;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.dataBase.HotelContract;
import com.example.myapplication.dataBase.HotelDbHelper;
import com.instabug.library.Instabug;
import com.instabug.library.invocation.InstabugInvocationEvent;

public class SecondActivity extends AppCompatActivity {

    private HotelDbHelper mDbHelper;
    private SnakeView mSnakeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mDbHelper = new HotelDbHelper(this);

    }

    @Override
    public void onStart(){
        super.onStart();
        //Вывод результатов игр
        displayDatabaseInfo();
    }

    private void displayDatabaseInfo(){

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        TextView textView = (TextView) findViewById(R.id.prov);

        // Зададим условие для выборки - список столбцов
        String[] projection = {
                HotelContract.Score._ID,
                HotelContract.Score.COLUMN_STATUS,
                HotelContract.Score.COLUMN_SCORE};

        // Делаем запрос
        Cursor cursor = db.query(
                HotelContract.Score.TABLE_NAME,   // таблица
                projection,            // столбцы
                null,                  // столбцы для условия WHERE
                null,                  // значения для условия WHERE
                null,                  // Don't group the rows
                null,                  // Don't filter by row groups
                null);

        try {
            textView.setText("У вас было - " + cursor.getCount() + " игр.\n\n");
            textView.append(HotelContract.Score.COLUMN_STATUS + " | " +
                    HotelContract.Score.COLUMN_SCORE + " | " + "\n");
            // Узнаем индекс каждого столбца
            int idColumnIndex = cursor.getColumnIndex(HotelContract.Score._ID);
            int statusColumnIndex = cursor.getColumnIndex(HotelContract.Score.COLUMN_STATUS);
            int nameColumnIndex = cursor.getColumnIndex(HotelContract.Score.COLUMN_SCORE);


            // Проходим через все ряды
            //cursor.moveToPosition(cursor.getCount() - 10);
            while (cursor.moveToNext()) {
                // Используем индекс для получения строки или числа
                String currentStatus = cursor.getString(statusColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                // Выводим значения каждого столбца
                textView.append(("\n" + currentStatus + " | " +
                        currentName + " | "));
            }
        } finally {
            // Всегда закрываем курсор после чтения
            cursor.close();
        }
    }

}

