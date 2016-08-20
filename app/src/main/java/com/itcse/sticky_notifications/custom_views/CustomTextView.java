package com.itcse.sticky_notifications.custom_views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.itcse.sticky_notifications.utilities.CustomFontHelper;

/**
 * Custom TextView class used for loading fonts through XML.
 */
public class CustomTextView extends TextView {

    public CustomTextView(Context context) {
        super(context);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        CustomFontHelper.setCustomFont(this, context, attrs);
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        CustomFontHelper.setCustomFont(this, context, attrs);
    }

}
