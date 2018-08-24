package gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;
import com.gyf.barlibrary.ImmersionBar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import cn.qqtheme.framework.picker.LinkagePicker;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.Model.DataServer;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.Model.Record;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.Model.RecordLab;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.R;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.View.LineChartMarkView;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.View.SuperEasyRefreshLayout;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Activity.AddRecordActivity;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Util.TimeUtil;

public class TubiaoFragment extends BaseFragment implements OnChartGestureListener,OnChartValueSelectedListener{


    private static final String TAG = "TubiaoFragment";
    private static Fragment mFragment;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FragmentManager fragmentManager;
    private ViewPager.OnPageChangeListener listener;
    private NestedScrollView scrollView;
    private LineChart lineChart;
    private LineDataSet set1;
    private Spinner date_spinner;
    private List<String> spinner_data;
    private ArrayAdapter arrayAdapter;
    //用于保存碎片
    List<Fragment> fragments;
    private FragmentStatePagerAdapter  pagerAdapter;
    //用于记录tab位置的枚举
    private final int PIE = 0;
    private final int BAR = 1;
    //初始化标志
    private boolean initFlag = true;
    public final int SHOW_DIAGLOG=0;
    private int selected = 0;
    //图标数据类型
    private String type = "分类支出";
    //用于记录上次的类型选择
    private int preFirstIndex = 0;
    private int preSecendIndex = 0;
    //折线图
    XAxis xAxis;
    YAxis leftYAxis;
    YAxis rightYaxis;
    Legend legend;
    //曲线数据最终版
    List<Map.Entry<String,Double>> zhichuList = new ArrayList<>();
    List<Map.Entry<String,Double>> shouruList = new ArrayList<>();
    List<Map.Entry<String,Double[]>> finalData = new ArrayList<>();
    private final int INCOME = 1;
    private final int COST = 0;
    //记录当前日期类型,默认本月
    private TimeUtil.DateType curDateType = TimeUtil.DateType.thisMonth;

    /**
     * 初始化沉浸式
     */
    @Override
    protected void initImmersionBar() {
        mImmersionBar = ImmersionBar.with(this);
        mImmersionBar.keyboardEnable(false).navigationBarWithKitkatEnable(false).
                titleBar(toolbar)
                .navigationBarColor(R.color.colorPrimary)
                .init();
    }

    public static Fragment newIntance(){
        mFragment=new TubiaoFragment();
        return mFragment;
    }


    @Override
    public void onResume() {
        super.onResume();
        //只有在非初次加载到时候才会刷新图表,因为初次加载时在onCreteView中已经刷新过图表了，不必重复刷新绘制
        if(!initFlag){
            //updateZheXianTu(TimeUtil.DateType.thisMonth);
            //updateTuBiao(PieFragment.CHART_TYPE_CLASSES, TimeUtil.DateType.thisMonth, RecordLab.RECORD_TYPE.COST);
            //修复本月情况下不自动刷新
            if(curDateType== TimeUtil.DateType.thisMonth){
                updateZheXianTu(TimeUtil.DateType.thisMonth);
                updateTuBiao(PieFragment.CHART_TYPE_CLASSES, TimeUtil.DateType.thisMonth, RecordLab.RECORD_TYPE.COST);
            }
            date_spinner.setSelection(spinner_data.indexOf("本月"));
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "onPause:");
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //嵌套fragment必须由子frgmentManager管理
        fragmentManager = getChildFragmentManager();
        //frgment中必须定义，否则菜单回调不执行
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e(TAG, "TuBiaoFragment onCreateView:" );
        View view = inflater.inflate(R.layout.tubiao_fragment,container,false);
        initView(view);
        initAdapter();
        setView();
        initLineChart(lineChart);


        initZheXianChartData(COST,RecordLab.getRecordLab(getActivity()).getRecordByDate(TimeUtil.DateType.thisMonth).stream()
                .filter(record -> record.getCost()<0).collect(Collectors.toList()));
        initZheXianChartData(INCOME,RecordLab.getRecordLab(getActivity()).getRecordByDate(TimeUtil.DateType.thisMonth).stream()
                .filter(record -> record.getCost()>0).collect(Collectors.toList()));
        initXAxis(TimeUtil.DateType.thisMonth);

        showLineChart("支出曲线",Color.CYAN);
        //暂时不需要收入曲线
        //addShouruLine("收入曲线",getResources().getColor(R.color.orange,null));
        //setZhichuChartFillDrawable(getResources().getDrawable(R.drawable.fade_blue,null));

        setMarkerView();
        return view;
    }


