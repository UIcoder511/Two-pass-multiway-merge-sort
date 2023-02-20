package com.TwoPhaseMultiwayMergeSort.util;

import java.io.File;

public class DeleteFilesInDirectory {

    public static void deleteFile(String pathToDirectory,String fileName)
    {
        File directory = new File(pathToDirectory);
        // Get all files in the directory
        File[] files = directory.listFiles();

        // Delete each file
        for (File file : files) {
            if(file.getName().equals(fileName))
            {
                if (file.isFile()) {
                    //System.out.println(fileName+ " file deleted "+file.delete());
                    file.delete();
                    break;
                }
            }

        }
    }
}