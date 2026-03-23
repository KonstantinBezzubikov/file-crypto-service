package com.example.filecrypto.cli;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class CommandLineArguments {

    private final Map<String, String> values;

    public CommandLineArguments(Map<String, String> values) {
        this.values = Collections.unmodifiableMap(new LinkedHashMap<String, String>(values));
    }

    public String get(String key) {
        return values.get(key);
    }

    public boolean has(String key) {
        return values.containsKey(key);
    }

    public Map<String, String> asMap() {
        return values;
    }
}
