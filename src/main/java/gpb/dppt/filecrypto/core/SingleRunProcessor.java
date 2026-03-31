package gpb.dppt.filecrypto.core;

import gpb.dppt.filecrypto.config.AppConfig;
import gpb.dppt.filecrypto.config.RunMode;
import gpb.dppt.filecrypto.fs.FileCollector;
import gpb.dppt.filecrypto.fs.FileReservationService;
import gpb.dppt.filecrypto.fs.OutputPathResolver;
import gpb.dppt.filecrypto.service.CryptoService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class SingleRunProcessor {

    private final AppConfig appConfig;
    private final CryptoService cryptoService;
    private final FileCollector fileCollector;
    private final FileReservationService reservationService;
    private final OutputPathResolver outputPathResolver;

    public SingleRunProcessor(AppConfig appConfig, CryptoService cryptoService) {
        this.appConfig = appConfig;
        this.cryptoService = cryptoService;
        this.fileCollector = new FileCollector(appConfig);
        this.reservationService = new FileReservationService(appConfig.getInProgressSuffix());
        this.outputPathResolver = new OutputPathResolver(appConfig);
    }

    public void process(Path inputPath, Path explicitOutputPath) {
        try {
            if (Files.isRegularFile(inputPath)) {
                processSingleFile(inputPath, explicitOutputPath);
                return;
            }
            processDirectory(inputPath, explicitOutputPath);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to process path " + inputPath + ": " + ex.getMessage(), ex);
        }
    }

    public boolean processSingleFile(Path sourcePath, Path explicitOutputPath) throws IOException {
        FileReservationService.Reservation reservation = reservationService.tryReserve(sourcePath);
        if (reservation == null) {
            return false;
        }

        Path reservedPath = reservation.getReservedPath();
        Path targetPath = null;
        boolean processedSuccessfully = false;

        boolean deleteSourceAfterSuccess =
                appConfig.getMode() == RunMode.DAEMON
                        || Files.isDirectory(appConfig.getInputPath());

        try {
            targetPath = outputPathResolver.resolve(sourcePath, explicitOutputPath);
            if (targetPath.getParent() != null) {
                Files.createDirectories(targetPath.getParent());
            }

            cryptoService.process(appConfig.getAction(), reservedPath, targetPath, appConfig.getCrlFilePath());
            processedSuccessfully = true;

            System.out.println("Processed: " + sourcePath + " -> " + targetPath);

            return true;
        } finally {
            if (processedSuccessfully) {
                if (deleteSourceAfterSuccess) {
                    if (Files.exists(reservedPath)) {
                        Files.delete(reservedPath);
                    }
                } else {
                    reservation.release();
                }
            } else {
                reservation.release();
            }
        }
    }

    private void processDirectory(Path directory, Path explicitOutputPath) throws IOException {
        List<Path> files = fileCollector.collect(directory);
        for (Path file : files) {
            processSingleFile(file, explicitOutputPath);
        }
    }
}
