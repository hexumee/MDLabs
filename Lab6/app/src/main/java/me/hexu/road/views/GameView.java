package me.hexu.road.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import me.hexu.road.callbacks.EventCallback;

public class GameView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private SurfaceHolder mSurfaceHolder;
    private Canvas mCanvas;
    private boolean mIsRunning;

    private EventCallback eventCallback;

    private final Paint mTopLinePaint = new Paint();
    private final Paint mDividerPaint = new Paint();
    private final Paint mTextPaint = new Paint();
    private final Paint mGrassPaint = new Paint();
    private final Paint mAsphaltPaint = new Paint();

    private final int mTopBarOffset = 128;    // Отступ с верхней части экрана
    private final int mRoadOffset = 144;      // Отступ по боковым сторонам

    private int score = 0;
    private long gameStartTime = 0;

    private int phase = 0;                            // Текущая фаза отрисовки разделительной полосы
    private int phaseAccelerator = 8;                 // Фаза ускорения отрисовки разделительной полосы
    private int phaseAcceleratorBefore = 0;           // Фаза ускорения, к которой нужно вернуться после конца торможения
    private final int phaseAcceleratorDefault = 8;    // Стандартная фаза ускорения

    private final GestureDetector swipeDetector;

    private int rotation = 0;                  // Переменная, определяющая поворот машины+
    private boolean isOnLeftSide = false;      // Переменная, определяющая положение машины на левой полосе
    private float carPosition = 0;             // Текущая позиция машины на экране по оси X
    private float defaultCarPosition = 0;      // Начальная позиция машины на экране по оси X
    private boolean isAccelerating = true;     // Переменная, определяющая ускорение машины
    private boolean actionLockDown = false;    // Переменная, позволяющая запретить одновременное ускорение и торможение

    private Bitmap car = null;
    private Bitmap carLeftRotated = null;
    private Bitmap carMidRotated = null;
    private Bitmap carRightRotated = null;
    private final CarBitmaps carSprites;               // Класс, хранящий в себе спрайты дополнительных машин

    private Bitmap camera = null;
    private Bitmap cameraWarning = null;
    private boolean youAreGoingToBeWatched = false;    // Предупреждение о камере наблюдения
    private boolean youAreBeingWatched = false;        // "Big Brother is watching you!"

    private Random carSpawner;                                                                                // Рандомайзер
    private final int[] hexSpeech = new int[]{0xDEADC0DE, 0xF00DCAFE, 0x00C0FFEE, 0x0BADF00D, 0x00FF00FF};    // Паттерны для генератора
    private int usingRandomPatternIndex = -1;

    private int carAcceleratorYValue = 24;                 // Начальное значение ускорения машины по оси Y
    private int carAcceleratorYRightValue = 24;            // Начальное значение ускорения машины по оси Y
    private int carAcceleratorXValue = 32;                 // Начальное значение ускорения машины по оси X
    private final int carAcceleratorYValueDefault = 24;    // Стандартное значение ускорения машины по оси Y
    private int carAcceleratorYValueBefore = 24;           // Значение ускорения машины по оси Y, к которому нужно вернуться после конца торможения

    // Списки неигровых машин на экране
    private final ArrayList<Car> carsOnLeftSide = new ArrayList<>();
    private final ArrayList<Car> carsOnRightSide = new ArrayList<>();

    // Позиции последних (неигровых) машин на экране
    private int leftCarOffset = 0;
    private int rightCarOffset = 0;

    // Обработчики торможения (удержание пальца на экране) и ускорения машины (отпускание пальца с экрана)
    private final Handler slowingDownHandler = new Handler();
    private final Handler acceleratingHandler = new Handler();
    private final Runnable slowingDownAction = () -> {
        if (actionLockDown) {
            return;
        }

        isAccelerating = false;
        actionLockDown = true;

        while (actionLockDown && !isAccelerating && phaseAccelerator >= phaseAcceleratorDefault) {
            if (carAcceleratorYRightValue <= carAcceleratorYValueDefault && phaseAccelerator <= phaseAcceleratorDefault) {
                break;
            }

            carAcceleratorYRightValue -= 2;
            carAcceleratorXValue -= 2;
            phaseAccelerator--;

            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        actionLockDown = false;
    };
    private final Runnable acceleratingAction = () -> {
        if (actionLockDown) {
            return;
        }

        isAccelerating = true;
        actionLockDown = true;

        while (actionLockDown && isAccelerating && phaseAccelerator <= phaseAcceleratorBefore) {
            if (carAcceleratorYRightValue >= carAcceleratorYValueBefore && phaseAccelerator >= phaseAcceleratorBefore) {
                break;
            }

            carAcceleratorYRightValue += 2;
            carAcceleratorXValue += 2;
            phaseAccelerator++;

            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        actionLockDown = false;
    };


    public GameView(Context context) {
        this(context, null);
    }

    public GameView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);

        mTopLinePaint.setColor(Color.WHITE);
        mTopLinePaint.setStrokeWidth(4f);
        mTopLinePaint.setAntiAlias(true);

        mDividerPaint.setColor(Color.WHITE);
        mDividerPaint.setStrokeWidth(32f);
        mDividerPaint.setAntiAlias(true);

        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setStrokeWidth(4f);
        mTextPaint.setTextSize(mTopBarOffset-32);
        mTextPaint.setTypeface(Typeface.createFromAsset(context.getAssets(),"teletactile.ttf"));
        mTextPaint.setAntiAlias(true);

        mAsphaltPaint.setColor(Color.rgb(80, 80, 80));
        mAsphaltPaint.setAntiAlias(true);

        mGrassPaint.setColor(Color.rgb(63, 142, 11));
        mGrassPaint.setAntiAlias(true);

        try {
            Matrix matrixLeft = new Matrix();
            Matrix matrixRight = new Matrix();
            matrixLeft.postRotate(-45);
            matrixRight.postRotate(45);

            carMidRotated = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(context.getAssets().open("car0.png")), 160, 256, false);
            car = carMidRotated;
            carLeftRotated = Bitmap.createBitmap(car, 0, 0, car.getWidth(), car.getHeight(), matrixLeft, false);
            carRightRotated = Bitmap.createBitmap(car, 0, 0, car.getWidth(), car.getHeight(), matrixRight, false);

            camera = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(context.getAssets().open("camera.png")), 164, 96, false);
            cameraWarning = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(context.getAssets().open("warning.png")), 96, 96, false);
        } catch (IOException e) {
            e.printStackTrace();
        }

        carSprites = new CarBitmaps(context);
        carsOnLeftSide.add(new Car(-car.getHeight(), carSprites.getRandomLeftCar()));
        carsOnRightSide.add(new Car(-car.getHeight()*3, carSprites.getRandomRightCar()));
        carSpawner = new Random(context.hashCode());

        swipeDetector = new GestureDetector(context, new CarSwipeGestureDetector());
    }

    public void registerCallback(EventCallback callback) {
        this.eventCallback = callback;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mIsRunning = true;
        gameStartTime = System.currentTimeMillis();

        new Thread(this).start();

        // Поток-ускоритель, а также псевдо-рандомайзер
        new Thread(() -> {
            while (mIsRunning) {
                try {
                    if (isAccelerating && score >= 10 && score % 10 == 0) {
                        phaseAccelerator++;
                        phaseAcceleratorBefore++;

                        carAcceleratorYValue += 2;
                        carAcceleratorYRightValue += 2;
                        carAcceleratorXValue += 2;
                        carAcceleratorYValueBefore += 2;

                        usingRandomPatternIndex++;
                        carSpawner = new Random(hexSpeech[usingRandomPatternIndex % hexSpeech.length]);
                    }

                    TimeUnit.MILLISECONDS.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        // Поток, "перерабатывающий" машины, прошедшие View от точки начала до конца, и продвигающий их по View по этому же маршруту
        new Thread(() -> {
            while (mIsRunning) {
                try {
                    Iterator<Car> itL = carsOnLeftSide.iterator();
                    while (itL.hasNext()) {
                        Car val = itL.next();
                        if (val.getCarOffset() > getHeight()-mRoadOffset*2+car.getHeight()) {
                            itL.remove();
                        }
                    }

                    for (int i = 0; i < carsOnLeftSide.size(); i++) {
                        carsOnLeftSide.get(i).setCarOffset(carsOnLeftSide.get(i).getCarOffset()+carAcceleratorYValue);

                        if (i == 0) {
                            leftCarOffset = carsOnLeftSide.get(i).getCarOffset();
                        }
                    }

                    Iterator<Car> itR = carsOnRightSide.iterator();
                    while (itR.hasNext()) {
                        Car val = itR.next();
                        if (val.getCarOffset()-car.getHeight() > getHeight()-mRoadOffset*2) {
                            itR.remove();
                            score++;
                        }
                    }

                    for (int i = 0; i < carsOnRightSide.size(); i++) {
                        if (isAccelerating) {
                            carsOnRightSide.get(i).setCarOffset(carsOnRightSide.get(i).getCarOffset()+carAcceleratorYRightValue);

                            if (i == 0) {
                                rightCarOffset = carsOnRightSide.get(i).getCarOffset();
                            }
                        }
                    }

                    TimeUnit.MILLISECONDS.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        // Поток-спавнер
        new Thread(() -> {
            while (mIsRunning) {
                try {
                    if (carSpawner.nextDouble() < 0.5) {
                        if (carsOnLeftSide.size() > 0 && carsOnLeftSide.get(carsOnLeftSide.size()-1).getCarOffset() > car.getHeight()) {
                            carsOnLeftSide.add(new Car(-car.getHeight(), carSprites.getRandomLeftCar()));
                        } else if (carsOnLeftSide.size() == 0) {
                            carsOnLeftSide.add(new Car(-car.getHeight(), carSprites.getRandomLeftCar()));
                        }
                    }

                    TimeUnit.MILLISECONDS.sleep(1000);

                    if (isAccelerating && carSpawner.nextDouble() > 0.5) {
                        if (carsOnRightSide.size() > 0 && carsOnRightSide.get(carsOnRightSide.size()-1).getCarOffset() > car.getHeight()) {
                            carsOnRightSide.add(new Car(-car.getHeight(), carSprites.getRandomRightCar()));
                        } else if (carsOnRightSide.size() == 0) {
                            carsOnRightSide.add(new Car(-car.getHeight(), carSprites.getRandomRightCar()));
                        }
                    }

                    TimeUnit.MILLISECONDS.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        // Поток, переключающий камеру
        new Thread(() -> {
            while (mIsRunning) {
                try {
                    if (score >= 20) {
                        youAreGoingToBeWatched = !youAreGoingToBeWatched;
                        TimeUnit.MILLISECONDS.sleep(3000);
                        youAreBeingWatched = !youAreBeingWatched;
                        TimeUnit.MILLISECONDS.sleep(10000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        // Поток, проверяющий условия конца игры
        new Thread(() -> {
            while (mIsRunning) {
                try {
                    if (isOnLeftSide && youAreBeingWatched) {
                        TimeUnit.MILLISECONDS.sleep(50);
                        mIsRunning = false;
                        eventCallback.onGameOver(score, (System.currentTimeMillis()-gameStartTime)/1000);
                    }

                    if (isOnLeftSide && rotation != 1 &&
                        leftCarOffset <= getHeight()-mRoadOffset*2.5 &&
                        leftCarOffset+car.getHeight() >= getHeight()-mRoadOffset*2
                    ) {
                        TimeUnit.MILLISECONDS.sleep(50);
                        mIsRunning = false;
                        eventCallback.onGameOver(score, (System.currentTimeMillis()-gameStartTime)/1000);
                    }

                    if (!isOnLeftSide && rotation != -1 &&
                        rightCarOffset <= getHeight()-mRoadOffset*2 &&
                        rightCarOffset+car.getHeight() >= getHeight()-mRoadOffset*2
                    ) {
                        TimeUnit.MILLISECONDS.sleep(50);
                        mIsRunning = false;
                        eventCallback.onGameOver(score, (System.currentTimeMillis()-gameStartTime)/1000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) { }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mIsRunning = false;
        this.mSurfaceHolder = null;
    }

    @Override
    public void run() {

        // Основной поток игры
        while (mIsRunning) {
            mCanvas = mSurfaceHolder.lockCanvas();

            if (mCanvas == null) {
                break;
            }

            // Определяем позицию машины на экране по оси X
            if (carPosition == 0) {
                defaultCarPosition = getWidth()/2f+mRoadOffset;
                carPosition = defaultCarPosition;
            }

            // Кладём газон и асфальт
            mCanvas.drawRect(0, mTopBarOffset, getWidth(), getHeight(), mGrassPaint);
            mCanvas.drawRect(mRoadOffset, mTopBarOffset, getWidth()-mRoadOffset, getHeight(), mAsphaltPaint);

            // Рисуем разделительную линию, двигая её по экрану
            mDividerPaint.setPathEffect(new DashPathEffect(new float[]{100, 100}, phase));
            phase -= phaseAccelerator;
            mCanvas.drawLine(getWidth()/2f, mTopBarOffset, getWidth()/2f, getHeight(), mDividerPaint);

            // Обработка поворота машины
            switch (rotation) {
                case -1:
                    mCanvas.drawBitmap(carLeftRotated, -carLeftRotated.getWidth()/4f+carPosition, getHeight()-mRoadOffset*2, new Paint());
                    break;
                case 0:
                    mCanvas.drawBitmap(carMidRotated, carPosition, getHeight()-mRoadOffset*2, new Paint());
                    break;
                case 1:
                    mCanvas.drawBitmap(carRightRotated, -carRightRotated.getWidth()/4f+carPosition, getHeight()-mRoadOffset*2, new Paint());
                    break;
            }

            // Рисуем дополнительные машины на обоих полосах дороги
            for (int i = 0; i < carsOnLeftSide.size(); i++) {
                mCanvas.drawBitmap(carsOnLeftSide.get(i).getCarBitmap(), getWidth()/2f-mRoadOffset*2, mTopBarOffset+carsOnLeftSide.get(i).getCarOffset(), new Paint());
            }

            for (int i = 0; i < carsOnRightSide.size(); i++) {
                mCanvas.drawBitmap(carsOnRightSide.get(i).getCarBitmap(), defaultCarPosition, carsOnRightSide.get(i).getCarOffset(), new Paint());
            }

            // Статус-бар
            mCanvas.drawRect(0, 0, getWidth(), mTopBarOffset, new Paint());
            mCanvas.drawText(String.valueOf(score), 32, mTopBarOffset-32, mTextPaint);
            mCanvas.drawLine(0, mTopBarOffset, getWidth(), mTopBarOffset, mTopLinePaint);

            if (youAreBeingWatched) {
                mCanvas.drawBitmap(camera, getWidth()-16-camera.getWidth(), 12, new Paint());
            } else if (youAreGoingToBeWatched) {
                mCanvas.drawBitmap(cameraWarning, getWidth()-32-cameraWarning.getWidth(), 12, new Paint());
            }

            mSurfaceHolder.unlockCanvasAndPost(mCanvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        swipeDetector.onTouchEvent(event);

        // Нажали на экран, но не свайпнули
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            acceleratingHandler.removeCallbacks(acceleratingAction);
            slowingDownHandler.postDelayed(slowingDownAction, 320);
            System.out.println("down");
        } else if (event.getAction() == MotionEvent.ACTION_UP) {    // Отпустили палец
            slowingDownHandler.removeCallbacks(slowingDownAction);
            acceleratingHandler.postDelayed(acceleratingAction, 320);
            System.out.println("up");
        }

        return true;
    }

    private class CarSwipeGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

            // Поворот налево
            if (distanceX > 0) {
                rotation = -1;

                while (carPosition > getWidth()/2f-mRoadOffset*2) {
                    try {
                        carPosition -= carAcceleratorXValue;

                        if (!isOnLeftSide && carPosition <= defaultCarPosition-(getWidth()/2f-mRoadOffset*2)+12) {
                            isOnLeftSide = true;
                        }

                        TimeUnit.MILLISECONDS.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                carPosition = getWidth()/2f-mRoadOffset*2;
            } else if (distanceX < 0) {    // Поворот направо
                rotation = 1;

                while (carPosition < defaultCarPosition) {
                    try {
                        carPosition += carAcceleratorXValue;

                        if (isOnLeftSide && carPosition >= defaultCarPosition-(getWidth()/2f-mRoadOffset*2)+24) {
                            isOnLeftSide = false;
                        }

                        TimeUnit.MILLISECONDS.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                carPosition = defaultCarPosition;
            }

            // Выравнивание параллельно дороге
            rotation = 0;
            return true;
        }
    }
}
