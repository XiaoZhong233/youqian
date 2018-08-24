package gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sdsmdg.tastytoast.TastyToast;
import com.sdsmdg.tastytoast.WarningToastView;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import cn.qqtheme.framework.picker.DateTimePicker;
import cn.qqtheme.framework.picker.LinkagePicker;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.Model.DataServer;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.Model.Record;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.Model.RecordLab;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.R;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Util.TimeUtil;

public class AddRecordFragment extends Fragment  implements View.OnClickListener{
    private static final String TAG = "AddRecordFragment";
    private TextInputEditText count_editText;
    private TextInputEditText detail_editText;
    private TextInputLayout textInputLayout;
    private TextInputLayout detailInputLayout;
    private TextView date_textView;
    private TextView countType_textView;
    private TextView classes_textView;
    private TextView title_textView;
    private RelativeLayout relativeLayout;
    private LinearLayout countType_layout;
    private LinearLayout class_layout;
    private LinearLayout count_layout;
    private LinearLayout date_layout;
    private LinearLayout detail_layout;
    private Button clear_button;
    private Button submit_button;
    private Date now;

    //判断是否是新增记录操作
    private boolean isAdd = true;
    private Record mRecord;

    //判断当前类型，默认为支出
    public static final int TYPE_ZHICHU=0;
    public static final int TYPE_SHOURU=1;
    private int currentType;

    public static AddRecordFragment newIntance(){
        AddRecordFragment addRecordFragment=new AddRecordFragment();
        Bundle bundle=new Bundle();
        bundle.putSerializable(TAG,"u can't get me! ha");
        addRecordFragment.setArguments(bundle);
        return addRecordFragment;
    }

    public static AddRecordFragment newIntance(UUID id){
        Bundle bundle=new Bundle();
        bundle.putSerializable(TAG,id);
        AddRecordFragment addRecordFragment = new AddRecordFragment();
        addRecordFragment.setArguments(bundle);
        return addRecordFragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideInputMethod(getActivity());
        Calendar calendar = Calendar.getInstance();
        now = calendar.getTime();

        //获取创建传入的参数
        UUID id;
        try {
            id = (UUID) getArguments().getSerializable(TAG);
            Log.e(TAG, "onCreate: 修改数据  id:"+ id.toString() );
            isAdd = false;
        }catch (Exception e){
            Log.e(TAG, "onCreate: 新增数据" );
            isAdd = true;
            id=null;
        }
        mRecord= RecordLab.getRecordLab(getActivity()).getRecord(id);
        if(mRecord==null){
            Log.e(TAG, "onCreate: isAdd");
            mRecord=new Record();
        }

        RecordListFragment.recordListFragment.dateSectionAdapter.isFirstOnly(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_record_layout,container,false);
        initView(view);
        setView();
        return view;
    }

    private void initView(View view){
        //文本框
        count_editText = view.findViewById(R.id.account);
        detail_editText = view.findViewById(R.id.detail);
        //文本布局
        textInputLayout = view.findViewById(R.id.text_input_layout);
        detailInputLayout = view.findViewById(R.id.detail_input_layout);
        //布局
        relativeLayout = view.findViewById(R.id.fuck);
        count_layout = view.findViewById(R.id.input_count_layout);
        class_layout = view.findViewById(R.id.classes_layout);
        countType_layout = view.findViewById(R.id.count_layout);
        date_layout = view.findViewById(R.id.date_layout);
        detail_layout = view.findViewById(R.id.detail_layout);
        //按钮
        clear_button = view.findViewById(R.id.clear_button);
        submit_button = view.findViewById(R.id.submit_button);
        //文本
        date_textView = view.findViewById(R.id.date);
        countType_textView = view.findViewById(R.id.count_type);
        title_textView = view.findViewById(R.id.title);
        classes_textView = view.findViewById(R.id.classes);
    }

