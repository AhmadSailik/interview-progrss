
package com.progressoft.tools;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

public class ScoringSummaryImp implements ScoringSummary{
    private BigDecimal mean=null;
    private BigDecimal standardDeviation=null;
    private BigDecimal variance=null;
    private BigDecimal median=null;
    private BigDecimal min=null;
    private BigDecimal max=null;
    private ArrayList<BigDecimal> arrayList;

    public ScoringSummaryImp() {
    }

    public ScoringSummaryImp(ArrayList<BigDecimal> arrayList) {
        this.arrayList=arrayList;
    }
    private void claMean(){
        BigDecimal sum=new BigDecimal(0);
        for (int i = 0; i < arrayList.size(); i++) {
            sum=sum.add(arrayList.get(i));
        }
        mean= sum.divide(BigDecimal.valueOf(arrayList.size()),0,RoundingMode.HALF_DOWN).setScale(2);
    }
    @Override
    public BigDecimal mean() {
       if(mean==null){
           claMean();
       }
        return mean;
    }

    @Override
    public BigDecimal standardDeviation() {
        if (variance==null){
            variance();
        }
        standardDeviation= new BigDecimal(Math.sqrt(Double.parseDouble(String.valueOf(variance)))).setScale(2, RoundingMode.HALF_DOWN);
        return standardDeviation;
    }

    @Override
    public BigDecimal variance() {
        BigDecimal sum=new BigDecimal(0);
        if (mean==null){
            mean();
        }
        for (int i = 0; i < arrayList.size(); i++) {
            sum=sum.add((arrayList.get(i).subtract(mean)).pow(2));
        }
        variance= sum.divide(BigDecimal.valueOf(arrayList.size()),0,RoundingMode.HALF_DOWN).setScale(2);
        return variance;
    }

    @Override
    public BigDecimal median() {
        Collections.sort(arrayList);
        if (arrayList.size()%2==0){
            median= (arrayList.get(arrayList.size()/2).add(arrayList.get(arrayList.size()/2-1))).divide(BigDecimal.valueOf(2),0,RoundingMode.HALF_DOWN).setScale(2);
        }else {
            median=arrayList.get(arrayList.size()/2).setScale(2);
        }
        return median;
    }

    @Override
    public BigDecimal min() {
        min= Collections.min(arrayList).setScale(2);
        return min;
    }

    @Override
    public BigDecimal max() {
        max= Collections.max(arrayList).setScale(2);
        return max;
    }
}

