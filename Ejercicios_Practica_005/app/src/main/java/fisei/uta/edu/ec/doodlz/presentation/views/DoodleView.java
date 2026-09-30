package fisei.uta.edu.ec.doodlz.presentation.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Custom View dedicated exclusively to capturing touch events and drawing on canvas.
 * Adheres to the Single Responsibility Principle: does not handle I/O, storage, or printing.
 */
public class DoodleView extends View {

    private static final float TOUCH_TOLERANCE = 10;

    private final Map<Integer, Path> pathMap = new HashMap<>();
    private final Map<Integer, Point> previousPointMap = new HashMap<>();

    private Bitmap bitmap;
    private Canvas bitmapCanvas;

    private final Paint paintScreen;
    private final Paint paintLine;

    private int maxCanvasWidth = 0;
    private int maxCanvasHeight = 0;

    public DoodleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        paintScreen = new Paint();
        paintLine = new Paint();

        paintLine.setAntiAlias(true);
        paintLine.setColor(Color.BLACK);
        paintLine.setStyle(Paint.Style.STROKE);
        paintLine.setStrokeWidth(5);
        paintLine.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        maxCanvasWidth = Math.max(maxCanvasWidth, w);
        maxCanvasHeight = Math.max(maxCanvasHeight, h);

        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(maxCanvasWidth, maxCanvasHeight, Bitmap.Config.ARGB_8888);
            bitmapCanvas = new Canvas(bitmap);
            bitmap.eraseColor(Color.WHITE);
        } else {
            if (maxCanvasWidth > bitmap.getWidth() || maxCanvasHeight > bitmap.getHeight()) {
                Bitmap newBitmap = Bitmap.createBitmap(
                        Math.max(bitmap.getWidth(), maxCanvasWidth),
                        Math.max(bitmap.getHeight(), maxCanvasHeight),
                        Bitmap.Config.ARGB_8888
                );
                Canvas newCanvas = new Canvas(newBitmap);
                newBitmap.eraseColor(Color.WHITE);
                newCanvas.drawBitmap(bitmap, 0, 0, null);

                bitmap = newBitmap;
                bitmapCanvas = newCanvas;
            }
        }
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        if (bitmap != null) {
            canvas.drawBitmap(bitmap, 0, 0, paintScreen);
        }

        for (Integer key : pathMap.keySet()) {
            Path path = pathMap.get(key);
            if (path != null) {
                canvas.drawPath(path, paintLine);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        int actionIndex = event.getActionIndex();

        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) {
            touchStarted(
                    event.getX(actionIndex),
                    event.getY(actionIndex),
                    event.getPointerId(actionIndex)
            );
        } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP) {
            touchEnded(event.getPointerId(actionIndex));
        } else {
            touchMoved(event);
        }

        invalidate();
        return true;
    }

    private void touchStarted(float x, float y, int lineID) {
        Path path;
        Point point;

        if (pathMap.containsKey(lineID)) {
            path = pathMap.get(lineID);
            path.reset();
            point = previousPointMap.get(lineID);
        } else {
            path = new Path();
            pathMap.put(lineID, path);
            point = new Point();
            previousPointMap.put(lineID, point);
        }

        path.moveTo(x, y);
        point.x = (int) x;
        point.y = (int) y;
    }

    private void touchMoved(MotionEvent event) {
        for (int i = 0; i < event.getPointerCount(); i++) {
            int pointerID = event.getPointerId(i);
            int pointerIndex = event.findPointerIndex(pointerID);

            if (pathMap.containsKey(pointerID)) {
                float newX = event.getX(pointerIndex);
                float newY = event.getY(pointerIndex);

                Path path = pathMap.get(pointerID);
                Point point = previousPointMap.get(pointerID);

                float deltaX = Math.abs(newX - point.x);
                float deltaY = Math.abs(newY - point.y);

                if (deltaX >= TOUCH_TOLERANCE || deltaY >= TOUCH_TOLERANCE) {
                    path.quadTo(
                            point.x,
                            point.y,
                            (newX + point.x) / 2,
                            (newY + point.y) / 2
                    );

                    point.x = (int) newX;
                    point.y = (int) newY;
                }
            }
        }
    }

    private void touchEnded(int lineID) {
        Path path = pathMap.get(lineID);

        if (path != null && bitmapCanvas != null) {
            Point point = previousPointMap.get(lineID);

            if (point != null) {
                path.lineTo(point.x, point.y);
            }

            bitmapCanvas.drawPath(path, paintLine);
            path.reset();
        }
    }

    public void clear() {
        pathMap.clear();
        previousPointMap.clear();

        if (bitmap != null) {
            bitmap.eraseColor(Color.WHITE);
        }

        invalidate();
    }

    public void setDrawingColor(int color) {
        paintLine.setColor(color);
    }

    public int getDrawingColor() {
        return paintLine.getColor();
    }

    public void setLineWidth(int width) {
        paintLine.setStrokeWidth(width);
    }

    public int getLineWidth() {
        return (int) paintLine.getStrokeWidth();
    }

    @Nullable
    public Bitmap getDrawingBitmap() {
        return bitmap;
    }
}
