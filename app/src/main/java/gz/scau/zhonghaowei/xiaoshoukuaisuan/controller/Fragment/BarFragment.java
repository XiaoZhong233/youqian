package gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import gz.scau.zhonghaowei.xiaoshoukuaisuan.Model.Record;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.Model.RecordLab;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.R;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Util.TimeUtil;

public class BarFragment extends Fragment implements OnChartValueSelectedListener {

    private static final String TAG = "BarFragment";
    private HorizontalBarChart mHorizontalBarChart ;
    private List<Record> data;
    private List<Map.Entry<String,Double>> entryList = new ArrayList<>();
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
    //轴线
    XAxis xl;


    public static BarFragment newIntance(int chartType,TimeUtil.DateType dateType, RecordLab.RECORD_TYPE record_type){
        BarFragment barFragment = new BarFragment();
        Bundle args = new Bundle();
        args.putInt(CHART_TYPE,chartType);
        args.putSerializable(DATETYPE_KEY,dateType);
        args.putSerializable(RECORDTYPE_KEY,record_type);
        barFragment.setArguments(args);
        return barFragment;
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
        View view = inflater.inflate(R.layout.bar_fragment,container,false);
        initView(view);
        setView();
        setData();
        return view;
    }


    private void initView(View view){
        mHorizontalBarChart  = view.findViewById(R.id.HorizontalBarChart);
    }

    private void setView(){
        //设置相关属性
        mHorizontalBarChart.setOnChartValueSelectedListener(this);
        mHorizontalBarChart.setDrawBarShadow(false);
        mHorizontalBarChart.setDrawValueAboveBar(true);
        mHorizontalBarChart.getDescription().setEnabled(false);
        mHorizontalBarChart.setMaxVisibleValueCount(60);
        mHorizontalBarChart.setPinchZoom(false);
        mHorizontalBarChart.setDrawGridBackground(false);

        //x轴
        xl = mHorizontalBarChart.getXAxis();
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setDrawAxisLine(true);
        xl.setDrawGridLines(false);
        xl.setGranularity(10f);
        //修复x轴自定义无法正确显示的问题
        xl.setCenterAxisLabels(false);
        xl.setGranularity(1f);

        //y轴
        YAxis yl = mHorizontalBarChart.getAxisLeft();
        yl.setDrawAxisLine(true);
        yl.setDrawGridLines(true);
        yl.setAxisMinimum(0f);

        //y轴
        YAxis yr = mHorizontalBarChart.getAxisRight();
        yr.setEnabled(false);
        yr.setDrawAxisLine(true);
        yr.setDrawGridLines(false);
        yr.setAxisMinimum(0f);

        //设置数据

        mHorizontalBarChart.setFitBars(true);
        mHorizontalBarChart.animateY(2500);

        Legend l = mHorizontalBarChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setFormSize(8f);
        l.setXEntrySpace(4f);
    }



    private void setData(){
        float barWidth = 0.3f;        //每个彩色数据条的宽度
        float spaceForBar = 1f;       //每个数据条实际占的宽度

        ArrayList<BarEntry> entries = new ArrayList<BarEntry>();

        if(curChartType == CHART_TYPE_CLASSES) {
            entries = updateClassesData(this.dateType, this.record_type);
        }else if(curChartType == CHART_TYPE_2td_ZHANGHU){
            entries = updateSecendZhangHuData(this.dateType,this.record_type);
        }

        int count = entries.size();
        //设置数量
        xl.setLabelCount(count);    //显示的坐标数量


        //自定义设置x轴线
        xl.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                int index = (int)value;
                Map.Entry<String,Double> entry = entryList.get(index);
                String s = entry.getKey();
                Log.e(TAG, "getFormattedValue: index="+index+" title="+s );
                return entry.getKey();
            }
        });


        BarDataSet dataSet = new BarDataSet(entries, "分类图表");
        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(dataSet);
        BarData barData = new BarData(dataSets);
        barData.setValueTextSize(10f);
        barData.setBarWidth(barWidth);
        mHorizontalBarChart.setData(barData);



    }


    /**
     * 更新Pie Bar图表数据
     * 适用于类别收入与类别支出
     */
    private ArrayList<BarEntry> updateClassesData(TimeUtil.DateType dateType, RecordLab.RECORD_TYPE record_type){
        float spaceForBar = 1f;       //每个数据条实际占的宽度
        ArrayList<BarEntry> entries = new ArrayList<>();
        RecordLab recordLab = RecordLab.getRecordLab(getActivity());
        //筛选符合条件的记录
        List<Record> data = recordLab.getRecordByDate(dateType).stream()
                .filter(record -> record_type == RecordLab.RECORD_TYPE.INCOME?
                        record.getCost()>0:record.getCost()<0).collect(Collectors.toList());
        this.data = data;
        //统计
        Map<String,Double> result = recordLab.getSumMapByClassesForRecord(data);
        //计算最大值，用于百分比计算
        //float max = getMax(data);
        //更新数据
        Set<Map.Entry<String,Double>> set = result.entrySet();
        entryList = new ArrayList<>();
        entryList.addAll(set);

        for(int i=0;i<entryList.size();i++){
            Map.Entry<String,Double> entry = entryList.get(i);
            BarEntry barEntry = new BarEntry(i*spaceForBar,Math.abs(entry.getValue().floatValue()));
            Log.e(TAG, "updateClassesData: i="+i+",count="+entry.getValue() );
            entries.add(barEntry);
        }
        return entries;
    }



    /**
     * 更新Pie Bar图表数据,适用于二级账户收入与账户支出
     * @param dateType
     * @param record_type
     * @return
     */
    private ArrayList<BarEntry> updateSecendZhangHuData(TimeUtil.DateType dateType, RecordLab.RECORD_TYPE record_type){
        ArrayList<BarEntry> entries = new ArrayList<>();
        RecordLab recordLab = RecordLab.getRecordLab(getActivity());
        //筛选符合条件的记录
        List<Record> data = recordLab.getRecordByDate(dateType).stream()
                .filter(record -> record_type == RecordLab.RECORD_TYPE.INCOME?
                        record.getCost()>0:record.getCost()<0).collect(Collectors.toList());
        this.data = data;
        //统计
        Map<String,Double> result = recordLab.getSumMapBySecendZhanghuForRecord(data);
        //计算最大值，用于百分比计算
        //float max = getMax(data);
        //更新数据
        Set<Map.Entry<String,Double>> set = result.entrySet();
        entryList = new ArrayList<>();
        entryList.addAll(set);

        for(int i=0;i<entryList.size();i++){
            Map.Entry<String,Double> entry = entryList.get(i);
            BarEntry barEntry = new BarEntry(i,Math.abs(entry.getValue().floatValue()));
            Log.e(TAG, "updateSecendZhangHuData: i="+i+",count="+entry.getValue() );
            entries.add(barEntry);
        }
        return entries;
    }


    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }
}
