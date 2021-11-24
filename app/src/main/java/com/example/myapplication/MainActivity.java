package com.example.myapplication;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.a2048.MainActivity_2048;
import com.example.myapplication.dataBase.HotelContract;
import com.example.myapplication.dataBase.HotelDbHelper;
import com.instabug.library.Instabug;
import com.instabug.library.invocation.InstabugInvocationEvent;

/**
 * Это java-файл для управления запуском SnakeView.java.
 * Обработчик будет посылать сообщение потоку пользовательского интерфейса с фиксированной частотой для обновления экрана.
 * В главном потоке есть бесконечный цикл while для повторения цикла "получить сообщение - заснуть".
 * В режиме "получения сообщения" поток будет выполнять полученное сообщение, а в режиме "сна"
 * (Thread.sleep()) будет блокировать поток на некоторое время. Эффект заключается в том, что
 * вид будет обновляться с заданной частотой, что заставит элементы выглядеть как "движущиеся"
 * на экране.
 * Этот файл также будет получать информацию о ширине, высоте и установит музыку, играющую во время игры.
 */
public class MainActivity extends AppCompatActivity {

    private HotelDbHelper mDbHelper;

    //установите время интервала обновления
    private SnakeView mSnakeView;
    private static final int REFRESH = 1;
    private static final int REFRESHINTERVAL = 150;
    private boolean isWin = false;
    private boolean pause = false;

    //Устанавливаем медиаплеер
    private MediaPlayer mediaPlayer;

    /*
    Обработчик будет посылать сообщение в поток UI во время работы игры.
    Если сообщение содержит целочисленную переменную REFRESH и SnakeView уже создан, метод
    invalidate() вытащит старый вид и повторно запустит метод onDraw() в SnakeView, чтобы отрисовать
    все элементы на экране снова
     */
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.arg1 == REFRESH) {
                if (mSnakeView != null)
                    //если SnakeView не NULL, то перепишите в нем метод onDraw().
                    mSnakeView.invalidate();
            }
        }
    };

    //информация о ширине, высоте и состоянии экрана
    public static int snakewidth ;
    public static int snakeheight ;
    public static float snakedensity;
    private final Object lock = new Object();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Подключаем библиотеку Instabag, для анализа всех ошибок у пользователей
        new Instabug
                .Builder(this.getApplication(),
                "9aad5ed187dab6a5343a3ea7cad1db2f")
                .setInvocationEvents(InstabugInvocationEvent.SHAKE, InstabugInvocationEvent.SCREENSHOT)
                .build();

        //включить фоновую музыку
        mediaPlayer = MediaPlayer.create(this, R.raw.snake);
        mediaPlayer.start();

        //получить информацию о размере экрана, и snakeview будет импортировать эти 2 статические переменные
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        snakewidth = dm.widthPixels;
        snakeheight = dm.heightPixels;
        snakedensity = dm.density;

        //установить окно игры в полноэкранный режим
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mSnakeView = new SnakeView(this);
        setContentView(mSnakeView);

        mDbHelper = new HotelDbHelper(this);

        //создаём новый поток с именем GameThread, а метод start() запустит этот поток
        new Thread(new GameThread()).start();
    }

    /**
     * GameThread реализует интерфейс - Runnable. Наиболее важным переопределенным методом является
     * run(), который будет управлять запуском игры. Когда условие isWin не выполнено, сообщение msg
     * получит сообщение от обработчика и отправит его в поток UI, чтобы переписать метод onDraw(),
     * и затем поток "заснет" (будет заблокирован) на заданный период времени.Если игрок
     * уже выиграл игру - окно игры перейдет к CatchGame и игра в змейку будет немедленно завершена.
     */
    class GameThread implements Runnable {

        @Override
        public void run() {
            while(!isWin )
            {
                if(mSnakeView.win){
                    isWin = true;

                    SQLiteDatabase db = mDbHelper.getWritableDatabase();

                    ContentValues values = new ContentValues();
                    values.put(HotelContract.Score.COLUMN_SCORE, mSnakeView.code);
                    values.put(HotelContract.Score.COLUMN_STATUS, "ПОБЕДА");

                    // Вставляем новый ряд в базу данных и запоминаем его идентификатор
                    long newRowId = db.insert(HotelContract.Score.TABLE_NAME, null, values);

                    Intent intent = new Intent(MainActivity.this,SecondActivity.class);
                    startActivity(intent);
                    finish();
                }
                //pause = mSnakeView.lose;
                Message msg = mHandler.obtainMessage();
                msg.arg1 = REFRESH;
                mHandler.sendMessage(msg);
                try{
                    Thread.sleep(REFRESHINTERVAL);//
                }catch (InterruptedException e){
                    e.printStackTrace();
                }

            }
        }
    }

    // Этот метод сработает, когда будет нажата кнопка "Назад".
    // Игра будет завершена и медиаплеер будет освобожден.
    @Override
    public void onBackPressed(){
        mediaPlayer.release();
        Intent intent = new Intent(MainActivity.this, MenuActivity.class);
        startActivity(intent);
        finish();
    }

    //Этот метод будет работать, когда игра приостановлена другим приложением
    //Игра будет поставлена на паузу, а медиаплеер будет освобожден
    @Override
    protected void onPause(){
        super.onPause();
        mediaPlayer.release();
    }


}
