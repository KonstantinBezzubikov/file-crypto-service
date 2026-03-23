package com.example.filecrypto.core;

import com.example.filecrypto.config.AppConfig;
import com.example.filecrypto.fs.FileCollector;
import com.example.filecrypto.fs.FileReservationService;
import com.example.filecrypto.fs.OutputPathResolver;
import com.example.filecrypto.service.CryptoService;

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
        try {
            targetPath = outputPathResolver.resolve(sourcePath, explicitOutputPath);
            if (targetPath.getParent() != null) {
                Files.createDirectories(targetPath.getParent());
            }
            cryptoService.process(appConfig.getAction(), reservedPath, targetPath, appConfig.getCrlFilePath());
            System.out.println("Processed: " + sourcePath + " -> " + targetPath);
            return true;
        } finally {
            reservation.release();
        }
    }

    private void processDirectory(Path directory, Path explicitOutputPath) throws IOException {
        List<Path> files = fileCollector.collect(directory);
        for (Path file : files) {
            processSingleFile(file, explicitOutputPath);
        }
    }
}
