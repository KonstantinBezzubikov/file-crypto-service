package gpb.dppt.filecrypto.config;

import java.nio.file.Path;

public final class AppConfig {

    private final ActionType action;
    private final RunMode mode;
    private final Path inputPath;
    private final Path outputPath;
    private final Path crlFilePath;
    private final Path configFilePath;
    private final long scanIntervalMs;
    private final boolean recursive;
    private final boolean includeHidden;
    private final long shutdownWaitMs;
    private final String inProgressSuffix;

    public AppConfig(ActionType action,
                     RunMode mode,
                     Path inputPath,
                     Path outputPath,
                     Path crlFilePath,
                     Path configFilePath,
                     long scanIntervalMs,
                     boolean recursive,
                     boolean includeHidden,
                     long shutdownWaitMs,
                     String inProgressSuffix) {
        this.action = action;
        this.mode = mode;
        this.inputPath = inputPath;
        this.outputPath = outputPath;
        this.crlFilePath = crlFilePath;
        this.configFilePath = configFilePath;
        this.scanIntervalMs = scanIntervalMs;
        this.recursive = recursive;
        this.includeHidden = includeHidden;
        this.shutdownWaitMs = shutdownWaitMs;
        this.inProgressSuffix = inProgressSuffix;
    }

    public ActionType getAction() {
        return action;
    }

    public RunMode getMode() {
        return mode;
    }

    public Path getInputPath() {
        return inputPath;
    }

    public Path getOutputPath() {
        return outputPath;
    }

    public Path getCrlFilePath() {
        return crlFilePath;
    }

    public Path getConfigFilePath() {
        return configFilePath;
    }

    public long getScanIntervalMs() {
        return scanIntervalMs;
    }

    public boolean isRecursive() {
        return recursive;
    }

    public boolean isIncludeHidden() {
        return includeHidden;
    }

    public long getShutdownWaitMs() {
        return shutdownWaitMs;
    }

    public String getInProgressSuffix() {
        return inProgressSuffix;
    }
}
