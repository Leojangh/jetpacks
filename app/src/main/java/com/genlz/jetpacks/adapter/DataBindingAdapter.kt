package com.genlz.jetpacks.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.view.*
import androidx.databinding.BindingAdapter
import coil.load

private const val TAG = "DataBindingAdapter"

@BindingAdapter("srcUri")
fun loadFromUri(img: ImageView, uri: String) {
    img.load(uri)
}

@BindingAdapter("identified")
fun isIdentifiedUser(imageView: ImageView, identified: Boolean) {
    imageView.isVisible = identified
}

@BindingAdapter("layout_marginTop")
fun View.adjustMarginTop(top: Int) {
    updateLayoutParams<ViewGroup.MarginLayoutParams> {
        topMargin = top + marginTop
    }
}

/**
 * @param ratio The ratio of width versus height.
 */
@BindingAdapter("aspect_ratio")
fun View.adjustRatio(ratio: Float) = post {
    var isWidth = true
    val edge = if (width == 0) {
        isWidth = false
        height
    } else {
        width
    }
    if (isWidth) {
        val newHeight = edge / ratio
        updateLayoutParams {
            width = edge
            height = newHeight.toInt()
        }
    } else {
        val newWidth = edge * ratio
        updateLayoutParams {
            width = newWidth.toInt()
            height = edge
        }
    }
}

///**
// * Make it possible that using format strings in XML.
// */
//@BindingAdapter("text")
//fun TextView.setTextExt(@StringRes stringResId: Int, vararg formatArgs: Any?) {
//    text = context.getString(stringResId, formatArgs)
//}
