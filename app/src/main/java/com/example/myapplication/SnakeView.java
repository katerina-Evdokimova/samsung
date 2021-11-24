package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

import static com.example.myapplication.MainActivity.snakedensity;
import static com.example.myapplication.MainActivity.snakeheight;
import static com.example.myapplication.MainActivity.snakewidth;

import com.example.myapplication.dataBase.HotelContract;
import com.example.myapplication.dataBase.HotelDbHelper;


/**
 Данный класс рисует все элементы на экране для игры "Змейка".
 Игрок может управлять направлением этой виртуальной змеи (зеленая полоса на рисунках), просто касаясь
 экрана. Когда змея съест еду (прикосновение), она станет длиннееи продолжит двигаться. Еда исчезнет после того,
 как змея съест ее, и появится в случайном месте на экране. Скорость увеличивается с каждым успешным
 съеденным квадратом, а условием проигрыша является касание стены или прикосновение к себе. Во всем
 режиме игры, как только игрок съест 4 последовательных блюда, дверь выхода (белый квадрат).
 */

public class SnakeView extends View {

    private HotelDbHelper mDbHelper;

    //победа или поражение - при 5 успешных съедениях появляется дверь выхода
    public boolean win = false;
    public boolean lose = false;
    private int winSize = 4;

    //Arraylist содержит точку тела змеи
    private ArrayList<Point> mSnakeList = new ArrayList<>();
    private int mSnakeDirection = 0;
    private final int UP = 1;
    private final int DOWN = 2;
    private final int LEFT = 3;
    private final int RIGHT = 4;

    //размер границы
    private int wallGap = (int) snakedensity * 10;

    // размер площадки
    private final int SnakeAndFoodWidth = snakewidth / 20;
    private Random random = new Random();
    private Point mFoodPosition;
    private boolean isFoodEaten = true;

    //краски для различных элементов
    private Paint mSnakePaint;
    private Paint mBackgroundPaint;
    private Paint mFoodPaint;
    private Paint mFramePaint;
    private Paint mExitPaint;
    private Point mExitPosition;

    private String text1 = "КОНЕЦ ИГРЫ!";
    private String text2 = "Нажмите на экран для начала игры!";

    //изменяем границу игры со змеей, чтобы убедиться, что она кратна ширине змеи и площади пищи
    private final int hcout = snakewidth / SnakeAndFoodWidth;
    private final int vcout = snakeheight / SnakeAndFoodWidth;
    private int mWidth = hcout * SnakeAndFoodWidth;
    private int mHeight = vcout * SnakeAndFoodWidth;

    //массив точек, содержащий все позиции
    private int[] xPosition = new int[hcout - 2];
    private int[] yPosition = new int[vcout - 4];

    public int code = 0;

    public SnakeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /*
    конструктор, создаваемый из Context,
    инициализирует стену, змею, еду, объект Paint, список тел змеи и список позиций еды
     */
    public SnakeView(Context context) {
        super(context);
        initSnake();
        initFood();
        initPaint();
        initPosition();
        mDbHelper = new HotelDbHelper(context);
    }

    /*
    инициализация массива потенциальных точек, состоящий из тела змеи
    все точки предопределены на основе ширины и высоты игровой границы
     */
    private void initPosition() {
        for (int i = 0; i < hcout - 2; i++)
            xPosition[i] = (1 + i) * SnakeAndFoodWidth;
        for (int i = 0; i < vcout - 4; i++)
            yPosition[i] = (1 + i) * SnakeAndFoodWidth;
    }

    /*
    инициализируйте все объекты Paint для последующего рисования различных элементов
     */
    private void initPaint() {
        mSnakePaint = new Paint();
        mSnakePaint.setColor(Color.parseColor("#375A38"));
        mSnakePaint.setStyle(Paint.Style.FILL);

        mFoodPaint = new Paint();
        mFoodPaint.setColor(Color.parseColor("#D37E15"));
        mFoodPaint.setStyle(Paint.Style.FILL);


        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(Color.parseColor("#858285"));
        mBackgroundPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mBackgroundPaint.setFakeBoldText(true);
        mBackgroundPaint.setAntiAlias(true);
        mBackgroundPaint.setTextSize(snakedensity * 20);

        mFramePaint = new Paint();
        mFramePaint.setColor(Color.parseColor("#706670"));
        mFramePaint.setStyle(Paint.Style.FILL);

        mExitPaint = new Paint();
        mExitPaint.setColor(Color.WHITE);
        mExitPaint.setStyle(Paint.Style.STROKE);
        mExitPaint.setStrokeWidth(snakedensity * 2);
    }

