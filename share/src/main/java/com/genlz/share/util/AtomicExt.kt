@file:Suppress("NOTHING_TO_INLINE")

package com.genlz.share.util

import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference
import kotlin.reflect.KProperty

inline operator fun AtomicBoolean.getValue(
    thisRef: Any,
    property: KProperty<*>
) = get()

inline operator fun AtomicBoolean.setValue(
    thisRef: Any,
    property: KProperty<*>,
    value: Boolean
) = set(value)

inline operator fun AtomicInteger.getValue(
    thisRef: Any,
    property: KProperty<*>,
) = get()

inline operator fun AtomicInteger.setValue(
    thisRef: Any,
    property: KProperty<*>,
    value: Int
) = set(value)

inline operator fun AtomicInteger.inc() = apply { incrementAndGet() }
inline operator fun AtomicInteger.plus(delta: Int) = get() + delta
inline operator fun AtomicInteger.unaryPlus() = get()
inline operator fun AtomicInteger.plusAssign(delta: Int) {
    addAndGet(delta)
}

inline operator fun AtomicInteger.dec() = apply { decrementAndGet() }
inline operator fun AtomicInteger.minus(delta: Int) = get() - delta
inline operator fun AtomicInteger.unaryMinus() = -get()
inline operator fun AtomicInteger.minusAssign(delta: Int) {
    addAndGet(-delta)
}

inline operator fun AtomicLong.getValue(
    thisRef: Any,
    property: KProperty<*>,
) = get()

inline operator fun AtomicLong.setValue(
    thisRef: Any,
    property: KProperty<*>,
    value: Long
) = set(value)

inline operator fun AtomicLong.inc() = apply { incrementAndGet() }
inline operator fun AtomicLong.plus(delta: Int) = get() + delta
inline operator fun AtomicLong.unaryPlus() = get()
inline operator fun AtomicLong.plusAssign(delta: Long) {
    addAndGet(delta)
}

inline operator fun AtomicLong.dec() = apply { decrementAndGet() }
inline operator fun AtomicLong.minus(delta: Int) = get() - delta
inline operator fun AtomicLong.unaryMinus() = -get()
inline operator fun AtomicLong.minusAssign(delta: Long) {
    addAndGet(-delta)
}

/**
 * Maybe it's nonsense and unnecessary because the other methods can't be utilized through operators.
 */
inline operator fun <V> AtomicReference<V>.getValue(
    thisRef: Any,
    property: KProperty<*>,
): V = get()

/**
 * Maybe it's nonsense and unnecessary because the other methods can't be utilized through operators.
 */
inline operator fun <V> AtomicReference<V>.setValue(
    thisRef: Any,
    property: KProperty<*>,
    value: V,
) = set(value)
