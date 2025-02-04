package com.os;

import java.io.IOException;
import java.util.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.*;

public class Os {
    private static final String OS_NAME = System.getProperty("os.name").toLowerCase();

    public void createDirectoryAndListProcesses() throws IOException, InterruptedException {
        // Create new directory
        String newDir = "system_info";
        ProcessBuilder mkdirPb = new ProcessBuilder();

        if (isWindows()) {
            mkdirPb.command("cmd", "/c", "mkdir", newDir);
        } else {
            mkdirPb.command("mkdir", "-p", newDir);
        }

        Process mkdirProcess = mkdirPb.start();
        mkdirProcess.waitFor();

        // Get list of running processes
        ProcessBuilder processList = new ProcessBuilder();
        if (isWindows()) {
            processList.command("tasklist");
        } else {
            processList.command("ps", "aux");
        }

        Process processListProcess = processList.start();
        StringBuilder output = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(processListProcess.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }

        // Write processes to file
        ProcessBuilder echoPb = new ProcessBuilder();
        Path filePath = Paths.get(newDir, "running_processes.txt");

        if (isWindows()) {
            Files.write(filePath, output.toString().getBytes());
        } else {
            echoPb.command("bash", "-c", "echo '" + output + "' > " +
                    newDir + "/running_processes.txt");
            Process echoProcess = echoPb.start();
            echoProcess.waitFor();
        }

        System.out.println("Process list has been written to " + filePath);
    }

    private boolean isWindows() {
        return OS_NAME.contains("win");
    }

    private boolean isUnix() {
        return OS_NAME.contains("nix") ||
                OS_NAME.contains("nux") ||
                OS_NAME.contains("mac");
    }

    public static void main(String[] args) {
        Os manager = new Os();
        try {
            // Create directory and handle processes
            manager.createDirectoryAndListProcesses();

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}