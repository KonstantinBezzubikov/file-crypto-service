package gpb.dppt.filecrypto.config;

public enum RunMode {
    DEFAULT,
    DAEMON;

    public static RunMode from(String value) {
        if (value == null || value.trim().isEmpty()) {
            return DEFAULT;
        }
        String normalized = value.trim().toUpperCase();
        for (RunMode mode : values()) {
            if (mode.name().equals(normalized)) {
                return mode;
            }
        }
        throw new IllegalArgumentException("Unsupported mode: " + value + ". Supported values: default, daemon");
    }
}