    private void setView(){
        listener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //viewpager适配沉浸式
                if(initFlag){
                    mImmersionBar.statusBarDarkFont(false).titleBar(R.id.toolbar).keyboardEnable(true).init();
                    initFlag = false;
                }
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
        viewPager.addOnPageChangeListener(listener);
        toolbar.inflateMenu(R.menu.record_detail_toolbar_menu);
        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()){
                case R.id.setting:
                    onCountPicker(toolbar);
                    break;
            }
            return true;
        });

        spinner_data = new ArrayList<>();
        spinner_data.add("本月");
        spinner_data.add("本周");
        spinner_data.add("今年");
        arrayAdapter = new ArrayAdapter(getActivity(),R.layout.item_spinselect,spinner_data);
        arrayAdapter.setDropDownViewResource(R.layout.item_dialogspinselect);
        date_spinner.setAdapter(arrayAdapter);
        date_spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String s = spinner_data.get(position);
                switch (s){
                    case "本月":
                        curDateType = TimeUtil.DateType.thisMonth;
                        break;
                    case "本周":
                        curDateType = TimeUtil.DateType.thisWeek;
                        break;
                    case "今年":
                        curDateType = TimeUtil.DateType.thisYear;
                        break;
                }

                if(!initFlag) {
                    updateZheXianTu(curDateType);
                    updateTuBiao();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initView(View view){
        toolbar = view.findViewById(R.id.toolbar);
        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewpager);
        scrollView = view.findViewById(R.id.scrollView);
        lineChart = view.findViewById(R.id.lineChart);
        date_spinner = view.findViewById(R.id.date_spinner);
    }


    /**
     * 关于刷新viewpagerAdapter的UI与数据的详细细节,可以参考
     * https://blog.csdn.net/jiang547860818/article/details/54380055
     */
    private void initAdapter(){
        fragments = new ArrayList<>();
        fragments.add(PieFragment.newIntance(PieFragment.CHART_TYPE_CLASSES, TimeUtil.DateType.thisMonth, RecordLab.RECORD_TYPE.COST));
        fragments.add(BarFragment.newIntance(BarFragment.CHART_TYPE_CLASSES, TimeUtil.DateType.thisMonth, RecordLab.RECORD_TYPE.COST));

        pagerAdapter = new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                switch (position){
                    case PIE:
                        return fragments.get(0);
                    case BAR:
                        return fragments.get(1);
                        default:
                            return null;
                }

            }

            @Override
            public int getCount() {
                return fragments.size();
            }


            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                switch (position){
                    case PIE:
                        return "饼图";
                    case BAR:
                        return "条形图";
                        default:
                            return "null";
                }
            }




            @Override
            public int getItemPosition(Object object) {
                return POSITION_NONE;
            }


        };

        viewPager.setAdapter(pagerAdapter);
        //tabLayout绑定ViewPager
        tabLayout.setupWithViewPager(viewPager);
