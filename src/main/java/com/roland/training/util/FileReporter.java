package com.roland.training.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

// FileReporter Class compatible with Java 11 and below
public class FileReporter {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final String BASE_DIR = System.getProperty("user.home") + "\\Desktop\\Java Projects\\Stratpoint\\Week_3_Solution_Web_Based"; // Added BASE_DIR as Java EE Web Project create the reports and logs directory in Tomcat/bin folder
    private static final String REPORTS_DIR =BASE_DIR+ "\\reports";
    private static final String LOGS_DIR = BASE_DIR+"\\logs";

    static {
        // Create directories if they don't exist
        new File(REPORTS_DIR).mkdirs();
        new File(LOGS_DIR).mkdirs();
    }

    /**
     * Saves a report to a file with the current date in the filename
     *
     * @param reportContent The content of the report
     * @throws IOException If there's an error writing to the file
     */
    public static void saveReport(String reportContent) throws IOException {
        String filename = REPORTS_DIR + File.separator + "reports-" + LocalDate.now().format(DATE_FORMATTER) + ".txt";
        writeToFile(filename, reportContent, true);
    }

    /**
     * Logs a transaction activity to a file with the current date in the filename
     *
     * @param activity The activity to log
     * @throws IOException If there's an error writing to the file
     */
    public static void logActivity(String activity) throws IOException {
        String filename = LOGS_DIR + File.separator + "transactions-" + LocalDate.now().format(DATE_FORMATTER) + ".txt";
        writeToFile(filename, activity, true);
    }

//    private static void writeToFile(String filename, String content, boolean append) throws IOException {
//        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, append))) {
//            writer.write(content);
//            writer.newLine();
//            writer.flush();
//        }
//    }

    /**
     * Updated writeToFile method for Java 17
     *
     * If Java 17 doesn't print → correctly when writing to a .txt file, it's likely an encoding issue. By default, FileWriter and BufferedWriter use the system's default encoding, which may not be UTF-8.
     *
     * Why This Works?
     * FileWriter doesn't let you specify encoding, so it might default to system encoding (e.g., Windows-1252).
     * OutputStreamWriter(new FileOutputStream(...), StandardCharsets.UTF_8) ensures the file is written in UTF-8, supporting → and other Unicode characters.
     * */
    private static void writeToFile(String filename, String content, boolean append) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(filename, append), StandardCharsets.UTF_8))) {
            writer.write(content);
            writer.newLine();
            writer.flush();
        }
    }

}

//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.nio.file.StandardOpenOption;
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
//
///**
// * The key changes I made:
// *
// * Added all required imports, including the NIO file package imports needed for modern file operations.
// * Replaced the deprecated FileWriter(filename, append) constructor with the modern Files.writeString() method that uses StandardOpenOption parameters.
// * Used System.lineSeparator() instead of explicit newLine() calls for better cross-platform compatibility.
// * Simplified the code by directly writing the content with the line separator in a single operation.
// * Made the conditional logic in writeToFile() clearer with separate code paths for append vs. overwrite operations.
// *
// * This updated version should work correctly with Java 17 and follows the recommended modern practices for file I/O operations.
// * */
//
//// FileReporter Class compatible with Java 17
//public class FileReporter {
//    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//    private static final String REPORTS_DIR = "reports";
//    private static final String LOGS_DIR = "logs";
//
//    static {
//        // Create directories if they don't exist
//        new File(REPORTS_DIR).mkdirs();
//        new File(LOGS_DIR).mkdirs();
//    }
//
//    /**
//     * Saves a report to a file with the current date in the filename
//     *
//     * @param reportContent The content of the report
//     * @throws IOException If there's an error writing to the file
//     */
//    public static void saveReport(String reportContent) throws IOException {
//        String filename = REPORTS_DIR + File.separator + "reports-" + LocalDate.now().format(DATE_FORMATTER) + ".txt";
//        writeToFile(filename, reportContent, true);
//    }
//
//    /**
//     * Logs a transaction activity to a file with the current date in the filename
//     *
//     * @param activity The activity to log
//     * @throws IOException If there's an error writing to the file
//     */
//    public static void logActivity(String activity) throws IOException {
//        String filename = LOGS_DIR + File.separator + "transactions-" + LocalDate.now().format(DATE_FORMATTER) + ".txt";
//        writeToFile(filename, activity, true);
//    }
//
//    /**
//     * Writes content to a file
//     *
//     * @param filename The path to the file
//     * @param content The content to write
//     * @param append Whether to append to existing content (true) or overwrite (false)
//     * @throws IOException If there's an error writing to the file
//     */
//    private static void writeToFile(String filename, String content, boolean append) throws IOException {
//        Path path = Paths.get(filename);
//
//        if (append) {
//            // Create the file if it doesn't exist, then append to it
//            Files.writeString(
//                    path,
//                    content + System.lineSeparator(),
//                    StandardOpenOption.CREATE,
//                    StandardOpenOption.APPEND
//            );
//        } else {
//            // Create a new file or overwrite existing one
//            Files.writeString(
//                    path,
//                    content + System.lineSeparator(),
//                    StandardOpenOption.CREATE,
//                    StandardOpenOption.TRUNCATE_EXISTING
//            );
//        }
//    }
//}