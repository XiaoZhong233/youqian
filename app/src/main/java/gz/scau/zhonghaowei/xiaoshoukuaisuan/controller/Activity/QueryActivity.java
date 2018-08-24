package gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.gyf.barlibrary.ImmersionBar;
import com.sdsmdg.tastytoast.TastyToast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.qqtheme.framework.picker.DateTimePicker;
import cn.qqtheme.framework.picker.LinkagePicker;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.Model.DataServer;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.R;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Util.TimeUtil;

public class QueryActivity extends AppCompatActivity implements View.OnClickListener{


    public static final String TAG = "QueryActivity";
    private Toolbar toolbar;
    private ImmersionBar mImmersionBar;
    //布局
    private LinearLayout classes_layout,countType_layout,start_date_layout,end_date_layout;
    //按钮
    private Button clear_button,submit_button;
    //文本框
    private TextView title_tv,classes_tv,countType_tv,startDate_tv,endDate_tv;
    //单选钮
    private RadioButton zhichu_rb,shouru_rb,all_rb;
    private RadioGroup radioGroup;
    //用于记录日期区间
    Date preDate;
    Date lastDate;
    //用于记录选择的账户类型
    String countType = "全部";
    //用于记录一级类别和二级类别
    String title ="全部";
    String classes = "全部";
    //记录是收入还是支出的枚举
    private final int TYPE_ZHICHU = 0;
    private final int TYPE_SHOURU = 1;
    private final int TYPE_ALL=2;
    private int currentType = TYPE_ALL;

