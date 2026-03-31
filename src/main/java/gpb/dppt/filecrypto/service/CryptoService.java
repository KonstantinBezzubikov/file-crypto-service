package gpb.dppt.filecrypto.service;

import gpb.dppt.filecrypto.config.ActionType;

import java.io.IOException;
import java.nio.file.Path;

public interface CryptoService {

    void process(ActionType action, Path sourcePath, Path targetPath, Path crlFilePath) throws IOException;
}
