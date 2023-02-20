package com.TwoPhaseMultiwayMergeSort.util;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class DeleteDirectory {

    public static void deleteDir(String pathToDirectory) throws IOException {
        // specify the folder path
        Path folderPath = Paths.get(pathToDirectory);
        try {
            Files.walkFileTree(folderPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
            System.out.println("Directory deleted successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
