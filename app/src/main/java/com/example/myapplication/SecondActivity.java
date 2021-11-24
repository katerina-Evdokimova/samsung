package com.example.myapplication;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
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
    SimpleCursorAdapter userAdapter;
    ListView userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        userList = findViewById(R.id.listArr);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mDbHelper = new HotelDbHelper(this);
        ImageButton button = (ImageButton) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SecondActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        ImageButton button1 = (ImageButton) findViewById(R.id.menu_btn);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SecondActivity.this, MenuActivity.class);
                startActivity(intent);
                finish();
            }
        });
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
            textView.setText("У Вас было - " + cursor.getCount() + " игр.\n\n");
            textView.append(HotelContract.Score.COLUMN_STATUS + " | " +
                    HotelContract.Score.COLUMN_SCORE + " | " + "\n");

            String[] headers = new String[] {HotelContract.Score.COLUMN_STATUS, HotelContract.Score.COLUMN_SCORE};

            userAdapter = new SimpleCursorAdapter(this, android.R.layout.two_line_list_item,
                    cursor, headers, new int[]{android.R.id.text1, android.R.id.text2}, 0);
            userList.setAdapter(userAdapter);
        } finally {
        }
    }

}

