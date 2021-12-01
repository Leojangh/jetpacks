@file:Suppress("NOTHING_TO_INLINE")

package com.genlz.share.util

import java.lang.reflect.Field
import java.lang.reflect.Method

private const val TAG = "ReflectExt"

/**
 * This method maybe escapes from black list lint,so
 * handle the exception correctly.
 *
 * @param className The fully-qualifier class name.
 * @param clazz The class object you wanna reflect.Param [className] can be omitted if this is supplied.
 * @param methodName The method name.
 * @param target The method invocation target. null for static method.
 * @param params The params of method.
 * @param paramTypes The param types,by default it could be inferred from params,else specify manually.
 * @param R The return type.
 */
@Throws(Throwable::class)
@Suppress("UNCHECKED_CAST")
fun <R> call(
    className: String = "",
    clazz: Class<*> = className.toClass(),
    methodName: String,
    target: Any? = null,
    params: Array<*> = emptyArray<Unit>(),
    paramTypes: Array<Class<*>> = params.mapAsArray { it?.javaClass as Class<*> },
) = clazz.withMethod(methodName, *paramTypes)(target, *params) as R

/**
 * Be care for black list apis.
 */
@Throws(Throwable::class)
@Suppress("UNCHECKED_CAST")
fun <T> get(
    className: String = "",
    clazz: Class<*> = className.toClass(),
    fieldName: String,
    target: Any? = null,
) = clazz.withField(fieldName)[target] as T

/**
 * Retrieve a method named [name] and accessible or throw a exception.
 */
@Throws(SecurityException::class, NoSuchMethodException::class)
private inline fun Class<*>.withMethod(name: String, vararg paramTypes: Class<*>): Method {
    return getDeclaredMethod(name, *paramTypes).apply { isAccessible = true }
}

/**
 * Retrieve a field named [name] and accessible or throw a exception.
 */
@Throws(SecurityException::class, NoSuchFieldException::class)
private inline fun Class<*>.withField(name: String): Field {
    return getDeclaredField(name).apply { isAccessible = true }
}

/**
 * Same as [Array.map],but this return a array type.
 */
private inline fun <T, reified R> Array<out T>.mapAsArray(transformer: (T) -> R) =
    Array(size) { transformer(this[it]) }

/**
 * Return the class of the qualifier represents.
 */
private fun String.toClass() = try {
    Class.forName(this)
} catch (e: Exception) {
    throw IllegalArgumentException("$this is not a class fully-qualifier name!", e)
}