//        updateTuBiao(PieFragment.CHART_TYPE_CLASSES, TimeUtil.DateType.thisMonth, RecordLab.RECORD_TYPE.COST);

    }


    /**
     * 调用此方法刷新图表即可
     * @param chart_type
     * @param dateType
     * @param record_type
     */
    private void updateTuBiao(int chart_type,TimeUtil.DateType dateType,RecordLab.RECORD_TYPE record_type){
        if(pagerAdapter!=null){
            PieFragment pieFragment = PieFragment.newIntance(chart_type,dateType,record_type);
            BarFragment barFragment = BarFragment.newIntance(chart_type,dateType,record_type);
            ArrayList<Fragment> fragments = new ArrayList<>();
            fragments.add(pieFragment);
            fragments.add(barFragment);
            setFragments(fragments);
        }
    }


    private void updateZheXianTu(TimeUtil.DateType dateType){
        initLineChart(lineChart);
        initZheXianChartData(COST,RecordLab.getRecordLab(getActivity()).getRecordByDate(dateType).stream()
                .filter(record -> record.getCost()<0).collect(Collectors.toList()));
        initZheXianChartData(INCOME,RecordLab.getRecordLab(getActivity()).getRecordByDate(dateType).stream()
                .filter(record -> record.getCost()>0).collect(Collectors.toList()));
        initXAxis(dateType);
        LineData lineData = lineChart.getData();
        if(lineData!=null){
            if(lineData.getDataSets()!=null){
                lineData.clearValues();
                Log.e(TAG, "updateZheXianTu: clear" );
            }
        }
        showLineChart("支出曲线",Color.CYAN);
        leftYAxis.resetAxisMaximum();
        //addShouruLine("收入曲线",getResources().getColor(R.color.orange,null));
        //leftYAxis.resetAxisMaximum();

        setMarkerView();
    }




    /**
     * 这个方法本来应该是在Adapter内部的,因为外部类不可访问内部类，又懒得把匿名内部类单独写成继承类,就直接把方法抽出来了
     * 所以这个方法只能已经创建了pagerAdapter实例下使用
     * 更新fragmentManager中的fragments队列
     * @param fragments
     */
    private void setFragments(ArrayList<Fragment> fragments) {
        if(pagerAdapter!=null) {
            if (TubiaoFragment.this.fragments != null) {
                FragmentTransaction ft = fragmentManager.beginTransaction();
                for (Fragment f : TubiaoFragment.this.fragments) {
                    ft.remove(f);
                }
                ft.commit();
                ft = null;
                fragmentManager.executePendingTransactions();
            }
            TubiaoFragment.this.fragments = fragments;
            pagerAdapter.notifyDataSetChanged();
        }
    }


    private void initLineChart(LineChart lineChart){
        /***图表设置***/
        //是否展示网格线
        lineChart.setDrawGridBackground(false);
        //是否显示边界
        lineChart.setDrawBorders(true);
        //是否可以拖动
        lineChart.setDragEnabled(false);
        //是否有触摸事件
        lineChart.setTouchEnabled(true);
        //设置XY轴动画效果
        lineChart.animateY(2000);
        lineChart.animateX(1000);
        lineChart.setBackgroundColor(Color.WHITE);
        //是否显示边界
        lineChart.setDrawBorders(false);
        Description description = new Description();
//        description.setText("需要展示的内容");
        description.setEnabled(false);
        lineChart.setDescription(description);



        /***XY轴的设置***/
        xAxis = lineChart.getXAxis();
        leftYAxis = lineChart.getAxisLeft();
        rightYaxis = lineChart.getAxisRight();
        //X轴设置显示位置在底部
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(1f);
        //保证Y轴从0开始，不然会上移一点
        leftYAxis.setAxisMinimum(0f);
        rightYaxis.setAxisMinimum(0f);
        xAxis.setDrawGridLines(false);
        rightYaxis.setDrawGridLines(false);
        leftYAxis.setDrawGridLines(true);
        leftYAxis.enableGridDashedLine(10f, 10f, 0f);
        rightYaxis.setEnabled(false);

        /***折线图例 标签 设置***/
        legend = lineChart.getLegend();
        //设置显示类型，LINE CIRCLE SQUARE EMPTY 等等 多种方式，查看LegendForm 即可
        legend.setForm(Legend.LegendForm.LINE);
        legend.setTextSize(12f);
        //显示位置 左下方
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        //是否绘制在图表里面
        legend.setDrawInside(false);

    }

    /**
     * 曲线初始化设置 一个LineDataSet 代表一条曲线
     *
     * @param lineDataSet 线条
     * @param color       线条颜色
     * @param mode
     */
    private void initLineDataSet(LineDataSet lineDataSet, int color, LineDataSet.Mode mode) {
        lineDataSet.setColor(color);
        lineDataSet.setCircleColor(color);
        lineDataSet.setLineWidth(1f);
        lineDataSet.setCircleRadius(3f);
        //设置曲线值的圆点是实心还是空心
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setValueTextSize(10f);
        //设置折线图填充
        lineDataSet.setDrawFilled(true);
        lineDataSet.setFormLineWidth(1f);
        lineDataSet.setFormSize(15.f);
        if (mode == null) {
            //设置曲线展示为圆滑曲线（如果不设置则默认折线）
            lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        } else {
            lineDataSet.setMode(mode);
        }
    }


    /**
     * 准备数据源
     * @param type
     * @param dataList
     */
    private void initZheXianChartData(int type,List<Record> dataList){
        if(dataList.isEmpty()){
            return;
        }

        Map<String,Double> result = RecordLab.getRecordLab(getActivity()).getSumMapByDayForRecord(dataList);
        Set<Map.Entry<String,Double>> entrySet = result.entrySet();
        List<Map.Entry<String,Double>> resultList = new ArrayList<>();
        for(Map.Entry<String,Double> e : entrySet){
            resultList.add(e);
        }
        //最终处理好的数据
        if(type == COST) {
            zhichuList = resultList.stream().sorted(Comparator.comparing(Map.Entry::getKey)).collect(Collectors.toList());
        }else if(type ==  INCOME){
            shouruList = resultList.stream().sorted(Comparator.comparing(Map.Entry::getKey)).collect(Collectors.toList());
        }
    }

    /**
     * 初始化X轴
     */
    private void initXAxis(TimeUtil.DateType dateType){
        //x轴数据处理
        List<Map.Entry<String,Double>> a = new ArrayList<>();
        a.addAll(shouruList);
        a.addAll(zhichuList);
        //1个日期,对应多个double值
        Map<String,Double[]> dateMap = new HashMap<>();
        for(Map.Entry<String,Double> map : a){
            String date = map.getKey();
            double count  = map.getValue();
            //如果不包含某一日期,则新建数组，否则加入新的double
            //约定:0索引为支出位,1索引为收入位
            if(!dateMap.containsKey(date)){
                Double[] doubles = new Double[2];
                if(count>0){
                    doubles[1]=count;
                    doubles[0]=0d;
                }else {
                    doubles[0]=count;
                    doubles[1]=0d;
                }
                dateMap.put(date,doubles);
            }else {
                Double[] doubles = dateMap.get(date);
                if(count>0){
                    doubles[1]=count;
                }else {
                    doubles[0]=count;
                }
            }
        }
        Set<Map.Entry<String,Double[]>> entries = dateMap.entrySet();
        List<Map.Entry<String,Double[]>> list = new ArrayList<>();
        for(Map.Entry<String,Double[]> entry : entries){
            list.add(entry);
        }
        //排序
        finalData = list.stream().sorted(Comparator.comparing(Map.Entry::getKey)).collect(Collectors.toList());
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                int index = (int)value;
                try {
                    Map.Entry<String, Double[]> entry = finalData.get(index);
                    return dateFormat(entry.getKey(), dateType);
                }catch (Exception e){
                    Map.Entry<String, Double[]> entry = finalData.get(0);
                    return dateFormat(entry.getKey(), dateType);
                }
            }
        });

        //leftYAxis.setValueFormatter((value, axis) -> value+"元");
        leftYAxis.resetAxisMaximum();

    }

    /**
     * 展示曲线
     *
     * @param name     曲线名称
     * @param color    曲线颜色
     */
    public void showLineChart(String name, int color) {
        if(finalData.isEmpty()){
            return;
        }
        List<Entry> zhichuEntries = new ArrayList<>();
            //zhichuList空则说明，一定是在shouruList中
        for(int i=0;i<finalData.size();i++){
            /**
             * 在此可查看 Entry构造方法，可发现 可传入数值 Entry(float x, float y)
             * 也可传入Drawable， Entry(float x, float y, Drawable icon) 可在XY轴交点 设置Drawable图像展示
             */

            Map.Entry<String,Double[]> stringEntry = finalData.get(i);
            float count = stringEntry.getValue()[0].floatValue();
            Entry entry = new Entry(i,Math.abs(count));
            zhichuEntries.add(entry);
        }
        // 每一个LineDataSet代表一条线
        LineDataSet lineDataSet = new LineDataSet(zhichuEntries, name);
        initLineDataSet(lineDataSet, color, LineDataSet.Mode.LINEAR);
        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);
    }


    /**
     * 设置支出线条填充背景颜色
     *
     * @param drawable
     */
    public void setZhichuChartFillDrawable(Drawable drawable) {
        if (lineChart.getData() != null && lineChart.getData().getDataSetCount() > 0) {
            LineDataSet lineDataSet = (LineDataSet) lineChart.getData().getDataSetByIndex(0);
            //避免在 initLineDataSet()方法中 设置了 lineDataSet.setDrawFilled(false); 而无法实现效果
            lineDataSet.setDrawFilled(true);
            lineDataSet.setFillDrawable(drawable);
            lineChart.invalidate();
        }
    }

    /**
     * 设置收入线条填充颜色
     * @param drawable
     */
    public void setShouruChartFillDrawable(Drawable drawable){
        if (lineChart.getData() != null && lineChart.getData().getDataSetCount() > 0) {
            LineDataSet lineDataSet = (LineDataSet) lineChart.getData().getDataSetByIndex(1);
            //避免在 initLineDataSet()方法中 设置了 lineDataSet.setDrawFilled(false); 而无法实现效果
            lineDataSet.setDrawFilled(true);
            lineDataSet.setFillDrawable(drawable);
            lineChart.invalidate();
        }
    }



    /**
     * 设置可以显示XY轴自定义值的MarkerView
     */
    public void setMarkerView() {
        LineChartMarkView mv = new LineChartMarkView(getActivity(), xAxis.getValueFormatter());
        mv.setChartView(lineChart);
        lineChart.setMarker(mv);
        lineChart.invalidate();
    }



    /**
     * 添加曲线
     */
    private void addShouruLine(String name, int color) {
        if(finalData.isEmpty()){
            return;
        }
        List<Entry> entries = new ArrayList<>();


        for(int i=0;i<finalData.size();i++){
            Map.Entry<String,Double[]> stringEntry = finalData.get(i);
            float count = stringEntry.getValue()[1].floatValue();
            Entry entry = new Entry(i,Math.abs(count));
            entries.add(entry);
        }
        // 每一个LineDataSet代表一条线
        LineDataSet lineDataSet = new LineDataSet(entries, name);
        initLineDataSet(lineDataSet, color, LineDataSet.Mode.LINEAR);
        lineChart.getLineData().addDataSet(lineDataSet);
        lineChart.invalidate();
    }


    /**
     * 点击切换不同类型事件响应
     */
    private void updateTuBiao(){
        switch (type){
            case "分类支出":
                updateTuBiao(PieFragment.CHART_TYPE_CLASSES, curDateType, RecordLab.RECORD_TYPE.COST);
                break;
            case "账户支出":
                updateTuBiao(PieFragment.CHART_TYPE_2td_ZHANGHU,curDateType,RecordLab.RECORD_TYPE.COST);
                break;
            case "分类收入":
                updateTuBiao(PieFragment.CHART_TYPE_CLASSES, curDateType, RecordLab.RECORD_TYPE.INCOME);
                break;
            case "账户收入":
                updateTuBiao(PieFragment.CHART_TYPE_2td_ZHANGHU,curDateType,RecordLab.RECORD_TYPE.INCOME);
                break;

        }
    }


    private String dateFormat(String sDate, TimeUtil.DateType dateType){
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd");
        switch (dateType) {
            case thisWeek:
                format = new SimpleDateFormat("E");
                break;
            case lastOneMonth:
            case lastThreeMonths:
            case thisMonth:
                format = new SimpleDateFormat("MM-dd");
                break;
            case thisYear:
                format = new SimpleDateFormat("MM-dd");
                break;
            case lastOneYear:
                format = new SimpleDateFormat("yy-MM-dd");
                break;
            case ALL:
                format = new SimpleDateFormat("yy-MM-dd");
                break;
        }
        String formatStr = "";
        try {
            Date date = sf.parse(sDate);
            formatStr = format.format(date);
        }catch (Exception e){
            Log.e(TAG, "dateFormat: error 日期格式化失败");
            e.printStackTrace();
        }
        return formatStr;
    }


    /**
     * 二级联动筛选器
     * @param view
     */
    public void onCountPicker(View view){
        List<String> secendl = new ArrayList<>();
        List<String> firstl = new ArrayList<>();
        LinkagePicker.DataProvider provider = new LinkagePicker.DataProvider() {
            @NonNull
            @Override
            public List<String> provideFirstData() {
                List<String> firstList = new ArrayList<>();
                firstList.add("支出");
                firstList.add("收入");
                firstl.addAll(firstList);
                return firstList;
            }
            @NonNull
            @Override
            public List<String> provideSecondData(int firstIndex) {
                List<String> secendList = new ArrayList<>();
                switch (firstIndex){
                    case 0:
                        secendList.add("分类支出");
                        secendList.add("账户支出");
                        break;
                    case 1:
                        secendList.add("分类收入");
                        secendList.add("账户收入");
                        break;
                }
                secendl.addAll(secendList);
                return secendList;
            }

            @Nullable
            @Override
            public List<String> provideThirdData(int firstIndex, int secondIndex) {
                return null;
            }

            @Override
            public boolean isOnlyTwo() {
                return true;
            }
        };

        LinkagePicker picker = new LinkagePicker(getActivity(), provider);
        picker.setCycleDisable(true);
        picker.setUseWeight(true);
        picker.setSelectedIndex(preFirstIndex,preSecendIndex);
        picker.setContentPadding(10, 0);
        picker.setLineSpaceMultiplier(3);
        picker.setCancelable(true);
        picker.setGravity(Gravity.CENTER);
        picker.setOnStringPickListener(new LinkagePicker.OnStringPickListener() {
            @Override
            public void onPicked(String first, String second, String third) {
                preFirstIndex = firstl.indexOf(first);
                preSecendIndex = secendl.indexOf(second);
                Log.e(TAG, "onPicked: f,s "+preFirstIndex+" ,"+preSecendIndex );
                type = second;
                updateTuBiao();
            }
        });
        picker.show();
    }


    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartLongPressed(MotionEvent me) {

    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {

    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {

    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {

    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }
}
