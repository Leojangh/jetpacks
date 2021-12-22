@file:Suppress("NOTHING_TO_INLINE")

package com.genlz.share.util

import java.lang.ref.Reference
import kotlin.reflect.KProperty

/**
 * The delegate for Java [Reference].
 */
inline operator fun <T> Reference<T>.getValue(thisRef: Any, property: KProperty<*>) = get()