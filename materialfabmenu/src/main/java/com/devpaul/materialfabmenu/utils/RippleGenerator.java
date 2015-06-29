package com.devpaul.materialfabmenu.utils;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.v4.view.MotionEventCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Pauly D on 3/8/2015.
 */
public class RippleGenerator {

    private final int EASE_ANIM_DURATION = 200;
    private final int MAX_RIPPLE_ALPHA = 255;
    private final int MAX_RIPPLE_RADIUS = 300;

    private View mView;
    private RectF mDrawRect;
    private int mClipRadius;
    private Path mCirclePath = new Path();
    private boolean hasRippleEffect = false;
    private int mEffectColor = Color.LTGRAY;
    private int mMaxRadius = MAX_RIPPLE_RADIUS;
    private int mMaxAlpha = MAX_RIPPLE_ALPHA;
    private float mCx, mCy, mCradius;
    private boolean isCircle;

    private int animDuration = EASE_ANIM_DURATION;
    private AnimationSet mAnimationSet = null;

    private ArrayList<RippleWave> mWavesList = new ArrayList<RippleWave>();
    private RippleWave mCurrentWave = null;

    public RippleGenerator(View mView) {
        this.mView = mView;
        this.mDrawRect = new RectF(0, 0, mView.getWidth(), mView.getHeight());
        this.mAnimationSet = new AnimationSet(true);
    }

    public RippleGenerator(View view, int color, int duration, boolean hasRipple) {
        this.mView = view;
        this.mDrawRect = new RectF(0, 0, mView.getWidth(), mView.getHeight());
        this.mAnimationSet = new AnimationSet(true);
        this.mEffectColor = color;
        this.animDuration = duration;
        this.hasRippleEffect = hasRipple;
    }

    public void setHasRippleEffect(boolean hasRippleEffect) {
        this.hasRippleEffect = hasRippleEffect;
    }

    public void setAnimDuration(int animDuration) {
        this.animDuration = animDuration;
    }

    public void setEffectColor(int effectColor) {
        mEffectColor = effectColor;
    }

    public void setClipRadius(int mClipRadius) {
        this.mClipRadius = mClipRadius;
    }

    public void setRippleSize(int radius) {
        mMaxRadius = radius;
    }

    public void setMaxAlpha(int alpha) {
        mMaxAlpha = alpha;
    }

    public void setIsCircleView(float cx, float cy, float radius) {
        this.isCircle = true;
        this.mCx = cx;
        this.mCy = cy;
        this.mCradius = radius;
    }

    public View getView() {
        return mView;
    }

