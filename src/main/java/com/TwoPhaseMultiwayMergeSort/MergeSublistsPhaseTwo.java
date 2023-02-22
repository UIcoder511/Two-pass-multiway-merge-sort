package com.TwoPhaseMultiwayMergeSort;

import com.TwoPhaseMultiwayMergeSort.util.DeleteFilesInDirectory;
import com.TwoPhaseMultiwayMergeSort.util.getFilesListFromDirectory;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class MergeSublistsPhaseTwo {

    protected static long readTime;
    protected static long writeTime;
    static int iteration = 0;
    static int writeCount = 0;
    static int readCount = 0;
    protected int total_records = 0;
    protected int no_of_subLists = 0;
    protected long mergeTime;


    //find the smallest tuple from all the sorted blocks in main memory, one tuple at a time
    public static int findListWithSmallestLine(List<List<String>> list) {
        int smallest = 0;
        for (int i = 0; i < list.size(); i++) {
            List<String> sub = list.get(i);
            if (sub.size() != 0) {
                smallest = i;
                break;
            }


        }


        for (int i = smallest + 1; i < list.size(); i++) {
            List<String> sub = list.get(i);
            if (sub.size() == 0)
                continue;
            if (sub.get(0).compareTo(list.get(smallest).get(0)) < 1) {
                smallest = i;
            }
        }
        return smallest;
    }

    //add 1 block~40 tuples at a time in main memory.
    public static void addLinesInBlock(List<String> block, BufferedReader br) {
//        long sum = 0;

        readCount++;
        for (int t = 0; t < Constants.MAX_TUPLES_IN_BLOCK; t++) {
            try {
                long tr1 = System.currentTimeMillis();
                String line = br.readLine();
                long tr2 = System.currentTimeMillis();
                readTime += (tr2 - tr1);
                if (line == null) break;

                block.add(line);
//                sum++;

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
//        System.out.println("Added lines in block :" + sum);
    }

    //checks if all the blocks in main memory are empty
    public static boolean checkIfListEmpty(List<List<String>> memoryList) {
//        boolean isEmpty=true;
        for (int i = 0; i < memoryList.size(); i++) {
            if (memoryList.get(i).size() != 0) {
                return false;
            }
        }
        return true;
    }


    // check if selected subLists are empty ie. when we covered all the tuples in a sublist
    public static boolean checkFilesEmpty(BufferedReader brArr[], int from) {
//        boolean isEmpty=true;
        for (int i = from; i < brArr.length && i < from + Constants.MAIN_MEMORY - 1; i++) {
            if (brArr[i].lines().count() != 0) {
                return false;
            }
        }
        return true;
    }


    //write output buffer to disk
    public static void writeFile(String filePath, List<String> list) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));

        for (int lineNo = 0; lineNo < Constants.MAX_TUPLES_IN_BLOCK; lineNo++) {
            if (list.isEmpty() || list.size() <= lineNo) break;
            try {
                bw.write(list.get(lineNo));
                bw.newLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        }

        bw.close();

    }

    public static void writeEntireFile(BufferedWriter bw, BufferedReader br) throws IOException {
//        System.out.println("wriitng final entire");
        int count = 0;
        String line;
        long tr1 = System.currentTimeMillis();
        line = br.readLine();
        long tr2 = System.currentTimeMillis();
        readTime += (tr2 - tr1);

        while ((line) != null) {


//            String s = sc.nextLine();

            tr1 = System.currentTimeMillis();
            bw.append(line);
            bw.newLine();
            tr2 = System.currentTimeMillis();
            writeTime += (tr2 - tr1);
            count++;

            tr1 = System.currentTimeMillis();
            line = br.readLine();
            tr2 = System.currentTimeMillis();
            readTime += (tr2 - tr1);
        }
        readCount += Math.ceil(count / Constants.MAX_TUPLES_IN_BLOCK);
        writeCount += Math.ceil(count / Constants.MAX_TUPLES_IN_BLOCK);
//        for (String l : list) {
//
//            bw.newLine();
//        }
    }

    public static void writeFileOP(BufferedWriter bw, List<String> list) throws IOException {

//        System.out.println("wriitng " + list.size());
        writeCount++;
        for (String l : list) {

            long tr1 = System.currentTimeMillis();
            bw.append(l);
            bw.newLine();
            long tr2 = System.currentTimeMillis();
            writeTime += (tr2 - tr1);
        }


    }


    //merging the sublists using k-way alogrithm
    public int mergeSublists(List<Path> listOfSubLists, File opDir) {
//        System.out.println(listOfSubLists);
        List<Path> newList = new ArrayList<>();
        for (Path p : listOfSubLists) {
            newList.add(p);
        }
        BufferedWriter bw = null;


        BufferedReader brArr[] = new BufferedReader[listOfSubLists.size()];
        int countSubs = 0;

//        List<Integer> emptyBuffers=new ArrayList<>();
//
        for (int i = 0; i < listOfSubLists.size(); i++) {
            try {
                brArr[i] = new BufferedReader(new FileReader(listOfSubLists.get(i).toString()));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        int curSubList = 0;
        while (curSubList < listOfSubLists.size()) {
//            System.out.println("MOVING _TOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO - " + curSubList);

            List<List<String>> memoryList = new ArrayList<>(Constants.MAIN_MEMORY);
            List<String> opBufferList = new ArrayList<>();
//            System.out.println("curSubList + " + curSubList);
//            memoryList.add(MAIN_MEMORY-1,);
            for (int cur = curSubList; cur < curSubList + Constants.MAIN_MEMORY - 1; cur++) {
                if (brArr.length == cur) break;
                if (brArr[cur] == null) break;
                List<String> block = new ArrayList<>(Constants.MAX_TUPLES_IN_BLOCK);

                //
                addLinesInBlock(block, brArr[cur]);
//                System.out.println("added Block in main file + " + cur);
                memoryList.add(block);

//            memoryList.get(i)

            }
            ///////////////////////

//            long sum = 0;
//            for (List<String> block : memoryList) {
//                sum += block.size();
//            }
//            System.out.println("in memory + " + sum);

            ///////////////////////

            String currentMergedFile = System.getProperty("user.dir") + System.getProperty("file.separator") + "buffer" + System.getProperty("file.separator") + iteration + "-sublist-" + countSubs + "_" + (countSubs + 1);
//            System.out.println(currentMergedFile);
            try {
                bw = new BufferedWriter(new FileWriter(currentMergedFile));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            if (memoryList.size() == 1) {
                //write in disk

                try {
                    writeFileOP(bw, memoryList.get(0));
                    writeEntireFile(bw, brArr[listOfSubLists.size() - 1]);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                memoryList.clear();


//                countSubs++;
            } else {
//                String currentMergedFile =System.getProperty("user.dir") +System.getProperty("file.separator")+"buffer" +System.getProperty("file.separator")+ iteration + "-sublist-" + countSubs + "_" + (countSubs+1);

                while (true) {

                    if (checkIfListEmpty(memoryList) && checkFilesEmpty(brArr, curSubList)) {
                        if (!opBufferList.isEmpty()) {

                            try {
                                writeFileOP(bw, opBufferList);
                                opBufferList.clear();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        break;
                    }


                    int blockNo = findListWithSmallestLine(memoryList);
                    List<String> blockOfLines = memoryList.get(blockNo);
                    if (blockNo == 0 && blockOfLines.size() == 0) continue;
                    String line = memoryList.get(blockNo).get(0);
//                    if(blockNo==0 && line)

                    //write line in op buffer
//                    System.out.println("MMM " + memoryList.get(0).size() + " " + memoryList.get(1).size());
                    opBufferList.add(line);
                    memoryList.get(blockNo).remove(0);

                    if (memoryList.get(blockNo).size() == 0) {
//                        System.out.println("adding in empty + " + blockNo);
                        addLinesInBlock(memoryList.get(blockNo), brArr[blockNo + curSubList]);
                    }

                    if (opBufferList.size() == Constants.MAX_TUPLES_IN_BLOCK) {
//                        System.out.println(Constants.MAX_TUPLES_IN_BLOCK + "- " + blockNo);
                        //write in disk
//                         currentMergedFile =System.getProperty("user.dir") +System.getProperty("file.separator")+"buffer" +System.getProperty("file.separator")+ iteration + "-sublist-" + countSubs + "_" + (countSubs+1);
                        try {
                            writeFileOP(bw, opBufferList);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        opBufferList.clear();

                    }

                }


            }

//            System.out.println(countSubs);
//            System.out.println(memoryList);
//            checkFile(currentMergedFile);
            countSubs++;
            no_of_subLists++;
            curSubList += (Constants.MAIN_MEMORY - 1);
            try {
                bw.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        for (BufferedReader br : brArr) {
            try {
                br.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
//        System.out.println("del");
        for (Path p : newList) {
            DeleteFilesInDirectory.deleteFile(opDir.getPath(), new File(p.toString()).getName());
        }


//        return 1;
        if (opDir.listFiles().length > 1) {
            iteration++;
            try {
                return mergeSublists(getFilesListFromDirectory.getFilesList(opDir.getPath()), opDir);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            return opDir.listFiles().length;
        }


    }


}
