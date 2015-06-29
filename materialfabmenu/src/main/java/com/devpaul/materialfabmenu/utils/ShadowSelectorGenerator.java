package com.devpaul.materialfabmenu.utils;

import android.animation.ArgbEvaluator;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.v4.view.MotionEventCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.OvershootInterpolator;

/**
 * Created by Pauly D on 3/13/2015.
 */
public class ShadowSelectorGenerator {
    private View mView;
    private Paint mPaint;
    private int mShadowColor,shadowColor, mNormalColor, mPressedColor, paintColor;
    private ArgbEvaluator argbEvaluator;
    private static final float MIN_ELEVATION = 0.0f;
    private static final float DEFAULT_ELEVATION = 0.2f;
    private static final float MAX_ELEVATION = 0.92f;
    private static final float MIN_SHADOW_ALPHA = 0.1f;
    private static final float MAX_SHADOW_ALPHA = 0.4f;
    private float elevation, mShadowRadius, minShadowOffset, maxShadowOffset,
            mShadowOffset, maxShadowSize, minShawdowSize, mShadowAlpha;

    private View.OnAttachStateChangeListener stateChangeListener = new View.OnAttachStateChangeListener() {
        @Override
        public void onViewAttachedToWindow(View v) {
            setElevation(elevation);
            mView.invalidate();
        }

        @Override
        public void onViewDetachedFromWindow(View v) {

        }
    };

    private boolean isFlat;
    private AnimationSet animationSet;

