package gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Adapter;


import android.content.Context;

import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import gz.scau.zhonghaowei.xiaoshoukuaisuan.Model.DateSection;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.Model.RecordLab;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.R;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Util.*;

import static gz.scau.zhonghaowei.xiaoshoukuaisuan.Model.RecordLab.COST_KEY;
import static gz.scau.zhonghaowei.xiaoshoukuaisuan.Model.RecordLab.INCOME_KEY;
import static gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Util.TimeUtil.MONDAY_FIRST;
import static gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Util.TimeUtil.getWeek;

public class DateSectionAdapter extends BaseSectionQuickAdapter<DateSection,BaseViewHolder> {

    private static final String TAG = "DateSectionAdapter";

    private Context context;
    public  Map<String,Float> cost;

    public DateSectionAdapter(Context context,int layoutResId, int sectionHeadResId, List data) {
        super(layoutResId, sectionHeadResId, data);
        this.context = context;
    }

    /**
     * 加载头部布局数据
     * @param helper
     * @param item
     */
    @Override
    protected void convertHead(BaseViewHolder helper, DateSection item) {
        TimeUtil.DateType dateType = item.getDateType();
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("M/yyyy");
        DecimalFormat decimalFormat=new DecimalFormat("#");
        //cost = costStatistics(dateType);
        cost = RecordLab.getRecordLab(this.context).getRecordStatistics(dateType);
        switch (dateType){
            case thisMonth:
                helper.setText(R.id.header,"本月");
                helper.setText(R.id.date,sdf.format(now));
                break;
            case thisWeek:
                List<Date> dateToWeek = getWeek(now, MONDAY_FIRST);
                SimpleDateFormat fucksdf = new SimpleDateFormat("d/M");
                helper.setText(R.id.header,"本周");
                helper.setText(R.id.date,String.format("%s-%s",fucksdf.format(dateToWeek.get(0)),fucksdf.format(dateToWeek.get(dateToWeek.size()-1))));
                break;
            case today:
                helper.setText(R.id.header,"今天");
                helper.setText(R.id.date,new SimpleDateFormat("dd/M").format(now));
                break;
            case lastWeek:
                helper.setText(R.id.header,"过去一周");
                helper.setText(R.id.date,sdf.format(now));
                break;
            case lastOneMonth:
                helper.setText(R.id.header,"过去一个月");
                String fuckThisMonth = new SimpleDateFormat("M").format(now);
                float next = (Float.valueOf(fuckThisMonth)-1)%12;
                String next1Month  = decimalFormat.format(next);
                helper.setText(R.id.date,fuckThisMonth+"月-"+next1Month+"月");
                break;
            case lastThreeMonths:
                helper.setText(R.id.header,"过去三个月");
                String thisMonth = new SimpleDateFormat("M").format(now);
                float fuckNext = (Float.valueOf(thisMonth)-3)%12;
                String next3Month  = decimalFormat.format(fuckNext);
                helper.setText(R.id.date,thisMonth+"月-"+next3Month+"月");
                break;
            case lastOneYear:
                helper.setText(R.id.header,"过去一年");
                String thisYear=new SimpleDateFormat("yyyy").format(now);
                String lastYear = Integer.valueOf(thisYear)-1+"";
                helper.setText(R.id.date,thisYear+"-"+lastYear);
                break;
            case thisYear:
                helper.setText(R.id.header,"今年");
                helper.setText(R.id.date,new SimpleDateFormat("yyyy").format(now));
                break;
                default:
        }
        if(!cost.isEmpty()) {
            helper.setText(R.id.shouru, "收 "+String.valueOf(cost.get(INCOME_KEY)));
            helper.setText(R.id.zhichu,"支 "+String.valueOf(cost.get(COST_KEY)));
        }
    }

    /**
     * 加载item数据
     * @param helper
     * @param item
     */
    @Override
    protected void convert(BaseViewHolder helper, DateSection item) {
        helper.setText(R.id.title,item.getRecord().getTitle());
        helper.setText(R.id.classes,item.getRecord().getClasses());
        //helper.setImageResource(R.id.classes_img,R.drawable.ic_launcher_background);
        LoadIconHelper.loadIconByClass(context,item.t.getClasses(),helper.getView(R.id.classes_img));
        helper.setText(R.id.count_type,item.getRecord().getPay_type());
        TimeUtil.DateType dateType = item.getDateType();
        helper.setText(R.id.date,getDateFormate(dateType).format(item.getRecord().getDate()));
        float cost=item.getRecord().getCost();
        String costText=String.valueOf(item.getRecord().getCost());
        if(cost>0.0){
            costText="+"+String.valueOf(item.getRecord().getCost());
        }
        helper.setText(R.id.cost,costText);
        //注册子控件点击事件
        helper.addOnClickListener(R.id.delete);
        helper.addOnClickListener(R.id.content);

    }

    private SimpleDateFormat getDateFormate(TimeUtil.DateType dateType){
        switch (dateType){
            case today:
                return new SimpleDateFormat("HH:mm");
            case thisWeek:
                return new SimpleDateFormat("HH:mm E");
            case thisMonth:
                return new SimpleDateFormat("M/dd/E");
                default:
                    return new SimpleDateFormat("M/dd/E");
        }
    }

}
