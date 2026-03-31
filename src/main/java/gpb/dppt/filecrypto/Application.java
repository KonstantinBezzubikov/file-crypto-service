package gpb.dppt.filecrypto;

import gpb.dppt.filecrypto.cli.CommandLineArguments;
import gpb.dppt.filecrypto.cli.CommandLineParser;
import gpb.dppt.filecrypto.config.AppConfig;
import gpb.dppt.filecrypto.config.ConfigLoader;
import gpb.dppt.filecrypto.core.ProcessingCoordinator;
import gpb.dppt.filecrypto.service.NoOpCryptoService;

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
