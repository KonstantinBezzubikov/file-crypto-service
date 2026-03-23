package com.example.filecrypto.cli;

import java.util.LinkedHashMap;
import java.util.Map;

public final class CommandLineParser {

    private CommandLineParser() {
    }

    public static CommandLineArguments parse(String[] args) {
        Map<String, String> values = new LinkedHashMap<String, String>();
        if (args == null) {
            return new CommandLineArguments(values);
        }

        for (int i = 0; i < args.length; i++) {
            String token = args[i];
            if (token == null || token.trim().isEmpty()) {
                continue;
            }
            if (!token.startsWith("-")) {
                throw new IllegalArgumentException("Unsupported argument format: " + token);
            }
            if ("-help".equalsIgnoreCase(token) || "--help".equalsIgnoreCase(token) || "-h".equalsIgnoreCase(token)) {
                values.put("help", "true");
                continue;
            }

            if (i + 1 >= args.length || args[i + 1].startsWith("-")) {
                throw new IllegalArgumentException("Missing value for argument: " + token);
            }

            String normalizedKey = normalizeKey(token);
            values.put(normalizedKey, args[++i]);
        }

        return new CommandLineArguments(values);
    }

    private static String normalizeKey(String token) {
        String key = token.trim();
        while (key.startsWith("-")) {
            key = key.substring(1);
        }
        return key;
    }

    public static void printUsage() {
        StringBuilder builder = new StringBuilder();
        builder.append("Usage:\n")
                .append("  java -jar file-crypto-service.jar ")
                .append("-action <encrypt|decrypt> ")
                .append("[-mode <default|daemon>] ")
                .append("-in <input file|input directory> ")
                .append("[-out <output file|output directory>] ")
                .append("[-crl-file <path>] ")
                .append("[-conf <properties file>]\n\n")
                .append("Examples:\n")
                .append("  java -jar file-crypto-service.jar -action encrypt -mode default -in /data/report.txt\n")
                .append("  java -jar file-crypto-service.jar -action decrypt -in /data/report.txt.enc -out /data/report.txt\n")
                .append("  java -jar file-crypto-service.jar -action encrypt -mode daemon -in /data/inbox -out /data/outbox -conf /opt/app/application.properties\n");
        System.out.println(builder.toString());
    }
}
