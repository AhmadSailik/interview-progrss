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
    public ScoringSummary generateScoringSummary(Path csvPath,Path destPath, String colToStandardize,String type){
        ScoringSummary summary=null;
        String[] nextRecord;
        ArrayList<Integer>dataOfCol=new ArrayList<>();
        ArrayList<String[]>arrayListAll=new ArrayList<>();
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
                    dataOfCol.add(Integer.parseInt(nextRecord[idx]));
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


        return summary;

    }
    public  void generateFile(Path destPath,ArrayList<String[]> arrayListAll,ScoringSummary summary,String colToStandardize,String type){
        int idxOfCol = 0;
        boolean addCol=false;
        File file=new File(String.valueOf(destPath));
        if (!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.getMessage();
            }
        }
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
                        BigDecimal markz= BigDecimal.valueOf((Double.parseDouble(arrayListAll.get(i)[idxOfCol])-Double.parseDouble(String.valueOf(summary.mean())))/Double.parseDouble(String.valueOf(summary.standardDeviation()))).setScale(2, RoundingMode.HALF_EVEN);
                        fileOutputStream.write((","+String.valueOf(markz)).getBytes());
                    }else if (i>0&&j==idxOfCol&&addCol&&type.equals("_mm")) {
                        BigDecimal markmm= BigDecimal.valueOf((Double.parseDouble(arrayListAll.get(i)[idxOfCol])-Double.parseDouble(String.valueOf(summary.min())))/(Double.parseDouble(String.valueOf(summary.max()))-Double.parseDouble(String.valueOf(summary.min())))).setScale(2, RoundingMode.HALF_DOWN);
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
