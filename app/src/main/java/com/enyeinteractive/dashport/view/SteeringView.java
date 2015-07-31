package com.enyeinteractive.dashport.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import com.enyeinteractive.dashport.util.GeometryUtil;

/**
 * @author tcastillo
 *         Date: 6/30/15
 *         Time: 9:45 PM
 */
public class SteeringView extends View {

    // //////////////////////
    // Constants
    private static final String TAG = SteeringView.class.getSimpleName();
    private static final boolean DEBUG = true;

    // //////////////////////
    // Fields

    private OnSteerActionListener listener;
    private Paint startColor;
    private Paint endColor;

    // //////////////////////
    // Constructors

    public SteeringView(Context context) {
        super(context);
        initTouchListener();
        if (DEBUG) {
            startColor = new Paint();
            startColor.setColor(Color.BLACK);
            endColor = new Paint();
            endColor.setColor(Color.GREEN);
        }

    }

    public SteeringView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initTouchListener();
    }

    public SteeringView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initTouchListener();
    }

    public SteeringView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initTouchListener();
    }

    // //////////////////////
    // Getter & Setter
    public void setListener(OnSteerActionListener listener) {
        this.listener = listener;
    }
    // //////////////////////
    // Methods from SuperClass/Interfaces

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (DEBUG) {
            //draw circles
            if ()
            canvas.drawCircle();
        }
    }

    // //////////////////////
    // Methods


    private void initTouchListener() {
        setOnGenericMotionListener(new DriveMotionListener());
    }
    // //////////////////////
    // Inner and Anonymous Classes
    public interface OnSteerActionListener {
        void onCancel(SteeringView view);
        void onSteerActionStarted();
        void onBearingChange(double direction, double magnitude);
    }

    private class DriveMotionListener implements OnGenericMotionListener {
        private MotionEvent.PointerCoords origin;
        private MotionEvent.PointerCoords location = new MotionEvent.PointerCoords();

        @Override
        public boolean onGenericMotion(View view, MotionEvent motionEvent) {
            int action = motionEvent.getAction();
            if (action == MotionEvent.ACTION_DOWN) {
                Log.v(TAG, "motion event started");
                origin = new MotionEvent.PointerCoords();
                motionEvent.getPointerCoords(0, origin);
                if (listener != null) listener.onSteerActionStarted();
            } else if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
                Log.v(TAG, "motion event ended");
                origin = null;
                if (listener != null) listener.onCancel(SteeringView.this);
            } else if (action == MotionEvent.ACTION_MOVE) {
                motionEvent.getPointerCoords(0, location);
                parseBearingAndDirection();
            }
            return false;
        }

        private void parseBearingAndDirection() {
            //distance between 2 points
            //angle between 2 points
            double distance = GeometryUtil.distance(origin, location);
            double angle = GeometryUtil.angle(origin, location);
            if (listener != null) {
                listener.onBearingChange(angle, distance);
            }
        }
    }
}
