/**
 * @author TzuKru
 */

package com.tzukru.dev.util;

import android.content.res.AssetManager;
import android.graphics.Typeface;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class TwitterConstants {

    public static String TZUKRU = "tzukru";
    public static String CHANNEL = "channel";

    public static final DateFormat FULL_DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

    public static final Typeface getHelveticaTF(AssetManager assetManager){
        return Typeface.createFromAsset(assetManager, "fonts/HelveticaNeueUltraLight.ttf");
    }
}
