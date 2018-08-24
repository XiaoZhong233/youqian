package gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Activity;

import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;

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

import gz.scau.zhonghaowei.xiaoshoukuaisuan.Model.Record;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.Model.RecordLab;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.R;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Adapter.YMRExpandableItemAdapter;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Item.MonthSecendItem;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Item.YearFirstItem;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Util.TimeUtil;

import static gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Activity.RecordsDetailActivity.MODIFY_RECORD;

public class QueryResultActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TickerView shouru_tv;
    private TickerView zhichu_tv;
    private TickerView zonge_tv;
    private RecyclerView recyclerView;
    private ImmersionBar mImmersionBar;
    private CollapsingToolbarLayout collapsingToolBar;

    private static final String TAG = "QueryResultActivity";

    //用于记录日期区间
    Date startDate;
    Date endDate;
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

    private ArrayList<MultiItemEntity> multiItemEntityArrayList;
    private YMRExpandableItemAdapter adapter;
    //原始数据
    private List<Record> recordList;
    //年分类数据
    Map<Integer,List<Record>> yearMap;
    //最终处理数据
    ArrayList<MultiItemEntity> data;

    Map<String,Float> tongji = new HashMap<>();
    private boolean initFlag = true;




    private void initData(){
        //初始化统计数据,防止空引用
        tongji.put(RecordLab.COST_KEY,0f);
        tongji.put(RecordLab.INCOME_KEY,0f);

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra(QueryActivity.TAG);
        startDate = (Date)bundle.get(QueryActivity.START_DATE);
        endDate = (Date)bundle.get(QueryActivity.END_DATE);
        countType = bundle.getString(QueryActivity.COUNT_TYPE);
        title = bundle.getString(QueryActivity.TITLE);
        classes = bundle.getString(QueryActivity.CLASSES);
        currentType = bundle.getInt(QueryActivity.CURRENT_TYPE);

        Log.e(TAG, "initData: preDate "+startDate);
        Log.e(TAG, "initData: endDate "+endDate );
        Log.e(TAG, "initData: countType "+countType );
        Log.e(TAG, "initData: title "+title );
        Log.e(TAG, "initData: classes "+classes );
        Log.e(TAG, "initData: curType "+currentType );

        recordList = new ArrayList<>();
        //只需根据二级标题筛选即可，因为数据都是经过处理的
        recordList = RecordLab.getRecordLab(this).getRecords().stream()
                .filter(r-> TimeUtil.belongTimeBucket(r.getDate(),startDate,endDate))
                .filter(r-> countType.equals("全部") || r.getPay_type().equals(countType))
                .filter(r-> classes.equals("全部") || r.getClasses().equals(classes))
                .collect(Collectors.toList());

        if(recordList != null) {
            tongji = RecordLab.getRecordLab(this).getRecordStatistics(recordList);
        }

        yearMap = RecordLab.getRecordLab(this).getYearMapForRecord(recordList);


    }


    private ArrayList<MultiItemEntity> generateData(){
        if(yearMap.isEmpty()){
            return new ArrayList<>();
        }
        Set<Map.Entry<Integer,List<Record>>> yearEntrySet = yearMap.entrySet();
        RecordLab recordLab = RecordLab.getRecordLab(this);
        //数据容器
        ArrayList<MultiItemEntity> content = new ArrayList<>();
        //遍历年数据
        for(Map.Entry<Integer,List<Record>> entry : yearEntrySet){
            int year = entry.getKey();
            List<Record> yearList = entry.getValue();
            YearFirstItem lv0Year = new YearFirstItem(this,year);
            content.add(lv0Year);
            //对当前年数据当月分类
            Map<Integer,List<Record>> monthMap = recordLab.getMonthMapForRecord(yearList);
            //遍历每一层,为每一层填充数据
            Set<Map.Entry<Integer,List<Record>>> entrySet = monthMap.entrySet();
            Iterator<Map.Entry<Integer,List<Record>>> iterator = entrySet.iterator();
            //遍历月数据
            while (iterator.hasNext()){
                Map.Entry<Integer,List<Record>> mapEntry = iterator.next();
                //获取每月数据
                List<Record> monthsList = mapEntry.getValue();
                //将月数据按照日期正序排序
                monthsList = monthsList.stream().sorted(Comparator.comparing(Record::getRecordDay)).collect(Collectors.toList());
                MonthSecendItem lv1Month = new MonthSecendItem(this,mapEntry.getKey(),monthsList);
                Log.e(TAG, "generateData: 加入第二层数据");
                lv0Year.addSubItem(lv1Month);
                //遍历加入第三层记录数据
                for(int i=0;i<monthsList.size();i++){
                    Record record = monthsList.get(i);
                    lv1Month.addSubItem(record);
                    Log.e(TAG, "generateData: 加入第三层数据");
                }
            }
        }
        return content;
    }


    private void getLoadMoreData(){
        //初始化统计数据,防止空引用
        tongji.put(RecordLab.COST_KEY,0f);
        tongji.put(RecordLab.INCOME_KEY,0f);
        RecordLab recordLab  = RecordLab.getRecordLab(this);
        recordList = RecordLab.getRecordLab(this).getRecords().stream()
                .filter(r-> TimeUtil.belongTimeBucket(r.getDate(),startDate,endDate))
                .filter(r-> countType.equals("全部") || r.getPay_type().equals(countType))
                .filter(r-> classes.equals("全部") || r.getClasses().equals(classes))
                .collect(Collectors.toList());
        yearMap = RecordLab.getRecordLab(this).getYearMapForRecord(recordList);
        List<Record> records =recordList;
        if(records != null) {
            tongji = recordLab.getRecordStatistics(records);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_result);
        initData();
        initImmersionBar();
        initView();
        setView();
        initAdapter();
    }


    @Override
    protected void onResume() {
        super.onResume();
        //只在非初次加载时候刷新
        if(!initFlag) {
            getLoadMoreData();
            adapter.setNewData(generateData());
            adapter.notifyDataSetChanged();
            adapter.expandAll();

            //刷新统计信息
            zonge_tv.setText(floatToInt(tongji.get(RecordLab.COST_KEY) + tongji.get(RecordLab.INCOME_KEY)));
            shouru_tv.setText(floatToInt(Math.abs(tongji.get(RecordLab.INCOME_KEY))));
            zhichu_tv.setText(floatToInt(Math.abs(tongji.get(RecordLab.COST_KEY))));
        }
        initFlag = false;

    }

    private void initView(){
        toolbar = findViewById(R.id.toolbar);
        shouru_tv = findViewById(R.id.shouru_tv);
        zhichu_tv = findViewById(R.id.zhichu_tv);
        zonge_tv = findViewById(R.id.zonge);
        recyclerView = findViewById(R.id.recyclerView);
        collapsingToolBar = findViewById(R.id.collapsingToolBar);
    }


    private void setView(){
        //设置折叠标题
        collapsingToolBar.setTitle("查询结果");
        collapsingToolBar.setExpandedTitleGravity(Gravity.TOP|Gravity.LEFT);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));

        zonge_tv.setText(floatToInt(tongji.get(RecordLab.COST_KEY)+tongji.get(RecordLab.INCOME_KEY)));
        shouru_tv.setText(floatToInt(Math.abs(tongji.get(RecordLab.INCOME_KEY))));
        zhichu_tv.setText(floatToInt(Math.abs(tongji.get(RecordLab.COST_KEY))));

    }

    private void initAdapter(){
        multiItemEntityArrayList = generateData();
        adapter = new YMRExpandableItemAdapter(this,multiItemEntityArrayList);
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
        adapter.expandAll();
        recyclerView.setAdapter(adapter);
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


}
