package com.itcse.sticky_notifications.custom_views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;

import com.itcse.sticky_notifications.utilities.CustomFontHelper;

/**
 * Custom CheckBox class used to load fonts from XML.
 */
public class CustomCheckBox extends CheckBox {
    public CustomCheckBox(Context context) {
        super(context);
    }

    public CustomCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        CustomFontHelper.setCustomFont(this, context, attrs);
    }

    public CustomCheckBox(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        CustomFontHelper.setCustomFont(this, context, attrs);
    }
}
