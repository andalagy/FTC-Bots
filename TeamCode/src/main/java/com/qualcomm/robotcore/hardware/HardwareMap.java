package com.qualcomm.robotcore.hardware;

import java.util.HashMap;
import java.util.Map;

public class HardwareMap {
    private final Map<String, Object> devices = new HashMap<>();

    public <T> void register(String name, T device) {
        devices.put(name, device);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> clazz, String name) {
        Object value = devices.get(name);
        if (value == null) {
            try {
                return clazz.getDeclaredConstructor().newInstance();
            } catch (Exception ignored) {
                return null;
            }
        }
        return (T) value;
    }
}
