package com.TwoPhaseMultiwayMergeSort;

import com.TwoPhaseMultiwayMergeSort.util.DeleteDirectory;
import com.TwoPhaseMultiwayMergeSort.util.getFilesListFromDirectory;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

public class SublistCreationPhaseOne {

    static File buffer = null;
    protected int no_of_subLists = 0;
    protected long totalTime;
    protected long sortTime;
    protected long readTime;
    protected long writeTime;
    protected int total_records = 0;
//
//    static File buffer1 = null;
//
//    String buffer1Loc = System.getProperty("user.dir") + "/buffer1";

//    String buffer2Loc = System.getProperty("user.dir") + "/buffer2";

    //    static File buffer2 = null;
    String bufferLoc = System.getProperty("user.dir") + "/buffer";

    private static String megabyteString(long bytes) {
        return String.format("%.1f", ((float) bytes) / 1024 / 1024);
    }

    private static void printUsedMemory() {
        Runtime run = Runtime.getRuntime();
        long free = run.freeMemory();
        long total = run.totalMemory();
        long max = run.maxMemory();
        long used = total - free;
        System.out.println("Memory: used " + megabyteString(used) + "M"
                + " free " + megabyteString(free) + "M"
                + " total " + megabyteString(total) + "M"
                + " max " + megabyteString(max) + "M");
    }

    public static void writeFinalResultsI(Path path) throws IOException {

        String record = null;
        BufferedReader br = new BufferedReader(new FileReader(path.toString()));

        String outputFile = System.getProperty("user.dir") + "/buffer/finalOutputFile.txt";
        BufferedWriter out = new BufferedWriter(new FileWriter(outputFile));

        Map<String, Integer> tupleCountWithRecordMap = new TreeMap<String, Integer>();

        while ((record = br.readLine()) != null) {
            if (!tupleCountWithRecordMap.containsKey(record)) {
                tupleCountWithRecordMap.put(record, 1);
            } else {
                tupleCountWithRecordMap.put(record, tupleCountWithRecordMap.get(record) + 1);
            }
        }

        for (Map.Entry<String, Integer> m : tupleCountWithRecordMap.entrySet()) {
            out.write(m.getKey() + ":" + m.getValue());
            out.newLine();
        }

        br.close();
        out.close();

    }

