package com.itcse.sticky_notifications.custom_views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

import com.itcse.sticky_notifications.utilities.CustomFontHelper;

/**
 * Class used to load custom EditText which loads font directly from XML.
 */
public class CustomEditText extends EditText {
    public CustomEditText(Context context) {
        super(context);
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        CustomFontHelper.setCustomFont(this, context, attrs);
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        CustomFontHelper.setCustomFont(this, context, attrs);
    }
}
