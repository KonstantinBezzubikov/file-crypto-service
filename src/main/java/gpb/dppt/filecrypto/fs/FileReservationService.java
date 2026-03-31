package gpb.dppt.filecrypto.fs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public final class FileReservationService {

    private final String inProgressSuffix;

    public FileReservationService(String inProgressSuffix) {
        this.inProgressSuffix = inProgressSuffix;
    }

    public Reservation tryReserve(Path sourcePath) throws IOException {
        if (!Files.exists(sourcePath) || !Files.isRegularFile(sourcePath)) {
            return null;
        }
        if (sourcePath.getFileName().toString().endsWith(inProgressSuffix)) {
            return null;
        }

        Path reservedPath = sourcePath.resolveSibling(sourcePath.getFileName().toString() + inProgressSuffix);
        try {
            Path moved = Files.move(sourcePath, reservedPath, StandardCopyOption.ATOMIC_MOVE);
            return new Reservation(sourcePath, moved);
        } catch (IOException ex) {
            return null;
        }
    }

    public static final class Reservation {

        private final Path originalPath;
        private final Path reservedPath;

        private Reservation(Path originalPath, Path reservedPath) {
            this.originalPath = originalPath;
            this.reservedPath = reservedPath;
        }

        public Path getReservedPath() {
            return reservedPath;
        }

        public void release() throws IOException {
            if (Files.exists(reservedPath)) {
                Files.move(reservedPath, originalPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            }
        }
    }
}
