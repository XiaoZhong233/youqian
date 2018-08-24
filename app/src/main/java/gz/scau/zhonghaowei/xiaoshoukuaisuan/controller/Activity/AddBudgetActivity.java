package gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gyf.barlibrary.ImmersionBar;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import cn.qqtheme.framework.picker.OptionPicker;
import cn.qqtheme.framework.widget.WheelView;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.Model.Budget;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.Model.RecordLab;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.R;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Util.TimeUtil;


public class AddBudgetActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "AddBudgetActivity";
    private ImmersionBar mImmersionBar;
    private TextInputLayout textInputLayout;
    private TextInputEditText account_editText;
    private LinearLayout budget_layout;
    private LinearLayout cur_budget_layout;
    private TextView cur_budget_tv;
    private TextView budget_tv;
    private TextView budgetLeft_tv;
    private Button clear_btn;
    private Button submit_btn;
    public static final String BUDGET="budget";
    Budget budget;
    private int curType=0;
    private float curValue = 0f;
    private Date curDate ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideInputMethod(this);
        setContentView(R.layout.activity_add_budget);
        initImmersionBar();
        save();
        initView();
        setView();
    }


    private void initView(){
        textInputLayout = findViewById(R.id.text_input_layout);
        account_editText = findViewById(R.id.account);
        budget_layout = findViewById(R.id.budget_layout);
        budget_tv = findViewById(R.id.budget_type);
        clear_btn = findViewById(R.id.clear_button);
        submit_btn = findViewById(R.id.submit_button);
        cur_budget_layout = findViewById(R.id.cur_budget_layout);
        cur_budget_tv = findViewById(R.id.cur_budget);
        budgetLeft_tv = findViewById(R.id.budgetLeft_tv);

    }


    private void save(){
        SharedPreferences spf = getSharedPreferences(BUDGET,MODE_PRIVATE);
        float value = spf.getFloat("budget",0);
        int type = spf.getInt("type",Budget.MONTH);
        Date date = new Date(spf.getLong("date",new Date().getTime()));
        curType = type;
        curValue = value;
        curDate = date;
    }

    private void setView(){
        textInputLayout.setOnClickListener(this);
        budget_layout.setOnClickListener(this);
        clear_btn.setOnClickListener(this);
        submit_btn.setOnClickListener(this);
        cur_budget_layout.setOnClickListener(this);
        //根据数据初始化
        account_editText.setText(floatToInt(curValue));
        budget_tv.setText(curType==Budget.MONTH?"月预算":"年预算");
        if(curType==Budget.MONTH){
            budgetLeft_tv.setText(TimeUtil.getMonth(curDate)+"月预算剩余");
            if(TimeUtil.isBelongThisMonth(curDate)) {
                Map<String, Float> costMap = RecordLab.getRecordLab(this).getRecordStatistics(TimeUtil.DateType.thisMonth);
                float cost = costMap.get(RecordLab.COST_KEY);
                float left = cost + curValue;
                if (left < 0) {
                    cur_budget_tv.setText("本月预算超支" + floatToInt(left) + "元");
                }else {
                    cur_budget_tv.setText(floatToInt(left) + "元");
                }
            }else {
                //预算日期过期了
                curValue=0f;
                SharedPreferences.Editor editor = getSharedPreferences(BUDGET,MODE_PRIVATE).edit();
                editor.putFloat("budget",0);
                budgetLeft_tv.setText(TimeUtil.getMonth(curDate)+"月预算已过期，请重新设置");
                cur_budget_tv.setText(floatToInt(curValue) + "元");
            }
        }else if(curType == Budget.YEAR){
            budgetLeft_tv.setText(TimeUtil.getYear(curDate)+"年预算剩余");
            if(TimeUtil.isBelongThisYear(curDate)) {
                Map<String, Float> costMap = RecordLab.getRecordLab(this).getRecordStatistics(TimeUtil.DateType.thisYear);
                float cost = costMap.get(RecordLab.COST_KEY);
                float left = cost + curValue;
                if (left < 0) {
                    cur_budget_tv.setText("本年预算超支" + floatToInt(left) + "元");
                }else {
                    cur_budget_tv.setText(floatToInt(left) + "元");
                }
            }else {
                //预算日期过期,清空预算
                curValue=0f;
                SharedPreferences.Editor editor = getSharedPreferences(BUDGET,MODE_PRIVATE).edit();
                editor.putFloat("budget",0);
                budgetLeft_tv.setText(TimeUtil.getYear(curDate)+"年预算已过期，请重新设置");
                cur_budget_tv.setText(floatToInt(curValue) + "元");
            }
        }


        account_editText.setOnFocusChangeListener((v, hasFocus) -> {
            resetBgColor();
            if(hasFocus){
                resetBgColor();
                textInputLayout.setBackgroundColor(0x2eFFEC8B);
                if(!account_editText.getText().toString().isEmpty()){
                    account_editText.getText().clear();
                }
            }else {
                resetBgColor();
                //输入完后不被归零的条件为不为空并且不等于0
                if(!account_editText.getText().toString().isEmpty() && Float.valueOf(account_editText.getText().toString())!=0) {
                    account_editText.setText(floatToInt(Math.abs(Float.valueOf(account_editText.getText().toString()))));
                }else {
                    account_editText.setText("00.00");
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        account_editText.clearFocus();
        hideInputMethod(this);
        resetBgColor();
        switch (v.getId()){
            case R.id.text_input_layout:
                v.setBackgroundColor(0x2eFFEC8B);
                break;
            case R.id.budget_layout:
                v.setBackgroundColor(0x2eFFEC8B);
                onOptionPicker(v);
                break;
            case R.id.cur_budget_layout:
                v.setBackgroundColor(0x2eFFEC8B);
                break;
            case R.id.clear_button:
                account_editText.setText("00.00");
                break;
            case R.id.submit_button:
                String type = budget_tv.getText().toString();
                float value = Float.valueOf(account_editText.getText().toString());
                if(type.equals("月预算")){
                    budget = new Budget(value,Budget.MONTH);
                }else if(type.equals("年预算")){
                    budget = new Budget(value,Budget.YEAR);
                }
                SharedPreferences.Editor editor = getSharedPreferences(BUDGET,MODE_PRIVATE).edit();
                editor.putFloat("budget",budget.getBudget());
                editor.putLong("date",budget.getDate().getTime());
                editor.putInt("type",budget.getType());
                editor.apply();
                setResult(Activity.RESULT_OK);
                this.finish();
                break;
        }
    }


    /**
     * 单项选择
     * @param view
     */
    public void onOptionPicker(View view) {
        OptionPicker picker = new OptionPicker(this, new String[]{
                "月预算","年预算"
        });
        picker.setCanceledOnTouchOutside(false);
        picker.setDividerRatio(WheelView.DividerConfig.FILL);
        picker.setShadowColor(Color.RED, 40);
        picker.setSelectedIndex(0);
        picker.setCycleDisable(true);
        picker.setTextSize(14);
        picker.setContentPadding(10, 10);
        picker.setLineSpaceMultiplier(4);
        picker.setCancelable(true);
        picker.setGravity(Gravity.CENTER);
        picker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener() {
            @Override
            public void onOptionPicked(int index, String item) {
                budget_tv.setText(item);
            }
        });
        picker.show();
    }


    /**
     * 重置布局背景颜色
     */
    private void resetBgColor(){
        textInputLayout.setBackgroundColor(0xffffffff);
        budget_layout.setBackgroundColor(0xffffffff);
        cur_budget_layout.setBackgroundColor(0xffffffff);
    }


    /**
     * 初始化沉浸式
     */
    private void initImmersionBar(){
        //初始化，默认透明状态栏和黑色导航栏
        mImmersionBar= ImmersionBar.with(this);
        mImmersionBar
                .keyboardEnable(true)//解决软键盘与底部输入框冲突问题
                .titleBar(R.id.toolbar)
                .navigationBarWithKitkatEnable(false)
                .init();
    }


    /**
     * 设置输入法,如果当前页面输入法打开则关闭
     * @param activity
     */
    public void hideInputMethod(Activity activity){
        View a = activity.getCurrentFocus();
        if(a != null){
            InputMethodManager imm = (InputMethodManager) activity.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            try {
                imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 强制显示输入法
     */
    public void toggleSoftInput(View view){
        try {
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view,InputMethodManager.SHOW_FORCED);
        } catch (Exception e) {

        }
    }

    /**
     * 金额格式转换
     * @param num
     * @return
     */
    private String floatToInt(float num){
        num=Math.abs(num);
        DecimalFormat decimalFormat=new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
        String p = decimalFormat.format(num);
        return p;
    }
}
