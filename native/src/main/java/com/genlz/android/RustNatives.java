package com.genlz.android;

/**
 * The java native method declarations.
 * <p>
 * Implementations:rust/src/lib.rs
 */
public final class RustNatives {

    static {
        System.loadLibrary("rust");
    }

    public static native int method();
}
