package com.qualaroo.internal;

import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

import com.qualaroo.internal.storage.LocalStorage;
import com.qualaroo.internal.storage.Settings;

import java.util.Map;
import java.util.UUID;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

@RestrictTo(LIBRARY)
public class UserInfo {

    private static final String KEY_USER_ID = "q.uid";
    private static final String KEY_DEVICE_ID = "q.did";

    private final Settings settings;
    private final LocalStorage localStorage;

    public UserInfo(Settings settings, LocalStorage localStorage) {
        this.settings = settings;
        this.localStorage = localStorage;
    }

    public synchronized void setUserId(String userId) {
        settings.store(KEY_USER_ID, userId);
    }


    @Nullable public synchronized String getUserId() {
        return settings.get(KEY_USER_ID);
    }

    public synchronized String getDeviceId() {
        String deviceId = settings.get(KEY_DEVICE_ID);
        if (deviceId == null) {
            deviceId = UUID.randomUUID().toString();
            settings.store(KEY_DEVICE_ID, deviceId);
        }
        return deviceId;
    }

    public synchronized void setUserProperty(String key, String value) {
        localStorage.updateUserProperty(key, value);
    }

    public synchronized Map<String, String> getUserProperties() {
        return localStorage.getUserProperties();
    }

}
