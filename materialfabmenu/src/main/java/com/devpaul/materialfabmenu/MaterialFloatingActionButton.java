package com.devpaul.materialfabmenu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.devpaul.materialfabmenu.utils.ColorUtils;
import com.devpaul.materialfabmenu.utils.ShadowRippleGenerator;
import com.devpaul.materialfabmenu.utils.ShadowSelectorGenerator;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Pauly D on 2/28/2015.
 */
public class MaterialFloatingActionButton extends View {
    public static final int SIZE_NORMAL = 0;
    public static final int SIZE_MINI = 1;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({ SIZE_NORMAL, SIZE_MINI })
    public @interface FAB_SIZE {
    }

    private Paint mButtonPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint bitmapPaint;
    private float mSize, cx, cy, buttonSize;
    private int mButtonColor, mButtonPressedColor;

    boolean useSelector = false;

    private ShadowRippleGenerator shadowRippleGenerator;
    private ShadowSelectorGenerator shadowSelectorGenerator;
    private Bitmap iconBitmap;
    private BitmapDrawable drawable;
    private Rect bitmapRect;
    private RectF bitmapDrawRect;
    private float iconSize;

    @DrawableRes
    private int mIcon;
    private Drawable mIconDrawable;
    private TransitionDrawable currentDrawable;


    public MaterialFloatingActionButton(Context context) {
        super(context);
        init(context, null);
    }

    public MaterialFloatingActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MaterialFloatingActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    void init(Context context, AttributeSet attributeSet) {
        int size = 0;
        TypedArray attr = context.obtainStyledAttributes(attributeSet, R.styleable.MaterialFloatingActionButton, 0, 0);
        mButtonColor = attr.getColor(R.styleable.MaterialFloatingActionButton_mat_fab_colorNormal,
                getColor(android.R.color.holo_blue_dark));
        mButtonPressedColor = attr.getColor(R.styleable.MaterialFloatingActionButton_mat_fab_colorPressed,
                ColorUtils.getDarkerColor(mButtonColor));
        mIcon = attr.getResourceId(R.styleable.MaterialFloatingActionButton_mat_fab_icon, 0);
        useSelector = attr.getBoolean(R.styleable.MaterialFloatingActionButton_mat_fab_use_selector, false);
        size = attr.getInt(R.styleable.MaterialFloatingActionButton_mat_fab_size, 0);
        attr.recycle();

        iconSize = getDimension(R.dimen.mat_fab_icon_size);

        if(mIcon != 0) {
            iconBitmap = BitmapFactory.decodeResource(getResources(), mIcon);
            drawable = new BitmapDrawable(getResources(), iconBitmap);
            drawable.setAntiAlias(true);
            bitmapRect = new Rect(0, 0, iconBitmap.getWidth(), iconBitmap.getHeight());
            bitmapDrawRect = new RectF(0.0f, 0.0f, iconSize, iconSize);
        } else {
            iconBitmap = getDefaulBitmap();
            bitmapRect = new Rect(0, 0, iconBitmap.getWidth(), iconBitmap.getHeight());
            bitmapDrawRect = new RectF(0.0f, 0.0f, iconSize, iconSize);
        }
        float maxShadowOffset = getDimension(R.dimen.mat_fab_shadow_offset) * 1.5f;
        float minShadowOffset = maxShadowOffset / 1.5f;
        float maxShadowSize = getDimension(R.dimen.mat_fab_shadow_max_radius);
        float minShawdowSize = getDimension(R.dimen.mat_fab_shadow_min_radius) / 2;

        buttonSize = size == 0 ? getDimension(R.dimen.mat_fab_normal_size):getDimension(R.dimen.mat_fab_mini_size);
        mSize = buttonSize + maxShadowSize * 4 + maxShadowOffset * 3;

        mButtonPaint.setStyle(Paint.Style.FILL);
        mButtonPaint.setColor(mButtonColor);

        bitmapPaint = new Paint();
        bitmapPaint.setAntiAlias(true);
        bitmapPaint.setFilterBitmap(true);
        bitmapPaint.setDither(true);

        shadowRippleGenerator = new ShadowRippleGenerator(this, mButtonPaint);
        shadowRippleGenerator.setRippleColor(ColorUtils.getDarkerColor(mButtonColor));
        shadowRippleGenerator.setClipRadius((int) buttonSize/2);
        shadowRippleGenerator.setAnimationDuration(200);
        shadowRippleGenerator.setMaxRippleRadius((int) (0.75f*buttonSize/2));
        shadowRippleGenerator.setMaxShadowOffset(maxShadowOffset);
        shadowRippleGenerator.setMaxShadowSize(maxShadowSize);
        shadowRippleGenerator.setMinShadowOffset(minShadowOffset);
        shadowRippleGenerator.setMinShawdowSize(minShawdowSize);

        shadowSelectorGenerator = new ShadowSelectorGenerator(this, mButtonPaint);
        shadowSelectorGenerator.setNormalColor(mButtonColor);
        shadowSelectorGenerator.setPressedColor(mButtonPressedColor);
        shadowSelectorGenerator.setAnimationDuration(200);
        shadowSelectorGenerator.setShadowLimits(minShadowOffset, maxShadowOffset, minShawdowSize, maxShadowSize);

        invalidate();
    }

