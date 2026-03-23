package com.example.filecrypto.core;

import com.example.filecrypto.config.AppConfig;
import com.example.filecrypto.config.RunMode;
import com.example.filecrypto.service.CryptoService;

public final class ProcessingCoordinator {

    private final AppConfig appConfig;
    private final SingleRunProcessor singleRunProcessor;
    private final DirectoryDaemon directoryDaemon;

    public ProcessingCoordinator(AppConfig appConfig, CryptoService cryptoService) {
        this.appConfig = appConfig;
        this.singleRunProcessor = new SingleRunProcessor(appConfig, cryptoService);
        this.directoryDaemon = new DirectoryDaemon(appConfig, singleRunProcessor);
    }

    public void start() {
        logStartup();
        if (appConfig.getMode() == RunMode.DAEMON) {
            directoryDaemon.run();
            return;
        }
        singleRunProcessor.process(appConfig.getInputPath(), appConfig.getOutputPath());
    }

    private void logStartup() {
        System.out.println("Starting file-crypto-service");
        System.out.println("Action      : " + appConfig.getAction());
        System.out.println("Mode        : " + appConfig.getMode());
        System.out.println("Input       : " + appConfig.getInputPath());
        System.out.println("Output      : " + (appConfig.getOutputPath() == null ? "<default>" : appConfig.getOutputPath()));
        System.out.println("CRL file    : " + (appConfig.getCrlFilePath() == null ? "<not set>" : appConfig.getCrlFilePath()));
        System.out.println("Recursive   : " + appConfig.isRecursive());
        System.out.println("In progress : " + appConfig.getInProgressSuffix());
    }
}
