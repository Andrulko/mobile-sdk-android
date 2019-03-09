package com.crowdin.platform.utils

import android.content.res.Resources
import android.util.AttributeSet
import android.view.Menu
import android.view.View

object TextUtils {

    fun getTextForAttribute(attrs: AttributeSet, index: Int, view: View, resources: Resources): String? {
        var text: String? = null
        val value = attrs.getAttributeValue(index)
        if (value != null && value.startsWith("@")) {
            text = resources.getString(attrs.getAttributeResourceValue(index, 0))
        }

        return text
    }

    fun updateMenuItemsText(menu: Menu, resources: Resources, resId: Int) {
        val itemStrings = XmlParserUtils.getMenuItemsStrings(resources, resId)

        for (i in 0 until itemStrings.size()) {
            val itemKey = itemStrings.keyAt(i)
            val itemValue = itemStrings.valueAt(i)

            if (itemValue.title != 0) {
                menu.findItem(itemKey).title = resources.getString(itemValue.title)
            }
            if (itemValue.titleCondensed != 0) {
                menu.findItem(itemKey).titleCondensed = resources.getString(itemValue.titleCondensed)
            }
        }
    }
}
