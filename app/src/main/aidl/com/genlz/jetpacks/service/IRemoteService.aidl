// IRemoteService.aidl
package com.genlz.jetpacks.service;

// Declare any non-default types here with import statements
// The server side must define AIDL and client side too.
interface IRemoteService {

    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);
}