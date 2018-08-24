package gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Util;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.security.auth.login.LoginException;


/**
 * @author Zhong.H.W
 * 日期工具类
 */



public class TimeUtil {

    public static final int MONDAY_FIRST = 1;
    public static final int SUNDAY_FIRST = 0;
    private static final String TAG = "TimeUtil";
    /**
     * 时间类型枚举，用于查询不同时间段的记录
     */
    public enum DateType {
        ALL,
        today,
        lastWeek,
        lastOneMonth,
        lastThreeMonths,
        lastOneYear,
        thisWeek,
        thisMonth,
        thisYear

    }

    /**
     * 根据传入的时间获得当前时间所在周的第一天和第七天日期
     * @param tm 时间
     * @param firstday 周日作为周一为0，周一作为周一1。
     * @return
     */
    public static List<Date> getWeek(Date tm,int firstday){
        Calendar c = Calendar.getInstance();
        c.setTime(tm);
        if(c.get(Calendar.DAY_OF_WEEK)==1){
            c.add(Calendar.DATE, -1);
        }
        List<Date> list = new ArrayList<Date>();
        Calendar cf = Calendar.getInstance();
        cf.setTime(c.getTime());
        cf.set(Calendar.DAY_OF_WEEK, cf.getFirstDayOfWeek());
        cf.add(Calendar.DATE, firstday);
        Calendar ce = Calendar.getInstance();
        ce.setTime(c.getTime());
        ce.set(Calendar.DAY_OF_WEEK, cf.getFirstDayOfWeek()+6);
        ce.add(Calendar.DATE, firstday);
        list.add(cf.getTime());
        list.add(ce.getTime());
        return list;
    }

    /**
     * 判断某个时间是否在某个时间段内
     * @param currentTime 某个时间
     * @param startTime   开始时间
     * @param endTime     结束时间
     * @return
     */
    public static boolean belongTimeBucket(Date currentTime,Date startTime,Date endTime) {
        SimpleDateFormat sf = getDefaultDateFormat();
        Log.e(TAG, "belongTimeBucket: currentTime:" +sf.format(currentTime).toString());
        Log.e(TAG, "belongTimeBucket: startTime: " +sf.format(startTime) );
        Log.e(TAG, "belongTimeBucket: endTime: "+sf.format(endTime) );
        boolean flag = sf.format(currentTime).toString()==sf.format(startTime).toString();
        Log.e(TAG, "belongTimeBucket: currentTime eaqual startTime? "+  flag);
        return (currentTime.after(startTime) && currentTime.before(endTime))
                || (sf.format(currentTime).toString().equals(sf.format(startTime).toString()))
                || (sf.format(currentTime).toString().equals(sf.format(endTime).toString()));
    }

    /**
     * 判断某个时间是否属于本周
     * @param time
     * @return
     */
    public static boolean isBelongThisWeek(Date time) {
        Log.e(TAG, "isBelongThisWeek: " +time.toString());
        List<Date> dateToWeek = getWeek(new Date(), MONDAY_FIRST);
        Log.e(TAG, "isBelongThisWeek: "+ belongTimeBucket(time, dateToWeek.get(0), dateToWeek.get(dateToWeek.size()-1)));
        return belongTimeBucket(time, dateToWeek.get(0), dateToWeek.get(dateToWeek.size()-1));
    }

    /**
     * 判断某个时间是否属于本月
     * @param time
     * @return
     */
    public static boolean isBelongThisMonth(Date time) {
        Date now = new Date();
        return getYear(time)==getYear(now) && getMonth(now)==getMonth(time);
    }

    public static boolean isBelongThisYear(Date time){
        Date now = new Date();
        return getYear(time)==getYear(now);
    }

    /**
     * 判断两个日期是否属于同一天
     * @param date1
     * @param date2
     * @return
     */
    public static boolean isBelongSameDay(Date date1,Date date2){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String a = simpleDateFormat.format(date1);
        String b = simpleDateFormat.format(date2);
        return a.equals(b);
    }


