@file:Suppress("NOTHING_TO_INLINE", "UNUSED") // Aliases to other public API.

package com.genlz.jetpacks.utility.appcompat

import androidx.core.view.WindowInsetsCompat

inline val WindowInsetsCompat.systemBarInsets get() = getInsets(WindowInsetsCompat.Type.systemBars())
inline val WindowInsetsCompat.statusBarInsets get() = getInsets(WindowInsetsCompat.Type.statusBars())
inline val WindowInsetsCompat.imeInsets get() = getInsets(WindowInsetsCompat.Type.ime())
inline val WindowInsetsCompat.navigationBarInsets get() = getInsets(WindowInsetsCompat.Type.navigationBars())
inline val WindowInsetsCompat.systemGesturesInsets get() = getInsets(WindowInsetsCompat.Type.systemGestures())
inline val WindowInsetsCompat.mandatorySystemGesturesInsets get() = getInsets(WindowInsetsCompat.Type.mandatorySystemGestures())
inline val WindowInsetsCompat.captionBarInsets get() = getInsets(WindowInsetsCompat.Type.captionBar())
inline val WindowInsetsCompat.displayCutoutInsets get() = getInsets(WindowInsetsCompat.Type.displayCutout())