    //键
    public static final String START_DATE = "startDate";
    public static final String END_DATE = "endDate";
    public static final String COUNT_TYPE = "countType";
    public static final String TITLE = "title";
    public static final String CLASSES = "classes";
    public static final String CURRENT_TYPE ="curType";





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);
        initImmersionBar();
        initData();
        initView();
        setView();
    }

    private void initData(){
        preDate = new Date();
        //获取preDate的下一天
        lastDate = new Date(preDate.getTime()+1000*60*60*24);
    }

    private void initView(){
        toolbar = findViewById(R.id.toolbar);
        //布局
        classes_layout = findViewById(R.id.classes_layout);
        countType_layout = findViewById(R.id.countType_layout);
        start_date_layout = findViewById(R.id.start_date_layout);
        end_date_layout = findViewById(R.id.end_date_layout);
        //文本框
        title_tv = findViewById(R.id.title);
        classes_tv = findViewById(R.id.classes);
        countType_tv = findViewById(R.id.count_type);
        startDate_tv = findViewById(R.id.start_date);
        endDate_tv = findViewById(R.id.end_date);
        //按钮
        clear_button = findViewById(R.id.clear_button);
        submit_button = findViewById(R.id.submit_button);
        shouru_rb = findViewById(R.id.shouru_rb);
        zhichu_rb = findViewById(R.id.zhichu_rb);
        all_rb = findViewById(R.id.all_rb);
        //
        radioGroup = findViewById(R.id.radio_group);
    }

    private void setView(){
        classes_layout.setOnClickListener(this);
        countType_layout.setOnClickListener(this);
        start_date_layout.setOnClickListener(this);
        end_date_layout.setOnClickListener(this);
        clear_button.setOnClickListener(this);
        submit_button.setOnClickListener(this);
        //初始化
        String preDateString = getDateFormat().format(preDate);
        String lastDateString = getDateFormat().format(lastDate);
        startDate_tv.setText(preDateString);
        endDate_tv.setText(lastDateString);
        countType_tv.setText(countType);
        title_tv.setText(title);
        classes_tv.setText(classes);
        zhichu_rb.setChecked(false);
        shouru_rb.setChecked(false);
        all_rb.setChecked(true);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId){
                case R.id.shouru_rb:
                    currentType = TYPE_SHOURU;
                    shouru_rb.setChecked(true);
                    title_tv.setText("全部");
                    classes_tv.setText("全部");
                    title = "全部";
                    classes = "全部";
                    break;
                case R.id.zhichu_rb:
                    currentType = TYPE_ZHICHU;
                    zhichu_rb.setChecked(true);
                    title_tv.setText("全部");
                    classes_tv.setText("全部");
                    title = "全部";
                    classes = "全部";
                    break;
                case R.id.all_rb:
                    currentType = TYPE_ALL;
                    all_rb.setChecked(true);
                    title_tv.setText("全部");
                    classes_tv.setText("全部");
                    title = "全部";
                    classes = "全部";
                    break;
            }
        });

    }

    @Override
    public void onClick(View v) {
        resetBgColor();
        switch (v.getId()){
            case R.id.classes_layout:
                v.setBackgroundColor(0x2eFFEC8B);
                onClassesPicker(classes_layout,currentType);
                break;
            case R.id.countType_layout:
                v.setBackgroundColor(0x2eFFEC8B);
                onCountPicker(countType_tv);
                break;
            case R.id.start_date_layout:
                v.setBackgroundColor(0x2eFFEC8B);
                onYearMonthDayTimePicker(startDate_tv,preDate,0);
                break;
            case R.id.end_date_layout:
                v.setBackgroundColor(0x2eFFEC8B);
                onYearMonthDayTimePicker(endDate_tv,lastDate,1);
                break;
            case R.id.clear_button:
                break;
            case R.id.submit_button:
                String preDateString = getDateFormat().format(preDate);
                String lastDateString = getDateFormat().format(lastDate);
                Log.e(TAG, "onClick: preDate "+preDateString);
                Log.e(TAG, "onClick: endDate "+lastDateString );
                Log.e(TAG, "onClick: countType "+countType );
                Log.e(TAG, "onClick: title "+title );
                Log.e(TAG, "onClick: classes "+classes );
                Log.e(TAG, "onClick: curType "+currentType );
                Intent intent = new Intent(QueryActivity.this,QueryResultActivity.class);
                Bundle args = new Bundle();
                args.putSerializable(START_DATE,preDate);
                args.putSerializable(END_DATE,lastDate);
                args.putString(COUNT_TYPE,countType);
                args.putString(TITLE,title);
                args.putString(CLASSES,classes);
                args.putInt(CURRENT_TYPE,currentType);
                intent.putExtra(TAG,args);
                startActivity(intent);
                break;
                default:
        }
    }

    /**
     * 类型联动筛选器
     * @param view
     */
    public void onClassesPicker(View view,int type){
        LinkagePicker.DataProvider provider = new LinkagePicker.DataProvider() {
            @NonNull
            @Override
            public List<String> provideFirstData() {
                if (type == TYPE_ZHICHU) {
                    List<String> list = new ArrayList<>();
                    list.add("全部");//0
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
                if (type == TYPE_SHOURU) {
                    List<String> list = new ArrayList<>();
                    list.add("全部");
                    list.add("职业收入");
                    list.add("理财收入");
                    list.add("其他收入");
                    return list;
                }else {
                    List<String> list = new ArrayList<>();
                    list.add("全部");
                    return list;
                }
            }

            @NonNull
            @Override
            public List<String> provideSecondData(int firstIndex) {
                if (type == TYPE_ZHICHU) {
                    List<String> secendList = new ArrayList<>();
                    switch (firstIndex) {
                        case 0:
                            secendList.add("全部");
                            break;
                        case 1:
                            secendList.add("充饭卡");
                            secendList.add("早午晚餐");
                            secendList.add("烟酒茶");
                            secendList.add("水果零食");
                            break;
                        case 2:
                            secendList.add("衣服裤子");
                            secendList.add("鞋帽包包");
                            secendList.add("化妆饰品");
                            break;
                        case 3:
                            secendList.add("日常用品");
                            secendList.add("水电煤气");
                            secendList.add("房租");
                            secendList.add("物业管理");
                            secendList.add("维修保养");
                            break;
                        case 4:
                            secendList.add("公共交通");
                            secendList.add("打车租车");
                            secendList.add("私家车费用");
                            break;
                        case 5:
                            secendList.add("座机费");
                            secendList.add("手机费");
                            secendList.add("上网费");
                            secendList.add("邮寄费");
                            break;
                        case 6:
                            secendList.add("运动健身");
                            secendList.add("腐败聚会");
                            secendList.add("休闲游戏");
                            secendList.add("宠物宝贝");
                            secendList.add("旅游度假");
                            break;
                        case 7:
                            secendList.add("书报杂志");
                            secendList.add("培训进修");
                            secendList.add("数码装备");
                            break;
                        case 8:
                            secendList.add("送礼请客");
                            secendList.add("孝敬家长");
                            secendList.add("还人钱物");
                            secendList.add("慈善捐助");
                            break;
                        case 9:
                            secendList.add("药品费");
                            secendList.add("保健费");
                            secendList.add("美容费");
                            secendList.add("治疗费");
                            secendList.add("检查费");
                            break;
                        case 10:
                            secendList.add("银行手续");
                            secendList.add("投资亏损");
                            secendList.add("按揭还款");
                            secendList.add("消费税收");
                            secendList.add("利息支出");
                            secendList.add("赔偿罚款");
                            break;
                        case 11:
                            secendList.add("其他支出");
                            secendList.add("意外丢失");
                            secendList.add("烂账损失");
                            break;
                    }
                    return secendList;
                }
                if (type == TYPE_SHOURU) {
                    List<String> secendList = new ArrayList<>();
                    switch (firstIndex) {
                        case 0:
                            secendList.add("全部");
                            break;
                        case 1:
                            secendList.add("工资收入");
                            secendList.add("兼职收入");
                            secendList.add("加班收入");
                            secendList.add("经营收入");
                            secendList.add("奖金收入");
                            break;
                        case 2:
                            secendList.add("投资收入");
                            secendList.add("利息收入");
                            break;
                        case 3:
                            secendList.add("礼金收入");
                            secendList.add("中奖收入");
                            secendList.add("意外收入");
                            break;
                    }
                    return secendList;
                }else {
                    List<String> list = new ArrayList<>();
                    list.add("全部");
                    return list;
                }
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

        LinkagePicker picker = new LinkagePicker(QueryActivity.this, provider);
        picker.setCycleDisable(true);
        picker.setUseWeight(true);
        if(currentType == TYPE_ZHICHU) {
            picker.setSelectedIndex(0, 0);
        }else if(currentType == TYPE_SHOURU){
            picker.setSelectedIndex(0,0);
        }else {
            picker.setSelectedIndex(0,0);
        }
        picker.setContentPadding(10, 0);
        picker.setOnStringPickListener(new LinkagePicker.OnStringPickListener() {
            @Override
            public void onPicked(String first, String second, String third) {
                title_tv.setText(first);
                classes_tv.setText(second);
                title = first;
                classes = second;
            }
        });
        picker.setLineSpaceMultiplier(3);

        picker.show();
    }


    /**
     * 日期筛选器
     * @param startOrEnd 0-pre 1-end
     */
    public void onYearMonthDayTimePicker(View view,Date defaultDate,int startOrEnd) {
        DateTimePicker picker = new DateTimePicker(QueryActivity.this, DateTimePicker.HOUR_24);
        picker.setDateRangeStart(2017, 1, 1);
        picker.setDateRangeEnd(2025, 11, 11);
        picker.setTimeRangeStart(0, 0);
        picker.setTimeRangeEnd(23, 59);
        picker.setTopLineColor(0x99FF0000);
        picker.setLabelTextColor(0xFFFF0000);
        picker.setLineSpaceMultiplier(3);
        Date date = defaultDate;
        picker.setSelectedItem(TimeUtil.getYear(date),TimeUtil.getMonth(date),TimeUtil.getDay(date)
                ,TimeUtil.getHour(date),TimeUtil.getMinute(date));
        picker.setOnDateTimePickListener(new DateTimePicker.OnYearMonthDayTimePickListener() {
            @Override
            public void onDateTimePicked(String year, String month, String day, String hour, String minute) {
                String s =String.format("%s-%s-%s %s-%s",year,month,day,hour,minute);
                picker.setTitleText(s);
                String ss = String.format("%s年%s月%s日 %s时%s分",year,month,day,hour,minute);
                try {
                    Date date = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分").parse(ss);
                    if(startOrEnd==1) {
                        if(date.before(preDate)){
                            TastyToast.makeText(QueryActivity.this,"结束日期不可在开始日期之前",TastyToast.LENGTH_SHORT,TastyToast.WARNING).show();
                            return;
                        }else {
                            lastDate = date;
                        }
                    }
                    if(startOrEnd==0){
                        preDate = date;
                    }
                    TextView textView = (TextView)view;
                    textView.setText(getDateFormat().format(date).toString());

                }catch (Exception e){
                    e.printStackTrace();
                    Log.e(TAG, "onDateTimePicked: 发生错误，date默认设置为record时间,请注意,record未保存" );
                }
            }

        });
        picker.show();
    }


    /**
     * 账户类型二级联动筛选器
     * @param view
     */
    public void onCountPicker(View view){
        LinkagePicker.DataProvider provider = new LinkagePicker.DataProvider() {
            @NonNull
            @Override
            public List<String> provideFirstData() {
                List<String> list = new ArrayList<>();
                list.add("全部");
                list.add("支付账户");
                list.add("负债账户");
                list.add("债权账户");
                list.add("投资账户");
                return  list;
            }

            @NonNull
            @Override
            public List<String> provideSecondData(int firstIndex) {
                List<String> secendList = new ArrayList<>();
                switch (firstIndex){
                    case 0:
                        secendList.add("全部");
                        break;
                    case 1:
                        secendList.add("现金(CNY)");
                        secendList.add("银行卡(CNY)");
                        secendList.add("支付宝(CNY)");
                        secendList.add("微信支付(CNY)");
                        break;
                    case 2:
                        secendList.add("应付款项(CNY)");
                        secendList.add("花呗(CNY)");
                        secendList.add("京东白条(CNY)");
                        secendList.add("分期乐(CNY)");
                        break;
                    case 3:
                        secendList.add("应收款项(CNY)");
                        break;
                    case 4:
                        secendList.add("余额宝(CNY)");
                        secendList.add("基金账户(CNY)");
                        secendList.add("股票账户(CNY)");
                        secendList.add("其他理财账户(CNY)");
                        break;
                }
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

        LinkagePicker picker = new LinkagePicker(QueryActivity.this, provider);
        picker.setCycleDisable(true);
        picker.setUseWeight(true);
        picker.setSelectedIndex(0,0);
        picker.setContentPadding(10, 0);
        picker.setLineSpaceMultiplier(3);
        picker.setCancelable(false);
        picker.setOnStringPickListener(new LinkagePicker.OnStringPickListener() {
            @Override
            public void onPicked(String first, String second, String third) {
                Log.e(TAG, "onPicked: ZhanghuType "+first );
                countType_tv.setText(second);
                countType = second;
            }
        });
        picker.show();
    }


    /**
     * 获取日期格式
     * @return
     */
    private SimpleDateFormat getDateFormat(){
        SimpleDateFormat s = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分 E ");
        return s;
    }

    /**
     * 重置布局背景颜色
     */
    private void resetBgColor(){
        classes_layout.setBackgroundColor(0xffffffff);
        countType_layout.setBackgroundColor(0xffffffff);
        start_date_layout.setBackgroundColor(0xffffffff);
        end_date_layout.setBackgroundColor(0xffffffff);
    }

    /**
     * 初始化沉浸式
     */
    private void initImmersionBar(){
        //初始化，默认透明状态栏和黑色导航栏
        mImmersionBar=ImmersionBar.with(this);
        mImmersionBar
                .keyboardEnable(true)//解决软键盘与底部输入框冲突问题
                .titleBar(R.id.toolbar)
                .navigationBarWithKitkatEnable(false)
                .init();
    }
}
