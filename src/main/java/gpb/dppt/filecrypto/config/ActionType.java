package gpb.dppt.filecrypto.config;

public enum ActionType {
    ENCRYPT,
    DECRYPT;

    public static ActionType from(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Parameter -action is required");
        }
        String normalized = value.trim().toUpperCase();
        for (ActionType type : values()) {
            if (type.name().equals(normalized)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unsupported action: " + value + ". Supported values: encrypt, decrypt");
    }
}
