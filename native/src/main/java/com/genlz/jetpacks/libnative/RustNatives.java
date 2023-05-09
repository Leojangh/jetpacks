package com.genlz.jetpacks.libnative;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

/**
 * The java native method declarations.
 * <p>
 * Implementations:<a href="{@docRoot}rust/src/lib.rs">lib.rs</a>
 */
@Keep
public final class RustNatives {

    static {
        System.loadLibrary("rust");
    }

    public static native String hello(String input);

    public static native boolean search(@NonNull byte[] bytes, @NonNull String pattern);
}
