package com.crowdin.platform;

import android.content.Context;
import android.content.ContextWrapper;
import android.view.LayoutInflater;

import com.crowdin.platform.repository.StringDataManager;
import com.crowdin.platform.transformers.ViewTransformerManager;

/**
 * Main Crowdin context wrapper which wraps the context for providing another layout inflater & resources.
 */
class CrowdinContextWrapper extends ContextWrapper {

    private CrowdinLayoutInflater layoutInflater;
    private ViewTransformerManager viewTransformerManager;

    public static CrowdinContextWrapper wrap(Context context,
                                             StringDataManager stringDataManager,
                                             ViewTransformerManager viewTransformerManager) {
        return new CrowdinContextWrapper(context, stringDataManager, viewTransformerManager);
    }

    private CrowdinContextWrapper(Context base,
                                  StringDataManager stringDataManager,
                                  ViewTransformerManager viewTransformerManager) {
        super(new CustomResourcesContextWrapper(base, new CrowdinResources(base.getResources(), stringDataManager)));
        this.viewTransformerManager = viewTransformerManager;
    }

    @Override
    public Object getSystemService(String name) {
        if (LAYOUT_INFLATER_SERVICE.equals(name)) {
            if (layoutInflater == null) {
                layoutInflater = new CrowdinLayoutInflater(LayoutInflater.from(getBaseContext()),
                        this, viewTransformerManager, true);
            }
            return layoutInflater;
        }

        return super.getSystemService(name);
    }
}