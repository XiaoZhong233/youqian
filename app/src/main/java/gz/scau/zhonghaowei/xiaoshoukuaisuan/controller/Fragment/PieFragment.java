package gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Fragment;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import gz.scau.zhonghaowei.xiaoshoukuaisuan.Model.DataServer;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.Model.Record;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.Model.RecordLab;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.R;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Util.TimeUtil;

public class PieFragment extends Fragment implements OnChartValueSelectedListener{

    private static final String TAG = "PieFragment";
    private PieChart pieChart;
    private List<Record> data;
    private Typeface tf;
    //用于通信标记的键
    public static final String CHART_TYPE = "chartType";
    public static final String DATETYPE_KEY = "dateType";
    public static final String RECORDTYPE_KEY = "recordType";
    //用于标记表格类型，类别图表或者二级账户图表
    public static final int CHART_TYPE_CLASSES = 0;
    public static final int CHART_TYPE_2td_ZHANGHU = 1;
    //用于标记当前实例的图表类型
    private  int curChartType = CHART_TYPE_CLASSES; //默认为类别图表
    private TimeUtil.DateType dateType;
    private RecordLab.RECORD_TYPE record_type;


    /**
     * 创建fragmeng实例
     * @param chartType  图表类型
     * @param dateType    时间类型
     * @param record_type 支出或收入
     * @return
     */
    public static PieFragment newIntance(int chartType,TimeUtil.DateType dateType, RecordLab.RECORD_TYPE record_type){
        PieFragment pieFragment = new PieFragment();
        Bundle args = new Bundle();
        args.putInt(CHART_TYPE,chartType);
        args.putSerializable(DATETYPE_KEY,dateType);
        args.putSerializable(RECORDTYPE_KEY,record_type);
        pieFragment.setArguments(args);
        return pieFragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //获取传入的数据
        Bundle args = getArguments();
        this.curChartType = args.getInt(CHART_TYPE);
        this.dateType = (TimeUtil.DateType) args.getSerializable(DATETYPE_KEY);
        this.record_type = (RecordLab.RECORD_TYPE) args.getSerializable(RECORDTYPE_KEY);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pie_frgment,container,false);
        initView(view);
        setView();
        setData();
        return view;
    }


    private void initView(View view){
        pieChart = view.findViewById(R.id.piechart);
    }

    private void setView(){
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(3, 15, 50, 20);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        tf = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Regular.ttf");
        
        pieChart.setCenterTextTypeface(Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Light.ttf"));

        switch (record_type){
            case COST:
                if(curChartType==CHART_TYPE_CLASSES){
                    pieChart.setCenterText(generateCenterSpannableText("分类支出"));

                }else if(curChartType == CHART_TYPE_2td_ZHANGHU){
                    pieChart.setCenterText(generateCenterSpannableText("账户支出"));

                }
                break;
            case INCOME:
                if(curChartType==CHART_TYPE_CLASSES){
                    pieChart.setCenterText(generateCenterSpannableText("分类收入"));
                }else if(curChartType == CHART_TYPE_2td_ZHANGHU){
                    pieChart.setCenterText(generateCenterSpannableText("账户收入"));
                }
                break;
                default:
                    pieChart.setCenterText(generateCenterSpannableText("分类图表"));
                    break;
        }


        //pieChart.setExtraOffsets(20.f, 0.f, 50.f, 0.f);

        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);

        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(110);

        pieChart.setHoleRadius(47f);
        pieChart.setTransparentCircleRadius(52f);
        pieChart.setCenterTextSize(10f);                //设置PieChart内部圆文字的大小

        pieChart.setDrawCenterText(true);

        pieChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);
        pieChart.setNoDataText("暂无数据");

        // pieChart.setUnit(" €");
        // pieChart.setDrawUnitsInChart(true);

        // add a selection listener
        pieChart.setOnChartValueSelectedListener(this);


        pieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        // pieChart.spin(2000, 0, 360);

        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setEnabled(true);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

    }

    private SpannableString generateCenterSpannableText(String ss) {

        SpannableString s = new SpannableString(ss+"\ndeveloped by zhong");
        s.setSpan(new RelativeSizeSpan(1.5f), 0, 4, 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), 4, s.length() - 18, 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), 4, s.length() - 18, 0);
        s.setSpan(new RelativeSizeSpan(.65f), 4, s.length() - 18, 0);
        s.setSpan(new StyleSpan(Typeface.ITALIC), s.length() - 4, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length() - 5, s.length(), 0);
        return s;
    }



    /**
     * 初始化设置图表数据
     *
     */
    private void setData(){
        ArrayList<PieEntry> entries = new ArrayList<>();
//        for(int i=0;i<count;i++){
//            entries.add(new PieEntry(data.get(i).getCost()/range,data.get(i).getClasses()));
//        }
        if(curChartType == CHART_TYPE_CLASSES) {
            entries = updateClassesData(this.dateType, this.record_type);
        }else if(curChartType == CHART_TYPE_2td_ZHANGHU){
            entries = updateSecendZhangHuData(this.dateType,this.record_type);
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(10f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);

        dataSet.setValueLinePart1OffsetPercentage(80.f);
        dataSet.setValueLinePart1Length(0.2f);
        dataSet.setValueLinePart2Length(0.4f);

        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.BLACK);
        data.setValueTypeface(tf);

        pieChart.setData(data);

        // undo all highlights
        //pieChart.highlightValues(null);
        pieChart.setEntryLabelColor(Color.BLACK);       //设置pieChart图表文本字体颜色
        pieChart.setEntryLabelTextSize(10f);
        pieChart.setEntryLabelTypeface(Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Light.ttf"));
        pieChart.invalidate();

    }


    /**
     * 更新Pie Bar图表数据
     * 适用于类别收入与类别支出
     */
    private ArrayList<PieEntry> updateClassesData(TimeUtil.DateType dateType, RecordLab.RECORD_TYPE record_type){
        ArrayList<PieEntry> entries = new ArrayList<>();
        RecordLab recordLab = RecordLab.getRecordLab(getActivity());
        //筛选符合条件的记录
        List<Record> data = recordLab.getRecordByDate(dateType).stream()
                .filter(record -> record_type == RecordLab.RECORD_TYPE.INCOME?
                record.getCost()>0:record.getCost()<0).collect(Collectors.toList());
        this.data = data;
        //统计
        Map<String,Double> result = recordLab.getSumMapByClassesForRecord(data);
        //计算最大值，用于百分比计算
        float max = getMax(data);
        //更新数据
        Set<Map.Entry<String,Double>> set = result.entrySet();
        for(Map.Entry<String,Double> entry : set){
            entries.add(new PieEntry( entry.getValue().floatValue()/max,
                    entry.getKey()));
        }
        return entries;
    }



    /**
     * 更新Pie Bar图表数据,适用于二级账户收入与账户支出
     * @param dateType
     * @param record_type
     * @return
     */
    private ArrayList<PieEntry> updateSecendZhangHuData(TimeUtil.DateType dateType,RecordLab.RECORD_TYPE record_type){
        ArrayList<PieEntry> entries = new ArrayList<>();
        RecordLab recordLab = RecordLab.getRecordLab(getActivity());
        //筛选符合条件的记录
        List<Record> data = recordLab.getRecordByDate(dateType).stream()
                .filter(record -> record_type == RecordLab.RECORD_TYPE.INCOME?
                        record.getCost()>0:record.getCost()<0).collect(Collectors.toList());
        this.data = data;
        //统计
        Map<String,Double> result = recordLab.getSumMapBySecendZhanghuForRecord(data);
        //计算最大值，用于百分比计算
        float max = getMax(data);
        //更新数据
        Set<Map.Entry<String,Double>> set = result.entrySet();
        for(Map.Entry<String,Double> entry : set){
            entries.add(new PieEntry( entry.getValue().floatValue()/max,entry.getKey()));
        }
        return entries;
    }




    @Override
    public void onValueSelected(Entry e, Highlight h) {
        if (e == null)
            return;
        Log.i("VAL SELECTED",
                "Value: " + e.getY() + ", xIndex: " + e.getX()
                        + ", DataSet index: " + h.getDataSetIndex());
    }

    @Override
    public void onNothingSelected() {
        Log.i("PieChart", "nothing selected");
    }

    private float getMax(List<Record> data){
        float max = 0;
        if(!data.isEmpty()) {
            max = data.get(0).getCost();
            for (int i = 0; i < data.size(); i++) {
                if (max < data.get(i).getCost()) {
                    max = data.get(i).getCost();
                }
            }
        }
        return max;
    }



}
