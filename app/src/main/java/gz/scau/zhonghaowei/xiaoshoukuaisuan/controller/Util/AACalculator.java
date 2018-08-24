package gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Util;

import android.util.Log;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import gz.scau.zhonghaowei.xiaoshoukuaisuan.Model.AAItem;


/**
 * @author zhong
 */
public class AACalculator {

    private static final String TAG = "AACalculator";
    private List<AAItem> data;
    private Map<String,Double> costMap;
    //正成本
    private Map<String,Double> posCost;
    //负成本
    private Map<String,Double> negCost;
    //记录结果,嵌套map 第一个String是fromName，第二个是toName
    private Map<Map<String,String>,Double> result;
    //平均值
    private double avg;


    public AACalculator(List<AAItem> data) {
        this.data = data;
        costMap = new HashMap<>();
        posCost = new HashMap<>();
        negCost = new HashMap<>();
        result = new HashMap<>();
        for(int i =0;i<data.size();i++){
            assignPersonCost(data.get(i).getName(),data.get(i).getPrice());
        }
        computeAAResult();
    }


    private void assignPersonCost(String name,Double cost){
        Log.e(TAG, "assignPersonCost: "+name+" "+cost );
        costMap.put(name,cost);
    }

    private double getAvgCost(){
        double avg = 0d;
        Set<String> keySet = costMap.keySet();
        for(String key:keySet){
            avg+=costMap.get(key);
        }
        return avg/costMap.size();
    }


    private void getReformCostList(){
        if(costMap.isEmpty()){
            return;
        }
        double avg = getAvgCost();
        this.avg=avg;
        Log.e(TAG, "getReformCostList: avg="+avg );
        Set<Map.Entry<String,Double>> entrySet = costMap.entrySet();
        for(Map.Entry<String,Double> entry : entrySet){
            double cost  = avg - entry.getValue();
            String name = entry.getKey();
            if(cost>=0){
                posCost.put(name,cost);
            }else {
                negCost.put(name,-cost);
            }
        }
        //升序排序
        posCost = sortMap(posCost);
        negCost = sortMap(negCost);
    }

    private Map<String, Double> sortMap(Map<String,Double> map){
        if(map==null || map.isEmpty()){
            return null;
        }
        Set<Map.Entry<String,Double>> entrySet = map.entrySet();
        List<Map.Entry<String,Double>> entryList = new ArrayList<>();
        entryList.addAll(entrySet);
        List<Map.Entry<String,Double>> finalList;
        finalList=entryList.stream().sorted(Comparator.comparing(Map.Entry::getValue)).collect(Collectors.toList());
        Map<String,Double> resultMap = new HashMap<>();
        for(Map.Entry<String,Double> entry: finalList){
            resultMap.put(entry.getKey(),entry.getValue());
        }
        return resultMap;
    }

    private void computeAAResult(){
        if(costMap.isEmpty()){
            return;
        }
        getReformCostList();
        List<Map.Entry<String,Double>> posCosts = new ArrayList<>();
        try {
            posCosts.addAll(posCost.entrySet());
        }catch (Exception e){
            e.printStackTrace();
        }
        List<Map.Entry<String,Double>> negCosts = new ArrayList<>();
        try {
            negCosts.addAll(negCost.entrySet());
        }catch (Exception e){
            e.printStackTrace();
        }
        while (posCosts.size()>0 && negCosts.size()>0){
            //弹出最后一个元素
            Map.Entry<String,Double> posEntry = posCosts.get(posCosts.size()-1);
            posCosts.remove(posCosts.size()-1);
            Map.Entry<String,Double> negEntry = negCosts.get(negCosts.size()-1);
            negCosts.remove(negCosts.size()-1);
            String posName = posEntry.getKey();
            String negName = negEntry.getKey();
            Double posCost = posEntry.getValue();
            Double negCost = negEntry.getValue();
            if(Double.doubleToLongBits(posCost) == Double.doubleToLongBits(negCost)){
                Map<String,String> nameMap = new HashMap<>();
                nameMap.put(posName,negName);
                result.put(nameMap,posCost);
            }else if(Double.doubleToLongBits(posCost) > Double.doubleToLongBits(negCost)){
                posCost = posCost - negCost;
                //重新入pos栈
                posEntry.setValue(posCost);
                posCosts.add(posEntry);
                Map<String,String> nameMap = new HashMap<>();
                nameMap.put(posName,negName);
                result.put(nameMap,negCost);
            }else {
                negCost = negCost - posCost;
                negEntry.setValue(negCost);
                negCosts.add(negEntry);
                Map<String,String> nameMap = new HashMap<>();
                nameMap.put(posName,negName);
                result.put(nameMap,posCost);
            }
        }
    }


    public Map<Map<String, String>, Double> getResult() {
        return result;
    }

    public void update(List<AAItem> list){
        this.data = list;
        costMap.clear();
        posCost.clear();
        negCost.clear();
        result.clear();
        for(int i =0;i<data.size();i++){
            assignPersonCost(data.get(i).getName(),data.get(i).getPrice());
        }
        computeAAResult();
    }

    private void log(Map<String,Double> map){
        Set<Map.Entry<String,Double>> entrySet = map.entrySet();
        for(Map.Entry<String,Double> entry : entrySet){
            Log.e(TAG, "map log: name:"+entry.getKey()+" value "+entry.getValue() );
        }
    }

    public double getAvg() {
        return avg;
    }
}
