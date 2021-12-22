package com.genlz.share.util

import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import kotlin.reflect.KProperty

operator fun AtomicBoolean.getValue(
    thisRef: Any,
    property: KProperty<*>
) = get()

operator fun AtomicBoolean.setValue(
    thisRef: Any,
    property: KProperty<*>,
    value: Boolean
) = set(value)

operator fun AtomicInteger.getValue(
    thisRef: Any,
    property: KProperty<*>,
) = get()

operator fun AtomicInteger.setValue(
    thisRef: Any,
    property: KProperty<*>,
    value: Int
) = set(value)

operator fun AtomicInteger.inc(
    thisRef: Any,
    property: KProperty<*>
) = inc()

operator fun AtomicInteger.inc() = apply { incrementAndGet() }
operator fun AtomicInteger.plus(delta: Int) = get() + delta
operator fun AtomicInteger.unaryPlus() = get()
operator fun AtomicInteger.plusAssign(delta: Int) {
    addAndGet(delta)
}

operator fun AtomicInteger.dec() = apply { decrementAndGet() }
operator fun AtomicInteger.minus(delta: Int) = get() - delta
operator fun AtomicInteger.unaryMinus() = -get()
operator fun AtomicInteger.minusAssign(delta: Int) {
    addAndGet(-delta)
}