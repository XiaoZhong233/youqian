package gz.scau.zhonghaowei.xiaoshoukuaisuan.Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

import gz.scau.zhonghaowei.xiaoshoukuaisuan.Model.DataSchema.RecordTable;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Adapter.ExpandableItemAdapter;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Adapter.MyPageAdapter;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Util.*;

/**
 * @author  zhong
 * 18/8/1
 * 用于管理Record类
 * 作为数据源与外部交流的统一媒介
 * 分为面向数据库的数据库操作层和面向外部的统计层
 *
 */

public class RecordLab {
    private List<Record> records;
    private static RecordLab recordLab;
    private Context context;
    private SQLiteDatabase database;
    private static final String TAG = "RecordLab";
    private float net_Assets;

    public static final String COST_KEY = TAG+"_COST";
    public static final String INCOME_KEY = TAG+"_INCOME";


    public enum RECORD_TYPE{
        INCOME,
        COST
    }


    private RecordLab(Context context){
        records=new ArrayList<>();
        this.context=context;
        database=new RecordBaseHelper(context).getWritableDatabase();
        initListData(database,records);
    }

    public static RecordLab getRecordLab(Context context) {
        if(recordLab==null){
            recordLab=new RecordLab(context);
        }
        return recordLab;
    }

    /**
     * 此构造方法弃用(已设为private)
     * @param records
     * @param context
     */
    private RecordLab(List<Record> records,Context context){
        this.records=records;
        this.context=context;
        database=new RecordBaseHelper(context).getWritableDatabase();
    }

    /**
     * 此方法弃用
     * @param records
     * @param context
     * @return
     */
    private static RecordLab getRecordLab(List<Record> records,Context context){
        if(recordLab==null){
            recordLab=new RecordLab(records,context);
        }
        return recordLab;
    }

    public List<Record> getRecords(){
        return records;
    }





    /** 数据库层 ------------------------------------------------------------------------------------*/



    /**
     * 增加数据
     * @param record
     */
    public void addRecord(Record record){
        records.add(0,record);
        for (Record r:records
             ) {
            Log.e(TAG, "record " + r.getCost() );
        }
        //加入到数据库
        ContentValues values = getContentValues(record);
        database.insert(RecordTable.NAME,null,values);
    }


    /**
     * 删除数据
     * @param record
     */
    public void removeRecord(Record record){
        if(records.contains(record)) {
            records.remove(record);
        }
        database.delete(RecordTable.NAME,RecordTable.Cols.UUID  + "= ?",
                new String[]{record.getId().toString()});
    }

    /**
     * 更新数据
     * @param id
     * @param record
     */
    public void setRecord(UUID id,Record record){
        for(int i=0;i<records.size();i++){
            if(records.get(i).getId().equals(id)){
                records.set(i,record);
                break;
            }
        }
        //更新数据库
        update(record);

    }

    /**
     * 更新数据
     * @param record
     */
    public void update(Record record){
        String uuid = record.getId().toString();
        ContentValues contentValues = getContentValues(record);
        database.update(RecordTable.NAME,contentValues,RecordTable.Cols.UUID  + "= ?",
                new String[]{uuid});
    }

    public Record getRecord(UUID id){
        for(Record record:records){
            if(record.getId().equals(id)){
                return record;
            }
        }
        return null;
    }

    private static ContentValues getContentValues(Record record){
        ContentValues values = new ContentValues();
        values.put(RecordTable.Cols.UUID,record.getId().toString());
        values.put(RecordTable.Cols.TITLE,record.getTitle().toString());
        values.put(RecordTable.Cols.CLASSES,record.getClasses().toString());
        values.put(RecordTable.Cols.COST,record.getCost());
        values.put(RecordTable.Cols.DATE,record.getDate().getTime());
        values.put(RecordTable.Cols.DETAIL,record.getDetail());
        values.put(RecordTable.Cols.ZHANGHU,record.getZhanghu());
        values.put(RecordTable.Cols.PAY_TYPE,record.getPay_type());
        return values;
    }



