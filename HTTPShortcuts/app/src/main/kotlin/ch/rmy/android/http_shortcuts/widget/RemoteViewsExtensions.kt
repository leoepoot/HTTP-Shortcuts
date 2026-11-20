package ch.rmy.android.http_shortcuts.widget

import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.style.TypefaceSpan
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RemoteViews
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.IdRes
import androidx.annotation.RequiresApi
import ch.rmy.android.framework.extensions.tryOrLog
import ch.rmy.android.http_shortcuts.data.enums.FontWeight
import java.lang.reflect.Method

fun RemoteViews.setTextSize(@IdRes id: Int, fontSize: Float) {
    setFloatSafely(TextView::class.java, id, "setTextSize", fontSize)
}

private const val FONT_WEIGHT_TAG = "WidgetFontWeight"

fun RemoteViews.setTextViewFontWeight(@IdRes id: Int, weight: Int) {
    val familyName = FontWeight.fromWeight(weight).familyName
    try {
        val annotated = TextView::class.java
            .getMethod("setFontFamily", String::class.java)
            .isRemoteViewMethod()
        Log.i(
            FONT_WEIGHT_TAG,
            "setFontFamily: viewId=$id weight=$weight family='$familyName' annotated=$annotated sdk=${Build.VERSION.SDK_INT}",
        )
        setString(id, "setFontFamily", familyName)
    } catch (e: Throwable) {
        Log.e(FONT_WEIGHT_TAG, "setFontFamily failed: viewId=$id weight=$weight family='$familyName'", e)
    }
}

fun RemoteViews.setTextViewTextWithFontWeight(@IdRes id: Int, text: CharSequence?, weight: Int) {
    if (text == null) {
        setTextViewText(id, null)
        return
    }
    val actualWeight = FontWeight.fromWeight(weight).renderWeight
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        try {
            val baseTypeface = Typeface.create("sans-serif", Typeface.NORMAL)
            val typeface = Typeface.create(baseTypeface, actualWeight, false)
            val spannable = SpannableString.valueOf(text)
            spannable.setSpan(
                TypefaceSpan(typeface),
                0,
                spannable.length,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE,
            )
            setTextViewText(id, spannable)
            Log.i(
                FONT_WEIGHT_TAG,
                "TypefaceSpan applied: viewId=$id weight=$actualWeight sdk=${Build.VERSION.SDK_INT}",
            )
            return
        } catch (e: Throwable) {
            Log.e(FONT_WEIGHT_TAG, "TypefaceSpan failed: viewId=$id weight=$actualWeight", e)
        }
    } else {
        Log.i(FONT_WEIGHT_TAG, "TypefaceSpan skipped: sdk=${Build.VERSION.SDK_INT} < 28, viewId=$id weight=$actualWeight")
    }
    setTextViewText(id, text)
}

fun RemoteViews.setLineHeight(@IdRes id: Int, lineHeightPx: Int) {
    setIntSafely(TextView::class.java, id, "setLineHeight", lineHeightPx)
}

fun RemoteViews.setImageViewScaleX(@IdRes id: Int, scaleX: Float) {
    setFloatSafely(ImageView::class.java, id, "setScaleX", scaleX)
}

fun RemoteViews.setImageViewScaleY(@IdRes id: Int, scaleY: Float) {
    setFloatSafely(ImageView::class.java, id, "setScaleY", scaleY)
}

fun RemoteViews.setImageViewScaleType(@IdRes id: Int, scaleType: ImageView.ScaleType) {
    setIntSafely(ImageView::class.java, id, "setScaleType", scaleType.ordinal)
}

fun RemoteViews.setViewPivotX(@IdRes id: Int, pivotX: Float) {
    setFloatSafely(View::class.java, id, "setPivotX", pivotX)
}

fun RemoteViews.setViewPivotY(@IdRes id: Int, pivotY: Float) {
    setFloatSafely(View::class.java, id, "setPivotY", pivotY)
}

private fun RemoteViews.setFloatSafely(clazz: Class<*>, @IdRes id: Int, methodName: String, value: Float) {
    tryOrLog {
        val method = clazz.getMethod(methodName, Float::class.java)
        if (method.isRemoteViewMethod()) {
            setFloat(id, methodName, value)
        }
    }
}

fun RemoteViews.setIntSafely(clazz: Class<*>, @IdRes id: Int, methodName: String, value: Int) {
    tryOrLog {
        val method = clazz.getMethod(methodName, Int::class.java)
        if (method.isRemoteViewMethod()) {
            setInt(id, methodName, value)
        }
    }
}

fun RemoteViews.setBooleanSafely(clazz: Class<*>, @IdRes id: Int, methodName: String, value: Boolean) {
    tryOrLog {
        val method = clazz.getMethod(methodName, Boolean::class.java)
        if (method.isRemoteViewMethod()) {
            setBoolean(id, methodName, value)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.S)
fun RemoteViews.setColorIntSafely(clazz: Class<*>, @IdRes id: Int, methodName: String, @ColorInt value: Int) {
    tryOrLog {
        val method = clazz.getMethod(methodName, Int::class.java)
        if (method.isRemoteViewMethod()) {
            setColorInt(id, methodName, value, value)
        }
    }
}

fun RemoteViews.setColorStateListSafely(
    clazz: Class<*>,
    @IdRes id: Int,
    methodName: String,
    value: ColorStateList,
): Boolean =
    tryOrLog {
        val method = clazz.getMethod(methodName, ColorStateList::class.java)
        if (method.isRemoteViewMethod()) {
            setColorStateList(id, methodName, value)
            true
        } else {
            false
        }
    } ?: false

private fun Method.isRemoteViewMethod() =
    annotations.any { it.annotationClass.simpleName == "RemotableViewMethod" }
