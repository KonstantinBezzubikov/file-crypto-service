package com.example.filecrypto;

import com.example.filecrypto.cli.CommandLineArguments;
import com.example.filecrypto.cli.CommandLineParser;
import com.example.filecrypto.config.AppConfig;
import com.example.filecrypto.config.ConfigLoader;
import com.example.filecrypto.core.ProcessingCoordinator;
import com.example.filecrypto.service.NoOpCryptoService;

public final class Application {

    private Application() {
    }

    public static void main(String[] args) {
        int exitCode = 1;
        try {
            CommandLineArguments cliArguments = CommandLineParser.parse(args);
            AppConfig appConfig = ConfigLoader.load(cliArguments);
            ProcessingCoordinator coordinator = new ProcessingCoordinator(appConfig, new NoOpCryptoService());
            coordinator.start();
            exitCode = 0;
        } catch (IllegalArgumentException ex) {
            System.err.println("Configuration error: " + ex.getMessage());
            CommandLineParser.printUsage();
        } catch (Exception ex) {
            System.err.println("Fatal error: " + ex.getMessage());
            ex.printStackTrace(System.err);
        }
        System.exit(exitCode);
    }
}