    /**
     * 保存数据到数据库，实现数据同步
     */
    public void saveData(){
        for (Record record:records){
            update(record);
            Log.e(TAG, "saveData:\n " +
                    record.getId().toString()+"\n"+
                    record.getTitle()+"\n"+
                    record.getClasses()+"\n"+
                    String.valueOf(record.getCost())+"\n"+
                    record.getDate().toString()+"\n"+
                    record.getDetail()+"\n" +
                    record.getZhanghu()+"\n"+
                    record.getPay_type()
            );
        }
    }



    /**
     * 查询接口
     * @param sql
     * @return 数据集
     */
    public Cursor doSql(String sql){
        Cursor cursor = database.rawQuery(sql,null);
        return cursor;
    }




    /**
     * 初始化数据源,从数据库中读取数据
     * @param database
     * @param list 数据源
     */
    private  void initListData(SQLiteDatabase database,List<Record> list){
        Cursor cursor = database.query(
                RecordTable.NAME,
                null,
                null,
                null,
                null,
                null,
                null);
        while (cursor.moveToNext()){
            String uuid = cursor.getString(cursor.getColumnIndex(RecordTable.Cols.UUID));
            String title = cursor.getString(cursor.getColumnIndex(RecordTable.Cols.TITLE));
            String classes = cursor.getString(cursor.getColumnIndex(RecordTable.Cols.CLASSES));
            float cost = cursor.getFloat(cursor.getColumnIndex(RecordTable.Cols.COST));
            Long mdate = cursor.getLong(cursor.getColumnIndex(RecordTable.Cols.DATE));
            String detail = cursor.getString(cursor.getColumnIndex(RecordTable.Cols.DETAIL));
            String zhanghu = cursor.getString(cursor.getColumnIndex(RecordTable.Cols.ZHANGHU));
            String pay_type = cursor.getString(cursor.getColumnIndex(RecordTable.Cols.PAY_TYPE));
            //转换格式
            UUID id = UUID.fromString(uuid);
            Date date = new Date(mdate);
            //生成实例，存入List
            Record record = new Record(id);
            record.setTitle(title);
            record.setClasses(classes);
            record.setCost(cost);
            record.setDate(date);
            record.setDetail(detail);
            record.setZhanghu(zhanghu);
            record.setPay_type(pay_type);
            list.add(record);
            Log.e(TAG, "initListData:\n "+
                    "id:"+id.toString()+"\n"+
                    "title:"+title+"\n"+
                    "classes:"+classes+"\n"+
                    "cost:"+String.valueOf(cost)+"\n"+
                    "date:"+date.toString()+"\n"+
                    "detail:"+detail+"\n" +
                    "zhanghu:"+ zhanghu +"\n" +
                    "pay_type:" + pay_type

            );

            //计算总花费
            net_Assets +=cost;
        }
        cursor.close();
        Collections.reverse(list);
    }




    /** 统计层 ------------------------------------------------------------------------------------*/

    /**
     * 统计某一时间类型的收支
     * @param dateType
     * @return
     */
    public Map<String,Float> getRecordStatistics(TimeUtil.DateType dateType){
        List<Record> recordByDate = getRecordByDate(dateType);
        Map<String,Float> result = new HashMap<>();
        float cost=0f;
        float income=0f;
        for(Record record : recordByDate){
            if(record.getCost()>0){
                income+=record.getCost();
            }else {
                cost+=record.getCost();
            }
        }
        result.put(INCOME_KEY,income);
        result.put(COST_KEY,cost);
        return result;
    }


    /**
     * 统计某一记录集的收支
     * @param records
     * @return
     */
    public Map<String,Float> getRecordStatistics(List<Record> records){
        Map<String,Float> result = new HashMap<>();
        float cost=0f;
        float income=0f;
        for(Record record : records){
            if(record.getCost()>0){
                income+=record.getCost();
            }else {
                cost+=record.getCost();
            }
        }
        result.put(INCOME_KEY,income);
        result.put(COST_KEY,cost);
        return result;
    }


    //账户总和=账户支出+账户收入
    //资产=除了负债账户其他账户结余的总和
    //资产-负债=净资产
    //负债=-(负债账户的支出/收入)

