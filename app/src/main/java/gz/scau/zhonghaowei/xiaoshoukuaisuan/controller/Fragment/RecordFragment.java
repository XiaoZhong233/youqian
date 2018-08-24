package gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectChangeListener;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.gyf.barlibrary.ImmersionBar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.security.auth.login.LoginException;

import gz.scau.zhonghaowei.xiaoshoukuaisuan.Model.Classes;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.Model.DateSection;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.Model.Record;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.Model.RecordLab;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.R;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Activity.MainActivity;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Activity.RecordListActivity;

public class RecordFragment extends BaseFragment{
    private EditText cost,detail;
    private TextView title,classes;
    private TextView date;
    private CardView photo_img;
    private RadioGroup radioGroup;
    private RadioButton radioButton_shouru;
    private RadioButton radioButton_zhichu;
    private Button clear_button,record_agin_button,submit_button;
    private LinearLayout date_layout;
    private LinearLayout classes_layout;
    private OptionsPickerView classesPicker;
    private Toolbar toolbar;

    private final int TYPE_COST=0;
    private final int TYPE_INCOME=1;
    private int currentType=0;
    public static final String TAG = "RecordFragment_ID_KEY";
    private Record mRecord;//用于记录改变
    private boolean addRecord=false;//辨认是否是新增操作
    private static Fragment recordFragemnt;

    //PickerView数据
    List<Classes> options1Item = new ArrayList<>();
    List<List<String>> options2Item = new ArrayList<>();




