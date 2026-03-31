package gpb.dppt.filecrypto.fs;

import gpb.dppt.filecrypto.config.AppConfig;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public final class FileCollector {

    private final AppConfig appConfig;

    public FileCollector(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    public List<Path> collect(Path directory) throws IOException {
        List<Path> result = new ArrayList<Path>();
        int maxDepth = appConfig.isRecursive() ? Integer.MAX_VALUE : 1;
        Stream<Path> stream = Files.walk(directory, maxDepth, FileVisitOption.FOLLOW_LINKS);
        try {
            stream.filter(Files::isRegularFile)
                    .filter(this::isEligible)
                    .forEach(result::add);
        } finally {
            stream.close();
        }
        return result;
    }

    private boolean isEligible(Path path) {
        try {
            if (!Files.isRegularFile(path)) {
                return false;
            }

            if (!appConfig.isIncludeHidden() && Files.isHidden(path)) {
                return false;
            }
        } catch (IOException ex) {
            return false;
        }

        String fileName = path.getFileName().toString().toLowerCase();
        String inProgressSuffix = appConfig.getInProgressSuffix() == null
                ? ".in_progress"
                : appConfig.getInProgressSuffix().toLowerCase();

        if (fileName.endsWith(inProgressSuffix)) {
            return false;
        }

        if (fileName.endsWith(".enc")) {
            return false;
        }

        if (fileName.endsWith(".dec")) {
            return false;
        }

        return true;
    }
}
