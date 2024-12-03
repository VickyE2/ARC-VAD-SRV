package com.arcvad.schoolquest.server.server.DataFormat.Converter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FolderZipper {

    /**
     * Compresses a folder into a ZIP file.
     *
     * @param sourceFolderPath Path to the folder to be compressed.
     * @param zipFilePath Path where the ZIP file should be saved.
     * @return true if the compression is successful, false otherwise.
     */
    public static boolean compressFolder(String sourceFolderPath, String zipFilePath) {
        Path sourcePath = Paths.get(sourceFolderPath);

        // Check if source directory exists
        if (!Files.exists(sourcePath) || !Files.isDirectory(sourcePath)) {
            System.err.println("Source folder does not exist: " + sourceFolderPath);
            return false;
        }

        Path zipFileDir = Paths.get(zipFilePath).getParent();

        // Ensure the parent directories for the ZIP file exist
        try {
            if (zipFileDir != null && !Files.exists(zipFileDir)) {
                Files.createDirectories(zipFileDir);
            }
        } catch (IOException e) {
            return false;
        }

        try (FileOutputStream fos = new FileOutputStream(zipFilePath);
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            Files.walk(sourcePath)
                .filter(path -> !Files.isDirectory(path)) // Skip directories, only add files
                .forEach(path -> {
                    String zipEntryPath = sourcePath.relativize(path).toString(); // Relative path inside ZIP
                    try {
                        zos.putNextEntry(new ZipEntry(zipEntryPath));
                        Files.copy(path, zos);
                        zos.closeEntry();
                    } catch (IOException e) {
                        throw new UncheckedIOException("Failed to add file to ZIP: " + path, e);
                    }
                });
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Deletes a folder and its contents.
     *
     * @param folderPath Path to the folder to be deleted.
     * @return true if the deletion is successful, false otherwise.
     */
    public static boolean deleteFolder(String folderPath) {
        Path folder = Paths.get(folderPath);

        // Check if folder exists
        if (!Files.exists(folder)) {
            return false;
        }

        try {
            Files.walk(folder)
                .sorted(Comparator.reverseOrder()) // Reverse order: files first, then directories
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        throw new UncheckedIOException("Failed to delete: " + path, e);
                    }
                });
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
