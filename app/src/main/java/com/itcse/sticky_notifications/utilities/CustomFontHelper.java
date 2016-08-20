package com.itcse.sticky_notifications.utilities;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import com.itcse.sticky_notifications.R;

/**
 * Class used to help loading font.
 */
public class CustomFontHelper {

    /**
     * Sets a font on a textview based on the custom com.my.package:font attribute
     * If the custom font attribute isn't found in the attributes nothing happens
     * @param textview TextView on which we want to set font
     * @param context Context object
     * @param attrs AttributeSet containing attributes of TextView
     */
    public static void setCustomFont(TextView textview, Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.CustomFont);
        String fonts = a.getString(R.styleable.CustomFont_fontName);
        // If we haven't added any font through XML, load regular font
        if (TextUtils.isEmpty(fonts)) {
            fonts = context.getString(R.string.font_open_sans_regular);
        }
        fonts = fonts + ".ttf";
        a.recycle();
        setCustomFont(textview, fonts, context);
    }

    /**
     * Sets a font on a textview
     * @param textview TextView instance on which we want to load font
     * @param font  String containing font name to be set on TextView
     * @param context Context instance
     */
    public static void setCustomFont(TextView textview, String font, Context context) {
        if(font == null) {
            return;
        }
        Typeface tf = FontCache.get(font, context);
        if(tf != null) {
            textview.setTypeface(tf);
        }
    }

}
