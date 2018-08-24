package gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.azimolabs.keyboardwatcher.KeyboardWatcher;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gyf.barlibrary.ImmersionBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import gz.scau.zhonghaowei.xiaoshoukuaisuan.Model.AAItem;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.Model.DataServer;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.R;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Activity.AddRecordActivity;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Activity.EntryActivity;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Adapter.AAAdapter;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Util.AACalculator;

public class CalculateFragment extends BaseFragment implements KeyboardWatcher.OnKeyboardToggleListener{

    private static final String TAG = "CalculateFragment";
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private Button add_btn;
    private Button cal_btn;
    private List<AAItem> data;
    private AAAdapter aaAdapter;
    private View footView;
    TextView result_tv;
    private double avg;
    private Map<Map<String,String>,Double> result = new HashMap<>();
    //监听软键盘
    private KeyboardWatcher mKeyboardWatcher;





    public static CalculateFragment newIntance(){
        return new CalculateFragment();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        initKeyWatch();
    }



    // 初始化软键盘监听
    private void initKeyWatch() {
        mKeyboardWatcher = new KeyboardWatcher(getActivity());
        mKeyboardWatcher.setListener(this);
    }


    @Override
    public void onKeyboardShown(int keyboardSize) {
        Log.e(TAG, "onKeyboardShown: show" );
        EntryActivity.bottom_layout.setVisibility(View.GONE);
    }

    @Override
    public void onKeyboardClosed() {
        Log.e(TAG, "onKeyboardClosed: hide" );
        EntryActivity.bottom_layout.setVisibility(View.VISIBLE);
    }

    /**
     * 初始化沉浸式
     */
    @Override
    protected void initImmersionBar() {
        mImmersionBar = ImmersionBar.with(this);
        mImmersionBar.keyboardEnable(true).navigationBarWithKitkatEnable(false).
                titleBar(toolbar)
                .navigationBarColor(R.color.colorPrimary)
                .init();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calculate_fragment,container,false);
        initData();
        initView(view);
        setView();
        initAdapter();
        return view;
    }



    private void initData(){
        data = new ArrayList<>();
        data.add(new AAItem("",0d));
    }

    private void initView(View view){
        toolbar = view.findViewById(R.id.toolbar);
        recyclerView = view.findViewById(R.id.recyclerView);
        add_btn = view.findViewById(R.id.add_button);
        cal_btn = view.findViewById(R.id.calculate_button);
    }

    private void setView(){
        //设置分割线
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        add_btn.setOnClickListener(view->{
            if(aaAdapter.getFooterLayoutCount()>0) {
                aaAdapter.removeAllFooterView();
            }
            data.add(new AAItem("",0));
            Log.e(TAG, "setView: data.size:" +data.size());
            aaAdapter.notifyItemChanged(data.size()-1);
        });
        cal_btn.setOnClickListener(view-> {
            if(aaAdapter.getFooterLayoutCount()==0) {
                aaAdapter.addFooterView(getResultFootView());
            }else {
                refreshTvResult();
            }
        }
        );
        toolbar.inflateMenu(R.menu.add_record_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case  R.id.add_record_icon:
                        showAddRecordFragment();
                        break;
                }
                return true;
            }
        });
    }

    private void initAdapter(){
        aaAdapter = new AAAdapter(R.layout.calculate_item,data);
        aaAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            switch (view.getId()){
                case R.id.delete:
                    adapter.removeAllFooterView();
                    data.remove(position);
                    adapter.notifyItemRemoved(position);
                    recyclerView.removeViewAt(position);
                    Log.e(TAG, "initAdapter: delete position: " + position);
                    break;
                case R.id.content:
                    //设置点击事件
                    break;
            }
        });
        recyclerView.setAdapter(aaAdapter);
    }

    private View getResultFootView(){
        View view = getLayoutInflater().inflate(R.layout.aa_result_layout, (ViewGroup) recyclerView.getParent(), false);
        footView = view;
        calculateResult();
        result_tv = view.findViewById(R.id.result);
        if(!result.isEmpty()) {
            Set<Map.Entry<Map<String, String>, Double>> entry = result.entrySet();
            StringBuilder s = new StringBuilder();
            s.append("计算结果: \n");
            for (Map.Entry<Map<String, String>, Double> mapDoubleEntry : entry) {
                Double cost = mapDoubleEntry.getValue();
                Map<String, String> nameMap = mapDoubleEntry.getKey();
                Set<Map.Entry<String, String>> nameEntrySet = nameMap.entrySet();
                for (Map.Entry<String, String> nameEntry : nameEntrySet) {
                    s.append(nameEntry.getKey());
                    s.append(" 给 ");
                    s.append(nameEntry.getValue());
                    s.append(DataServer.floatToInt(cost.floatValue()) + "元");
                    s.append("\n");
                }
            }
            s.append("人均花费: "+DataServer.floatToInt((float) avg));
            result_tv.setText(s.toString());
        }else {
            result_tv.setText("别闹了,人数不够怎么算嘛！");
        }
        return view;
    }

    private void calculateResult(){
        for(int i =0;i<data.size();i++){
            String s = data.get(i).getName() + " " +data.get(i).getPrice();
            Log.e(TAG, "calculateResult: "+s );
        }
        if(data.size()<=1){
            return;
        }
        AACalculator aaCalculator = new AACalculator(data);
        this.result =  aaCalculator.getResult();
        this.avg = aaCalculator.getAvg();
    }

    private void refreshTvResult(){
        if(result_tv!=null){
            calculateResult();
            if(!result.isEmpty()) {
                Set<Map.Entry<Map<String, String>, Double>> entry = result.entrySet();
                StringBuilder s = new StringBuilder();
                s.append("计算结果: \n");
                for (Map.Entry<Map<String, String>, Double> mapDoubleEntry : entry) {
                    Double cost = mapDoubleEntry.getValue();
                    Map<String, String> nameMap = mapDoubleEntry.getKey();
                    Set<Map.Entry<String, String>> nameEntrySet = nameMap.entrySet();
                    for (Map.Entry<String, String> nameEntry : nameEntrySet) {
                        s.append(nameEntry.getKey());
                        s.append(" 给 ");
                        s.append(nameEntry.getValue());
                        s.append(DataServer.floatToInt(cost.floatValue()) + "元");
                        s.append("\n");
                    }
                }
                s.append("人均花费: "+DataServer.floatToInt((float) avg));
                result_tv.setText(s.toString());
            }else {
                result_tv.setText("别闹了,人数不够怎么算嘛！");
            }
        }
    }

    /**
     * 显示增加记录fragment
     */
    private void showAddRecordFragment(){
        Intent intent = new Intent(getActivity(), AddRecordActivity.class);
        startActivity(intent);
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
            Log.e(TAG, "toggleSoftInput: erro occur");
        }
    }

}