    /**
     * 获取负债
     * @return
     */
    public float getObligatory(){
        Map<String,Float> map = getRecordStatistics(getRecordByCountType("负债账户"));
        float a = map.get(INCOME_KEY);
        float b = map.get(COST_KEY);
        return -(a+b)==0?a+b:-(a+b);
    }

    /**
     * 获取账户资产
     * @return
     */
    public float getAsset(){
        List<String> countList = DataServer.provideCountFirstData();
        countList.remove("负债账户");
        float sum = 0;
        for(String countType :countList){
            sum += getCountTypeBalance(countType);
        }
        return sum;
    }


    /**
     * 获取净资产
     * @return
     */
    public float getNet_Assets(){
        return getAsset() - getObligatory();
    }



    /**
     * 获取账户结余
     * @return
     */
    public float getCountTypeBalance(String type){
        Map<String,Float> map = getRecordStatistics(getRecordByCountType(type));
        float a = map.get(INCOME_KEY);
        float b = map.get(COST_KEY);
        return a+b;
    }



    /**
     * 获取不同账户下的支付结余
     * @param type
     * @return
     */
    public float getPayTypeBalance(String type){
        Map<String,Float> map = getRecordStatistics(getRecordByPayType(type));
        float a = map.get(INCOME_KEY);
        float b = map.get(COST_KEY);
        return a+b;
    }







    /**
     * 按照类别进行分类
     * @param list
     * @return
     */
    public Map<String,List<Record>> getClassesMapForRecord(List<Record> list){
        return list.stream().collect(Collectors.groupingBy(Record::getClasses));
    }

    public Map<String,List<Record>> getClassesMapForRecord(){
        return records.stream().collect(Collectors.groupingBy(Record::getClasses));
    }


    /**
     * 统计不同日花费的总和,不将花费为0的日计入统计,升序排序
     * @param data
     * @return
     */
    public Map<String,Double> getSumMapByDayForRecord(List<Record> data){
        return data.stream().filter(record -> record.getCost()!=0f).
                collect(Collectors.groupingBy(Record::getRecordByYearMonthDay,Collectors.summingDouble(Record::getCost)));

    }



    /**
     * 统计不同类别花费的总和,不将花费为0的类别计入统计
     * @param data 数据源
     * @return 键为类别,值为和
     */
    public Map<String,Double> getSumMapByClassesForRecord(List<Record> data) {
        return data.stream().filter(record -> record.getCost()!=0f).
                collect(Collectors.groupingBy(Record::getClasses, Collectors.summingDouble(Record::getCost)));
    }

    /**
     * 统计不同二级账户花费的总和,花费为0不计入
     * @param data
     * @return
     */
    public Map<String,Double> getSumMapBySecendZhanghuForRecord(List<Record> data){
        return data.stream().filter(record -> record.getCost()!=0f)
                .collect(Collectors.groupingBy(Record::getPay_type,Collectors.summingDouble(Record::getCost)));
    }



    /**
     * 获取某一年的记录，按月汇总
     * @param year
     * @return
     */
    public Map<Integer,List<Record>> getYearMonthMapForRecord(int year){
        List<Record> list = getRecordByYear(year);
        return getMonthMapForRecord(list);
    }


    /**
     * 按月分类
     * @param list
     * @return
     */
    public  Map<Integer,List<Record>> getMonthMapForRecord(List<Record> list){
        Map<Integer,List<Record>> monthMap = new ConcurrentHashMap<>();

        for(Record record : list){
            Set keySet = monthMap.keySet();
            int month = TimeUtil.getMonth(record.getDate());
            if(!keySet.contains(month)) {
                List<Record> records = new ArrayList<>();
                records.add(record);
                monthMap.put(month, records);
            }else {
                List<Record> records = monthMap.get(month);
                records.add(record);
            }
        }
        return monthMap;
    }

