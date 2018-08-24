package gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import gz.scau.zhonghaowei.xiaoshoukuaisuan.Model.Record;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.Model.RecordLab;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.R;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Item.MonthSecendItem;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Item.YearFirstItem;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Util.LoadIconHelper;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Util.TimeUtil;

import static gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Adapter.ExpandableItemAdapter.TYPE_RECORD;

public class YMRExpandableItemAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {


    private Context context;
    private List<Record> curData;
    private int curYear;
    //目前只有三层,第三层是Record已经标记过了
    public static final int TYPE_LEVEL_0 = 0;
    public static final int TYPE_LEVEL_1 =1;

    public YMRExpandableItemAdapter(Context context,List<MultiItemEntity> data) {
        super(data);
        this.context = context;
        this.curData = new ArrayList<>();
        addItemType(TYPE_LEVEL_0, R.layout.lv0_year_layout);
        addItemType(TYPE_LEVEL_1,R.layout.item_lv0_month);
        addItemType(TYPE_RECORD,R.layout.item_lv1_record);

    }


    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {
        switch (helper.getItemViewType()){
            case TYPE_LEVEL_0:
                YearFirstItem yearFirstItem = (YearFirstItem)item;
                int year = yearFirstItem.getYear();
                curYear = year;
                helper.setText(R.id.year_tv,year+"年");
                helper.setImageResource(R.id.iv, yearFirstItem.isExpanded() ? R.mipmap.arrow_b : R.mipmap.arrow_r);

                //设置点击事件
                helper.itemView.setOnClickListener(v -> {
                    int pos = helper.getAdapterPosition();
                    Log.d(TAG, "Level 0 item pos: " + pos);
                    if (yearFirstItem.isExpanded()) {
                        collapse(pos);
                    } else {
                        expand(pos);
                    }
                });
                break;
            case TYPE_LEVEL_1:
                MonthSecendItem monthSecendItem = (MonthSecendItem)item;
                int month = monthSecendItem.getMonth();
                curData = monthSecendItem.getData();
                Map<String,Float> map = RecordLab.getRecordLab(context).getRecordStatistics(curData);
                String liuru = floatToInt(Math.abs(map.get(RecordLab.INCOME_KEY)));
                String liuchu = floatToInt(Math.abs(map.get(RecordLab.COST_KEY)));
                String jieyu = floatToInt(map.get(RecordLab.INCOME_KEY)+map.get(RecordLab.COST_KEY));
                //设置数据
                helper.setText(R.id.month, String.valueOf(month+"月"));
                helper.setText(R.id.year,String.valueOf(curYear+"年"));
                helper.setText(R.id.liuru,liuru);
                helper.setText(R.id.liuchu,liuchu);
                helper.setText(R.id.count,jieyu);
                helper.setImageResource(R.id.iv, monthSecendItem.isExpanded() ? R.mipmap.arrow_b : R.mipmap.arrow_r);
                //设置点击事件
                helper.itemView.setOnClickListener(v -> {
                    int pos = helper.getAdapterPosition();
                    Log.d(TAG, "Level 0 item pos: " + pos);
                    if (monthSecendItem.isExpanded()) {
                        collapse(pos);
                    } else {
                        expand(pos);
                    }
                });
                break;
            case TYPE_RECORD:
                int preDay=-1;
                for(int i=0;i<curData.size();i++) {
                    Record fuckTheRecord = curData.get(i);
                    Log.e(TAG, "generateData: preDay " + preDay);
                    Log.e(TAG, "generateData: record.getRecordDay()" + fuckTheRecord.getRecordDay());
                    if (preDay != fuckTheRecord.getRecordDay()) {
                        //不相等则说明不是同一天,是新的一天了
                        fuckTheRecord.setFirst(true);
                        //更新当前日
                        preDay = fuckTheRecord.getRecordDay();
                    }
                }

                Record record = (Record)item;
                boolean isFirst = record.isFirst();
                DecimalFormat decimalFormat = new DecimalFormat("00");

                if(isFirst){
                    helper.getView(R.id.day).setVisibility(View.VISIBLE);
                    helper.getView(R.id.week).setVisibility(View.VISIBLE);
                    helper.setText(R.id.day,decimalFormat.format(record.getRecordDay()));
                    helper.setText(R.id.week,new SimpleDateFormat("E").format(record.getDate()));
                }else {
                    helper.getView(R.id.day).setVisibility(View.INVISIBLE);
                    helper.getView(R.id.week).setVisibility(View.INVISIBLE);
                }

                helper.setText(R.id.classes,record.getClasses());
                helper.setText(R.id.date,getDateFormate(TimeUtil.DateType.today).format(record.getDate()));
                helper.setText(R.id.count_type,record.getPay_type());
                helper.setText(R.id.cost,floatToInt(record.getCost()));
                ImageView imageView = (ImageView)helper.getView(R.id.classes_img);
                LoadIconHelper.loadIconByClass(context,record.getClasses(),imageView);
                helper.addOnClickListener(R.id.content).addOnClickListener(R.id.delete);
                //要将isFirst设置回来
                record.setFirst(false);
                break;
        }
    }

    /**
     * 日期格式化
     * @param dateType
     * @return
     */
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


    /**
     * 金额格式化
     * @param num
     * @return
     */
    private String floatToInt(float num){
        //num=Math.abs(num); //情况是为小数，首位为0或者等于0
        if(num == 0 || Math.abs(num) < 1){
            DecimalFormat decimalFormat=new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
            String p = decimalFormat.format(num);
            return p;
        }
        else {
            DecimalFormat titleFormat=new DecimalFormat(",###.00");
            return titleFormat.format(num);
        }
    }

}