    /** 生命周期------------------------------------------------------------------------------------*/




    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //获取创建传入的参数
        UUID id;
        try {
            id = (UUID) getArguments().getSerializable(TAG);
        }catch (Exception e){
            id=null;
        }
        mRecord= RecordLab.getRecordLab(getActivity()).getRecord(id);
        if(mRecord==null){
            addRecord=true;
            mRecord=new Record();
        }
        Log.e(TAG, "onCreate: isFirstOnly");

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.record,container,false);
        getOptionData();
        initView(view);
        initClearText();
        setView();
        if(!addRecord) {
            resetButtonForViewPagerActivity();
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "onPause: " );
        RecordListFragment.recordListFragment.refresh();
    }


    /** 生命周期------------------------------------------------------------------------------------*/


    /** 外部接口------------------------------------------------------------------------------------*/
    public static Fragment newIntance(UUID id){
        Bundle bundle=new Bundle();
        bundle.putSerializable(TAG,id);
        recordFragemnt=new RecordFragment();
        recordFragemnt.setArguments(bundle);
        return recordFragemnt;
    }

    public static Fragment newIntance(){
        recordFragemnt=new RecordFragment();
        Bundle bundle=new Bundle();
        bundle.putSerializable(TAG,"u can't get me! ha");
        recordFragemnt.setArguments(bundle);
        return recordFragemnt;
    }
    /** 外部接口------------------------------------------------------------------------------------*/


    /** 初始化------------------------------------------------------------------------------------*/


    /**
     * 初始化沉浸式
     */
    protected void initImmersionBar() {
        mImmersionBar = ImmersionBar.with(this);
        mImmersionBar.keyboardEnable(false) //keyboardEnable默认为false,这样就不会弹到编辑框了
                .navigationBarWithKitkatEnable(false)
                .statusBarColor(R.color.colorPrimary)
                .fitsSystemWindows(false)    //解决状态栏和布局的重叠问题  ---这他妈就是个坑逼,与小键盘冲突会造成沉浸式失效
                .titleBar(toolbar)
                .init();
    }



    private void initClassesPicker(){
        classesPicker = new OptionsPickerBuilder(getActivity(), new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                String classes1 = options1Item.get(options1).getLabel();
                String classes2 = options2Item.get(options1).get(options2);
                //设置文本
                title.setText(classes1);
                classes.setText(classes2);

            }
        })      .setTitleText("类别选择")
                .setContentTextSize(20)
                .setDividerColor(Color.LTGRAY)//设置分割线的颜色
                .setSelectOptions(0, 1)//默认选中项
                .setBgColor(Color.BLACK)
                .setTitleBgColor(Color.DKGRAY)
                .setTitleColor(Color.LTGRAY)
                .setCancelColor(Color.YELLOW)
                .setSubmitColor(Color.YELLOW)
                .setTextColorCenter(Color.LTGRAY)
                .isRestoreItem(true)//切换时是否还原，设置默认选中第一项。
                .isCenterLabel(true) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setCyclic(true,true,true)
                .setBackgroundId(0x00000000) //设置外部遮罩颜色
                .setOptionsSelectChangeListener(new OnOptionsSelectChangeListener() {
                    @Override
                    public void onOptionsSelectChanged(int options1, int options2, int options3) {
                        String classes1 = options1Item.get(options1).getLabel();
                        String classes2 = options2Item.get(options1).get(options2);
                        //设置文本
                        title.setText(classes1);
                        classes.setText(classes2);

                        String str = "options1: " + options1 + "\noptions2: " + options2 + "\noptions3: " + options3;
                        Log.e(TAG, "onOptionsSelectChanged:你选择了: "+str );
                    }
                })
                .build();
        //抽取一级标题数据源
        List<String> labels = new ArrayList<>();
        for(Classes label : options1Item){
            labels.add(label.getLabel());
        }

        classesPicker.setPicker(labels,options2Item);
    }

    private void getOptionData(){
        //选项1
        options1Item.add(new Classes(1,"食品酒水"));
        options1Item.add(new Classes(2,"衣服饰品"));
        options1Item.add(new Classes(3,"居家物业"));
        options1Item.add(new Classes(4,"行车交通"));
        options1Item.add(new Classes(5,"交流通讯"));
        options1Item.add(new Classes(6,"休闲娱乐"));
        options1Item.add(new Classes(7,"学习进修"));
        options1Item.add(new Classes(8,"人情往来"));
        options1Item.add(new Classes(9,"医疗保健"));
        options1Item.add(new Classes(10,"金融保险"));
        options1Item.add(new Classes(11,"其他杂项"));

        //选项2
        //子选项
        List<String> option_01_data = new ArrayList<>();
        option_01_data.add("充饭卡");
        option_01_data.add("早午晚餐");
        option_01_data.add("烟酒茶");
        option_01_data.add("水果零食");
        options2Item.add(option_01_data);

        List<String> option_02_data = new ArrayList<>();
        option_02_data.add("衣服裤子");
        option_02_data.add("鞋帽包包");
        option_02_data.add("化妆饰品");
        options2Item.add(option_02_data);

        List<String> option_03_data = new ArrayList<>();
        option_03_data.add("日常用品");
        option_03_data.add("水电煤气");
        option_03_data.add("房租");
        option_03_data.add("物业管理");
        option_03_data.add("维修保养");
        options2Item.add(option_03_data);

        List<String> option_04_data = new ArrayList<>();
        option_04_data.add("公共交通");
        option_04_data.add("打车租车");
        option_04_data.add("私家车费用");
        options2Item.add(option_04_data);

        List<String> option_05_data = new ArrayList<>();
        option_05_data.add("座机费");
        option_05_data.add("手机费");
        option_05_data.add("上网费");
        option_05_data.add("邮寄费");
        options2Item.add(option_05_data);

        List<String> option_06_data = new ArrayList<>();
        option_06_data.add("运动健身");
        option_06_data.add("腐败聚会");
        option_06_data.add("休闲游戏");
        option_06_data.add("宠物宝贝");
        option_06_data.add("旅游度假");
        options2Item.add(option_06_data);

        List<String> option_07_data = new ArrayList<>();
        option_07_data.add("书报杂志");
        option_07_data.add("培训进修");
        option_07_data.add("数码装备");
        options2Item.add(option_07_data);

        List<String> option_08_data = new ArrayList<>();
        option_08_data.add("送礼请客");
        option_08_data.add("孝敬家长");
        option_08_data.add("还人钱物");
        option_08_data.add("慈善捐助");
        options2Item.add(option_08_data);

        List<String> option_09_data = new ArrayList<>();
        option_09_data.add("药品费");
        option_09_data.add("保健费");
        option_09_data.add("美容费");
        option_09_data.add("治疗费");
        option_01_data.add("检查费");
        options2Item.add(option_09_data);

        List<String> option_10_data = new ArrayList<>();
        option_10_data.add("其他支出");
        option_10_data.add("意外丢失");
        option_10_data.add("烂账损失");
        options2Item.add(option_10_data);

    }

    private void resetButtonForViewPagerActivity(){
        record_agin_button.setVisibility(View.INVISIBLE);
        submit_button.setText("修改");
    }
  
    private void initView(View view){
        cost=view.findViewById(R.id.cost);
        title=view.findViewById(R.id.title);
        classes=view.findViewById(R.id.classes);
        date=view.findViewById(R.id.date);
        detail=view.findViewById(R.id.detail);
        photo_img=view.findViewById(R.id.photo_img);
        radioGroup=view.findViewById(R.id.radio);
        clear_button=view.findViewById(R.id.clear_button);
        record_agin_button=view.findViewById(R.id.record_again_button);
        submit_button=view.findViewById(R.id.submit_button);
        radioButton_shouru=view.findViewById(R.id.radio_shouru);
        radioButton_zhichu=view.findViewById(R.id.radio_zhichu);
        date_layout=view.findViewById(R.id.date_layout);
        classes_layout=view.findViewById(R.id.classes_layout);
        toolbar = view.findViewById(R.id.toolbar);
    }

    private void setView(){
        //record_agin_button弃用
        record_agin_button.setVisibility(View.GONE);
        if(mRecord.getCost()>0 && !addRecord){
            radioButton_shouru.setChecked(true);
            radioButton_zhichu.setChecked(false);
            currentType = TYPE_INCOME;
        }else if(mRecord.getCost()<0 && !addRecord){
            radioButton_shouru.setChecked(true);
            radioButton_zhichu.setChecked(false);
            currentType = TYPE_COST;
        }

        if(!addRecord){
            radioButton_zhichu.setEnabled(false);
            radioButton_shouru.setEnabled(false);
        }
        //更新数据
        if(mRecord.getCost()!=0f) {
            cost.setText(String.valueOf(Math.abs(mRecord.getCost())));
        }
        title.setText(mRecord.getTitle());
        classes.setText(mRecord.getClasses());
        updateDate();
        initClassesPicker();


        classes_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //初始化选择器
                classesPicker.show();
            }
        });

        date_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager=getFragmentManager();
                DatePickerFragment fragment = DatePickerFragment.newIntance(mRecord.getDate());
                //获取Picker传出的值
                fragment.setTargetFragment(RecordFragment.this,0);
                fragment.show(fragmentManager,TAG);

            }
        });

        detail.setText(mRecord.getDetail());

        photo_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //打开拍照功能
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.radio_zhichu:
                        currentType=TYPE_COST;

                        break;
                    case R.id.radio_shouru:
                        currentType=TYPE_INCOME;
                        break;
                }
            }
        });

        clear_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cost.setText("");
                title.setText("");
                classes.setText("");
                date.setText("");
                detail.setText("");
            }
        });

        record_agin_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(addRecord){
                    checkTextEmpty();
                    RecordLab.getRecordLab(getActivity()).addRecord(mRecord);
                }
                clear_button.callOnClick();
            }
        });

        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果不是创建页面的话
                checkTextEmpty();
                saveData();
                if (!addRecord) {
                    Toast.makeText(getActivity(),"修改成功",Toast.LENGTH_SHORT).show();
                    if(currentType == TYPE_COST){
                        mRecord.setCost(-Float.valueOf(cost.getText().toString()));
                    }
                    //恢复原来的列表模式
                    RecordListFragment.recordListFragment.refresh();
                }
                else {
                    //新增记录
                    RecordLab.getRecordLab(getActivity()).addRecord(mRecord);
                    float cost = mRecord.getCost();
                    if (currentType == TYPE_COST && cost > 0) {
                        cost = -cost;
                        mRecord.setCost(cost);
                    }
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    int backStackEntryCount = fragmentManager.getBackStackEntryCount();
                    //大于一个就回退
                    Log.e(TAG, "回退栈fragment个数:: " + backStackEntryCount);
                    fragmentManager.beginTransaction().remove(RecordFragment.this).commit();
                    fragmentManager.beginTransaction().show(RecordListFragment.recordListFragment).commit();
                    fragmentManager.popBackStack();
                    List<Record> records = RecordLab.getRecordLab(getActivity()).getRecords();
                    Log.e(TAG, "records.size() :"+ records.size());
                    if(!records.isEmpty()) {
                        Log.e(TAG, "onClick: notifyDataChange");
                    //恢复原来的列表模式
                     RecordListFragment.recordListFragment.refresh();
                        //RecordListFragment.recordListFragment.getRecord_list().smoothScrollToPosition(records.size());
                    }
                }
            }
        });
    }

    /** 初始化------------------------------------------------------------------------------------*/


    /** UI控制------------------------------------------------------------------------------------*/
    private void updateDate() {
        date.setText(mRecord.getDateString());
    }

    private void saveData(){
        String f = cost.getText().toString();
        if(f!=null){
            mRecord.setCost(Float.valueOf(cost.getText().toString()));
        }else {
            mRecord.setCost(0);

        }
        mRecord.setTitle(title.getText().toString());
        mRecord.setClasses(classes.getText().toString());
        mRecord.setDetail(detail.getText().toString());
    }

    /**
     * 回调函数
     * 从DatePickerDialogFragment接收用户的日期选择结果
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode!= Activity.RESULT_OK)
            return;
        if(requestCode == 0){
            Date date = (Date)data.getSerializableExtra(DatePickerFragment.TAG);
            mRecord.setDate(date);
            updateDate();
        }
    }

    /**
     * 清除"暂无数据"的文本框的文本
     */
    private void initClearText(){
        if(classes.getText().equals("暂无数据")){
            classes.setText("");
        }
        if(detail.getText().equals("暂无数据")){
            detail.setText("");
        }
        if(title.getText().equals("暂无数据")){
            title.setText("");
        }

    }
    private void checkTextEmpty(){
        if(classes.getText().equals("")){
            classes.setText("暂无数据");
        }
        if(detail.getText().equals("")){
            detail.setText("暂无数据");
        }
        if(title.getText().equals("")){
            title.setText("暂无数据");
        }
    }
    /** UI控制------------------------------------------------------------------------------------*/
}