    private void setView(){
        //count_layout.setBackgroundColor(0x2eFFEC8B);
        //count_layout.setBackgroundColor(0x2eFFEC8B);
        relativeLayout.setBackgroundColor(0x2eD3D3D3);
        buildEditText();
        //注册点击监听
        class_layout.setOnClickListener(this);
        countType_layout.setOnClickListener(this);
        date_layout.setOnClickListener(this);
        clear_button.setOnClickListener(this);
        submit_button.setOnClickListener(this);

        //判断是否为新增记录,是则初始化，否则读取值
        if(isAdd){
            //初始化
            if(currentType == TYPE_ZHICHU){
            mRecord.setTitle(title_textView.getText().toString());
            mRecord.setClasses(classes_textView.getText().toString());
            mRecord.setZhanghu("支付账户");
            mRecord.setPay_type("现金(CNY)");
            }
            if(currentType == TYPE_SHOURU){
                title_textView.setText("职业收入");
                classes_textView.setText("工资收入");
                mRecord.setTitle(title_textView.getText().toString());
                mRecord.setClasses(classes_textView.getText().toString());
                mRecord.setZhanghu("支付账户");
                mRecord.setPay_type("现金(CNY)");
            }
            count_editText.setText("00.00");
            date_textView.setText(getDateFormat().format(now));
        }else {

            float cost = mRecord.getCost();
            //读取金额值(格式化处理)
            count_editText.setText(floatToInt(cost));
            //读取类别值
            title_textView.setText(mRecord.getTitle());
            classes_textView.setText(mRecord.getClasses());
            //读取账户类型
            countType_textView.setText(mRecord.getPay_type());
            //读取时间
            date_textView.setText(mRecord.getDateString());
            //读取备注
            detail_editText.setText(mRecord.getDetail());
        }

    }

    @Override
    public void onClick(View v) {
        count_editText.clearFocus();
        detail_editText.clearFocus();
        hideInputMethod(getActivity());
        resetBgColor();
        switch (v.getId()){
            case R.id.classes_layout:
                v.setBackgroundColor(0x2eFFEC8B);
                onClassesPicker(getView(),currentType);
                break;
            case R.id.count_layout:
                v.setBackgroundColor(0x2eFFEC8B);
                onCountPicker(getView());
                break;
            case R.id.date_layout:
                v.setBackgroundColor(0x2eFFEC8B);
                onYearMonthDayTimePicker(getView());
                break;
            case R.id.clear_button:
                count_editText.setText("00.00");
                detail_editText.setText("");
                break;
            case R.id.submit_button:
                save();
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
                break;
        }
    }


    private void save(){
        //金额保存
        float count = Float.valueOf(count_editText.getText().toString());
        if(currentType == TYPE_ZHICHU){
            count = -count;
        }else if(currentType == TYPE_SHOURU){
            //do nothing;
        }
        mRecord.setCost(count);
        //备注保存
        mRecord.setDetail(detail_editText.getText().toString()==null?"":detail_editText.getText().toString());
        //类别保存
        mRecord.setTitle(title_textView.getText().toString());
        mRecord.setClasses(classes_textView.getText().toString());
        //账户类型保存
        //mRecord.setZhanghu(countType_textView.getText().toString());
        if(isAdd){
            //新增数据
            RecordLab.getRecordLab(getActivity()).addRecord(mRecord);
        }else {
            //更新数据,理论上什么都不用做,因为mRecord为记录引用,但这里安全起见,还是更新下好了
            //RecordLab.getRecordLab(getActivity()).update(mRecord);
        }
    }


    /**
     * 重置布局背景颜色
     */
    private void resetBgColor(){
        count_layout.setBackgroundColor(0xffffffff);
        class_layout.setBackgroundColor(0xffffffff);
        countType_layout.setBackgroundColor(0xffffffff);
        date_layout.setBackgroundColor(0xffffffff);
        detail_layout.setBackgroundColor(0xffffffff);
    }



