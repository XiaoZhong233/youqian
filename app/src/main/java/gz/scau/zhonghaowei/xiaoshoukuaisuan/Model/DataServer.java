package gz.scau.zhonghaowei.xiaoshoukuaisuan.Model;


import android.content.Context;
import android.util.Log;

import java.sql.Time;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Util.TimeUtil;

/**
 * 分组列表数据源
 */
public class DataServer {

    private static final String TAG = "DataServer";
    public static String floatToInt(float num){
        //num=Math.abs(num);
        DecimalFormat titleFormat=new DecimalFormat(",###.00");
        if(num == 0|| Math.abs(num) < 1){
            DecimalFormat decimalFormat=new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
            String p = decimalFormat.format(num);
            return p;
        }
        else{
            return titleFormat.format(num);
        }
    }


    public static List<CountTypeSection> getCountTypeData(Context context){
        List<CountTypeSection> list = new ArrayList<>();
        List<String> countTypeList =  provideCountFirstData();
        for(int i=0;i<countTypeList.size();i++){
            setCountTypeData(context,list,countTypeList.get(i),i);
        }
        return list;
    }

    private static void setCountTypeData(Context context,List<CountTypeSection> list,String countType,int fistIndex){
        //加入头布局数据
        List<Record> recordsByCountType = RecordLab.getRecordLab(context).getRecordByCountType(countType);
        Map<String,Float> count  = RecordLab.getRecordLab(context).getRecordStatistics(recordsByCountType);
        float income = count.get(RecordLab.INCOME_KEY);
        float cost = count.get(RecordLab.COST_KEY);
        float total = income + cost;
        Log.e(TAG, "setCountTypeData: 头布局统计数据 类型 :" +countType+" 金额: "+total );
        list.add(new CountTypeSection(true,countType,floatToInt(total),floatToInt(income),floatToInt(Math.abs(cost))));
        //加入Item数据
        List<String> payTypeList = provideCountSecondData(fistIndex);
        for(String payType :payTypeList){
            List<Record> recordsByPayType = RecordLab.getRecordLab(context).getRecordByPayType(payType);
            Map<String,Float> Paycount  = RecordLab.getRecordLab(context).getRecordStatistics(recordsByPayType);
            float payIncome = Paycount.get(RecordLab.INCOME_KEY);
            float payCost = Paycount.get(RecordLab.COST_KEY);
            float payTotal = payIncome + payCost;
            Log.e(TAG, "setCountTypeData: 子布局统计数据 类型 :" +payType+" 金额: "+payTotal );
            list.add(new CountTypeSection(payType,floatToInt(payTotal)));
        }

    }

    /**
     * 获取默认分组列表数据源
     * @return 返回数据源引用
     */
    public static List<DateSection> getDateData(Context context){
        List<DateSection> list = new ArrayList<>();
        setDateRecord(context,list, TimeUtil.DateType.today);
        //setDateRecord(context,list, TimeUtil.DateType.lastWeek);
        //setDateRecord(context,list, TimeUtil.DateType.lastOneMonth);
        //setDateRecord(context,list, TimeUtil.DateType.lastThreeMonths);
        //setDateRecord(context,list, TimeUtil.DateType.lastOneYear);
        setDateRecord(context,list,TimeUtil.DateType.thisWeek);
        setDateRecord(context,list,TimeUtil.DateType.thisMonth);
        return list;
    }

    /**
     * 获取单一数据源
     * @return 返回数据源引用
     */
    public static List<DateSection> getDateData(Context context, TimeUtil.DateType dateType){
        List<DateSection> list = new ArrayList<>();
        setDateRecord(context,list,dateType);
        return list;
    }

    /**
     * 私有内部方法
     * 设置分组列表数据源
     * @param context 上下文
     * @param list    分组列表引用
     * @param dateType 日期类型
     */
    private static void setDateRecord(Context context,List<DateSection> list,TimeUtil.DateType dateType){
        list.add(new DateSection(true,dateType.name(),dateType));
        List<Record> records = RecordLab.getRecordLab(context).getRecordByDate(dateType);
        for(Record record : records){
            list.add(new DateSection(record,dateType));
        }
    }


    /**
     * 返回账户类型一级联动数据
     * @return
     */
    public static List<String> provideCountFirstData(){
        List<String> list = new ArrayList<>();
        list.add("支付账户");
        list.add("负债账户");
        list.add("债权账户");
        list.add("投资账户");
        return list;
    }

