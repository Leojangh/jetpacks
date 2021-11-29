package com.genlz.share.util

import java.lang.ref.Reference
import kotlin.reflect.KProperty

/**
 * The delegate for Java [Reference].
 */
operator fun <T> Reference<T>.getValue(thisRef: T, property: KProperty<*>) = get()