    @SuppressLint("ClickableViewAccessibility")
    private void buildEditText(){

        count_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        count_editText.setOnFocusChangeListener((v, hasFocus) -> {
            resetBgColor();
            if(hasFocus){
                resetBgColor();
                count_layout.setBackgroundColor(0x2eFFEC8B);
                if(!count_editText.getText().toString().isEmpty()){
                    count_editText.getText().clear();
                }
            }else {
                resetBgColor();
                //输入完后不被归零的条件为不为空并且不等于0
                if(!count_editText.getText().toString().isEmpty() && Float.valueOf(count_editText.getText().toString())!=0) {
                    count_editText.setText(floatToInt(Float.valueOf(count_editText.getText().toString())));
                }else {
                    count_editText.setText("00.00");
                    TastyToast.makeText(getActivity(),"确定金额为0吗？(°Д°)",TastyToast.LENGTH_SHORT, TastyToast.INFO);

                }
            }
        });

        detail_editText.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus){
                resetBgColor();
                detail_layout.setBackgroundColor(0x2eFFEC8B);
            }else{
                resetBgColor();
            }
        });

    }

    /**
     * 显示错误提示，并获取焦点
     * @param textInputLayout
     * @param error
     */
    private void showError(TextInputLayout textInputLayout,String error){
        textInputLayout.setError(error);
        textInputLayout.getEditText().setFocusable(true);
        textInputLayout.getEditText().setFocusableInTouchMode(true);
        textInputLayout.getEditText().requestFocus();
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
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view,InputMethodManager.SHOW_FORCED);
        } catch (Exception e) {

        }
    }


    /**
     * 日期筛选器
     * @param view
     */
    public void onYearMonthDayTimePicker(View view) {
        DateTimePicker picker = new DateTimePicker(getActivity(), DateTimePicker.HOUR_24);
        picker.setDateRangeStart(2017, 1, 1);
        picker.setDateRangeEnd(2025, 11, 11);
        picker.setTimeRangeStart(0, 0);
        picker.setTimeRangeEnd(23, 59);
        picker.setTopLineColor(0x99FF0000);
        picker.setLabelTextColor(0xFFFF0000);
        picker.setLineSpaceMultiplier(3);
        Date date = mRecord.getDate();
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
                    date_textView.setText(getDateFormat().format(date).toString());
                    if(date.after(now)){
                    TastyToast.makeText(getActivity(),"确定是发生在未来的记录吗？(°Д°)",TastyToast.LENGTH_SHORT, TastyToast.INFO);
                    }
                    mRecord.setDate(date);
                }catch (Exception e){
                    e.printStackTrace();
                    Log.e(TAG, "onDateTimePicked: 发生错误，date默认设置为record时间,请注意,record未保存" );
                    date_textView.setText(mRecord.getDateString());
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
               return DataServer.provideCountFirstData();
            }

            @NonNull
            @Override
            public List<String> provideSecondData(int firstIndex) {
                return DataServer.provideCountSecondData(firstIndex);
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
        picker.setSelectedIndex(0,0);
        picker.setContentPadding(10, 0);
        picker.setLineSpaceMultiplier(3);
        picker.setCancelable(false);
        picker.setOnStringPickListener(new LinkagePicker.OnStringPickListener() {
            @Override
            public void onPicked(String first, String second, String third) {
                Log.e(TAG, "onPicked: ZhanghuType "+first );
                mRecord.setZhanghu(first);
                mRecord.setPay_type(second);
                countType_textView.setText(second);
                Log.e(TAG, "onPicked: "+mRecord.getZhanghu()+ "-" + mRecord.getPay_type() );

            }
        });
        picker.show();
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
                if(type == TYPE_ZHICHU){
                    return DataServer.provideZhiChuClassesFirstData();
                }
                if (type == TYPE_SHOURU){
                    return DataServer.provideShouRuClassesFirstData();
                }
                return null;
            }

            @NonNull
            @Override
            public List<String> provideSecondData(int firstIndex) {
                if(type == TYPE_ZHICHU){
                    return DataServer.provideZhiChuClassesSecendData(firstIndex);
                }
                if (type == TYPE_SHOURU){
                    return DataServer.provideShouRuClassesSecendData(firstIndex);
                }
                return null;
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
        if(currentType == TYPE_ZHICHU) {
            picker.setSelectedIndex(0, 1);
        }else if(currentType == TYPE_SHOURU){
            picker.setSelectedIndex(0,0);
        }else {
            picker.setSelectedIndex(0,0);
        }
        picker.setContentPadding(10, 0);
        picker.setOnStringPickListener(new LinkagePicker.OnStringPickListener() {
            @Override
            public void onPicked(String first, String second, String third) {
                mRecord.setTitle(first);
                mRecord.setClasses(second);
                title_textView.setText(first);
                classes_textView.setText(second);
            }
        });
        picker.setLineSpaceMultiplier(3);

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


    /**
     * 获取当前支出收入类型
     * @return
     */
    public int getCurrentType() {
        return currentType;
    }

    public void setCurrentType(int currentType) {
        this.currentType = currentType;
    }

    public void setClass_title_TextView(String a,String b){
        title_textView.setText(a);
        classes_textView.setText(b);
    }

}
