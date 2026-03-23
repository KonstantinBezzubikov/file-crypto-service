package com.example.filecrypto.service;

import com.example.filecrypto.config.ActionType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public final class NoOpCryptoService implements CryptoService {

    @Override
    public void process(ActionType action, Path sourcePath, Path targetPath, Path crlFilePath) throws IOException {
        if (crlFilePath != null) {
            System.out.println("CRL file is accepted but not used yet: " + crlFilePath);
        }
        System.out.println("Stub crypto service executed in mode: " + action);
        Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
    }
}
