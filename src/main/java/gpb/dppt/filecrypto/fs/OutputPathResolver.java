package gpb.dppt.filecrypto.fs;

import gpb.dppt.filecrypto.config.ActionType;
import gpb.dppt.filecrypto.config.AppConfig;

import java.nio.file.Files;
import java.nio.file.Path;

public final class OutputPathResolver {

    private final AppConfig appConfig;

    public OutputPathResolver(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    public Path resolve(Path sourcePath, Path explicitOutputPath) {
        if (Files.isRegularFile(appConfig.getInputPath())) {
            if (explicitOutputPath != null) {
                return explicitOutputPath;
            }
            return sourcePath.resolveSibling(defaultFileName(sourcePath.getFileName().toString()));
        }

        if (explicitOutputPath == null) {
            return sourcePath.resolveSibling(defaultFileName(sourcePath.getFileName().toString()));
        }

        Path inputRoot = appConfig.getInputPath();
        Path relativePath = inputRoot.relativize(sourcePath);
        Path targetDirectory = explicitOutputPath;
        Path relativeParent = relativePath.getParent();
        if (relativeParent != null) {
            targetDirectory = explicitOutputPath.resolve(relativeParent);
        }
        return targetDirectory.resolve(defaultFileName(sourcePath.getFileName().toString()));
    }

    private String defaultFileName(String originalName) {
        if (appConfig.getAction() == ActionType.ENCRYPT) {
            return originalName + ".enc";
        }
        return originalName + ".dec";
    }
}
