package com.genlz.share.util

/**
 * @param className The qualifier class name.
 * @param methodName The method name.
 * @param target The method invocation target. null for static method.
 * @param params The method params for method.
 */
@Throws(Throwable::class)
@Suppress("UNCHECKED_CAST")
fun <R> call(
    className: String,
    methodName: String,
    target: Any? = null,
    vararg params: Any?,
): R {
    val clazz = className.toClass()
    val paramTypes = params.map { it?.javaClass }.toTypedArray()
    val method = clazz.getDeclaredMethod(methodName, *paramTypes).apply { isAccessible = true }
    return method(target, *params) as R
}

/**
 * Return the class of the qualifier represents.
 */
fun String.toClass(): Class<*> = Class.forName(this)

