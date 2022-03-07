package com.progressoft.tools;



import java.io.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.*;


public class NormalizerImpl implements Normalizer {

    @Override
    public ScoringSummary zscore(Path csvPath, Path destPath, String colToStandardize) {
        ScoringSummary summary = null;
        String type="_z";
        summary = generateScoringSummary(csvPath,destPath, colToStandardize,type);
        return summary;
    }
    @Override
    public ScoringSummary minMaxScaling(Path csvPath, Path destPath, String colToNormalize) {
        ScoringSummary summary = null;
        String type="_mm";
        summary = generateScoringSummary(csvPath,destPath, colToNormalize,type);
        return summary;
    }
//    public BigDecimal[] converToBigarr(String[] arr){
//        BigDecimal[] bigDecimals=new BigDecimal[arr.length];
//        for (int i = 0; i < arr.length; i++) {
//           bigDecimals[i]=new BigDecimal(arr[i]);
//        }
//        return bigDecimals;
//    }
    public ScoringSummary generateScoringSummary(Path csvPath,Path destPath, String colToStandardize,String type){
        ScoringSummary summary=null;
        String[] nextRecord;
        ArrayList<BigDecimal>dataOfCol=new ArrayList<>();
        ArrayList<String[]> arrayListAll=new ArrayList<>();
        Integer idx=null;
        try {
            List<String>   csvReaders = Files.readAllLines(csvPath);
            for (String lineString : csvReaders) {
                nextRecord=lineString.split(",");
                arrayListAll.add(nextRecord);
                if(idx==null){
                    for (int i = 0; i < nextRecord.length; i++) {
                        if(nextRecord[i].equals(colToStandardize)){
                            idx=i;
                        }
                    }
                    if(idx==null){
                        throw new IllegalArgumentException("column "+colToStandardize+" not found");
                    }
                }else {
                    dataOfCol.add(new BigDecimal(nextRecord[idx]));
                }
            }
        } catch (IOException ex) {
            throw new IllegalArgumentException("source file not found");
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
        }
        if (!dataOfCol.isEmpty()){
            summary=new ScoringSummaryImp(dataOfCol);
        }
        generateFile(destPath,arrayListAll,summary,colToStandardize,type);
//        checkExist(destPath,arrayListAll,summary,colToStandardize,type);


        return summary;

    }
    public File checkExist(Path destPath){
        File file=new File(String.valueOf(destPath));
        if (!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.getMessage();
            }
        }
       return file;
    }

    public  void generateFile(Path destPath,ArrayList<String[]> arrayListAll,ScoringSummary summary,String colToStandardize,String type){
        int idxOfCol = 0;
        boolean addCol=false;
        File file=checkExist(destPath);
        try {
            FileOutputStream fileOutputStream=new FileOutputStream(file);
            for (int i = 0; i < arrayListAll.size(); i++) {
                for (int j = 0; j < arrayListAll.get(i).length; j++) {
                    fileOutputStream.write(arrayListAll.get(i)[j].getBytes());
                    if (arrayListAll.get(i)[j].equals(colToStandardize)){
                        idxOfCol=j;
                        addCol=true;
                        fileOutputStream.write((","+colToStandardize+type).getBytes());
                    }
                    if (i>0&&j==idxOfCol&&addCol&&type.equals("_z")){
                        BigDecimal markz= new BigDecimal(String.valueOf(arrayListAll.get(i)[idxOfCol])).subtract(summary.mean()).divide(summary.standardDeviation(),2,RoundingMode.HALF_DOWN).setScale(2);
                        fileOutputStream.write((","+String.valueOf(markz)).getBytes());
                    }else if (i>0&&j==idxOfCol&&addCol&&type.equals("_mm")) {
//                        BigDecimal markmm=(arrayListAll.get(i)[idxOfCol].subtract(summary.min())).divide((summary.max().subtract(summary.min())),2,RoundingMode.HALF_DOWN).setScale(2);
//                        BigDecimal markmm=new BigDecimal(arrayListAll.get(i)[idxOfCol]).subtract(summary.min()).divide((summary.max().subtract(summary.min())),2,RoundingMode.HALF_DOWN).setScale(2);
                        BigDecimal markmm=new BigDecimal(arrayListAll.get(i)[idxOfCol]).subtract(summary.min()).divide((summary.max().subtract(summary.min())),2,RoundingMode.HALF_DOWN).setScale(2);
                        fileOutputStream.write((","+String.valueOf(markmm)).getBytes());
                    }

                    if(j!=arrayListAll.get(i).length-1){
                        fileOutputStream.write(",".getBytes());
                    }
                }
                fileOutputStream.write("\n".getBytes());
            }
            fileOutputStream.close();
        } catch (IOException|NullPointerException e) {
            System.out.println(e.getMessage());

        }

    }




}
