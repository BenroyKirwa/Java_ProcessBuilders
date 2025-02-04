package com.backup;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

public class Backup {
    private static final String BACKUP_FOLDER = "BackupFolder";
    private static final int MAX_BACKUPS = 10;
    private static final String DB_USERNAME = "dbusername";
    private static final String DB_HOST = "host";
    private static final int DB_PORT = 5432;
    private static final String DB_NAME = "dbname";
    private static final String PASSWORD = "password"; // Set the password here

    public static void main(String[] args) {
        // Step 1: Schedule backup every minute
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(Backup::createBackup, 0, 1, TimeUnit.MINUTES);
    }

    private static void createBackup() {
        try {
            // Step 2: Ensure backup folder exists
            File backupDir = new File(BACKUP_FOLDER);
            if (!backupDir.exists()) {
                if (!backupDir.mkdir()) {
                    throw new IOException("Failed to create backup folder");
                }
            }

            // Step 3: Generate a timestamped backup file name
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String backupFilePath = new File(backupDir, "backup_" + timestamp + ".sql").getAbsolutePath();

            // Step 4: Run the pg_dump command to create the backup
            String[] command = {
//                    "bash",
                    "pg_dump",
                    "-U", DB_USERNAME,
                    "-h", DB_HOST,
                    "-p", String.valueOf(DB_PORT),
                    "-d", DB_NAME,
                    "-f", backupFilePath
            };

            // Step 5: Set environment variables for pg_dump
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.environment().put("PGPASSWORD", PASSWORD);
            processBuilder.redirectErrorStream(true);

            // Step 6: Start the process and capture output
            Process process = processBuilder.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line); // Print process output
                }
            }

            // Step 7: Wait for the process to finish and check if it was successful
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Backup completed successfully: " + backupFilePath);
            } else {
                System.err.println("Backup failed. Exit code: " + exitCode);
            }

            // Step 8: Rotate backups to maintain only the latest 10 backups
            rotateBackups(backupDir);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void rotateBackups(File backupDir) {
        File[] backupFiles = backupDir.listFiles((dir, name) -> name.startsWith("backup_"));
        if (backupFiles != null && backupFiles.length > MAX_BACKUPS) {
            // Sort files by last modified date (oldest first)
            Arrays.sort(backupFiles, Comparator.comparingLong(File::lastModified));

            // Delete the oldest files until we have at most 10 backups
            for (int i = 0; i < backupFiles.length - MAX_BACKUPS; i++) {
                if (backupFiles[i].delete()) {
                    System.out.println("Deleted old backup: " + backupFiles[i].getName());
                } else {
                    System.err.println("Failed to delete old backup: " + backupFiles[i].getName());
                }
            }
        }
    }
}