    public static void main(String args[]) throws IOException {
        printUsedMemory();

        MergeSublistsPhaseTwo mergeSortPhaseTwo = new MergeSublistsPhaseTwo();

        Scanner sc = new Scanner(System.in);
        System.out.println("Please enter file1 location:");
        String file1_loc = sc.nextLine();
        System.out.println("Please enter file2 location:");
        String file2_loc = sc.nextLine();

        long phase1Time = 0;
        SublistCreationPhaseOne spo = new SublistCreationPhaseOne();
        //sort the files and create sorted sublists (runs) & store it in buffer1 folder- content of file1 and buffer folder- content of file2
        if (file1_loc != null && file2_loc != null) {

            spo.emptyBuffer();
            System.out.println("--------------Phase 1 for Relation 1----------------");
            spo.sortFile(file1_loc, "R1"); // D:\Masters\COMP 6521- DB\Sample Project\r1.txt
            int blockCount1 = 0;
            int recordCountForR1 = spo.total_records;
            int numberOfSublistsForR1 = spo.no_of_subLists;
            if (recordCountForR1 % Constants.MAX_TUPLES_IN_BLOCK == 0) {
                blockCount1 = recordCountForR1 / Constants.MAX_TUPLES_IN_BLOCK;
            } else {
                blockCount1 = (recordCountForR1 / Constants.MAX_TUPLES_IN_BLOCK) + 1;

            }
            System.out.println("Records in  R1 : " + recordCountForR1);
            System.out.println("Number of subLists for R1 : " + numberOfSublistsForR1);
            System.out.println("Number of Blocks for R1 : " + blockCount1);
            System.out.println("Total Time for reading Blocks for R1 : " + spo.readTime + " ms");
            System.out.println("Total Time for sorting Blocks for R1 : " + spo.sortTime + " ms");
            System.out.println("Total Time for writing Blocks for R1 : " + spo.writeTime + " ms");
            System.out.println("Block transfer time (t) for R1 : " + spo.readTime + " ms");
            System.out.println("Total Time for R1 : " + spo.totalTime);
            phase1Time += spo.totalTime;
//            //transfer the content of buffer to buffer1 to avoid overwriting by second file subLists.
//            Path source = Paths.get(spo.bufferLoc);
//            Path target = Paths.get(spo.buffer1Loc);
//            CopyDirectoryUtility.copyDirectory(source, target);

            System.out.println("--------------Phase 1 for Relation 2----------------");
            spo.sortFile(file2_loc, "R2");
            int blockCount2 = 0;
            int recordCountForR2 = spo.total_records - recordCountForR1;
            if (recordCountForR2 % Constants.MAX_TUPLES_IN_BLOCK == 0) {
                blockCount2 = recordCountForR2 / Constants.MAX_TUPLES_IN_BLOCK;
            } else {
                blockCount2 = (recordCountForR2 / Constants.MAX_TUPLES_IN_BLOCK) + 1;

            }
            System.out.println("Records in  R2 : " + recordCountForR2);
            System.out.println("Number of subLists for R2 : " + (spo.no_of_subLists - numberOfSublistsForR1));
            System.out.println("Number of Blocks for R2 : " + blockCount2);
            System.out.println("Total Time for reading Blocks for R2 : " + spo.readTime + " ms");
            System.out.println("Total Time for sorting Blocks for R2 : " + spo.sortTime + " ms");
            System.out.println("Total Time for writing Blocks for R2 : " + spo.writeTime + " ms");
            System.out.println("Block transfer time (t) for R2 : " + spo.readTime + " ms");
            System.out.println("Total Time for R2 : " + spo.totalTime);
            phase1Time += spo.totalTime;

            System.out.println("Total number of records in R1 and R2: " + spo.total_records);
            System.out.println("Total Number of sublist " + spo.no_of_subLists);

            int diskIOPhaseOne = 2 * (blockCount1 + blockCount2) / Constants.MAIN_MEMORY;
            System.out.println("Total number of disk I/Os in phase1:  " + diskIOPhaseOne);
            System.out.println("Time take to sort relation R1 and R2 is " + phase1Time + "ms" + "(" + "~approx " + phase1Time / 1000.0 + "sec)");


            //merge the sorted sublists till we get 1 sorted file
            boolean run = true;
            while (run) {
                if (buffer.exists()) {
                    List<Path> subLists = null;

                    subLists = getFilesListFromDirectory.getFilesList(buffer.getPath()); //get file list from buffer folder

                    if (subLists.size() > 1) {
                        System.out.println("--------------Phase 2-Merge----------------");
                        if (mergeSortPhaseTwo.mergeSublists(subLists, buffer) == 1)
                            break;

                    }

                }
            }

            List<Path> subLists = getFilesListFromDirectory.getFilesList(buffer.getPath()); //get file list from buffer folder

            writeFinalResultsI(subLists.get(0));


            int diskIOForPhaseTwo = mergeSortPhaseTwo.readCount + mergeSortPhaseTwo.writeCount;
            System.out.println("Total Time for reading Blocks  : " + mergeSortPhaseTwo.readTime + "ms");
            System.out.println("Total Time for writing Blocks  : " + mergeSortPhaseTwo.writeTime + "ms");
            System.out.println("Phase 2 Time : " + (mergeSortPhaseTwo.readTime + mergeSortPhaseTwo.writeTime) + "ms" + " ("
                    + (mergeSortPhaseTwo.readTime + mergeSortPhaseTwo.writeTime) / 1000.0 + " sec)");
            System.out.println("Merge Phase Disk I/Os :" + diskIOForPhaseTwo);
            System.out.println("Phase 2: total sublists :" + mergeSortPhaseTwo.no_of_subLists);
            System.out.println("--------------Point 3- assignment----------------");

            //read output file to count number of records
            Path filePath = getFilesListFromDirectory.getFilesList(buffer.getPath()).get(0);
            int recordCount = 0;
            try (BufferedReader br = new BufferedReader(new FileReader(filePath.toString()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    recordCount++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


            System.out.println(
                    "Total Number of Records in Output File: " + recordCount);

            int blockCountOutput = 0;
            int recordCountOutput = recordCount;
            if (recordCountOutput % Constants.MAX_TUPLES_IN_BLOCK == 0) {
                blockCountOutput = recordCountOutput / Constants.MAX_TUPLES_IN_BLOCK;
            } else {
                blockCountOutput = (recordCountOutput / Constants.MAX_TUPLES_IN_BLOCK) + 1;

            }

            System.out.println("Total number of Blocks: " + blockCountOutput);

//            System.out.println(
//                    "Total time  Phase 2 : " + (mergeSortPhaseTwo.mergeTime) + "ms(~approx" + ((mergeSortPhaseTwo.mergeTime) / 1000.0) + " sec");
            System.out.println("Total time Phase 1 & Phase 2 : "
                    + ((mergeSortPhaseTwo.readTime + mergeSortPhaseTwo.writeTime + phase1Time) / 1000.0) + " sec");
            System.out.println(
                    "Total Number of I/O : " + (diskIOPhaseOne + diskIOForPhaseTwo));

        }

    }

    private void emptyBuffer() throws IOException {
        buffer = new File(bufferLoc); //to store subLists
//        buffer1 = new File(buffer1Loc); //to store subLists, so that it cannot be overwritten by another
//        buffer2 = new File(buffer2Loc);
        if (!buffer.exists()) {
            buffer.mkdir();
        } else if (buffer.isDirectory()) {
            DeleteDirectory.deleteDir(String.valueOf(buffer.toPath()));
            buffer.mkdir();
        }
    }

    private List<String> sortFile(String fileLoc, String fileName) throws FileNotFoundException {

        totalTime = 0;
        sortTime = 0;
        readTime = 0;
        writeTime = 0;

        String record = null;
        BufferedReader br = new BufferedReader(new FileReader(fileLoc));
        List<String> tempSubListsLoc = new ArrayList<String>();
        int j = 0; //this is used to suffix subList number


        int tuplesCount = 0;

        do {
            try {
                List<String> subList = new ArrayList<String>(); //create new subList after reading data in 51 blocks
//____________________start timer for read blocks_________
                long tr1 = System.currentTimeMillis();
//                for (int i = 0; i < MAX_RECORDS; i++) {
                while ((record = br.readLine()) != null) {
                    subList.add(record); //add a tuple or record in subList- 40.
                    ++total_records; //to print total records
                    ++tuplesCount;
                    if (tuplesCount == Constants.MAX_TUPLES_IN_BLOCK * Constants.MAIN_MEMORY) {
                        tuplesCount = 0;
                        break;
                    }
                }
                long tr2 = System.currentTimeMillis();
                readTime += (tr2 - tr1);

                //____________________stop timer for read blocks_________
                //____________________start timer for sort blocks_________
                long ts1 = System.currentTimeMillis();
                //sort the records based on whole string
                Collections.sort(subList);
                //____________________stop timer for sort blocks_________
                long ts2 = System.currentTimeMillis();
                sortTime += (ts2 - ts1);
                //subLists.add((ArrayList<String>) subList); // added sorted subList to subLists.

                //add this sorted list to buffer
                String outputFile = System.getProperty("user.dir") + "/buffer/sublist-" + fileName + "-" + ++j;
                BufferedWriter out = new BufferedWriter(new FileWriter(outputFile));

                //____________________start timer for write blocks_________
                long tw1 = System.currentTimeMillis();
                for (int i = 0; i < subList.size(); i++) {
                    out.write(subList.get(i));
                    out.newLine();
                }
                //____________________stop timer for write blocks_________
                long tw2 = System.currentTimeMillis();
                writeTime += (tw2 - tw1);
                out.close();
                tempSubListsLoc.add(outputFile);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } while (record != null);

        no_of_subLists = no_of_subLists + tempSubListsLoc.size();
        totalTime += (readTime + sortTime + writeTime);
//        long endTime = System.currentTimeMillis();
//        sort_time = sort_time + (endTime-beginTime);
//        System.out.println("Time take to sort relation "+fileName+" is "+(endTime-beginTime)+"ms"+"("+"~approx "+(endTime-beginTime)/1000.0+"sec)");

        return tempSubListsLoc;

    }
}