    /*
    инициализирует список змей и добавляет в него первую точку. Это то же самое, как если бы первый
    успешно съел еду и увеличился на фиксированную длину.
    условие победы или условие поражения
     */
    private void initSnake() {
        mSnakeList.add(0, new Point(4 * SnakeAndFoodWidth, 4 * SnakeAndFoodWidth));
        mSnakeDirection = RIGHT;
        isFoodEaten = true;
    }

    //добавляет еду
    private void initFood() {
        mFoodPosition = new Point();
    }

    /**
     * Этот метод анализирует событие касания от игрока и изменяет "направление змеи" на основе
     * места касания на экране, когда выполняется условие lose (касание стены или тела змеи),
     * метод выполнит оператор if - инициализирует список тел змей и установит значение lose в false
     * значение, что позволит игроку перезапустить игру, коснувшись экрана.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //if lose, перезапустить игру, коснувшись экрана
        //if оператор создаст новый список тел змей и инициализирует "змею" методом iniSnake()
        //переменная lose будет установлена в false, чтобы избежать выполнения оператора if, когда условие lose
        // условие не будет выполнено.
        if (lose) {
            mExitPosition = new Point();
            mSnakeList = new ArrayList<>();
            initSnake();
            lose = false;
        }
        //else, изменять направление змейки в зависимости от места касания на экране
        else {
            int x = (int) (event.getX());
            int y = (int) (event.getY());

            Point head = mSnakeList.get(0);

            //поворачиваться влево/вправо только тогда, когда текущее движение направлено вверх/вниз,
            // избегать поворота прямо на 180 градусов
            if (mSnakeDirection == UP || mSnakeDirection == DOWN) {
                if (x < head.x)
                    mSnakeDirection = LEFT;
                if (x > head.x)
                    mSnakeDirection = RIGHT;
            }

            //поворачиваться вверх/вниз только при текущем движении влево/вправо,
            else if (mSnakeDirection == LEFT || mSnakeDirection == RIGHT) {
                if (y < head.y)
                    mSnakeDirection = UP;
                if (y > head.y)
                    mSnakeDirection = DOWN;
            }
        }

        return super.onTouchEvent(event);
    }

    /**
     * Этот метод является основополагающим для игры, поскольку он будет выполняться после каждого обновления
     * и интервала времени методом invalidate() из другого класса - snake.java. Говоря по порядку,
     * этот метод будет "перерисовывать" все элементы снова и снова на настроенном представлении, чтобы показать
     * обновленное движение на экране. Он заставит змею "двигаться", когда соответствующая частота обновления
     * частота установлена
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawBackground(canvas, mBackgroundPaint);
        drawFrame(canvas, mFramePaint);
        drawFood(canvas, mFoodPaint);

        //если игрок выиграл игру (несколько удачных съеденных партий), то появляется выход
        if (mSnakeList.size() > winSize) {
            drawExit(canvas, mExitPaint);
        }
        drawSnake(canvas, mSnakePaint);

        //нарисовать счет в нижней части экрана
        canvas.drawText("СЧЁТ: " + String.valueOf(code), mWidth / 3 + SnakeAndFoodWidth, mHeight, mBackgroundPaint);


        if (lose) {
            canvas.drawText(text1, mWidth / 2 - 3 * SnakeAndFoodWidth, mHeight / 2, mBackgroundPaint);
            canvas.drawText(text2, mWidth / 2 - 6 * SnakeAndFoodWidth, mHeight / 2 + SnakeAndFoodWidth, mBackgroundPaint);
        }
    }

    /*
    нарисовать выход на экране
     */
    private void drawExit(Canvas canvas, Paint mExitPaint) {
        mExitPosition = new Point();
        mExitPosition.x = hcout / 2 * SnakeAndFoodWidth;
        mExitPosition.y = vcout / 2 * SnakeAndFoodWidth;
        canvas.drawRect(new Rect(mExitPosition.x, mExitPosition.y, mExitPosition.x + SnakeAndFoodWidth, mExitPosition.y + SnakeAndFoodWidth), mExitPaint);
    }

