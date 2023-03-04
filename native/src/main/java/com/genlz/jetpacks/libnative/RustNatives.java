package com.genlz.jetpacks.libnative;

/**
 * The java native method declarations.
 * <p>
 * Implementations:<a href="{@docRoot}rust/src/lib.rs">lib.rs</a>
 */
public final class RustNatives {

    static {
        System.loadLibrary("rust");
    }

    public static native String hello(String input);

    public static native void runNative();
}
