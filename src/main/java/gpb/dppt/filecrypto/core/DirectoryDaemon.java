package gpb.dppt.filecrypto.core;

import gpb.dppt.filecrypto.config.AppConfig;

import java.nio.file.Path;

public final class DirectoryDaemon {

    private final AppConfig appConfig;
    private final SingleRunProcessor processor;
    private volatile boolean running = true;

    public DirectoryDaemon(AppConfig appConfig, SingleRunProcessor processor) {
        this.appConfig = appConfig;
        this.processor = processor;
    }

    public void run() {
        registerShutdownHook();
        System.out.println("Daemon mode started. Scan interval: " + appConfig.getScanIntervalMs() + " ms");
        while (running) {
            try {
                processor.process(appConfig.getInputPath(), appConfig.getOutputPath());
                Thread.sleep(appConfig.getScanIntervalMs());
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                running = false;
            } catch (Exception ex) {
                System.err.println("Daemon cycle failed: " + ex.getMessage());
                sleepQuietly(appConfig.getScanIntervalMs());
            }
        }
        System.out.println("Daemon stopped");
    }

    private void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                running = false;
                sleepQuietly(appConfig.getShutdownWaitMs());
            }
        }, "file-crypto-shutdown-hook"));
    }

    private void sleepQuietly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
        }
    }
}