    public ShadowSelectorGenerator(View view, Paint paint) {
        this.mView = view;
        this.mPaint = paint;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mView.setLayerType(View.LAYER_TYPE_SOFTWARE, mPaint);
        }
        animationSet = new AnimationSet(true);
        animationSet.setInterpolator(interpolator);
        argbEvaluator = new ArgbEvaluator();
        init();
    }

    public void setNormalColor(int color) {
        this.mNormalColor = color;
        this.paintColor = mNormalColor;
    }

    public void setPressedColor(int color) {
        this.mPressedColor = color;
    }

    public void setShadowLimits(float minShadowOffset, float maxShadowOffset, float minShadowSize, float maxShadowSize) {
        setMinShadowOffset(minShadowOffset);
        setMaxShadowSize(maxShadowSize);
        setMinShawdowSize(minShadowSize);
        setMaxShadowOffset(maxShadowOffset);
        setElevation(DEFAULT_ELEVATION);
        mView.invalidate();
    }

    private void setMaxShadowOffset(float maxShadowOffset) {
        this.maxShadowOffset = maxShadowOffset;
    }

    private void setMinShadowOffset(float minShadowOffset) {
        this.minShadowOffset = minShadowOffset;
        this.mShadowOffset = minShadowOffset;
    }

    private void setMaxShadowSize(float maxShadowSize) {
        this.maxShadowSize = maxShadowSize;
    }

    private void setMinShawdowSize(float minShawdowSize) {
        this.minShawdowSize = minShawdowSize;
    }

    public void setAnimationDuration(long duration) {
        this.animationSet.setDuration(duration);
    }

    public float getMaxShadowOffset() {
        return maxShadowOffset;
    }

    public float getMaxShadowSize() {
        return maxShadowSize;
    }

    public float getMinShadowOffset() {
        return minShadowOffset;
    }

    public float getMinShawdowSize() {
        return minShawdowSize;
    }

    /**
     * Initialize and do the first drawing.
     */
    private void init() {
        mShadowColor = Color.BLACK;
        shadowColor = ColorUtils.getNewColorAlpha(mShadowColor, MIN_SHADOW_ALPHA);
        elevation = DEFAULT_ELEVATION;
        mShadowAlpha = (MAX_SHADOW_ALPHA - MIN_SHADOW_ALPHA)*(elevation/MAX_ELEVATION)  + MAX_SHADOW_ALPHA;
        mShadowRadius = (maxShadowSize - minShawdowSize)*(elevation/MAX_ELEVATION)  + minShawdowSize;
        mShadowOffset = (maxShadowOffset-minShadowOffset)*(elevation/MAX_ELEVATION) + minShadowOffset;
        shadowColor = ColorUtils.getNewColorAlpha(mShadowColor, mShadowAlpha);
        mView.addOnAttachStateChangeListener(stateChangeListener);
        isFlat = false;
    }

    /**
     * Draw the shadow layer.
     * @param paint, the paint to draw the shadow layer on.
     */
    public void onDraw(Paint paint) {
        mPaint.setShadowLayer(mShadowRadius, 0.0f, mShadowOffset, shadowColor);
        mPaint.setColor(paintColor);
    }

    /**
     * Inject this into the view onTouchEvent to handle the touch event.
     * @param event, {@code MotionEvent} from the custom view.
     */
    public boolean onTouchEvent(final MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                elevate();
                return true;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                lower();
                return true;
        }

        return false;
    }

    private static OvershootInterpolator interpolator = new OvershootInterpolator();
    private static AccelerateDecelerateInterpolator accelDecel = new AccelerateDecelerateInterpolator();

    /**
     * Handles the animation to flatten the view.
     */
    public void flatten() {
        ValueGeneratorAnim anim = new ValueGeneratorAnim(new ValueGeneratorAnim.InterpolatedTimeCallback() {
            @Override
            public void onTimeUpdate(float interpolatedTime) {
                ShadowSelectorGenerator.this.setFlattenElevation(1 - interpolatedTime);
                mView.invalidate();
            }
        });
        animationSet.cancel();
        animationSet.getAnimations().clear();
        animationSet.addAnimation(anim);
        mView.startAnimation(animationSet);
        isFlat = true;
    }

    /**
     * Handles the animation from flat back to normal.
     * UNDER CONSTRUCTION
     */
    public void unflatten() {
        ValueGeneratorAnim anim = new ValueGeneratorAnim(new ValueGeneratorAnim.InterpolatedTimeCallback() {
            @Override
            public void onTimeUpdate(float interpolatedTime) {
                ShadowSelectorGenerator.this.setFlattenElevation(interpolatedTime);
                mView.invalidate();
            }
        });
        anim.setDuration(50);
        ValueGeneratorAnim next = new ValueGeneratorAnim(new ValueGeneratorAnim.InterpolatedTimeCallback() {
            @Override
            public void onTimeUpdate(float interpolatedTime) {
                ShadowSelectorGenerator.this.setElevation(interpolatedTime);
                mView.invalidate();
            }
        });
        next.setInterpolator(interpolator);
        next.setDuration(50);
        ValueGeneratorAnim theEnd = new ValueGeneratorAnim(new ValueGeneratorAnim.InterpolatedTimeCallback() {
            @Override
            public void onTimeUpdate(float interpolatedTime) {
                ShadowSelectorGenerator.this.setElevation(1.0f-interpolatedTime);
                mView.invalidate();
            }
        });
        theEnd.setDuration(50);
        animationSet.cancel();
        animationSet.getAnimations().clear();
        animationSet.addAnimation(anim);
        animationSet.addAnimation(next);
        animationSet.addAnimation(theEnd);
        mView.startAnimation(animationSet);
        isFlat = false;
    }
    /**
     * Elevate the view.
     */
    private void elevate() {
        ValueGeneratorAnim anim = new ValueGeneratorAnim(new ValueGeneratorAnim.InterpolatedTimeCallback() {
            @Override
            public void onTimeUpdate(float interpolatedTime) {
                if(!isFlat)
                    ShadowSelectorGenerator.this.setElevation(interpolatedTime);
                paintColor = (int) argbEvaluator.evaluate(interpolatedTime, mNormalColor, mPressedColor);
                mView.invalidate();
            }
        });
        animationSet.cancel();
        animationSet.getAnimations().clear();
        animationSet.addAnimation(anim);
        mView.startAnimation(animationSet);
    }

    private void elevate(Animation.AnimationListener listener, long duration) {
        ValueGeneratorAnim anim = new ValueGeneratorAnim(new ValueGeneratorAnim.InterpolatedTimeCallback() {
            @Override
            public void onTimeUpdate(float interpolatedTime) {
                ShadowSelectorGenerator.this.setElevation(interpolatedTime);

            }
        });
        anim.setAnimationListener(listener);
        anim.setDuration(duration);
        animationSet.cancel();
        animationSet.getAnimations().clear();
        animationSet.addAnimation(anim);
        mView.startAnimation(animationSet);
    }

    /**
     * Lower the view.
     */
    private void lower() {
        ValueGeneratorAnim anim = new ValueGeneratorAnim(new ValueGeneratorAnim.InterpolatedTimeCallback() {
            @Override
            public void onTimeUpdate(float interpolatedTime) {
                if(!isFlat) ShadowSelectorGenerator.this.setElevation(1.0f-interpolatedTime);
                paintColor = (int) argbEvaluator.evaluate(interpolatedTime, mPressedColor, mNormalColor);
                mView.invalidate();
            }
        });
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mView.performClick();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animationSet.cancel();
        animationSet.getAnimations().clear();
        animationSet.addAnimation(anim);
        mView.startAnimation(animationSet);
    }

    private void setFlattenElevation(float interpolatedTime) {
        this.elevation = interpolatedTime;
        if(elevation > DEFAULT_ELEVATION) {
            elevation = DEFAULT_ELEVATION;
        }

        if(elevation > 0.0f) {
            this.mShadowAlpha =  (MAX_SHADOW_ALPHA - MIN_SHADOW_ALPHA)*(elevation/MAX_ELEVATION)  + MAX_SHADOW_ALPHA;
            this.mShadowRadius = (maxShadowSize - minShawdowSize)*(elevation/MAX_ELEVATION)  + minShawdowSize;
            this.mShadowOffset = (maxShadowOffset-minShadowOffset)*(elevation/MAX_ELEVATION) + minShadowOffset;
        } else {
            this.mShadowAlpha = 0.0f;
            this.mShadowRadius = 0.0f;
            this.mShadowOffset = 0.0f;
        }
        shadowColor = ColorUtils.getNewColorAlpha(mShadowColor, mShadowAlpha);
    }

    /**
     * Sets the elevation and changes the parameters of the shadow drawing.
     * @param interpolatedTime the new elevation.
     */
    private void setElevation(float interpolatedTime) {
        if(interpolatedTime == 0.0f) {
            interpolatedTime+=MIN_ELEVATION;
        } else if (interpolatedTime > MAX_ELEVATION) {
            interpolatedTime = MAX_ELEVATION;
        }
        this.elevation = interpolatedTime;
        this.mShadowAlpha = (MAX_SHADOW_ALPHA - MIN_SHADOW_ALPHA)*(elevation/MAX_ELEVATION)  + MAX_SHADOW_ALPHA;
        this.mShadowRadius = (maxShadowSize - minShawdowSize)*(elevation/MAX_ELEVATION)  + minShawdowSize;
        this.mShadowOffset = (maxShadowOffset-minShadowOffset)*(elevation/MAX_ELEVATION) + minShadowOffset;
        shadowColor = ColorUtils.getNewColorAlpha(mShadowColor, mShadowAlpha);
    }

    private float getElevation() {
        return this.elevation;
    }
}
