package com.TwoPhaseMultiwayMergeSort.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/** This is a Java method for copying a directory and all of its contents from a source path to a target path. The method uses the Files.walk method to traverse the source directory and all of its subdirectories, and for each file or directory found, it creates the corresponding file or directory in the target path using the Files.createDirectories and Files.copy methods.**/

public class CopyDirectoryUtility {

    public static void copyDirectory(Path source, Path target) throws IOException {
        Files.walk(source)
                .forEach(sourcePath -> {
                    Path targetPath = target.resolve(source.relativize(sourcePath));
                    try {
                        if (Files.isDirectory(sourcePath)) {
                            Files.createDirectories(targetPath);
                        } else {
                            Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING); //this will replace existing files with new ones
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }
}
