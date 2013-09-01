package edu.SJTU.ChiChi.layouts;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * Created with IntelliJ IDEA.
 * User: JeffreyZhang
 * Date: 13-8-29
 * Time: 下午7:12
 */
public class SwipeLayout extends ViewGroup {

    private static final String TAG = "SwipeLayout";
    private static final int SNAP_VELOCITY = 600;   // velocity threshold for x-axis to judge screen switch
    private int currentScreen;                      // current screen number
    private int defaultScreen = 0;                  // default screen number
    private float lastMotionX;                      //
    private VelocityTracker velocityTracker;        // judge swipe
    private Scroller scroller;                      // scroll control

    private OnViewChangeListener onViewChangeListener;

    public SwipeLayout(Context context) {
        super(context);
        init(context);
    }

    public SwipeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        currentScreen = defaultScreen;
        scroller = new Scroller(context);
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            int childLeft = 0;
            final int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View childView = getChildAt(i);
                if (childView.getVisibility() != View.GONE) {
                    final int childWidth = childView.getMeasuredWidth();
                    childView.layout(childLeft, 0, childLeft + childWidth, childView.getMeasuredHeight());
                    childLeft += childWidth;
                }
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int width = MeasureSpec.getSize(widthMeasureSpec);
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
        }
        scrollTo(currentScreen * width, 0);
    }

    public void snapToScreen(int whichScreen) {
        if (getScrollX() != (whichScreen * getWidth())) {
            final int delta = whichScreen * getWidth() - getScrollX();
            scroller.startScroll(getScrollX(), 0, delta, 0, Math.abs(delta) * 2);
            currentScreen = whichScreen;
            invalidate();

            if (onViewChangeListener != null) {
                onViewChangeListener.OnViewChange(currentScreen);
            }
        }
    }

    public void snapToDestination() {
        final int screenWidth = getWidth();
        final int destinationScreen = (getScrollX() + screenWidth / 2) / screenWidth;
        snapToScreen(destinationScreen);
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            postInvalidate();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        final float x = event.getX();
        final float y = event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (velocityTracker == null) {
                    velocityTracker = VelocityTracker.obtain();
                    velocityTracker.addMovement(event);
                }
                if (!scroller.isFinished()) {
                    scroller.abortAnimation();
                }
                break;

            case MotionEvent.ACTION_MOVE:
                int deltaX = (int) (lastMotionX - x);
                if (IsCanMove(deltaX)) {
                    if (velocityTracker != null) {
                        velocityTracker.addMovement(event);
                    }
                    lastMotionX = x;
                    // screen follows finger
                    scrollBy(deltaX, 0);
                }
                break;

            case MotionEvent.ACTION_UP:
                int velocityX = 0;
                if (velocityTracker != null) {
                    velocityTracker.addMovement(event);
                    velocityTracker.computeCurrentVelocity(1000);
                    // get finger velocity on x-axis
                    velocityX = (int) velocityTracker.getXVelocity();
                }
                // velocityX > 0 == swipe right, < 0 == swipe left
                if (velocityX > SNAP_VELOCITY && currentScreen > 0) {
                    // Fling enough to move left
                    snapToScreen(currentScreen - 1);
                } else if (velocityX < -SNAP_VELOCITY && (currentScreen < getChildCount() - 1)) {
                    snapToScreen(currentScreen + 1);
                } else {
                    snapToDestination();
                }
                if (velocityTracker != null) {
                    velocityTracker.recycle();
                    velocityTracker = null;
                }
                break;
        }
        return true;
    }

    private boolean IsCanMove(int deltaX) {
        // deltaX < 0 == swipe right
        if (getScrollX() <= 0 && deltaX < 0) {
            return false;
        }
        // deltaX > 0 == swipe left
        if (getScrollX() >= (getChildCount() - 1) * getWidth() && deltaX > 0) {
            return false;
        }
        return true;
    }

    public void SetOnViewChangeListener(OnViewChangeListener listener) {
        onViewChangeListener = listener;
    }

}

interface OnViewChangeListener {
    public void OnViewChange(int View);
}