    /**
     * 返回账户类型二级联动数据
     * @param firstIndex 一级联动位置
     * @return
     */
    public static List<String> provideCountSecondData(int firstIndex){
        List<String> secendList = new ArrayList<>();
        switch (firstIndex){
            case 0:
                secendList.add("现金(CNY)");
                secendList.add("银行卡(CNY)");
                secendList.add("支付宝(CNY)");
                secendList.add("微信支付(CNY)");
                break;
            case 1:
                secendList.add("应付款项(CNY)");
                secendList.add("花呗(CNY)");
                secendList.add("京东白条(CNY)");
                secendList.add("分期乐(CNY)");
                break;
            case 2:
                secendList.add("应收款项(CNY)");
                break;
            case 3:
                secendList.add("余额宝(CNY)");
                secendList.add("基金账户(CNY)");
                secendList.add("股票账户(CNY)");
                secendList.add("其他理财账户(CNY)");
                break;
        }
        return secendList;
    }

    /**
     * 返回支出类型一级联动数据
     * @return
     */
    public static List<String> provideZhiChuClassesFirstData(){
        List<String> list = new ArrayList<>();
        list.add("食品酒水");//0
        list.add("衣服饰品");//1
        list.add("居家物业");//2
        list.add("行车交通");//3
        list.add("交流通讯");//4
        list.add("休闲娱乐");//5
        list.add("学习进修");//6
        list.add("人情往来");//7
        list.add("医疗保健");//8
        list.add("金融保险");//9
        list.add("其他杂项");//10
        return list;
    }

    /**
     * 返回支出类型二级联动数据
     * @param firstIndex
     * @return
     */
    public static List<String> provideZhiChuClassesSecendData(int firstIndex){
        List<String> secendList = new ArrayList<>();
        switch (firstIndex){
            case 0:
                secendList.add("充饭卡");
                secendList.add("早午晚餐");
                secendList.add("烟酒茶");
                secendList.add("水果零食");
                break;
            case 1:
                secendList.add("衣服裤子");
                secendList.add("鞋帽包包");
                secendList.add("化妆饰品");
                break;
            case 2:
                secendList.add("日常用品");
                secendList.add("水电煤气");
                secendList.add("房租");
                secendList.add("物业管理");
                secendList.add("维修保养");
                break;
            case 3:
                secendList.add("公共交通");
                secendList.add("打车租车");
                secendList.add("私家车费用");
                break;
            case 4:
                secendList.add("座机费");
                secendList.add("手机费");
                secendList.add("上网费");
                secendList.add("邮寄费");
                break;
            case 5:
                secendList.add("运动健身");
                secendList.add("腐败聚会");
                secendList.add("休闲游戏");
                secendList.add("宠物宝贝");
                secendList.add("旅游度假");
                break;
            case 6:
                secendList.add("书报杂志");
                secendList.add("培训进修");
                secendList.add("数码装备");
                break;
            case 7:
                secendList.add("送礼请客");
                secendList.add("孝敬家长");
                secendList.add("还人钱物");
                secendList.add("慈善捐助");
                break;
            case 8:
                secendList.add("药品费");
                secendList.add("保健费");
                secendList.add("美容费");
                secendList.add("治疗费");
                secendList.add("检查费");
                break;
            case 9:
                secendList.add("银行手续");
                secendList.add("投资亏损");
                secendList.add("按揭还款");
                secendList.add("消费税收");
                secendList.add("利息支出");
                secendList.add("赔偿罚款");
                break;
            case 10:
                secendList.add("其他支出");
                secendList.add("意外丢失");
                secendList.add("烂账损失");
                break;
        }
        return secendList;
    }


    public static List<String> provideShouRuClassesFirstData(){
        List<String> list = new ArrayList<>();
        list.add("职业收入");
        list.add("理财收入");
        list.add("其他收入");
        return list;
    }


    public static List<String> provideShouRuClassesSecendData(int firstIndex){
        List<String> secendList = new ArrayList<>();
        switch (firstIndex){
            case 0:
                secendList.add("工资收入");
                secendList.add("兼职收入");
                secendList.add("加班收入");
                secendList.add("经营收入");
                secendList.add("奖金收入");
                break;
            case 1:
                secendList.add("投资收入");
                secendList.add("利息收入");
                break;
            case 2:
                secendList.add("礼金收入");
                secendList.add("中奖收入");
                secendList.add("意外收入");
                break;
        }
        return secendList;
    }

}
