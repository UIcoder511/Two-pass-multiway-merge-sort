package com.TwoPhaseMultiwayMergeSort.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class getFilesListFromDirectory {

    public static List<Path> getFilesList(String pathToDirectory) throws IOException {
        // specify the folder path
        Path folderPath = Paths.get(pathToDirectory);
        return Files.list(folderPath).collect(Collectors.toList());
    }
}
