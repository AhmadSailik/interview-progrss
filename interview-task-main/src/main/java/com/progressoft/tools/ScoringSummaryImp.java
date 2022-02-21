
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
    private ArrayList<Integer> arrayList;

    public ScoringSummaryImp() {
    }

    public ScoringSummaryImp(ArrayList<Integer> arrayList) {
        this.arrayList=arrayList;
    }

    @Override
    public BigDecimal mean() {
        mean= BigDecimal.valueOf(arrayList.stream().collect(Collectors.summingInt(i->i))/arrayList.size()).setScale(2);;
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
        double squareSum=0;
        if (mean==null){
            mean();
        }
        for (int i = 0; i < arrayList.size(); i++) {
            squareSum+=Math.pow(arrayList.get(i)-Double.parseDouble(String.valueOf(mean)),2);
        }
        variance= BigDecimal.valueOf(Math.ceil(squareSum/arrayList.size())).setScale(2);
        return variance;
    }

    @Override
    public BigDecimal median() {
        Collections.sort(arrayList);
        if (arrayList.size()%2==0){
            median= BigDecimal.valueOf((arrayList.get(arrayList.size()/2)+arrayList.get(arrayList.size()/2-1))/2).setScale(2);
        }else {
            median= BigDecimal.valueOf(arrayList.get(arrayList.size()/2)).setScale(2);
        }
        return median;
    }

    @Override
    public BigDecimal min() {
        min= BigDecimal.valueOf(Collections.min(arrayList)).setScale(2);
        return min;
    }

    @Override
    public BigDecimal max() {
        max= BigDecimal.valueOf(Collections.max(arrayList)).setScale(2);
        return max;
    }
}