    /**
     * 获取本月第一天
     * @return
     */
    public static Date getThisMonthFirstDay(){
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, 0);
        c.set(Calendar.DAY_OF_MONTH,1);//设置为1号,当前日期既为本月第一天
        return c.getTime();
    }

    /**
     * 获取本月最后一天
     * @return
     */
    public static Date getThisMonthLastDay(){
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
        return ca.getTime();
    }

    /**
     * 计算时间间隔，单位为天数
     * @param startTime
     * @param endTime
     * @return
     */
    public static int equationOfDay(long startTime, long endTime) {
        startTime = dateToStamp(stampToDate(startTime,getDefaultDateFormat()),getDefaultDateFormat());
        endTime = dateToStamp(stampToDate(endTime,getDefaultDateFormat()),getDefaultDateFormat());
        int newL = (int) ((endTime - startTime) / (1000 * 3600 * 24));
        return newL;
    }

    /**
     * 计算时间间隔，单位为自然月
     * @param startTime
     * @param endTime
     * @return
     */
    public static int equationOfMonth(long startTime,long endTime){
        return equationOfMonth(stampToDate(startTime,getDefaultDateFormat()),stampToDate(endTime,getDefaultDateFormat()));
    }

    /**
     * 计算时间间隔，单位为年
     * @param startTime
     * @param endTime
     * @return
     */
    public static int equationOfYear(long startTime,long endTime){
        return equationOfMonth(startTime,endTime)/12;
    }


    private static int equationOfMonth(String startDate,String endDate){
        int result=0;
        try {
            SimpleDateFormat sfd=new SimpleDateFormat("yyyy-MM-dd");
            Date start = sfd.parse(startDate);
            Date end = sfd.parse(endDate);
            int startYear=getYear(start);
            int startMonth=getMonth(start);
            int startDay=getDay(start);
            int endYear=getYear(end);
            int endMonth=getMonth(end);
            int endDay=getDay(end);
            if (startDay>endDay){ //1月17  大于 2月28
                if (endDay==getDaysOfMonth(getYear(new Date()),2)){   //也满足一月
                    result=(endYear-startYear)*12+endMonth-startMonth;
                }else{
                    result=(endYear-startYear)*12+endMonth-startMonth-1;
                }
            }else{
                result=(endYear-startYear)*12+endMonth-startMonth;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return result;
    }



    public static SimpleDateFormat getDefaultDateFormat(){
        return new SimpleDateFormat("yyyy-MM-dd");
    }

    /**
     * 时间戳转换为时间
     * @param l
     * @return
     */
    public static String stampToDate(long l,SimpleDateFormat sdf) {
        String res;
        SimpleDateFormat simpleDateFormat = sdf;
        long lt = l;
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    /**
     * 时间转换为时间戳
     * @param s
     * @return
     */
    public static long dateToStamp(String s,SimpleDateFormat sdf) {
        SimpleDateFormat simpleDateFormat = sdf;
        Date date = null;
        try {
            date = simpleDateFormat.parse(s);
            return date.getTime();
        } catch (java.text.ParseException e) {
            // TODO Auto-generated catch block

            e.printStackTrace();
            return -1;
        }
    }



    /**
     * 返回日期的日,即yyyy-MM-dd中的dd
     * @param date
     * @return
     */
    public static int getDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DATE);
    }

    /**
     * 返回日期的月份，1-12,即yyyy-MM-dd中的MM
     *
     * @param date
     * @return
     */
    public static int getMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH) + 1;
    }

    /**
     * 返回日期的年,即yyyy-MM-dd中的yyyy
     *
     * @param date
     * @return int
     */
    public static int getYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }




    /**
     * 获取当前时间的小时 24小时制
     * @param date
     * @return
     */
    public static int getHour(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 获取当前时间的分钟数
     * @param date
     * @return
     */
    public static int getMinute(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MINUTE);
    }


    /**
     * 获取当前时间的秒数
     * @param date
     * @return
     */
    public static int getSecend(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.SECOND);
    }


    public static int getDaysOfMonth(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, 1);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }


    /**
     * 得到明天的日期
     * @return
     */
    public static Date getTomorrowTime(){
        try {
            Thread.sleep(60*60*24*1000);
        }catch (InterruptedException e ){
            e.printStackTrace();
        }
        return new Date();
    }

}
