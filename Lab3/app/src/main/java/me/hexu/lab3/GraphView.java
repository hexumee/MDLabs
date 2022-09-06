package me.hexu.lab3;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

public class GraphView extends View {
    private final Paint axisColor = new Paint();
    private final Paint gridColor = new Paint();
    private final Paint lineColor = new Paint();

    private final Path graphPath = new Path();
    private final Path xAxisArrow = new Path();
    private final Path yAxisArrow = new Path();

    private final ScaleGestureDetector scaleGestureDetector;
    private final GestureDetector scrollGestureDetector;

    private final int maxZoomOut = 100;
    private final int maxZoomIn = 1;
    /*private final int gridStep = 32;
    private float gridScaleFactor = 10f;*/

    private float x0, x1;
    private float y0, y1;
    private float xDiff, yDiff;

    private boolean doDraw = false;
    private GraphCallback activityCallback;

    public GraphView(Context context, AttributeSet attrs) {
        super(context, attrs);

        axisColor.setColor(Color.RED);
        axisColor.setStrokeWidth(6f);
        gridColor.setColor(Color.DKGRAY);
        gridColor.setStrokeWidth(2f);
        lineColor.setColor(Color.GREEN);
        lineColor.setStrokeWidth(4f);

        lineColor.setAntiAlias(true);
        lineColor.setDither(true);
        lineColor.setStrokeJoin(Paint.Join.ROUND);
        lineColor.setStrokeCap(Paint.Cap.ROUND);
        lineColor.setStyle(Paint.Style.STROKE);

        scaleGestureDetector = new ScaleGestureDetector(context, new GraphScaleGestureListener());
        scrollGestureDetector = new GestureDetector(context, new GraphScrollGestureListener());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!doDraw) {
            return;
        }

        // Сброс предыдущего состояния линии (аналог invalidate() для View)
        graphPath.reset();
        xAxisArrow.reset();
        yAxisArrow.reset();

        xDiff = x1-x0;
        yDiff = y1-y0;

        float hValue = getHeight() - (getHeight() * (-y0 / yDiff));
        float vValue = getWidth() * (-x0 / xDiff);

        // Ось X
        canvas.drawLine(0, hValue, getWidth(), hValue, axisColor);

        // Ось Y
        canvas.drawLine(vValue, 0, vValue, getHeight(), axisColor);

        // Стрелки на осях
        yAxisArrow.moveTo(vValue, 0);
        yAxisArrow.lineTo(vValue-20, 20);
        yAxisArrow.lineTo(vValue+20, 20);
        yAxisArrow.lineTo(vValue, 0);
        canvas.drawPath(yAxisArrow, axisColor);

        xAxisArrow.moveTo(getWidth(), hValue);
        xAxisArrow.lineTo(getWidth()-20, hValue-20);
        xAxisArrow.lineTo(getWidth()-20, hValue+20);
        xAxisArrow.lineTo(getWidth(), hValue);
        canvas.drawPath(xAxisArrow, axisColor);

        /*// Сетка - вертикальные полосы
        for (float i = vValue; i < getWidth(); i += gridStep*(gridScaleFactor/2f)) {
            canvas.drawLine(i, 0, i, getHeight(), gridColor);
        }

        for (float j = vValue; j > 0; j -= gridStep*(gridScaleFactor/2f)) {
            canvas.drawLine(j, 0, j, getHeight(), gridColor);
        }

        // Сетка - горизонтальные полосы
        for (float i = hValue; i < getWidth(); i += gridStep*(gridScaleFactor/2f)) {
            canvas.drawLine(0, i, getWidth(), i, gridColor);
        }

        for (float j = hValue; j > 0; j -= gridStep*(gridScaleFactor/2f)) {
            canvas.drawLine(0, j, getWidth(), j, gridColor);
        }*/

        // Собственно график
        for (int p = 0; p < getWidth(); p++) {
            float x = (x0 + xDiff * ((float) p / getWidth()));
            float y = x * (float) (Math.cos(x/2));

            if (p == 0) { // Начинаем с этой точки, имея в виду размеры View
                graphPath.moveTo(0, ((-y * getHeight() / yDiff) + (getHeight() / yDiff) * y1));
            } else {
                graphPath.lineTo(p, ((-y * getHeight() / yDiff) + (getHeight() / yDiff) * y1));
            }
        }

        canvas.drawPath(graphPath, lineColor);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);
        scrollGestureDetector.onTouchEvent(event);

        return true;
    }

    public void setBounds(float leftBarrier, float rightBarrier, float upperBarrier, float lowerBarrier) {
        this.x0 = leftBarrier;
        this.x1 = rightBarrier;
        this.y0 = upperBarrier;
        this.y1 = lowerBarrier;
        this.doDraw = true;

        invalidate();
    }

    public void registerCallback(GraphCallback callback) {
        this.activityCallback = callback;
    }

    private class GraphScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            /*gridScaleFactor *= detector.getScaleFactor();
            gridScaleFactor = Math.max(2f, Math.min(gridScaleFactor, 200f));*/

            float cX = (x0 + x1) / 2f;
            float cY = (y0 + y1) / 2f;
            float alphaX = cX-x0;
            float alphaY = cY-y0;

            x0 += alphaX - (alphaX / detector.getScaleFactor());
            x1 -= alphaX - (alphaX / detector.getScaleFactor());
            y0 += alphaY - (alphaY / detector.getScaleFactor());
            y1 -= alphaY - (alphaY / detector.getScaleFactor());

            float newWidth = x1-x0;
            float newHeight = y1-y0;

            if (newWidth > maxZoomOut) {
                float xScale = 2*alphaX / (maxZoomOut + x0 - x1 + 2*alphaX);
                float aXScaled = alphaX - (alphaX / xScale);

                x0 += aXScaled;
                x1 -= aXScaled;
            } else if (newWidth < maxZoomIn) {
                float xScale = 2*alphaX / (maxZoomIn + x0 - x1 + 2*alphaX);
                float aXScaled = alphaX - (alphaX / xScale);

                x0 += aXScaled;
                x1 -= aXScaled;
            }

            if (newHeight > maxZoomOut) {
                float yScale = 2*alphaY / (maxZoomOut + y0 - y1 + 2*alphaY);
                float aYScaled = alphaY - (alphaX / yScale);

                y0 += aYScaled;
                y1 -= aYScaled;
            } else if (newHeight < maxZoomIn) {
                float yScale = 2*alphaY / (maxZoomIn + y0 - y1 + 2*alphaY);
                float aYScaled = alphaY - (alphaX / yScale);

                y0 += aYScaled;
                y1 -= aYScaled;
            }

            activityCallback.onChange(x0, x1, y0, y1);
            invalidate();

            return true;
        }
    }

    private class GraphScrollGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            x0 += (distanceX / getWidth()) * xDiff;
            x1 += (distanceX / getWidth()) * xDiff;
            y0 -= (distanceY / getHeight()) * yDiff;
            y1 -= (distanceY / getHeight()) * yDiff;

            activityCallback.onChange(x0, x1, y0, y1);
            invalidate();

            return true;
        }
    }
}
