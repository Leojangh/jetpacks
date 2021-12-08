@file:Suppress("UNUSED", "NOTHING_TO_INLINE")

package com.genlz.share.util.appcompat

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import androidx.core.content.ContentResolverCompat
import androidx.core.os.CancellationSignal

/**
 * @see ContentResolverCompat.query
 */
inline fun ContentResolver.queryExt(
    uri: Uri,
    projection: Array<String>?,
    selection: String? = null,
    selectionArgs: Array<String>? = null,
    sortOrder: String? = null,
    cancellationSignal: CancellationSignal? = null
): Cursor? = ContentResolverCompat.query(
    this,
    uri,
    projection,
    selection,
    selectionArgs,
    sortOrder,
    cancellationSignal
)