    /**
     * 按年分类
     * @param list
     * @return
     */
    public  Map<Integer,List<Record>> getYearMapForRecord(List<Record> list){
        //SparseArray利用的二分查找来寻找,而map利用hash查找，低数据量时,性能无差异,SparseArray更省内存
        Map<Integer,List<Record>> yearMap  = new ConcurrentHashMap<>();
        for(Record record : list){
            Set keySet = yearMap.keySet();
            int year = TimeUtil.getYear(record.getDate());
            if(!keySet.contains(year)){
                List<Record> records = new ArrayList<>();
                records.add(record);
                yearMap.put(year, records);
            }else {
                List<Record> records = yearMap.get(year);
                records.add(record);
                //不确定需不需要
                //yearMap.put(year,records);
            }
        }

        return yearMap;
    }


    /**
     * 根据年份返回记录集
     * @return
     */
    public List<Record> getRecordByYear(int year){
        return records.stream().
                filter(record -> TimeUtil.getYear(record.getDate()) == year)
                .collect(Collectors.toList());
    }


    /**
     * 根据月份返回记录集
     */
    public List<Record> getRecordByMonth(int month){
        return records.stream().
                filter(record -> TimeUtil.getMonth(record.getDate()) == month)
                .collect(Collectors.toList());
    }

    /**
     * 根据某年某月具体一天返回记录集
     * @param date
     * @return
     */
    public List<Record> getRecordByday(Date date){
        return records.stream().
                filter(record -> TimeUtil.isBelongSameDay(record.getDate(),date))
                .collect(Collectors.toList());
    }


    /**
     * 根据账户类型返回记录集
     * @param countType
     * @return
     */
    public List<Record> getRecordByCountType(String countType){
        return records.stream().
                filter(record-> record.getZhanghu().equals(countType))
                .collect(Collectors.toList());
    }

    /**
     * 根据账户类型下的支付方式返回记录集
     * @param payType
     * @return
     */
    public List<Record> getRecordByPayType(String payType){
        return records.stream().
                filter(record-> record.getPay_type().equals(payType))
                .collect(Collectors.toList());
    }


    /**
     * 根据类别返回记录集
     * @param classes
     * @return
     */
    public List<Record> getRecordByClasses(String classes){
        return records.stream().
                filter(record -> record.getClasses().equals(classes))
                .collect(Collectors.toList());
    }


    /**
     * 获取相应时间段的记录
     * @param dateType
     * @return
     */
    public  List<Record> getRecordByDate(TimeUtil.DateType dateType){
        //获取系统时间，返回为秒
        final long currentSecends = System.currentTimeMillis();

        switch (dateType){
            case today:
                return records.stream().
                        filter(record-> TimeUtil.equationOfDay(record.getDate().getTime(),currentSecends)==0)
                        .collect(Collectors.toList());
            case lastWeek:
                return records.stream().
                        filter(record-> TimeUtil.equationOfDay(record.getDate().getTime(),currentSecends)<=7)
                        .collect(Collectors.toList());
            case lastOneMonth:
                return records.stream().
                        filter(record-> TimeUtil.equationOfMonth(record.getDate().getTime(),currentSecends)<=1
                                && TimeUtil.equationOfMonth(record.getDate().getTime(),currentSecends)>=0)
                        .collect(Collectors.toList());
            case lastThreeMonths:
                return records.stream().
                        filter(record-> TimeUtil.equationOfMonth(record.getDate().getTime(),currentSecends)<=3
                                && TimeUtil.equationOfMonth(record.getDate().getTime(),currentSecends)>=0)
                        .collect(Collectors.toList());
            case lastOneYear:
                return records.stream().
                        filter(record-> TimeUtil.equationOfYear(record.getDate().getTime(),currentSecends)<=1)
                        .collect(Collectors.toList());
            case thisWeek:
                return records.stream().
                        filter(record -> TimeUtil.isBelongThisWeek(record.getDate())).collect(Collectors.toList());
            case thisMonth:
                return records.stream().
                        filter(record -> TimeUtil.isBelongThisMonth(record.getDate())).collect(Collectors.toList());
            case thisYear:
                return records.stream().
                        filter(record -> TimeUtil.isBelongThisYear(record.getDate())).collect(Collectors.toList());
            case ALL:
                return records;
                default:
                    return records;
        }
    }




}
