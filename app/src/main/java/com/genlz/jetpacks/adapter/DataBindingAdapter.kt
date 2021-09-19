package com.genlz.jetpacks.adapter

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.load

@BindingAdapter("srcUri")
fun loadFromUri(img: ImageView, uri: String) {
    img.load(uri)
}

@BindingAdapter("identified")
fun isIdentifiedUser(imageView: ImageView, identified: Boolean) {
    imageView.visibility = if (identified) View.VISIBLE else View.INVISIBLE
}