package com.diskinformation;

import com.os.Os;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class DiskInformation {

    private static final String OS_NAME = System.getProperty("os.name").toLowerCase();


    private boolean isWindows() {
        return OS_NAME.contains("win");
    }

    private boolean isUnix() {
        return OS_NAME.contains("nix") ||
                OS_NAME.contains("nux") ||
                OS_NAME.contains("mac");
    }

    public void displayDiskInfo() throws IOException, InterruptedException {
        ProcessBuilder diskInfoPb = new ProcessBuilder();

        if (isWindows()) {
            diskInfoPb.command("cmd", "/c", "dir", "/s", "C:\\");
        } else {
            diskInfoPb.command("df", "-h");
        }

        diskInfoPb.redirectErrorStream(true);
        Process diskProcess = diskInfoPb.start();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(diskProcess.getInputStream()))) {
            String line;
            System.out.println("\nDisk Information:");
            System.out.println("----------------");
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }

        diskProcess.waitFor();
    }

    public static void main(String[] args) {
        DiskInformation manager = new DiskInformation();
        try {
            // Display disk information
            manager.displayDiskInfo();

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
