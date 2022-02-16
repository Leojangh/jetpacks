// ILocationService.aidl
package com.genlz.jetpacks.service;

// Declare any non-default types here with import statements

interface ILocationService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void requestLocation(double lat, double lng);

    void setOnLocationResovledListener(IOnLocationResovledListener listener);
}