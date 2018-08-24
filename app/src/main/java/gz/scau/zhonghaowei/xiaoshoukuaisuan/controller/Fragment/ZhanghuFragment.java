package gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Fragment;

import android.animation.FloatEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gyf.barlibrary.ImmersionBar;
import com.robinhood.ticker.TickerUtils;
import com.robinhood.ticker.TickerView;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import gz.scau.zhonghaowei.xiaoshoukuaisuan.Model.Budget;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.Model.CountTypeSection;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.Model.DataServer;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.Model.DateSection;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.Model.RecordLab;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.R;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.View.BeatTextView;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.View.WaterWaveView;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.View.WaveView;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Activity.AddBudgetActivity;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Activity.RecordsDetailActivity;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Adapter.CountTypeSectionAdapter;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Adapter.DateSectionAdapter;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Util.TimeUtil;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Util.WaveHelper;

import static android.content.Context.MODE_PRIVATE;
import static gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Activity.AddBudgetActivity.BUDGET;


public class ZhanghuFragment extends BaseFragment {

    public static final String TAG = "ZhanghuFragment";
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolBar;

    private WaveView waveView;
    private int mBorderColor = Color.parseColor("#44FFFFFF");
    private int mBorderWidth = 5;
    private WaveHelper mWaveHelper;
    private Button button;
    private float lastRadio=0;
    private TickerView jingzichan;
    private TickerView zichan;
    private TickerView fuzhai;
    private TextView persent;
    private TextView budget_tv;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CountTypeSectionAdapter countTypeSectionAdapter;
    private List<CountTypeSection> countTypeSectionList;

    //波动条颜色枚举值
    private static final int RED = 0;
    private static final int GREEN = 1;
    private static final int BLUE = 2;
    private static final int DEFAULT= 3;

    //标题格式化
    DecimalFormat titleFormat=new DecimalFormat(",###.00");

    //标志
    private boolean needAnimation = true;

    //请求码
    private final int ADD_BUDGET_REQUEST_CODE=3;
    //预算信息
    private int type=0;
    private float value = 0f;
    private Date budgetDate = new Date();

    /** 初始化沉浸式------------------------------------------------------------------------------------*/

    /**
     * 是否在Fragment使用沉浸式
     *
     * @return the boolean
     */
    @Override
    protected boolean isImmersionBarEnabled() {
        return true;
    }


    /**
     * 初始化沉浸式
     */
    @Override
    protected void initImmersionBar() {
        mImmersionBar = ImmersionBar.with(this);
        mImmersionBar.keyboardEnable(true).navigationBarWithKitkatEnable(false).titleBar(toolbar).init();
    }

