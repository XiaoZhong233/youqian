package gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.gyf.barlibrary.ImmersionBar;
import com.robinhood.ticker.TickerView;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import cn.qqtheme.framework.picker.OptionPicker;
import cn.qqtheme.framework.popup.BasicPopup;
import cn.qqtheme.framework.widget.WheelView;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.Model.Record;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.Model.RecordLab;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.R;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.View.CustomLoadView;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Adapter.ExpandableItemAdapter;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Fragment.ZhanghuFragment;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Item.MonthfirstItem;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Util.TimeUtil;


public class RecordsDetailActivity extends AppCompatActivity {

    private static final String TAG = "RecordsDetailActivity";
    private Toolbar toolbar;
    private TickerView zonge;
    private TickerView liuru;
    private TickerView liuchu;
    private RecyclerView recyclerView;
    private ImmersionBar mImmersionBar;
    private CollapsingToolbarLayout collapsingToolBar;
    private String title = "空记录";
    ExpandableItemAdapter adapter;
    ArrayList<MultiItemEntity> multiItemEntityArrayList;
    //年分类数据
    Map<Integer,List<Record>> yearMap;
    Map<String,Float> tongji = new HashMap<>();
    //请求码
    public static final int MODIFY_RECORD = 0;
    //获取当年
    final int currentYear = TimeUtil.getYear(new Date());
    int targetYear = currentYear;
    //初始化标志
    boolean initFlag = false;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case MODIFY_RECORD:
                if(Activity.RESULT_OK == resultCode){
                    Log.e(TAG, "onActivityResult: 修改数据成功！" );
                    adapter.notifyDataSetChanged();
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records_detail);
        //获取传入的数据
        Intent intent = getIntent();
        String s = intent.getStringExtra(ZhanghuFragment.TAG);
        if(!s.isEmpty()){
            title = s;
        }
        initFlag=true;
        initImmersionBar();
        initData();
        initView();
        setView();
        initAdapter();
    }

    private void initView(){
        toolbar = findViewById(R.id.toolbar);
        zonge  = findViewById(R.id.zonge);
        liuru = findViewById(R.id.liuru);
        liuchu = findViewById(R.id.liuchu);
        recyclerView = findViewById(R.id.recyclerView);
        collapsingToolBar = findViewById(R.id.collapsingToolBar);
    }

    private void setView(){
        //设置折叠标题
        collapsingToolBar.setTitle(title);
        collapsingToolBar.setExpandedTitleGravity(Gravity.TOP|Gravity.LEFT);
        //设置分割线
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        zonge.setText(floatToInt(tongji.get(RecordLab.COST_KEY)+tongji.get(RecordLab.INCOME_KEY)));
        liuru.setText(floatToInt(Math.abs(tongji.get(RecordLab.INCOME_KEY))));
        liuchu.setText(floatToInt(Math.abs(tongji.get(RecordLab.COST_KEY))));

        toolbar.inflateMenu(R.menu.record_detail_toolbar_menu);
        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()){
                case R.id.setting:
                    onOptionPicker(recyclerView);
                break;
            }
            return true;
        }
        );

    }

    public void onOptionPicker(View view) {

        String a[] = new String[]{
                String.valueOf(currentYear-3),String.valueOf(currentYear-2),String.valueOf(currentYear-1),
                String.valueOf(currentYear),
                String.valueOf(currentYear+1),String.valueOf(currentYear+2),String.valueOf(currentYear+3)
        };
        OptionPicker picker = new OptionPicker(this, a);
        picker.setCanceledOnTouchOutside(false);

        picker.setDividerRatio(WheelView.DividerConfig.FILL);
        picker.setShadowColor(Color.RED, 40);
        picker.setSelectedItem(String.valueOf(targetYear));
        picker.setGravity(Gravity.CENTER);
        picker.setCycleDisable(true);
        picker.setTextSize(15);
        picker.setLineSpaceMultiplier(4);
        picker.setWidth(900);
        picker.setLabel("年");
        picker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener() {
            @Override
            public void onOptionPicked(int index, String item) {
                targetYear=Integer.valueOf(item);
                getLoadMoreData(targetYear);
                adapter.setNewData(generateData(targetYear));
                adapter.notifyDataSetChanged();
                //adapter.expandAll();
                //停止刷新
                //swipeRefreshLayout.setRefreshing(false);
                //刷新统计信息
                zonge.setText(floatToInt(tongji.get(RecordLab.COST_KEY)+tongji.get(RecordLab.INCOME_KEY)));
                liuru.setText(floatToInt(Math.abs(tongji.get(RecordLab.INCOME_KEY))));
                liuchu.setText(floatToInt(Math.abs(tongji.get(RecordLab.COST_KEY))));
            }
        });
        picker.show();
    }

    private void initData(){
        //初始化统计数据,防止空引用
        tongji.put(RecordLab.COST_KEY,0f);
        tongji.put(RecordLab.INCOME_KEY,0f);
        //初始化年分类数据
        RecordLab recordLab  = RecordLab.getRecordLab(this);
        //从数据层获取按类别分类的数据源
        List<Record> list = recordLab.getRecordByPayType(title);
        //对数据按年进行分类
        yearMap = recordLab.getYearMapForRecord(list);

        List<Record> records =yearMap.get(currentYear);
        if(records != null) {
            tongji = recordLab.getRecordStatistics(records);
        }
    }



    private void getLoadMoreData(int year){
        //初始化统计数据,防止空引用
        tongji.put(RecordLab.COST_KEY,0f);
        tongji.put(RecordLab.INCOME_KEY,0f);
        RecordLab recordLab  = RecordLab.getRecordLab(this);
        List<Record> records =yearMap.get(year);
        if(records != null) {
            tongji = recordLab.getRecordStatistics(records);
        }
    }

    /**
     * 下拉刷新
     */
    private void onSwipeLayoutRefresh(){
        new Thread(() -> {
            try {
                //防止用户一直下拉 想睡多久睡多久 :)
                //swipeRefreshLayout.setRefreshing(true);
                View view = getLayoutInflater().inflate(new CustomLoadView().getLayoutId(), (ViewGroup) recyclerView.getParent(), false);
                adapter.addHeaderView(view);
                Thread.sleep(1000);
            }catch (Exception e){
                e.printStackTrace();
            }
            //回到主线程
            runOnUiThread(() -> {
                //刷新
                getLoadMoreData(currentYear);
                adapter.setNewData(generateData(currentYear));
                adapter.notifyDataSetChanged();
                //adapter.expandAll();
                adapter.setEnableLoadMore(false);
                adapter.removeAllHeaderView();
                //停止刷新
                //swipeRefreshLayout.setRefreshing(false);
                //刷新统计信息
                zonge.setText(floatToInt(tongji.get(RecordLab.COST_KEY)+tongji.get(RecordLab.INCOME_KEY)));
                liuru.setText(floatToInt(Math.abs(tongji.get(RecordLab.INCOME_KEY))));
                liuchu.setText(floatToInt(Math.abs(tongji.get(RecordLab.COST_KEY))));
            });
        }).start();
    }




    private void initAdapter(){
        multiItemEntityArrayList = generateData(currentYear);
        adapter = new ExpandableItemAdapter(this,multiItemEntityArrayList);
        adapter.setEnableLoadMore(true);
        adapter.setEmptyView(R.layout.empty_layout,(ViewGroup) recyclerView.getParent());
        adapter.setOnItemChildClickListener((adapter, view, position) -> {
            switch (view.getId()){
                case R.id.delete:
                    Record r = (Record)adapter.getData().get(position);
                    RecordLab.getRecordLab(this).removeRecord(r);
                    adapter.remove(position);
                    break;
                case R.id.content:
                    Intent intent = new Intent(this,AddRecordActivity.class);
                    Record record = (Record)adapter.getData().get(position);
                    intent.putExtra(AddRecordActivity.TAG,record.getId());
                    startActivityForResult(intent,MODIFY_RECORD);
                    break;
            }
        });
        //adapter.expandAll();
        recyclerView.setAdapter(adapter);

    }



    private void refreshList(){

    }

    /**
     * 装填树形适配器数据,获取某一年的列表数据
     * 在initData后生效
     * @param currentYear
     * @return
     */
    private ArrayList<MultiItemEntity> generateData(int currentYear){
        Set key = yearMap.keySet();
        if(!key.contains(currentYear)){
            Log.e(TAG, "generateData: 该列表中不包含 "+currentYear+"年的数据" );
            //返回空
            return new ArrayList<>();
        }
        //获取传入的年份的的数据
        List<Record> listOfCuurentYear  = yearMap.get(currentYear);
        //对处理好的年数据进行月分类
        Map<Integer,List<Record>> monthMap  = RecordLab.getRecordLab(this).getMonthMapForRecord(listOfCuurentYear);
        //数据容器
        ArrayList<MultiItemEntity> content = new ArrayList<>();
        //遍历每一层,为每一层填充数据
        Set<Map.Entry<Integer,List<Record>>> entrySet = monthMap.entrySet();
        Iterator<Map.Entry<Integer,List<Record>>> iterator = entrySet.iterator();
        while (iterator.hasNext()){
            Map.Entry<Integer,List<Record>> entry = iterator.next();
            //获取每月数据
            List<Record> monthsList = entry.getValue();
            //将月数据按照日期正序排序
            monthsList = monthsList.stream().sorted(Comparator.comparing(Record::getRecordDay)).collect(Collectors.toList());
            MonthfirstItem lv0Data = new MonthfirstItem(this, monthsList.get(0).getDate(),monthsList);
            //加入第一层数据
            content.add(lv0Data);
            Log.e(TAG, "generateData: 加入第一层数据");
            //遍历加入第二层数据
            for(int i=0;i<monthsList.size();i++){
                Record record = monthsList.get(i);
                lv0Data.addSubItem(record);
                Log.e(TAG, "generateData: 加入第二层数据");
            }
        }
        return content;
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mImmersionBar.destroy();
    }


    @Override
    protected void onResume() {
        super.onResume();
        //只在非初次加载时候刷新
        if(!initFlag) {
            getLoadMoreData(targetYear);
            adapter.setNewData(generateData(targetYear));
            adapter.notifyDataSetChanged();
            //adapter.expandAll();
            //停止刷新
            //swipeRefreshLayout.setRefreshing(false);
            //刷新统计信息
            zonge.setText(floatToInt(tongji.get(RecordLab.COST_KEY) + tongji.get(RecordLab.INCOME_KEY)));
            liuru.setText(floatToInt(Math.abs(tongji.get(RecordLab.INCOME_KEY))));
            liuchu.setText(floatToInt(Math.abs(tongji.get(RecordLab.COST_KEY))));
        }
        initFlag = false;
    }

}
