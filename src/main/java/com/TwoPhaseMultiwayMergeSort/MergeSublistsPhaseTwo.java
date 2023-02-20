package com.TwoPhaseMultiwayMergeSort;

import com.TwoPhaseMultiwayMergeSort.util.DeleteFilesInDirectory;
import com.TwoPhaseMultiwayMergeSort.util.getFilesListFromDirectory;

import java.io.*;
import java.lang.reflect.Member;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MergeSublistsPhaseTwo {

    protected int total_records = 0;
    private static final int MAX_TUPLES_IN_BLOCK=40;

    protected static int MAIN_MEMORY = 51; // 51 blocks -> 1 block = 40 tuples , then 51 blocks = 40*51 =2040 tuples

//    private static final int MAX_TUPLES_IN_BLOCK=40;
//    private static int MAIN_MEMORY = 3; // 51 blocks -> 1 block = 40 tuples , then 51 blocks = 40*51 =2040 tuples

    protected int no_of_subLists = 0;

    static int iteration=0;

    static int writeCount =0;

    static int readCount =0;

    protected long mergeTime;


    //find the smallest tuple from all the sorted blocks in main memory, one tuple at a time
    public static int findListWithSmallestLine(List<List<String>> list){
        int smallest=0;
        for(int i=0;i< list.size();i++) {
            List<String> sub = list.get(i);
            if (sub.size() != 0){
                smallest=i;
                break;
            }


        }


        for(int i=smallest+1;i< list.size();i++){
            List<String> sub=list.get(i);
            if(sub.size()==0)
                continue;
            if(sub.get(0).compareTo(list.get(smallest).get(0))<1){
                smallest=i;
            }
        }
        return smallest;
    }

    //add 1 block~40 tuples at a time in main memory.
    public static void addLinesInBlock(   List<String> block, BufferedReader br){
        for(int t=0;t<40;t++){
            try {
                String line=br.readLine();
                if(line==null)break;
                block.add(line);



            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

    //checks if all the blocks in main memory are empty
    public static boolean checkIfListEmpty(  List<List<String>> memoryList){
//        boolean isEmpty=true;
        for(int i=0;i<memoryList.size();i++){
            if(memoryList.get(i).size()!=0){
                return false;
            }
        }
        return true;
    }



    // check if selected subLists are empty ie. when we covered all the tuples in a sublist
    public static boolean checkFilesEmpty(  BufferedReader brArr[],int from){
//        boolean isEmpty=true;
        for(int i=from;i<brArr.length && i<from+MAIN_MEMORY-1;i++){
            if(brArr[i].lines().count()!=0){
                return false;
            }
        }
        return true;
    }


    //write output buffer to disk
    public static void writeFile(String filePath,List<String> list) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));

        for(int lineNo=0;lineNo<40;lineNo++){
            if(list.isEmpty() || list.size()<=lineNo)break;
            try {
                bw.write(list.get(lineNo));
                bw.newLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        }

        bw.close();

    }

    public static void writeFileOP(BufferedWriter bw,List<String> list) throws IOException {


        for(String l:list) {
            bw.append(l);
            bw.newLine();
        }

//            if(list.isEmpty() || list.size()<=lineNo)break;
//            try {
//
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }


//        }



    }

    //merging the sublists using k-way alogrithm
    public  int mergeSublists(List<Path> listOfSubLists,File opDir) {
        System.out.println(listOfSubLists);
        List<Path> newList=new ArrayList<>();
        for(Path p:listOfSubLists){
            newList.add(p);
        }
        BufferedWriter bw=null;


        BufferedReader brArr[]=new BufferedReader[listOfSubLists.size()];
    int countSubs=0;

        for(int i=0;i<listOfSubLists.size();i++) {
            try {
                brArr[i]=new BufferedReader(new FileReader(listOfSubLists.get(i).toString()));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        int curSubList=0;
        while(curSubList<listOfSubLists.size()){

            List<List<String>> memoryList=new ArrayList<>(MAIN_MEMORY);
            List<String> opBufferList=new ArrayList<>();
//            memoryList.add(MAIN_MEMORY-1,);
            for(int cur=curSubList;cur<curSubList+MAIN_MEMORY-1;cur++){
                if(brArr.length==cur)break;
    if( brArr[cur]==null)break;
                List<String> block=new ArrayList<>(40);

                //
                addLinesInBlock(block,brArr[cur]);

                memoryList.add(block);

//            memoryList.get(i)

            }
            String currentMergedFile =System.getProperty("user.dir") +System.getProperty("file.separator")+"buffer" +System.getProperty("file.separator")+ iteration + "-sublist-" + countSubs + "_" + (countSubs+1);

            try {
                 bw = new BufferedWriter(new FileWriter(currentMergedFile));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if(memoryList.size()==1){
                //write in disk

                try {
                    writeFileOP(bw,memoryList.get(0));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                countSubs++;
            }else{
//                String currentMergedFile =System.getProperty("user.dir") +System.getProperty("file.separator")+"buffer" +System.getProperty("file.separator")+ iteration + "-sublist-" + countSubs + "_" + (countSubs+1);

                while(true){

                    if(checkIfListEmpty(memoryList) && checkFilesEmpty(brArr,curSubList)){
                        if(!opBufferList.isEmpty()){

                            try {
                                writeFileOP(bw,opBufferList);
                                opBufferList.clear();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        break;
                    }


                    int blockNo=findListWithSmallestLine(memoryList);
                    List<String> blockOfLines=memoryList.get(blockNo);
                    if(blockNo==0 && blockOfLines.size()==0)break;
                    String line=memoryList.get(blockNo).get(0);

//                    if(blockNo==0 && line)

                    //write line in op buffer

                  opBufferList.add(line);
                    memoryList.get(blockNo).remove(0);

                    if(memoryList.get(blockNo).size()==0){
                        addLinesInBlock(memoryList.get(blockNo),brArr[blockNo]);
                    }

                    if(opBufferList.size()==40){
                        //write in disk
//                         currentMergedFile =System.getProperty("user.dir") +System.getProperty("file.separator")+"buffer" +System.getProperty("file.separator")+ iteration + "-sublist-" + countSubs + "_" + (countSubs+1);
                        try {
                            writeFileOP(bw,opBufferList);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        opBufferList.clear();

                    }

                }


            }

            System.out.println(countSubs);
            countSubs++;
            curSubList+=(MAIN_MEMORY-1);
            try {
                bw.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        for(BufferedReader br:brArr){
            try {
                br.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("del");
        for(Path p:newList){
            DeleteFilesInDirectory.deleteFile(opDir.getPath(),new File(p.toString()).getName());
        }



//return 1;
        if (opDir.listFiles().length > 1) {
            iteration++;
            try {
                return mergeSublists(getFilesListFromDirectory.getFilesList(opDir.getPath()),opDir);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            return opDir.listFiles().length;
        }


    }

//    public int mergeSortToOneFile(List<Path> listOfSubLists,File buffer) throws IOException {
//        long itertionStart = System.currentTimeMillis();
//        System.lineSeparator();
//        String recordFromFile1 = null;
//        String recordFromFile2 = null;
//        File file1 = null;
//        File file2 = null;
//        int tuplesCount1 =0;
//        int tuplesCount2 =0;
//        int write = 0;
//
//
//        for(int i=0;i<listOfSubLists.size();i=i+2) { //i=0 0,1    i=2 2,3 ...
//            String currentMergedFile =System.getProperty("user.dir") +System.getProperty("file.separator")+"buffer" +System.getProperty("file.separator")+ iteration + "-sublist-" + i + "_" + (i+1);
//
//            file1 = new File(listOfSubLists.get(i).toString()); //sublist1
//            if((i+1)<listOfSubLists.size())
//            {
//                file2 = new File(listOfSubLists.get(i+1).toString()); //sublist2
//            }
//            else
//            {
//                file2 = null;
//            }
//
//            try {
//
//                BufferedReader br1 = new BufferedReader(new FileReader(file1));
//                BufferedReader br2=null;
//
//                if(file2!=null )
//                {
//                    br2 = new BufferedReader(new FileReader(file2));
//                }
//
//                BufferedWriter bw = new BufferedWriter(new FileWriter(currentMergedFile));
//
//                if(br1!=null && br2!=null)
//                {
//                    recordFromFile1 = br1.readLine();
//                    recordFromFile2 = br2.readLine();
//                    while (true) {
//                        if(recordFromFile1==null){ //if no data left in file1 to mergex`
//                            while(recordFromFile2 != null){
//                                bw.write(recordFromFile2);
//                                bw.newLine();
//                                write++;
//                                if(write == MAX_TUPLES_IN_BLOCK) {
//                                    ++writeCount;
//                                    write = 0;
//                                }
//                                recordFromFile2 = br2.readLine();
//                            }
//                            break;
//                        }else if(recordFromFile2==null){ //if no data left in file2 to merge
//                            while(recordFromFile1!= null){
//                                bw.write(recordFromFile1);
//                                bw.newLine();
//                                write++;
//                                if(write == MAX_TUPLES_IN_BLOCK) {
//                                    ++writeCount;
//                                    write = 0;
//                                }
//                                recordFromFile1 = br1.readLine();
//                            }
//                            break;
//                        }
//                        //if both records are equal
//                        if (recordFromFile1.equals(recordFromFile2)) {
//                            bw.write(recordFromFile1); //write first record
//                            ++total_records; //to print total records
//                            ++tuplesCount1;
//                            bw.newLine();
//                            write++;
//
//                            bw.write(recordFromFile2); //write second record
//                            ++total_records; //to print total records
//                            ++tuplesCount2;
//                            bw.newLine();
//                            write++;
//
//
//                            if(write == MAX_TUPLES_IN_BLOCK) {
//                                ++writeCount;
//                                write = 0;
//                            }
//
//                            recordFromFile1 = br1.readLine();
//                            recordFromFile2 = br2.readLine();
//                        } else if (recordFromFile1.compareTo(recordFromFile2) < 0)  //if record in file 1 is less than record in file2
//                        {
//                            bw.write(recordFromFile1);
//                            bw.newLine();
//                            write++;
//                            if(write == MAX_TUPLES_IN_BLOCK) {
//                                ++writeCount;
//                                write = 0;
//                            }
//
//                            ++total_records; //to print total records
//                            ++tuplesCount1;
//                            recordFromFile1 = br1.readLine();
//                        } else if (recordFromFile1.compareTo(recordFromFile2) > 0) //if record in file 1 is greater than record in file2
//                        {
//                            bw.write(recordFromFile2);
//                            bw.newLine();
//                            write++;
//                            if(write == MAX_TUPLES_IN_BLOCK) {
//                                ++writeCount;
//                                write = 0;
//                            }
//
//                            ++total_records; //to print total records
//                            ++tuplesCount2;
//                            recordFromFile2 = br2.readLine();
//                        }
//
//                        if (tuplesCount1 == MAX_TUPLES_IN_BLOCK || tuplesCount2 == MAX_TUPLES_IN_BLOCK) {
//                            ++readCount;
//                            tuplesCount1 = 0;
//                            ++readCount;
//                            tuplesCount2= 0;
//                        }
//                    }
//                    bw.close();
//                    br1.close();
//                    br2.close();
//                    //delete processed subList
//                    DeleteFilesInDirectory.deleteFile(buffer.getPath(), file1.getName());
//                    DeleteFilesInDirectory.deleteFile(buffer.getPath(), file2.getName());
//                }
//                else if(br1!=null) {  //if only one subList left at the end
//                    while ((recordFromFile1 = br1.readLine()) != null) {
//                        bw.write(recordFromFile1);
//                        bw.newLine();
//                        write++;
//                        if(write == MAX_TUPLES_IN_BLOCK) {
//                            ++writeCount;
//                            write = 0;
//                        }
//
//                        ++total_records; //to print total records
//                        ++tuplesCount1;
//                    }
//                    bw.close();
//                    br1.close();
//                    //delete processed subList
//                    DeleteFilesInDirectory.deleteFile(buffer.getPath(), file1.getName());
//                }
//
//
//            } catch (FileNotFoundException e) {
//                throw new RuntimeException(e);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }
//
//        mergeTime  = mergeTime + (System.currentTimeMillis() - itertionStart);
//        System.out.println(
//                "Phase 2 merge time per iteration " + iteration + " : " + (System.currentTimeMillis() - itertionStart)
//                        + "ms" + "(" + "~approx " + (System.currentTimeMillis() - itertionStart) / 1000.0 + "sec)");
//
//        if (buffer.listFiles().length > 1) {
//            iteration++;
//            return mergeSortToOneFile(getFilesListFromDirectory.getFilesList(buffer.getPath()),buffer);
//        } else {
//            return buffer.listFiles().length;
//        }
//    }

}