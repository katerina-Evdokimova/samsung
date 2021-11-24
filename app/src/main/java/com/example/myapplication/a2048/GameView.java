package com.example.myapplication.a2048;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridLayout;

import java.util.ArrayList;
import java.util.List;


// Квадрат 4 × 4
public class GameView extends GridLayout {

    public static Card[][] cards = new Card[4][4];
    private static List<Point> emptyPoints = new ArrayList<Point>();  // Положение пустой карты (значение равно 0)
    public int num[][] = new int[4][4];
    public int score;
    public boolean hasTouched = false;

    public GameView(Context context) {
        super(context);
        initGameView();
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initGameView();
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initGameView();
    }


    // Инициализация раскладки игры
    private void initGameView() {
        setRowCount(4);
        setColumnCount(4);
        setOnTouchListener(new Listener());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        super.onSizeChanged(w, h, oldw, oldh);
        int cardWidth = (Math.min(w, h) - 10) / 4; // ширину вкладки в зависимости от размера экрана
        addCards(cardWidth, cardWidth); //квадрат
        startGame();

    }

    //
    private void addCards(int cardWidth, int cardHeight) {
        this.removeAllViews();
        Card c;
        for (int y = 0; y < 4; ++y) {
            for (int x = 0; x < 4; ++x) {
                c = new Card(getContext());
                c.setNum(0);
                addView(c, cardWidth, cardHeight);
                cards[x][y] = c;
            }
        }
    }

    // Добавьте карту, значение которой равно 2 или 4 (вероятность различна).
    private static void addRandomNum() {
        emptyPoints.clear();
        for (int y = 0; y < 4; ++y) {
            for (int x = 0; x < 4; ++x) {
                if (cards[x][y].getNum() == 0) {
                    emptyPoints.add(new Point(x, y));
                }
            }
        }

        //  случайным образом установливаем значение пустой карты на 2 или 4 (соотношение вероятностей 9: 1)
        Point p = emptyPoints.remove((int) (Math.random() * emptyPoints.size())); // здесь мы заменяем значение по умолчанию 0 на 2 или 4
        cards[p.x][p.y].setNum(Math.random() > 0.1 ? 2 : 4);
    }


    // начало игру
    public static void startGame() {
        MainActivity_2048.getMainActivity().clearScore();
        for (int y = 0; y < 4; ++y) {
            for (int x = 0; x < 4; ++x) {
                cards[x][y].setNum(0);
            }
        }
        addRandomNum();
        addRandomNum();

    }


    // влево
    private void swipeLeft() {
        boolean b = false;
        // Каждая линия (абсцисса - x, ордината - y)
        for (int y = 0; y < 4; ++y) {
            // Каждый столбец (учитывая, что последний столбец не нужно сравнивать, поэтому необходимо только 3 столбца)
            for (int x = 0; x < 3; ++x) {
                // Сравнить значения карт
                for (int x1 = x + 1; x1 < 4; ++x1) {
                    // карточка (x1, y) не пуста, тогда сравните с (x, y)
                    if (cards[x1][y].getNum() > 0) {
                        // Карточка (x, y) пуста, тогда переместите (x1, y) влево
                        if (cards[x][y].getNum() == 0) {
                            cards[x][y].setNum(cards[x1][y].getNum());
                            cards[x1][y].setNum(0);
                            --x; // (x1, y) должны продолжить сравнение
                            b = true;
                        } else if (cards[x][y].equals(cards[x1][y])) {
                            // Scal karty
                            cards[x][y].setNum(cards[x][y].getNum() * 2);
                            cards[x1][y].setNum(0);
                            MainActivity_2048.getMainActivity().addScore(cards[x][y].getNum());
                            b = true;
                        }
                        // встретили непустую карту, а затем (x, y), вам не нужно продолжать сравнение.
                        break;
                    }
                }
            }
        }
// Когда карта меняется, случайным образом добавляем карту достоинством 2 или 4, чтобы продолжить игру
        if (b) {
            addRandomNum();
            checkGameOver(); // Каждый раз, когда вы добавляем карту со значением 2 или 4, должны определить, закончилась ли игра
            winGame();
        }
    }


    private void swipeRight() {
        boolean b = false;
        for (int y = 0; y < 4; ++y) {
            for (int x = 3; x > 0; --x) {
                for (int x1 = x - 1; x1 >= 0; --x1) {
                    if (cards[x1][y].getNum() > 0) {
                        if (cards[x][y].getNum() == 0) {
                            cards[x][y].setNum(cards[x1][y].getNum());
                            cards[x1][y].setNum(0);
                            ++x;
                            b = true;
                        } else if (cards[x][y].equals(cards[x1][y])) {
                            cards[x][y].setNum(cards[x][y].getNum() * 2);
                            cards[x1][y].setNum(0);
                            MainActivity_2048.getMainActivity().addScore(cards[x][y].getNum());
                            b = true;
                        }
                        break;
                    }
                }
            }
        }
        if (b) {
            addRandomNum();
            checkGameOver();
            winGame();
        }
    }

