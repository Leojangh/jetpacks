@file:Suppress("NOTHING_TO_INLINE", "UNUSED") // Aliases to other public API.

package com.genlz.share.util.appcompat

import androidx.core.graphics.Insets
import androidx.core.view.WindowInsetsCompat

/**
 * Note:They can't combine with another result:
 *
 * ```kotlin
 * windowInsetsCompat.imeInsets.bottom + windowInsetsCompat.navigationBarInsets.bottom.
 * ```
 *
 * is error-prone. Use this individually.
 *
 */
inline val WindowInsetsCompat.systemBarInsets get() = getInsets(WindowInsetsCompat.Type.systemBars())

/**
 * Note:They can't combine with another result:
 *
 * ```kotlin
 * windowInsetsCompat.imeInsets.bottom + windowInsetsCompat.navigationBarInsets.bottom.
 * ```
 *
 * is error-prone. Use this individually.
 *
 */
inline val WindowInsetsCompat.statusBarInsets get() = getInsets(WindowInsetsCompat.Type.statusBars())

/**
 * Note:They can't combine with another result:
 *
 * ```kotlin
 * windowInsetsCompat.imeInsets.bottom + windowInsetsCompat.navigationBarInsets.bottom.
 * ```
 *
 * is error-prone. Use this individually.
 *
 */
inline val WindowInsetsCompat.imeInsets get() = getInsets(WindowInsetsCompat.Type.ime())

/**
 * Note:They can't combine with another result:
 *
 * ```kotlin
 * windowInsetsCompat.imeInsets.bottom + windowInsetsCompat.navigationBarInsets.bottom.
 * ```
 *
 * is error-prone. Use this individually.
 *
 */
inline val WindowInsetsCompat.navigationBarInsets get() = getInsets(WindowInsetsCompat.Type.navigationBars())

/**
 * Note:They can't combine with another result:
 *
 * ```kotlin
 * windowInsetsCompat.imeInsets.bottom + windowInsetsCompat.navigationBarInsets.bottom.
 * ```
 *
 * is error-prone. Use this individually.
 *
 */
inline val WindowInsetsCompat.systemGesturesInsets get() = getInsets(WindowInsetsCompat.Type.systemGestures())

/**
 * Note:They can't combine with another result:
 *
 * ```kotlin
 * windowInsetsCompat.imeInsets.bottom + windowInsetsCompat.navigationBarInsets.bottom.
 * ```
 *
 * is error-prone. Use this individually.
 *
 */
inline val WindowInsetsCompat.mandatorySystemGesturesInsets get() = getInsets(WindowInsetsCompat.Type.mandatorySystemGestures())

/**
 * Note:They can't combine with another result:
 *
 * ```kotlin
 * windowInsetsCompat.imeInsets.bottom + windowInsetsCompat.navigationBarInsets.bottom.
 * ```
 *
 * is error-prone. Use this individually.
 *
 */
inline val WindowInsetsCompat.captionBarInsets get() = getInsets(WindowInsetsCompat.Type.captionBar())

/**
 * Note:They can't combine with another result:
 *
 * ```kotlin
 * windowInsetsCompat.imeInsets.bottom + windowInsetsCompat.navigationBarInsets.bottom.
 * ```
 *
 * is error-prone. Use this individually.
 *
 */
inline val WindowInsetsCompat.displayCutoutInsets get() = getInsets(WindowInsetsCompat.Type.displayCutout())

operator fun Insets.plus(insets: Insets) = Insets.add(this, insets)

operator fun Insets.minus(insets: Insets) = Insets.subtract(this, insets)
