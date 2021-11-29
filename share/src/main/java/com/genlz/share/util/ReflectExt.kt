package com.genlz.share.util

private const val TAG = "ReflectExt"

/**
 * This method maybe escapes from black list lint,so
 * handle the exception correctly.
 *
 * @param className The qualifier class name.
 * @param methodName The method name.
 * @param target The method invocation target. null for static method.
 * @param params The params of method.
 * @param paramTypes The param types,by default it could be inferred from params,else specify manually.
 * @param R The return type.
 */
@Throws(Throwable::class)
@Suppress("UNCHECKED_CAST")
fun <R> call(
    className: String,
    methodName: String,
    target: Any? = null,
    params: Array<Any?> = arrayOf(),
    paramTypes: Array<Class<*>> = params.mapAsArray { it?.javaClass as Class<*> },
) = className
    .toClass()
    .getDeclaredMethod(methodName, *paramTypes)
    .apply { isAccessible = true }(target, *params) as R

/**
 * Be care for black list apis.
 */
@Throws(Throwable::class)
@Suppress("UNCHECKED_CAST")
fun <T> get(
    className: String,
    fieldName: String,
    target: Any? = null,
) = className.toClass().getDeclaredField(fieldName).apply { isAccessible = true }[target] as T


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

