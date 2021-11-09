package com.genlz.share.widget.web.bridge

import java.lang.annotation.Inherited

/**
 * A flag annotation to indicate the method needs to be run in UI thread.
 * I must define this instead of [androidx.annotation.UiThread] because that
 * can't be detect in subclass.So [Inherited] to rescue.
 */
@Retention
@Inherited
@Target(AnnotationTarget.FUNCTION)
annotation class InheritedUiThread
