package com.enyeinteractive.dashport.view;

import android.content.Context;
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

    // //////////////////////
    // Fields

    private OnSteerActionListener listener;

    // //////////////////////
    // Constructors

    public SteeringView(Context context) {
        super(context);
        initTouchListener();
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
//                if ()
            } else if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
                Log.v(TAG, "motion event ended");
                origin = null;
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
