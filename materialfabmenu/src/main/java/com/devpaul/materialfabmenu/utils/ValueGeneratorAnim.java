package com.devpaul.materialfabmenu.utils;

import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by Pauly D on 3/12/2015.
 */
public class ValueGeneratorAnim extends Animation {

    private InterpolatedTimeCallback interpolatedTimeCallback;

    ValueGeneratorAnim(InterpolatedTimeCallback interpolatedTimeCallback) {
        this.interpolatedTimeCallback = interpolatedTimeCallback;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        this.interpolatedTimeCallback.onTimeUpdate(interpolatedTime);
    }

    public interface InterpolatedTimeCallback {
        public void onTimeUpdate(float interpolatedTime);
    }
}