    private void swipeUp() {
        boolean b = false;
        for (int x = 0; x < 4; ++x) {
            for (int y = 0; y < 3; ++y) {
                for (int y1 = y + 1; y1 < 4; ++y1) {
                    if (cards[x][y1].getNum() > 0) {
                        if (cards[x][y].getNum() == 0) {
                            cards[x][y].setNum(cards[x][y1].getNum());
                            cards[x][y1].setNum(0);
                            --y;
                            b = true;
                        } else if (cards[x][y].equals(cards[x][y1])) {
                            cards[x][y].setNum(cards[x][y].getNum() * 2);
                            cards[x][y1].setNum(0);
                            MainActivity_2048.getMainActivity().addScore(cards[x][y].getNum());
                            b = true;
                        }
                        break;
                    }
                }
            }
        }
        if (b) {
            addRandomNum();
            checkGameOver();
            winGame();
        }
    }


    private void swipeDown() {
        boolean b = false;
        for (int x = 0; x < 4; ++x) {
            for (int y = 3; y > 0; --y) {
                for (int y1 = y - 1; y1 >= 0; --y1) {
                    if (cards[x][y1].getNum() > 0) {
                        if (cards[x][y].getNum() == 0) {
                            cards[x][y].setNum(cards[x][y1].getNum());
                            cards[x][y1].setNum(0);
                            ++y;
                            b = true;
                        } else if (cards[x][y].equals(cards[x][y1])) {
                            cards[x][y].setNum(cards[x][y].getNum() * 2);
                            cards[x][y1].setNum(0);
                            MainActivity_2048.getMainActivity().addScore(cards[x][y].getNum());
                            b = true;
                        }
                        break;
                    }
                }
            }
        }
        if (b) {
            addRandomNum();
            checkGameOver();
            winGame();
        }
    }

    private void winGame() {
        boolean isOver = true;

        for (int y = 0; y < 4; ++y) {
            for (int x = 0; x < 4; ++x) {
                if (cards[x][y].getNum() == 2048) {
                    if (isOver) {
                        new AlertDialog.Builder(getContext()).setTitle("Поздравляем, вы победили! ").setMessage("\n" +
                                "Текущий результат " + MainActivity_2048.score + "， Играть снова ！").setPositiveButton("\n" + " Сыграйте еще раз. Нажмите здесь, чтобы сыграть еще один раунд", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                startGame();

                            }
                        }).show();
                    }

                }

            }
        }

    }

    private void checkGameOver() {
        boolean isOver = true;

        for (int y = 0; y < 4; ++y) {
            for (int x = 0; x < 4; ++x) {


               /* Условия для продолжения игры следующие:
                1.По крайней мере, одна пустая карточка
                2.Пустых карт нет, но есть две соседние карты одинакового достоинства.
                */
                if (cards[x][y].getNum() == 0 ||
                        (x < 3 && cards[x][y].getNum() == cards[x + 1][y].getNum()) ||
                        (y < 3 && cards[x][y].getNum() == cards[x][y + 1].getNum())) {
                    isOver = false;

                }

            }
        }

        if (isOver) {
            new AlertDialog.Builder(getContext()).setTitle("Извините, игра окончена. ").setMessage("\n" +
                    "Текущий результат " + MainActivity_2048.score + "， Продолжайте играть, чтобы улучшить свой результат ！")
                    .setPositiveButton("\n" + " Нажмите здесь, чтобы сыграть в следующий раунд", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    startGame();

                }
            }).show();
        }
    }


    class Listener implements OnTouchListener {

        private float startX, startY, offsetX, offsetY;

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

            if (!hasTouched) {
                hasTouched = true;
            }

            score = MainActivity_2048.score;

            for (int y = 0; y < 4; ++y) {
                for (int x = 0; x < 4; ++x) {
                    num[y][x] = cards[y][x].getNum();
                }
            }

            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startX = motionEvent.getX();
                    startY = motionEvent.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    offsetX = motionEvent.getX() - startX;
                    offsetY = motionEvent.getY() - startY;

                    if (Math.abs(offsetX) > Math.abs(offsetY)) {
                        if (offsetX < -5) {
                            swipeLeft();
                        } else if (offsetX > 5) {
                            swipeRight();
                        }
                    } else {
                        if (offsetY < -5) {
                            swipeUp();
                        } else if (offsetY > 5) {
                            swipeDown();
                        }
                    }

            }

            return true;

        }

    }

}