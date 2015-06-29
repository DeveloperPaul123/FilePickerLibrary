package com.devpaul.materialfabmenu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Pauly D on 3/21/2015.
 */
public class MaterialRecordingView extends ViewGroup {

    private MaterialFloatingActionButton fab;
    private TextView timerView;
    private float textViewWidth;
    private float textViewHeight;
    private float mHeight;
    private float mWidth;

    public MaterialRecordingView(Context context) {
        super(context);
        init(context, null);
    }

    public MaterialRecordingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MaterialRecordingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        fab = new MaterialFloatingActionButton(context);
        timerView = new TextView(context);

        mHeight = getDimension(R.dimen.mat_record_view_height);
        mWidth = getDimension(R.dimen.mat_record_view_width);

        textViewHeight = getDimension(R.dimen.mat_record_view_text_height);
        textViewWidth = getDimension(R.dimen.mat_record_view_text_width);
        timerView.setWidth((int) (textViewWidth + (0.5 * textViewWidth)));
        timerView.setHeight((int) textViewHeight);
        timerView.setGravity(Gravity.RIGHT);

        addView(fab);
        addView(timerView);
    }

    private float getDimension(int resId) {
        return getResources().getDimension(resId);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        int bLeft = getLeft();
        int bTop = getTop();

        int buttonWidth = fab.getWidth();
        int buttonHeight = fab.getHeight();

        getChildAt(0).layout(bLeft, bTop, buttonWidth, buttonHeight);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        int width = 0;
        int height = getChildAt(0).getHeight();
        width += getChildAt(0).getWidth();
        width += textViewWidth;
        setMeasuredDimension(width, height);
    }
}