    public void onTouchEvent(final MotionEvent event) {
        if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_CANCEL) {
            mCurrentWave.fadeOutWave();
        } else if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_UP) {
            mCurrentWave.fadeOutWave();
        } else if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
            mCurrentWave = new RippleWave(mView, mAnimationSet, animDuration, mEffectColor);
            mCurrentWave.setMaxAlpha(mMaxAlpha);
            mCurrentWave.setWaveMaxRadius(mMaxRadius);
            mCurrentWave.startWave(event.getX(), event.getY());
            mWavesList.add(mCurrentWave);
        } else if(MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_MOVE) {
            if(mCurrentWave != null) {
                mCurrentWave.startWave(event.getX(), event.getY());
            }
        }
    }

    public void onDraw(final Canvas canvas) {

        if (hasRippleEffect && mWavesList.size() > 0) {
            if(isCircle) {
                mDrawRect.set(mCx - mCradius, mCy-mCradius, mCx + mCradius, mCy + mCradius);
            } else {
                mDrawRect.set(0, 0, mView.getWidth(), mView.getHeight());
            }
            mCirclePath.reset();
            mCirclePath.addRoundRect(this.mDrawRect,
                    mClipRadius, mClipRadius, Path.Direction.CW);
            canvas.clipPath(mCirclePath);

            // Draw ripples
            for (Iterator<RippleWave> iterator = mWavesList.iterator(); iterator.hasNext();) {
                RippleWave wave = iterator.next();
                if (wave.isFinished()) {
                    iterator.remove();
                } else {
                    wave.onDraw(canvas);
                }
            }
        }

    }

    private class RippleWave {
        private final float opacityDecayVelocity = 1.2f;
        private int waveMaxRadius = MAX_RIPPLE_RADIUS;
        private boolean waveFinished = false;

        private AnimationSet mAnimationSet = new AnimationSet(true);
        private View mView;

        private int animDuration = EASE_ANIM_DURATION;
        private int mCircleAlpha = MAX_RIPPLE_ALPHA;
        private int mMaxAlpha = MAX_RIPPLE_ALPHA;
        private Paint mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private float mDownX;
        private float mDownY;
        private float mRadius;

        private Animation.AnimationListener radiusAnimationListener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        };

        private Animation.AnimationListener opacityAnimationListener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                waveFinished = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        };

        public RippleWave(View view, AnimationSet animationSet, int animDuration, int effectColor) {
            this.mView = view;
            this.mAnimationSet = animationSet;
            this.animDuration = animDuration;
            this.mCirclePaint.setColor(effectColor);
        }

        private int waveOpacity(float interpolatedTime) {
            return (int)Math.max(0, mMaxAlpha - (mMaxAlpha * interpolatedTime * this.opacityDecayVelocity));
        }

        private float waveRadius(float interpolatedTime) {
            float radius = waveMaxRadius * 1.1f + 5;
            return radius * (1 - (float)Math.pow(80, -interpolatedTime));
        }

        /**
         * Get the new radius for when the person lets go of the
         * @param interpolatedTime
         * @return
         */
        private float waveFinishRadius(float interpolatedTime) {
            if(isCircle) {
                return (mCradius - mMaxRadius) * interpolatedTime + mRadius;
            } else {
                return mMaxRadius * 2 * interpolatedTime;
            }
        }

        public boolean isFinished() {
            return waveFinished;
        }

        /**
         * Start the wave.
         * @param x the x coordinate of the start point for the wave.
         * @param y the y coordinate of the start point for the wave.
         */
        public void startWave(float x, float y) {
            mDownX = x;
            mDownY = y;

            mCircleAlpha = mMaxAlpha;

            ValueGeneratorAnim valueGeneratorAnim = new ValueGeneratorAnim(new ValueGeneratorAnim.InterpolatedTimeCallback() {
                @Override
                public void onTimeUpdate(float interpolatedTime) {
                    mRadius = RippleWave.this.waveRadius(interpolatedTime);
                    mView.invalidate();
                }
            });
            valueGeneratorAnim.setInterpolator(new DecelerateInterpolator());
            valueGeneratorAnim.setDuration(animDuration/2);
            valueGeneratorAnim.setAnimationListener(radiusAnimationListener);
            mAnimationSet.addAnimation(valueGeneratorAnim);
            if (!mAnimationSet.hasStarted() || mAnimationSet.hasEnded()) {
                mView.startAnimation(mAnimationSet);
            }
        }

        /**
         * Fade out the wave.
         */
        public void fadeOutWave() {
            ValueGeneratorAnim valueGeneratorAnim = new ValueGeneratorAnim(new ValueGeneratorAnim.InterpolatedTimeCallback() {
                @Override
                public void onTimeUpdate(float interpolatedTime) {
                    mCircleAlpha = RippleWave.this.waveOpacity(interpolatedTime);
                    mRadius = RippleWave.this.waveFinishRadius(interpolatedTime);
                    mView.invalidate();
                }
            });
            valueGeneratorAnim.setDuration(animDuration);
            valueGeneratorAnim.setAnimationListener(opacityAnimationListener);

            // If all animations stopped
            if (mAnimationSet.hasEnded()) {
                mAnimationSet.getAnimations().clear();
                mAnimationSet.addAnimation(valueGeneratorAnim);
                mView.startAnimation(mAnimationSet);
            } else {
                // Add new animation to current set
                mAnimationSet.addAnimation(valueGeneratorAnim);
            }
        }

        public void onDraw(final Canvas canvas) {
            mCirclePaint.setAlpha(mCircleAlpha);
            canvas.drawCircle(mDownX, mDownY, mRadius, mCirclePaint);
        }

        public void setWaveMaxRadius(int radius) {
            waveMaxRadius = radius;
        }

        public void setMaxAlpha(int alpha) {
            mMaxAlpha = alpha;
        }
    }

    public static class Builder {

        private static View view;
        private static int mColor, mDuration;
        private static boolean hasRipple;

        public Builder(View view) {
            Builder.view = view;
        }

        public static void setRippleColor(int color) {
            Builder.mColor = color;
        }

        public static void setAniationDuration(long duration) {
           Builder.mDuration = (int) duration;
        }

        public static void setHasRipple(boolean ripple) {
            Builder.hasRipple = ripple;
        }

        public static RippleGenerator build() {
            return new RippleGenerator(view, mColor, mDuration, hasRipple);
        }
    }
}
