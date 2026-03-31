package gpb.dppt.filecrypto.config;

import gpb.dppt.filecrypto.cli.CommandLineArguments;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public final class ConfigLoader {

    private ConfigLoader() {
    }

    public static AppConfig load(CommandLineArguments arguments) throws IOException {
        if (arguments.has("help")) {
            throw new IllegalArgumentException("Help requested");
        }

        Properties properties = loadDefaults();
        Path configFilePath = getOptionalPath(arguments.get("conf"));
        if (configFilePath != null) {
            loadExternalProperties(properties, configFilePath);
        }

        String actionValue = firstNonBlank(arguments.get("action"), properties.getProperty("app.action"));
        String modeValue = firstNonBlank(arguments.get("mode"), properties.getProperty("app.mode"));
        String inValue = firstNonBlank(arguments.get("in"), properties.getProperty("app.in"));
        String outValue = firstNonBlank(arguments.get("out"), properties.getProperty("app.out"));
        String crlValue = firstNonBlank(arguments.get("crl-file"), properties.getProperty("app.crl-file"));

        ActionType action = ActionType.from(actionValue);
        RunMode mode = RunMode.from(modeValue);
        Path inputPath = requiredPath(inValue, "Parameter -in is required");
        Path outputPath = getOptionalPath(outValue);
        Path crlFilePath = getOptionalPath(crlValue);

        long scanIntervalMs = getLong(arguments, properties, "app.scanIntervalMs", 10000L);
        long shutdownWaitMs = getLong(arguments, properties, "app.shutdownWaitMs", 3000L);
        boolean recursive = getBoolean(arguments, properties, "recursive", "app.recursive", false);
        boolean includeHidden = getBoolean(arguments, properties, "include-hidden", "app.includeHidden", false);
        String inProgressSuffix = firstNonBlank(arguments.get("in-progress-suffix"), properties.getProperty("app.inProgressSuffix"), ".in_progress");

        validatePaths(inputPath, outputPath, crlFilePath, mode);

        return new AppConfig(action,
                mode,
                inputPath,
                outputPath,
                crlFilePath,
                configFilePath,
                scanIntervalMs,
                recursive,
                includeHidden,
                shutdownWaitMs,
                inProgressSuffix);
    }

    private static Properties loadDefaults() throws IOException {
        Properties properties = new Properties();
        InputStream inputStream = ConfigLoader.class.getClassLoader().getResourceAsStream("application.properties");
        if (inputStream != null) {
            try {
                properties.load(inputStream);
            } finally {
                inputStream.close();
            }
        }
        return properties;
    }

    private static void loadExternalProperties(Properties properties, Path configFilePath) throws IOException {
        if (!Files.exists(configFilePath)) {
            throw new IllegalArgumentException("Configuration file does not exist: " + configFilePath);
        }
        try (InputStream inputStream = Files.newInputStream(configFilePath)) {
            properties.load(inputStream);
        }
    }

    private static boolean getBoolean(CommandLineArguments arguments,
                                      Properties properties,
                                      String cliKey,
                                      String propertyKey,
                                      boolean defaultValue) {
        String value = firstNonBlank(arguments.get(cliKey), properties.getProperty(propertyKey));
        return value == null ? defaultValue : Boolean.parseBoolean(value);
    }

    private static long getLong(CommandLineArguments arguments,
                                Properties properties,
                                String propertyKey,
                                long defaultValue) {
        String value = firstNonBlank(arguments.get(propertyKey), properties.getProperty(propertyKey));
        return value == null ? defaultValue : Long.parseLong(value);
    }

    private static Path requiredPath(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(message);
        }
        return Paths.get(value).toAbsolutePath().normalize();
    }

    private static Path getOptionalPath(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return Paths.get(value).toAbsolutePath().normalize();
    }

    private static String firstNonBlank(String... candidates) {
        if (candidates == null) {
            return null;
        }
        for (String candidate : candidates) {
            if (candidate != null && !candidate.trim().isEmpty()) {
                return candidate.trim();
            }
        }
        return null;
    }

    private static void validatePaths(Path inputPath, Path outputPath, Path crlFilePath, RunMode mode) {
        if (!Files.exists(inputPath)) {
            throw new IllegalArgumentException("Input path does not exist: " + inputPath);
        }
        if (mode == RunMode.DAEMON && !Files.isDirectory(inputPath)) {
            throw new IllegalArgumentException("Daemon mode requires input directory: " + inputPath);
        }
        if (crlFilePath != null && !Files.exists(crlFilePath)) {
            throw new IllegalArgumentException("CRL file does not exist: " + crlFilePath);
        }
        if (outputPath != null && Files.exists(outputPath) && Files.isDirectory(inputPath) && !Files.isDirectory(outputPath)) {
            throw new IllegalArgumentException("When input is a directory, output must be a directory: " + outputPath);
        }
    }
}