    public void setUseSelector(boolean selector) {
        this.useSelector = selector;
    }

    private Bitmap getDefaulBitmap() {
        Bitmap bitmap = Bitmap.createBitmap((int) iconSize, (int) iconSize, Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(Color.TRANSPARENT);
        Canvas canvas = new Canvas(bitmap);
        float oneDp = getDimension(R.dimen.mat_fab_single_dp);
        int halfWidthStart = (int) (canvas.getWidth()/2 - oneDp);
        int halfWidthEnd = (int) (canvas.getWidth()/2 + oneDp);
        Rect rect = new Rect(halfWidthStart, (int) oneDp*3,
                halfWidthEnd, (int) (canvas.getHeight() - oneDp*3));
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        paint.setAlpha(255);
        canvas.drawRect(rect, paint);
        rect.set((int) (oneDp*3), (int) (canvas.getHeight()/2 - oneDp),
                (int) (canvas.getWidth() - (oneDp*3)), (int) (canvas.getHeight()/2 + oneDp));
        canvas.drawRect(rect, paint);
        return bitmap;
    }

    /**
     * Returns a color from a resource id.
     * @param resId, the resource id.
     * @return the color.
     */
    private int getColor(int resId) {
        return getResources().getColor(resId);
    }

    private float getDimension(int resId) {
        return getResources().getDimension(resId);
    }

    public void setButtonColor(int color) {
        this.mButtonColor = color;
        this.mButtonPaint.setColor(mButtonColor);
        invalidate();
    }

    public void setButtonPressedColor(int color) {
        this.mButtonPressedColor = color;
    }

    public float getSize() {
        return mSize;
    }

    public void setIcon(@DrawableRes int icon) {
        if (mIcon != icon) {
            mIcon = icon;
        }
    }

    public void flatten() {
        shadowRippleGenerator.flatten();
    }

    public void unflatten() {
        shadowRippleGenerator.unflatten();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        if(!useSelector) shadowRippleGenerator.onDrawShadow(mButtonPaint);
        canvas.drawCircle(cx, cy, buttonSize / 2, mButtonPaint);
        if(iconBitmap != null) canvas.drawBitmap(iconBitmap, bitmapRect, bitmapDrawRect, bitmapPaint);
        if(useSelector) {
            shadowSelectorGenerator.onDraw(mButtonPaint);
        } else {
            shadowRippleGenerator.onDrawRipple(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(useSelector) {
            return shadowSelectorGenerator.onTouchEvent(event);
        } else {
            return shadowRippleGenerator.onTouchEvent(event);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        cx = mSize/2;
        cy = mSize/2;
        shadowRippleGenerator.setIsCircleView(cx, cy, buttonSize/2);
        float halfBitmapSize = iconSize/2;
        bitmapDrawRect.set(cx - halfBitmapSize, cy - halfBitmapSize, cx + halfBitmapSize, cy + halfBitmapSize);
        invalidate();
        setMeasuredDimension((int) mSize, (int) mSize);
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public void setBackgroundCompat(Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setBackground(drawable);
        } else {
            setBackgroundDrawable(drawable);
        }
    }

}