    /** 初始化沉浸式------------------------------------------------------------------------------------*/


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        save();
        SimpleDateFormat  sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm E");
        Log.e(TAG, "onCreate: type ="+this.type );
        Log.e(TAG, "onCreate: budget ="+this.value );
        Log.e(TAG, "onCreate: date ="+sdf.format(this.budgetDate));
    }


    public static Fragment newIntance(){
        return new ZhanghuFragment();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.e(TAG, "onHiddenChanged: " );
        if (!hidden && mImmersionBar != null)
            Log.e(TAG, "onHiddenChanged: init" );
        //如果不重写这一句，可能会造成在切换的过程中沉浸式失效
        //因为父类的变量是所以碎片共享的
        //另外需要注意的是，在hide/show切换的过程中一旦涉及到会让此碎片生命周期重新走一遍的碎片，则该碎片不要继承
        //于BaseFragment，否则会造成沉浸式失效,因为改变了初始化.
        mImmersionBar.keyboardEnable(false).navigationBarWithKitkatEnable(false).titleBar(toolbar).init();
        if(!hidden) {
            needAnimation = false;
            onSwipeLayoutRefresh();
            Log.e(TAG, "onHiddenChanged: refresh" );
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.zhanghu_fragment,container,false);
        initView(view);
        initData();
        setView();
        initAdapter();
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        mWaveHelper.cancel();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: " );
        needAnimation = false;
        onSwipeLayoutRefresh();
        Log.e(TAG, "onHiddenChanged: refresh" );
        mWaveHelper.start();
    }

    private void initView(View view){
        toolbar = view.findViewById(R.id.toolbar);
        collapsingToolBar = view.findViewById(R.id.collapsingToolBar);
        waveView = view.findViewById(R.id.waveView);
        button = view.findViewById(R.id.button);
        jingzichan = view.findViewById(R.id.jingzichan);
        zichan = view.findViewById(R.id.zichan);
        fuzhai = view.findViewById(R.id.fuzhai);
        persent = view.findViewById(R.id.percent);
        recyclerView = view.findViewById(R.id.recyclerView);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        budget_tv = view.findViewById(R.id.budget_tv);
    }

    private void setView(){
        //设置折叠标题
        collapsingToolBar.setTitle("账户");
        collapsingToolBar.setExpandedTitleGravity(Gravity.TOP|Gravity.LEFT);
        mWaveHelper = new WaveHelper(waveView,0,0f);
        waveView.setBorder(mBorderWidth,mBorderColor);
        waveView.setShapeType(WaveView.ShapeType.CIRCLE);
        jingzichan.setCharacterLists(TickerUtils.provideNumberList());
        zichan.setCharacterLists(TickerUtils.provideNumberList());
        fuzhai.setCharacterLists(TickerUtils.provideNumberList());
        //刷新标题信息
        refreshCountText();
        //waveView.setAmplitudeRatio(20);
        //设置分割线
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(()-> onSwipeLayoutRefresh());
        waveView.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddBudgetActivity.class);
            startActivityForResult(intent,ADD_BUDGET_REQUEST_CODE);
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case ADD_BUDGET_REQUEST_CODE:
                if(resultCode == Activity.RESULT_OK){
                    Log.e(TAG, "onActivityResult: ok" );
                    Toast.makeText(getActivity(),"get",Toast.LENGTH_SHORT).show();
                    save();
                    SimpleDateFormat  sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm E");
                    Log.e(TAG, "onActivityResult: type ="+this.type );
                    Log.e(TAG, "onActivityResult: budget ="+this.value );
                    Log.e(TAG, "onActivityResult: date ="+sdf.format(this.budgetDate));
                }
                break;
        }
    }

    private void save(){
        SharedPreferences spf = getActivity().getSharedPreferences(BUDGET,MODE_PRIVATE);
        float value = spf.getFloat("budget",0);
        int type = spf.getInt("type", Budget.MONTH);
        long date = spf.getLong("date",new Date().getTime());
        this.type = type;
        this.value = value;
        this.budgetDate = new Date(date);
    }




    /**
     * 初始化数据源
     */
    private void initData(){
        countTypeSectionList = DataServer.getCountTypeData(getActivity());

    }
    /**
     * 初始化适配器
     */
    private void initAdapter(){
        countTypeSectionAdapter = new CountTypeSectionAdapter(getActivity(),
                R.layout.zhanghu_item,R.layout.zhanghu_headview,countTypeSectionList);
        if(needAnimation) {
            countTypeSectionAdapter.openLoadAnimation(RecordListFragment.myAnimation);
        }
        countTypeSectionAdapter.isFirstOnly(true);
        countTypeSectionAdapter.setOnItemClickListener((adapter, view, position) -> {
            Log.e(TAG, "onItemClick: position "+position );
            List<CountTypeSection> list;
            list = adapter.getData();
            boolean isHeader = list.get(position).isHeader;
            if(!isHeader) {
                Intent intent = new Intent(getActivity(), RecordsDetailActivity.class);
                intent.putExtra(TAG,list.get(position).getPayType());
                startActivityForResult(intent,0);
            }else {
                //处理头部布局的点击事件
                Toast.makeText(getActivity(),"head",Toast.LENGTH_SHORT).show();
            }
        });


        countTypeSectionAdapter.setEmptyView(R.layout.empty_layout,(ViewGroup) recyclerView.getParent());
        recyclerView.setAdapter(countTypeSectionAdapter);
        //初次进入不需要动画，下拉才需要
        needAnimation=true;
    }

    /**
     * 下拉刷新
     */
    private void onSwipeLayoutRefresh(){
        new Thread(() -> {
            try {
                //防止用户一直下拉 想睡多久睡多久 :)
                Thread.sleep(100);
            }catch (Exception e){
                e.printStackTrace();
            }
            //回到主线程
            getActivity().runOnUiThread(() -> {
                //刷新
//                float radio = (float) Math.random();
//                refreshWaveView(radio);
//                autoIncrement(persent,lastRadio,radio,1000);
                updateWavaViewBudget();
                refreshCountText();
                initData();
                initAdapter();
                //停止刷新
                swipeRefreshLayout.setRefreshing(false);
            });
        }).start();
    }


    /**
     * 根据预算更新
     */
    private void updateWavaViewBudget(){
        if(type==Budget.MONTH){
            //判断预算时间是否是本月
            if(TimeUtil.isBelongThisMonth(budgetDate)){

                Map<String,Float> costMap = RecordLab.getRecordLab(getActivity()).getRecordStatistics(TimeUtil.DateType.thisMonth);
                float cost = costMap.get(RecordLab.COST_KEY);
                float radio=0f;
                int month = TimeUtil.getMonth(budgetDate);
                if(value!=0) {
                    budget_tv.setText(month+"月预算");
                    radio= (cost + this.value) / value;
                    if (radio < 0) {
                        radio = 0f;
                    }
                }else {
                    //未设置预算
                    budget_tv.setText("请设置预算");
                }
                refreshWaveView(radio);
                autoIncrement(persent,lastRadio,radio,1000);
            }else {
                //预算时间不在本月,预算清空
                budget_tv.setText("请设置预算");
                value=0f;
                refreshWaveView(0f);
                autoIncrement(persent,lastRadio,0f,1000);
            }
        }else if(type==Budget.YEAR){
            //判断时间是否属于本年
            if(TimeUtil.isBelongThisYear(budgetDate)) {
                Map<String, Float> costMap = RecordLab.getRecordLab(getActivity()).getRecordStatistics(TimeUtil.DateType.thisYear);
                float cost = costMap.get(RecordLab.COST_KEY);
                float radio = 0f;
                if (value != 0) {
                    budget_tv.setText("年预算");
                    radio = (cost + this.value) / value;
                    if (radio < 0) {
                        radio = 0f;
                    }
                } else {
                    //未设置预算
                    budget_tv.setText("请设置预算");
                }

                refreshWaveView(radio);
                autoIncrement(persent, lastRadio, radio, 1000);
            }else {
                //预算时间不在本年,预算清空
                budget_tv.setText("请设置预算");
                value=0f;
                refreshWaveView(0f);
                autoIncrement(persent,lastRadio,0f,1000);
            }
        }
    }

    /**
     * 直接调用该方法刷新waveView即可
     * @param radio
     */
    private void refreshWaveView(float radio){
        if(waveView!=null) {
            mWaveHelper.cancel();
            mWaveHelper = new WaveHelper(waveView, lastRadio,radio);
            selectColorByRadio(radio);
            lastRadio = radio;
            mWaveHelper.start();
        }
    }


    /**
     * 刷新账户标题信息
     * 动态统计
     */
    private void refreshCountText(){
        RecordLab recordLab = RecordLab.getRecordLab(getActivity());
        refreshCountText(recordLab.getAsset(),recordLab.getNet_Assets(),recordLab.getObligatory());
    }
    /**
     * 刷新账户标题信息
     * @param asset 资产
     * @param netAsset 净资产
     * @param obligtory 债务
     */
    private void refreshCountText(float asset,float netAsset,float obligtory){
        jingzichan.setText("¥"+floatToInt(netAsset));
        zichan.setText("¥"+floatToInt(asset));
        fuzhai.setText("¥"+floatToInt(obligtory));
    }

    //radio范围为0-1
    private void selectColorByRadio(float radio){
        radio *=100;
        //DecimalFormat format = new DecimalFormat("####.0");
        if(radio<20){
            setWaveViewColor(RED);
        }
        else {
            setWaveViewColor(DEFAULT);
        }
    }

    private void setWaveViewColor(int color){
        switch (color){
            case RED:
                waveView.setWaveColor(
                        Color.parseColor("#28f16d7a"),
                        Color.parseColor("#3cf16d7a"));
                mBorderColor = Color.parseColor("#44f16d7a");
                waveView.setBorder(mBorderWidth, mBorderColor);
                break;
            case GREEN:
                waveView.setWaveColor(
                        Color.parseColor("#40b7d28d"),
                        Color.parseColor("#80b7d28d"));
                mBorderColor = Color.parseColor("#B0b7d28d");
                waveView.setBorder(mBorderWidth, mBorderColor);
                break;
            case BLUE:
                waveView.setWaveColor(
                        Color.parseColor("#88b8f1ed"),
                        Color.parseColor("#b8f1ed"));
                mBorderColor = Color.parseColor("#b8f1ed");
                waveView.setBorder(mBorderWidth, mBorderColor);
                break;
            case DEFAULT:
                waveView.setWaveColor(
                        WaveView.DEFAULT_BEHIND_WAVE_COLOR,
                        WaveView.DEFAULT_FRONT_WAVE_COLOR);
                mBorderColor = Color.parseColor("#44FFFFFF");
                waveView.setBorder(mBorderWidth, mBorderColor);
                break;
        }
    }

    private String floatToInt(float num){
        //num=Math.abs(num); //情况是为小数，首位为0或者等于0
        if(num == 0 || Math.abs(num) < 1){
        DecimalFormat decimalFormat=new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
        String p = decimalFormat.format(num);
        return p;
        }
        else {
            return titleFormat.format(num);
        }
    }

    private DecimalFormat getDecimalFormat(){
        return new DecimalFormat("0.00");
    }

    public void autoIncrement(final TextView target, final float start,
                                     final float end, long duration) {

        ValueAnimator animator = ValueAnimator.ofFloat(start, end);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            private FloatEvaluator evalutor = new FloatEvaluator();
            private DecimalFormat format = new DecimalFormat("####0.0");

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                float fraction = animation.getAnimatedFraction();
                float currentValue = evalutor.evaluate(fraction, start, end);
                currentValue*=100;
                target.setText(format.format(currentValue)+"%");
            }
        });
        animator.setDuration(duration);
        animator.start();
    }
}
