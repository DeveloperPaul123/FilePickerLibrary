package com.devpaul.materialfabmenu.utils;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
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
 * Created by Pauly D on 3/12/2015.
 * Class that can be injected into any view to easily draw a shadow and a ripple effect.
 */
public class ShadowRippleGenerator{

    private View mView;
    private Paint shadowPaint;
    private int mShadowColor,shadowColor, rippleColor, rippleAlpha, effectColor;
    private static final float MIN_ELEVATION = 0.0f;
    private static final float DEFAULT_ELEVATION = 0.2f;
    private static final float MAX_ELEVATION = 0.92f;
    private static final float MIN_SHADOW_ALPHA = 0.1f;
    private static final float MAX_SHADOW_ALPHA = 0.4f;

    private static final int MIN_RIPPLE_ALPHA = 100;
    private float elevation, mShadowRadius, minShadowOffset, maxShadowOffset,
            mShadowOffset, maxShadowSize, minShawdowSize, mShadowAlpha, clipRadius
            ,cx, cy, rectRadius, mRadius, maxRippleRadius, touchX, touchY, endRippleRadius;

    private boolean isFlat, isCircle, isOutsideView;
    private AnimationSet animationSet;

    private Paint circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Path circlePath = new Path();
    private RectF drawRect;

    public ShadowRippleGenerator(View view, Paint paint) {
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

    public void setClipRadius(float clipRadius) {
        this.clipRadius = clipRadius;
    }

    public void setIsCircleView(float cx, float cy, float radius) {
        this.cx = cx;
        this.cy = cy;
        this.rectRadius = radius;
    }

    public void setMaxRippleRadius(float rippleRadius) {
        this.maxRippleRadius = rippleRadius;
        this.endRippleRadius = rippleRadius*2.1f;
    }

    public void setRippleColor(int rippleColor) {
        this.rippleColor = ColorUtils.getNewColorAlpha(rippleColor, rippleAlpha);
        this.effectColor = rippleColor;
        circlePaint.setColor(rippleColor);
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
                mView.invalidate();
            }
            @Override
            public void onViewDetachedFromWindow(View v) {
            }
        });
        isFlat = false;
        rippleAlpha = MIN_RIPPLE_ALPHA;
        mRadius = 0.0f;
        drawRect = new RectF(0.0f, 0.0f, 20.0f, 20.0f);
        isOutsideView = false;
        endRippleRadius = 0.0f;
    }

    /**
     * Draw the shadow layer.
     * @param paint, the paint to draw the shadow layer on.
     */
    public void onDrawShadow(Paint paint) {
        shadowPaint.setShadowLayer(mShadowRadius, 0.0f, mShadowOffset, shadowColor);
    }

    /**
     * Call this in the views onDraw method.
     */
    public void onDrawRipple(Canvas canvas) {
        if(isOutsideView) {
            circlePath.reset();
            drawRect.set(cx - rectRadius, cy - rectRadius, cx + rectRadius, cy + rectRadius);
            circlePath.addRoundRect(drawRect, clipRadius, clipRadius, Path.Direction.CW);
            canvas.clipPath(circlePath);
            canvas.drawCircle(touchX, touchY, mRadius, circlePaint);
        }
    }

    /**
     * Inject this into the view onTouchEvent to handle the touch event.
     * @param event, {@code MotionEvent} from the custom view.
     */
    public boolean onTouchEvent(final MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);
        drawRect.set(cx - rectRadius, cy - rectRadius, cx + rectRadius, cy + rectRadius);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                touchX = event.getX();
                touchY = event.getY();
//                endRippleRadius = touchY > touchX ? Math.max(Math.abs(touchY - drawRect.top), Math.abs(touchY - drawRect.bottom))
//                        : Math.max(Math.abs(touchX - drawRect.left), Math.abs(touchX - drawRect.right));
//                endRippleRadius *= 2.5f;
                isOutsideView = drawRect.contains(touchX, touchY);
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
                ShadowRippleGenerator.this.setFlattenElevation(1 - interpolatedTime);
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
                ShadowRippleGenerator.this.setFlattenElevation(interpolatedTime);
                mView.invalidate();
            }
        });
        anim.setDuration(50);
        ValueGeneratorAnim next = new ValueGeneratorAnim(new ValueGeneratorAnim.InterpolatedTimeCallback() {
            @Override
            public void onTimeUpdate(float interpolatedTime) {
                ShadowRippleGenerator.this.setElevation(interpolatedTime);
                mView.invalidate();
            }
        });
        next.setInterpolator(interpolator);
        next.setDuration(50);
        ValueGeneratorAnim theEnd = new ValueGeneratorAnim(new ValueGeneratorAnim.InterpolatedTimeCallback() {
            @Override
            public void onTimeUpdate(float interpolatedTime) {
                ShadowRippleGenerator.this.setElevation(1.0f-interpolatedTime);
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
        rippleAlpha = MIN_RIPPLE_ALPHA;
        rippleColor = ColorUtils.getNewColorAlpha(effectColor, rippleAlpha);
        circlePaint.setColor(rippleColor);
        ValueGeneratorAnim anim = new ValueGeneratorAnim(new ValueGeneratorAnim.InterpolatedTimeCallback() {
            @Override
            public void onTimeUpdate(float interpolatedTime) {
                if(!isFlat)
                ShadowRippleGenerator.this.setElevation(interpolatedTime);
                mRadius = maxRippleRadius*interpolatedTime;
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
                ShadowRippleGenerator.this.setElevation(interpolatedTime);
                mRadius = maxRippleRadius*interpolatedTime;
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
                if(!isFlat) ShadowRippleGenerator.this.setElevation(1.0f-interpolatedTime);
                float dif = 0.0f;
                if(mRadius < maxRippleRadius) {
                    dif = maxRippleRadius - mRadius;
                }
                mRadius = (endRippleRadius - maxRippleRadius - dif)*interpolatedTime + (maxRippleRadius - dif);
                rippleAlpha = (int) (MIN_RIPPLE_ALPHA * (1.0f - interpolatedTime));
                rippleColor = ColorUtils.getNewColorAlpha(effectColor, rippleAlpha);
                circlePaint.setColor(rippleColor);
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
