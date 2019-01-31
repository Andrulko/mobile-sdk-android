package com.crowdin.platform.transformers;

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toolbar;

import com.crowdin.platform.utils.TextUtils;

/**
 * A transformer which transforms Toolbar: it transforms the text set as title.
 */
public class ToolbarTransformer implements ViewTransformerManager.Transformer {

    private static final String ATTRIBUTE_TITLE = "title";
    private static final String ATTRIBUTE_ANDROID_TITLE = "android:title";

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public Class<? extends View> getViewType() {
        return Toolbar.class;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View transform(View view, AttributeSet attrs) {
        if (view == null || !getViewType().isInstance(view)) {
            return view;
        }

        Resources resources = view.getContext().getResources();
        for (int index = 0; index < attrs.getAttributeCount(); index++) {
            String attributeName = attrs.getAttributeName(index);
            switch (attributeName) {
                case ATTRIBUTE_ANDROID_TITLE:
                case ATTRIBUTE_TITLE:
                    String title = TextUtils.getTextForAttribute(attrs, index, view, resources);
                    if (title != null) {
                        ((Toolbar) view).setTitle(title);
                    }
                    break;

                default:
                    break;
            }
        }
        return view;
    }
}