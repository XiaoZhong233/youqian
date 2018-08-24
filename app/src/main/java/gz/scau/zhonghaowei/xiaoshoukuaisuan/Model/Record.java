package gz.scau.zhonghaowei.xiaoshoukuaisuan.Model;

import android.support.annotation.IntDef;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Adapter.ExpandableItemAdapter;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Util.TimeUtil;


/**
 * 记录账本单条记录数据
 */
public class Record implements MultiItemEntity {

    private float cost=0.0f;
    private Date date;
    private UUID id;
    private String classes="";
    private String detail="";
    private String title="";
    private String zhanghu="";
    private String pay_type="";
    //当前数据是否位于列表的第一个,默认为假
    private boolean isFirst = false;

    public Record(){
        this.date=new Date();
        this.id=UUID.randomUUID();

    }

    public Record(UUID id){
        this.id=id;
        this.date=new Date();
    }


    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public UUID getId() {
        return id;
    }

    public String getClasses() {
        return classes;
    }

    public void setClasses(String classes) {
        this.classes = classes;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDateString(){
        return getDateFormat().format(date);
    }


    public String getZhanghu() {
        return zhanghu;
    }

    public void setZhanghu(String zhanghu) {
        this.zhanghu = zhanghu;
    }

    public String getPay_type() {
        return pay_type;
    }

    public void setPay_type(String pay_type) {
        this.pay_type = pay_type;
    }

    public boolean isFirst() {
        return isFirst;
    }

    public void setFirst(boolean first) {
        isFirst = first;
    }


    //排序依据
    /**
     * 获取记录的日
     */
    public int getRecordDay(){
        return TimeUtil.getDay(date);
    }

    /**
     * 获取记录的年月日
     */
    public String getRecordByYearMonthDay(){
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        return s.format(date);

    }

    /**
     * 获取日期格式
     * @return
     */
    public static  SimpleDateFormat getDateFormat(){
        SimpleDateFormat s = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分 E ");
        return s;
    }


    @Override
    public int getItemType() {
        return ExpandableItemAdapter.TYPE_RECORD;
    }


}
