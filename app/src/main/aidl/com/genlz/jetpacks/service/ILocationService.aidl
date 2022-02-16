// ILocationService.aidl
package com.genlz.jetpacks.service;

// Declare any non-default types here with import statements

interface ILocationService {
    String requestLocation(double lat, double lng);
}