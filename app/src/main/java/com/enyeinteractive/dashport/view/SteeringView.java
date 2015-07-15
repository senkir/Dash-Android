package com.enyeinteractive.dashport.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

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
        void onBearingChange(float direction, int magnitude);
    }

    private static class DriveMotionListener implements OnGenericMotionListener {
        private MotionEvent.PointerCoords coords;

        @Override
        public boolean onGenericMotion(View view, MotionEvent motionEvent) {
            int action = motionEvent.getAction();
            if (action == MotionEvent.ACTION_DOWN) {
                Log.v(TAG, "motion event started");
                coords = new MotionEvent.PointerCoords();
                motionEvent.getPointerCoords(0,coords);
                if ()
            } else if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
                Log.v(TAG, "motion event ended");
                coords = null;
            } else if (action == MotionEvent.ACTION_MOVE) {
                motionEvent.
            }
            return false;
        }
    }
}
