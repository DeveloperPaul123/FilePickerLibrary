package com.devpaul.materialfabmenu.utils;

import android.animation.ArgbEvaluator;
import android.graphics.Paint;
import android.support.v4.view.MotionEventCompat;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Pauly D on 3/13/2015.
 */
public class SelectorGenerator {

    ArgbEvaluator evaluator;
    private int mNormalColor, mPressedColor, paintColor;
    private View mView;
    private Paint mPaint;

    private static long ANIM_DURATION = 150;

    /**
     * Class that generates a selector for buttons and other views.
     * @param view the view to apply to.
     * @param paint the paint to apply this effect to.
     */
    public SelectorGenerator(View view, Paint paint) {
        this.mView = view;
        this.mPaint = paint;
        evaluator = new ArgbEvaluator();
    }

    /**
     * Set the normal color for the selector.
     * @param color
     */
    public void setNormalColor(int color) {
        this.mNormalColor = color;
    }

    /**
     * Set the pressed color of the selector.
     * @param color
     */
    public void setPressedColor(int color) {
        this.mPressedColor = color;
    }

    /**
     * This method needs to be injected into the view's onDraw method.
     * @param paint, the {@code Paint} that is used to draw the view.
     */
    public void onDraw(Paint paint) {
        paint.setColor(paintColor);
    }

    /**
     * This method needs to be injected into the view's onTouchEvent method.
     * @param event, the {@code MotionEvent} of the view.
     */
    public void onTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                onDown();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                onUp();
                break;
        }
    }

    /**
     * Handles the down animation.
     */
    private void onDown() {
        ValueGeneratorAnim anim = new ValueGeneratorAnim(new ValueGeneratorAnim.InterpolatedTimeCallback() {
            @Override
            public void onTimeUpdate(float interpolatedTime) {
                paintColor = (int) evaluator.evaluate(interpolatedTime, mNormalColor, mPressedColor);
                mView.invalidate();
            }
        });
        mView.clearAnimation();
        anim.setDuration(ANIM_DURATION);
        mView.startAnimation(anim);
    }

    /**
     * Handles the up animation.
     */
    private void onUp() {
        ValueGeneratorAnim anim = new ValueGeneratorAnim(new ValueGeneratorAnim.InterpolatedTimeCallback() {
            @Override
            public void onTimeUpdate(float interpolatedTime) {
                paintColor = (int) evaluator.evaluate(interpolatedTime, mPressedColor, mNormalColor);
                mView.invalidate();
            }
        });
        mView.clearAnimation();
        anim.setDuration(ANIM_DURATION);
        mView.startAnimation(anim);
    }
}
