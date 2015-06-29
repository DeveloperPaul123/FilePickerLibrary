package com.devpaul.materialfabmenu.utils;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.OvershootInterpolator;

/**
 * Created by Pauly D on 3/9/2015.
 */
public class ShadowGenerator {

    private View mView;
    private Paint shadowPaint;
    private int mShadowColor,shadowColor;
    private static final float MIN_ELEVATION = 0.0f;
    private static final float DEFAULT_ELEVATION = 0.2f;
    private static final float MAX_ELEVATION = 0.92f;
    private static final float MIN_SHADOW_ALPHA = 0.1f;
    private static final float MAX_SHADOW_ALPHA = 0.4f;
    private float elevation, mShadowRadius, minShadowOffset, maxShadowOffset,
            mShadowOffset, maxShadowSize, minShawdowSize, mShadowAlpha;

    private boolean isFlat;
    private AnimationSet animationSet;

    public ShadowGenerator(View view, Paint paint) {
        this.mView = view;
        this.shadowPaint = paint;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mView.setLayerType(View.LAYER_TYPE_SOFTWARE, shadowPaint);
        }

        animationSet = new AnimationSet(true);
        animationSet.setInterpolator(interpolator);
        init();
    }

    public void setMaxShadowOffset(float maxShadowOffset) {
        this.maxShadowOffset = maxShadowOffset;
    }

    public void setMinShadowOffset(float minShadowOffset) {
        this.minShadowOffset = minShadowOffset;
        this.mShadowOffset = minShadowOffset;
    }

    public void setMaxShadowSize(float maxShadowSize) {
        this.maxShadowSize = maxShadowSize;
    }

    public void setMinShawdowSize(float minShawdowSize) {
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
        mView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                Log.i("ShadowGenerator", "Attached");
                setElevation(elevation);
            }
            @Override
            public void onViewDetachedFromWindow(View v) {
            }
        });
        isFlat = false;
    }

    /**
     * Call this in the views onDraw method.
     * @param paint, the paint to draw with.
     */
    public void onDraw(Paint paint) {
        shadowPaint.setShadowLayer(mShadowRadius, 0.0f, mShadowOffset, shadowColor);
    }

    /**
     * Inject this into the view onTouchEvent to handle the touch event.
     * @param event
     */
    public void onTouchEvent(final MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if(!isFlat)
                elevate();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                lower();
                break;
        }
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
                ShadowGenerator.this.setFlattenElevation(1 - interpolatedTime);
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
     */
    public void unflatten() {
        ValueGeneratorAnim anim = new ValueGeneratorAnim(new ValueGeneratorAnim.InterpolatedTimeCallback() {
            @Override
            public void onTimeUpdate(float interpolatedTime) {
                ShadowGenerator.this.setFlattenElevation(interpolatedTime);
            }
        });
        anim.setDuration(100);
        ValueGeneratorAnim next = new ValueGeneratorAnim(new ValueGeneratorAnim.InterpolatedTimeCallback() {
            @Override
            public void onTimeUpdate(float interpolatedTime) {
                ShadowGenerator.this.setElevation(interpolatedTime);
            }
        });
        next.setDuration(110);
        next.setInterpolator(interpolator);

        ValueGeneratorAnim theEnd = new ValueGeneratorAnim(new ValueGeneratorAnim.InterpolatedTimeCallback() {
            @Override
            public void onTimeUpdate(float interpolatedTime) {
                ShadowGenerator.this.setElevation(1.0f-interpolatedTime);
            }
        });
        theEnd.setDuration(150);
        animationSet.cancel();
        animationSet.setInterpolator(accelDecel);
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
                ShadowGenerator.this.setElevation(interpolatedTime);
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
                ShadowGenerator.this.setElevation(interpolatedTime);
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
                ShadowGenerator.this.setElevation(1.0f-interpolatedTime);
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
        mView.invalidate();
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
        mView.invalidate();
    }

    private float getElevation() {
        return this.elevation;
    }

}
