package com.devpaul.materialfabmenu.utils;

import android.graphics.Color;

import java.util.Random;

/**
 * Created by Pauly D on 2/22/2015.
 * Class that does simple color transformations and variations.
 */
public class ColorUtils {

    /**
     * Gets a darker color.
     * @param color the color to darken
     * @return the newly darkened color.
     */
    public static int getDarkerColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.80f*hsv[2];
        if(hsv[2] > 1) {
            hsv[2] = 1.0f;
        } else if(hsv[2] < 0) {
            hsv[2] = 0.0f;
        }
        return Color.HSVToColor(hsv);
    }

    /**
     * Gets a lighter color.
     * @param color the color to lighten.
     * @return the newly lightened color.
     */
    public static int getLigherColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        float value = 1.0f - 0.6f*(1.0f - hsv[2]);
        if(value > 1) {
            hsv[2] = 1.0f;
        } else if(value < 0) {
            hsv[2] = 0.0f;
        } else {
            hsv[2] = value;
        }
        return Color.HSVToColor(hsv);
    }

    /**
     * Alters the alpha of a given color and spits out the new color.
     * @param color the color to change the alpha of.
     * @param value the new value of the alpha field.
     * @return the new color with the new alpha level.
     */
    public static int getNewColorAlpha(int color, float value) {
        int alpha = Math.round(Color.alpha(color)*value);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        return Color.argb(alpha, r, g, b);
    }

    /**
     * Alters the alpha of the given color without multiplying the current value.
     * The current alpha value is replaced by the new one. See {#getNewColorAlpha} to see
     * how to get a new alpha color by multiplying the current value by the new value.
     * @param color the color to alter.
     * @param value the new alpha value.
     * @return the new color.
     */
    public static int getNewColorAlphaNoMult(int color, float value) {
        int alpha = Math.round(value);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        return Color.argb(alpha, r, g, b);
    }

    /**
     * Generates a random number. See {#getRandomColors}
     * @return the new random number.
     */
    public static int getRandomColor() {
        int[] color = getRandomColors(1);
        return color[0];
    }

    /**
     * Generates an array of random colors given the number of colors to create.
     * @param numOfColors, the number of colors to create.
     * @return an array of the random colors.
     */
    public static int[] getRandomColors(int numOfColors) {
        Random random = new Random();
        int colors[] = new int[numOfColors];
        for(int i = 0; i < numOfColors; i++) {
            int r = random.nextInt(256);
            int g = random.nextInt(256);
            int b = random.nextInt(256);

            if((r+g+b) > 450) {
                r  = 110;
                b = 110;
                g = 110;
            }
            colors[i] = Color.rgb(r, g, b);
        }
        return colors;
    }
}