    /*
    рамка
     */
    private void drawFrame(Canvas canvas, Paint mFramePaint) {
        Rect frame1 = new Rect(wallGap, wallGap, mWidth - wallGap, SnakeAndFoodWidth);//top
        canvas.drawRect(frame1, mFramePaint);
        Rect frame2 = new Rect(wallGap, wallGap, SnakeAndFoodWidth, mHeight - 2 * SnakeAndFoodWidth - wallGap);//left
        canvas.drawRect(frame2, mFramePaint);
        Rect frame3 = new Rect(wallGap, (mHeight - 3 * SnakeAndFoodWidth), mWidth - wallGap, mHeight - 2 * SnakeAndFoodWidth - wallGap);//bottom
        canvas.drawRect(frame3, mFramePaint);
        Rect frame4 = new Rect((mWidth - SnakeAndFoodWidth), wallGap, mWidth - wallGap, mHeight - 2 * SnakeAndFoodWidth - wallGap);//right
        canvas.drawRect(frame4, mFramePaint);
    }

    /*
    Этот метод обновляет состояние "змеи" - как будет выглядеть "змея" в следующем обновлении.
    Если условие lose не выполнено - метод удлиняет
    "змею" вперед и проверяет, не коснулись ли мы выхода или пищи.
    Если условие lose выполнено - метод нарисует тело змеи только с нуля. В противном случае,
    метод добавит новую точку в голову "змеи" и установит последнее направление. Метод
    решит, нужно ли удалить последнюю точку "змеи", основываясь на количестве съеденной пищи.
     */
    private void drawSnake(Canvas canvas, Paint mSnakePaint) {
        for (int i = 0; i < mSnakeList.size(); i++) {
            Point point = mSnakeList.get(i);
            Rect rect = new Rect(point.x, point.y, point.x + SnakeAndFoodWidth, point.y + SnakeAndFoodWidth);
            canvas.drawRect(rect, mSnakePaint);
        }
        if (!lose) {
            SnakeMove(mSnakeList, mSnakeDirection);
            if (mSnakeList.contains(mExitPosition))
                win = true;
        /*
        UPDATE
        */
            if (mSnakeList.contains(mFoodPosition)) {
                isFoodEaten = true;
                code += 10;
            } else
                mSnakeList.remove(mSnakeList.size() - 1);
        }

    }

    /*
 Этот метод нарисует прямоугольник с едой на экране случайным образом, когда старый будет "съеден" "змеей".     */
    private void drawFood(Canvas canvas, Paint mFoodPaint) {
        if (isFoodEaten) {
            mFoodPosition.x = xPosition[random.nextInt(hcout - 2)];
            mFoodPosition.y = yPosition[random.nextInt(vcout - 4)];
            isFoodEaten = false;
        }
        Rect food = new Rect(mFoodPosition.x, mFoodPosition.y, mFoodPosition.x + SnakeAndFoodWidth, mFoodPosition.y + SnakeAndFoodWidth);
        canvas.drawRect(food, mFoodPaint);
    }

    private void drawBackground(Canvas canvas, Paint mBackgroundPaint) {
        canvas.drawColor(Color.BLACK);
    }

    /**
     * Этот метод предназначен для добавления новой "головы" (новой точки) в список змей (индекс:0) и установки
     * нового направления на основе события касания.
     * Он также проверит выполнение условия lose - коснется ли "змея" стены или съест саму себя.
     */
    public void SnakeMove(ArrayList<Point> mSnakeList, int direction) {
        Point oldHead = mSnakeList.get(0);
        Point newHead = new Point(oldHead);

        switch (direction) {
            case (UP):
                newHead.y -= SnakeAndFoodWidth;
                break;
            case (DOWN):
                newHead.y += SnakeAndFoodWidth;
                break;
            case (LEFT):
                newHead.x -= SnakeAndFoodWidth;
                break;
            case (RIGHT):
                newHead.x += SnakeAndFoodWidth;
                break;
            default:
                break;
        }

        if (mSnakeList.contains(newHead) || newHead.x == 0 || newHead.y == 0 || newHead.x == mWidth - SnakeAndFoodWidth || newHead.y == mHeight - 3 * SnakeAndFoodWidth) {
            //Сьел себя или коснулся стены
            lose = true;
            SQLiteDatabase db = mDbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(HotelContract.Score.COLUMN_SCORE, code);
            values.put(HotelContract.Score.COLUMN_STATUS, "ПОРАЖЕНИЕ");


            // Вставляем новый ряд в базу данных и запоминаем его идентификатор
            long newRowId = db.insert(HotelContract.Score.TABLE_NAME, null, values);

            // Выводим сообщение в успешном случае или при ошибке
            if (newRowId == -1) {
                // Если ID  -1, значит произошла ошибка
                Toast.makeText(this.getContext(), "Ошибка", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this.getContext(), "Рейтинг обновлен: " + newRowId, Toast.LENGTH_SHORT).show();
            }
            code = 0;
        } else
            mSnakeList.add(0, newHead);

    }


}
