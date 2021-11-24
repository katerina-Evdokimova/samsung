package com.example.myapplication.a2048;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.MenuActivity;
import com.example.myapplication.R;
import com.example.myapplication.SecondActivity;


public class MainActivity_2048 extends Activity {

    public static MainActivity_2048 mainActivity_2048 = null;
    private TextView Score;
    public static int score = 0; //Актуальный результат
    private TextView maxScore;
    private ImageView share;
    private ImageButton restart;
    private ImageButton back;
    private ImageButton pause;
    private GameView gameView;

    public MainActivity_2048() {
        mainActivity_2048 = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_2048);

        Score = (TextView) findViewById(R.id.Score);
        maxScore = (TextView) findViewById(R.id.maxScore);
        maxScore.setText(getSharedPreferences("pMaxScore", MODE_PRIVATE).getInt("maxScore", 0) + ""); //domyślny tryb, w którym do utworzonego pliku można uzyskać dostęp tylko przez aplikację wywołującą (lub wszystkie aplikacje o tym samym identyfikatorze użytkownika)
        share = (ImageView) findViewById(R.id.share);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("текст/план");
                String s = "Моя оценка за игру \"2048\" составляет " + maxScore.getText();
                shareIntent.putExtra(Intent.EXTRA_TEXT, s);
                startActivity(Intent.createChooser(shareIntent, "Показать"));
            }
        });
        gameView = (GameView)findViewById(R.id.gameView);
        restart = (ImageButton) findViewById(R.id.restart);
        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GameView.startGame();
            }
        });
        back = (ImageButton)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (gameView.hasTouched ) {
                    score = gameView.score;
                    showScore();
                    for(int y=0;y<4;++y) {
                        for(int x=0;x<4;++x) {
                            gameView.cards[y][x].setNum(gameView.num[y][x]);
                        }
                    }
                }
            }
        });
        pause = (ImageButton)findViewById(R.id.pause);
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity_2048.this, MenuActivity.class);
                startActivity(intent);
            }
        });

    }

    public static MainActivity_2048 getMainActivity() {
        return mainActivity_2048;
    }


    public void clearScore() {
        score = 0;
        showScore();
    }

    // добавить результат
    public void addScore(int i) {

        score += i;
        showScore();
        SharedPreferences pref = getSharedPreferences("pMaxScore", MODE_PRIVATE);


        // Если текущий результат превышает наивысший рекорд, обновляем
        if (score > pref.getInt("maxScore", 0)) {
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt("maxScore", score);
            editor.commit();
            maxScore.setText(pref.getInt("maxScore", 0) + "");

        }

    }

    // Текущий счет
    public void showScore() {
        Score.setText(score + "");
    }

    @Override
    public void onBackPressed() {
        createExitTipDialog();
    }

    private void createExitTipDialog() {
        new AlertDialog.Builder(MainActivity_2048.this)
                .setMessage("Czy napewno chcesz to zrobic ?")
                .setTitle("Wskazowka")
                .setIcon(R.drawable.tip)
                .setPositiveButton("Potwierdź", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        finish();
                    }
                })
                .setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }

}
