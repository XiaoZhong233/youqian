package gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Fragment;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.INotificationSideChannel;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.animation.BaseAnimation;
import com.gyf.barlibrary.ImmersionBar;
import com.sdsmdg.tastytoast.TastyToast;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import gz.scau.zhonghaowei.xiaoshoukuaisuan.Model.DataServer;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.Model.DateSection;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.Model.Record;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.Model.RecordLab;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.R;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.View.ScrollTextView;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Activity.AddRecordActivity;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Activity.QueryActivity;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Activity.ViewPagerRecordActivity;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Adapter.DateSectionAdapter;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Adapter.EasyRecordListAdapter;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Adapter.RecordListAdapter;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Util.TimeUtil;

public class RecordListFragment extends BaseFragment {

    /**
     * 标记
     */
    public static final String TAG = "RecordListFragment";

    /**
     * 控件
     */
    private RecyclerView record_list;
    private Toolbar toolbar;
    private Button add_record_button;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageView toolBarImg ;
    RecordListAdapter adapter;
    EasyRecordListAdapter easyAdapter;
    //1表示小数点前面，2表示小数点后面
    private ScrollTextView shouru_img1;
    private ScrollTextView zhichu_img1;
    private ScrollTextView shouru_img2;
    private ScrollTextView zhichu_img2;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    /**
     * 数据源
     */
    List<DateSection> dateSectionList;
    private List<Record> records;
    private Map<String,Float> monthStatistics;

    /**
     * 其他
     */
    private int temPosition;
    public static RecordListFragment recordListFragment;
    private Fragment recordFragment;
    DateSectionAdapter dateSectionAdapter;
    //是否打开滑动动画
    private boolean isOpenScrollAnimation =false;
    //是否需要刷新动画
    private boolean isOpenRefreshAnimation = true;
    //当前日期类型
    public  TimeUtil.DateType currentDateType;
    //判断当前是否为总览列表
    public static boolean isTotalList=false;
    //请求码
    public static final int ADD_REQUEST_CODE = 0;
    public static final int MODIFY_REQUEST_CODE = 1;
    //自定义动画效果
    public static BaseAnimation myAnimation= view -> new Animator[]{
            ObjectAnimator.ofFloat(view,"scaleY",1,1.1f,1),
            ObjectAnimator.ofFloat(view,"scaleX",1,1.1f,1)
    };

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
        mImmersionBar.keyboardEnable(false).navigationBarWithKitkatEnable(false).titleBar(toolbar).init();
    }

    /** 初始化沉浸式------------------------------------------------------------------------------------*/


    /** 调用接口------------------------------------------------------------------------------------*/

    /**
     * 外部创建调用
     * @return
     */
    public static Fragment newIntance(){
        recordListFragment=new RecordListFragment();
        return recordListFragment;
    }

    /**
     * 当数据变化,外部调用此方法刷新列表
     */
    public void refresh(){
        Log.e(TAG, "refresh: isTotalList? "+isTotalList );
        //刷新统计信息
        refreshMonthStatistics();
        if(!RecordListFragment.recordListFragment.isTotalList){
            //如果为空则为默认时间类型
            if(RecordListFragment.recordListFragment.currentDateType!=null){
                Log.e(TAG, "refresh: currentDateType? "+currentDateType.name() );
                RecordListFragment.recordListFragment.refreshData(RecordListFragment.recordListFragment.currentDateType);
            }else {
                RecordListFragment.recordListFragment.refreshData();
            }
        }else {
            RecordListFragment.recordListFragment.initEasyAdapter();
        }
    }

    /**
     * 显示增加记录fragment
     */
    private void showAddRecordFragment(){
//        FragmentManager fragmentManager=getFragmentManager();
//        if(recordFragment==null){
//            recordFragment=RecordFragment.newIntance();
//            fragmentManager.beginTransaction().hide(recordListFragment).add(R.id.container,recordFragment).addToBackStack("null").commit();
//            //fragmentManager.beginTransaction().hide(recordListFragment).show(recordListFragment).addToBackStack("null").commit();
//            recordFragment=null;
//            Log.e(TAG, "onClick: recordFragment newIntance()" );
//        }

        Intent intent = new Intent(getActivity(), AddRecordActivity.class);
        startActivityForResult(intent,ADD_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case ADD_REQUEST_CODE:
                if(resultCode == Activity.RESULT_OK) {
                    Log.e(TAG, "onActivityResult: 新增数据成功！");
                    refresh();
                }
                break;

            case MODIFY_REQUEST_CODE:
                if(resultCode == Activity.RESULT_OK){
                    Log.e(TAG, "onActivityResult: 修改数据成功！" );
                    refresh();
                }
        }
    }


    /** 调用接口------------------------------------------------------------------------------------*/


    /** 生命周期------------------------------------------------------------------------------------*/

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //frgment中必须定义，否则菜单回调不执行
        setHasOptionsMenu(true);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.record_list_layout,container,false);
        initView(view);
        initData();
        //initEasyAdapter();
        initDateSectionAdapter();
        setView();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: ");
        dateSectionAdapter.notifyItemChanged(temPosition);
        //record_list.smoothScrollToPosition(temPosition);
        refresh();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e(TAG, "onStart: " );
        //adapter.notifyItemChanged(temPosition);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        shouru_img1.destroy();
        shouru_img2.destroy();
        zhichu_img1.destroy();
        zhichu_img2.destroy();
    }


    //    @Override
//    public void onHiddenChanged(boolean hidden) {
//        super.onHiddenChanged(hidden);
//        Log.e(TAG, "onHiddenChanged: " );
//        if (!hidden && mImmersionBar != null)
//            Log.e(TAG, "onHiddenChanged: init" );
//            //如果不重写这一句，可能会造成在切换的过程中沉浸式失效
//            //因为父类的变量是所以碎片共享的
//            //另外需要注意的是，在hide/show切换的过程中一旦涉及到会让此碎片生命周期重新走一遍的碎片，则该碎片不要继承
//            //于BaseFragment
//            mImmersionBar.keyboardEnable(true).navigationBarWithKitkatEnable(false).titleBar(toolbar).init();
//        }

    /** 生命周期------------------------------------------------------------------------------------*/


    /** 初始化------------------------------------------------------------------------------------*/

    /**
     * 初始化列表数据
     */
    private void initData(){
        records= RecordLab.getRecordLab(getActivity()).getRecords();
        dateSectionList = DataServer.getDateData(getActivity());
        monthStatistics = RecordLab.getRecordLab(getActivity()).getRecordStatistics(TimeUtil.DateType.thisMonth);
    }

    /**
     * 初始化视图
     * @param view
     */
    private void initView(View view){
        record_list=view.findViewById(R.id.recyclerview_recordlist);
        add_record_button=view.findViewById(R.id.add_record_button);
        toolbar=view.findViewById(R.id.toolbar);
        swipeRefreshLayout=view.findViewById(R.id.swipeRefreshLayout);
        toolBarImg = view.findViewById(R.id.toolbar_img);
        shouru_img1 = view.findViewById(R.id.shouru_img1);
        zhichu_img1 = view.findViewById(R.id.zhichu_img1);
        shouru_img2 = view.findViewById(R.id.shouru_img_2);
        zhichu_img2 = view.findViewById(R.id.zhichu_img2);
        collapsingToolbarLayout = view.findViewById(R.id.collapsingToolBar);
    }

    /**
     * 初始化原始列表适配器
     */
    private void initAdapter(){
        adapter=new RecordListAdapter(getActivity(),records);
        adapter.setOnItemClickListener(new RecordListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Context context, int position, UUID id) {
                //设置点击事件
                temPosition=position;
                Intent intent= ViewPagerRecordActivity.newIntent(context,id,position);
                startActivity(intent);
            }
        });
        record_list.setAdapter(adapter);
    }

    /**
     * 初始化简化列表适配器
     */
    void initEasyAdapter(){
        isTotalList=true;
        isOpenScrollAnimation =true;
        easyAdapter = new EasyRecordListAdapter(getActivity(),R.layout.record_list_item,records);
        //滑动动画
        if(isOpenRefreshAnimation) {
            easyAdapter.openLoadAnimation(myAnimation);
            isOpenScrollAnimation = true;
        }
        //执行多次
        easyAdapter.isFirstOnly(false);

        easyAdapter.setOnItemClickListener(new EasyRecordListAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                //设置点击事件
                temPosition=position;
                Record record=(Record) adapter.getData().get(position);
                Log.e(TAG, "onItemChildClick: id " + record.getId() );
                Intent intent= ViewPagerRecordActivity.newIntent(getActivity(),record.getId(),position);
                startActivity(intent);
            }
        });

        //子布局点击事件注册
        easyAdapter.setOnItemChildClickListener(new EasyRecordListAdapter.OnItemChildClickListener(){

            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()){
                    case R.id.delete:
                        deleteRecord(adapter, position);
                        break;
                    case R.id.content:
                        //设置点击事件
                        modifyRecord(position,adapter.getData());
                        break;
                }


            }
        });

        //Lambda写法
        //easyAdapter.setOnItemChildClickListener((adapter,view,position) -> Log.e(TAG, "initEasyAdapter: "));
        //设置空布局
        easyAdapter.setEmptyView(R.layout.empty_layout,(ViewGroup)record_list.getParent());

        record_list.setAdapter(easyAdapter);

        //绑定滑动回调


    }



    /**
     * 初始化分组列表适配器
     */
    private void initDateSectionAdapter(){
        isTotalList=false;
        dateSectionAdapter = new DateSectionAdapter(getActivity(),R.layout.record_list_item,R.layout.date_section_head_,dateSectionList);
        dateSectionAdapter.openLoadAnimation(DateSectionAdapter.SCALEIN);
        dateSectionAdapter.isFirstOnly(false);
        dateSectionAdapter.setOnItemClickListener((adapter, view, position) -> {
            Log.e(TAG, "onItemClick: position "+position );
            List<DateSection> list;
            list = adapter.getData();
            boolean isHeader = list.get(position).isHeader;
            if(!isHeader) {
                //内容布局点击事件，子控件点击已经实现，这里就不需要了
            }else {
                //处理头部布局的点击事件
            }
        });

        dateSectionAdapter.setOnItemChildClickListener(new DateSectionAdapter.OnItemChildClickListener(){

            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

                List<DateSection> list;
                list = adapter.getData();
                boolean isHeader = list.get(position).isHeader;

                switch (view.getId()){
                    case R.id.delete:
                        deleteRecord(adapter, position);
                        break;
                    case R.id.content:
                        //设置点击事件
                        if(!isHeader) {
                            modifyRecord(position,list);
                        }
                        break;
                }
            }
        });

        dateSectionAdapter.setEmptyView(R.layout.empty_layout,(ViewGroup)record_list.getParent());

        record_list.setAdapter(dateSectionAdapter);

    }

    private String[] floatToInt(float num){
        num=Math.abs(num);
        DecimalFormat decimalFormat=new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
        String p = decimalFormat.format(num);
        return p.split("\\.");
    }

    /**
     * 初始化视图参数
     */
    private void setView(){
        //设置折叠标题
        collapsingToolbarLayout.setTitle("流水");
        collapsingToolbarLayout.setExpandedTitleGravity(Gravity.TOP|Gravity.LEFT);
        //collapsingToolbarLayout.setExpandedTitleColor(Color.parseColor("#888888"));
        //统计文本
        refreshMonthStatistics();
        //设置标题栏背景
        Glide.with(getActivity()).load(R.mipmap.bg2).into(toolBarImg);
        //设置分割线
        record_list.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
        //设置刷新颜色
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //刷新布局
                RecordListFragment.this.refreshSwipeLayout();
            }
        });
        record_list.setLayoutManager(new LinearLayoutManager(getActivity()));
        //为按钮注册点击事件
        add_record_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("RecordList", "onClick: here");
                FragmentManager fragmentManager=getFragmentManager();
                if(recordFragment==null){
                    showAddRecordFragment();
                }
            }
        });
        add_record_button.setVisibility(View.GONE);

        toolbar.inflateMenu(R.menu.toolbar_menu);
        toolbar.setOnMenuItemClickListener(item -> {

            switch (item.getItemId()){
                case R.id.query:
                    startActivity(new Intent(getActivity(), QueryActivity.class));
                    break;
                case R.id.add_record_icon:
                    showAddRecordFragment();
                    break;
                case R.id.setting:
                    //do nothing
                    return true;
                case R.id.today_item:
                    isTotalList=false;
                    currentDateType = TimeUtil.DateType.today;

                    break;
                case R.id.thisWeek_item:
                    isTotalList=false;
                    currentDateType = TimeUtil.DateType.thisWeek;

                    break;
                case R.id.thisMonth_item:
                    isTotalList=false;
                    currentDateType = TimeUtil.DateType.thisMonth;

                    break;
                case R.id.last_three_Month:
                    isTotalList=false;
                    currentDateType = TimeUtil.DateType.lastThreeMonths;

                    break;
                case R.id.thisYear_item:
                    isTotalList=false;
                    currentDateType = TimeUtil.DateType.thisYear;

                    break;
                case R.id.total:
                    currentDateType=null;
                    isTotalList=true;
                    break;
                case R.id.GaiLan:
                    isTotalList=false;
                    currentDateType=null;

                default:
            }
            refresh();
            return true;
        });

        //设置滑动事件,主要用来控制滑动效果
        record_list.setOnScrollChangeListener((view,X,Y,oldX,oldY)->{
            if(isOpenScrollAnimation) {
                dateSectionAdapter.openLoadAnimation(DateSectionAdapter.SCALEIN);
                dateSectionAdapter.isFirstOnly(false);
                isOpenScrollAnimation =false;
                if(easyAdapter!=null){
                    easyAdapter.openLoadAnimation(DateSectionAdapter.SCALEIN);
                    easyAdapter.isFirstOnly(false);
                }
            }
        });

    }


    /** 初始化------------------------------------------------------------------------------------*/





    /** UI控制------------------------------------------------------------------------------------*/


    /**
     * 设置列表数据源
     * 用于DateSectionAdapter重新获取数据
     * @param dateType 时间类型
     */
    public void setData(TimeUtil.DateType dateType){
        dateSectionList = DataServer.getDateData(getActivity(),dateType);
    }

    public void setData(){
        dateSectionList = DataServer.getDateData(getActivity());
    }

    private void refreshData(){
        setData();
        refreshDateAdapter();
    }

    private void refreshData(TimeUtil.DateType dateType){
        setData(dateType);
        refreshDateAdapter();
    }




    /**
     * 下拉刷新
     */
    private void refreshSwipeLayout(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                }catch (Exception e){
                    e.printStackTrace();
                }
                //回到主线程
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //刷新
                        refresh();
                        //列表切换成另一种动画效果
                        if(isOpenRefreshAnimation) {
                            dateSectionAdapter.openLoadAnimation(myAnimation);
                            isOpenScrollAnimation=true;
                        }
                        //停止刷新
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }).start();
    }


    /**
     * 刷新统计信息
     */
    private void refreshMonthStatistics(){
        monthStatistics=RecordLab.getRecordLab(getActivity()).getRecordStatistics(TimeUtil.DateType.thisMonth);
        String[] income = floatToInt(monthStatistics.get(RecordLab.INCOME_KEY));
        String[] cost = floatToInt(monthStatistics.get(RecordLab.COST_KEY));

        Log.e(TAG, "refreshMonthStatistics: "+income[0]+"|||"+income[1] );
        refreshText(shouru_img1,income[0]);
        refreshText(shouru_img2,income[1]);
        refreshText(zhichu_img1,cost[0]);
        refreshText(zhichu_img2,cost[1]);

    }


    private void refreshText(ScrollTextView scrollTextView, String text){
        scrollTextView.setText(text);
        scrollTextView.setPianyilian(ScrollTextView.FIRSTF_FIRST);
        scrollTextView.start();
    }

    /**
     * 刷新
     */
    private void refreshDateAdapter(){
        dateSectionAdapter = new DateSectionAdapter(getActivity(),R.layout.record_list_item,R.layout.date_section_head_,dateSectionList);
        dateSectionAdapter.setOnItemClickListener(new DateSectionAdapter.OnItemClickListener(){

            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Log.e(TAG, "onItemClick: position "+position );
                List<DateSection> list;
                list = adapter.getData();
                boolean isHeader = list.get(position).isHeader;
                if(!isHeader) {
                    //设置点击事件
                    //modifyRecord(position, list);
                }else {

                }
            }
        });

        dateSectionAdapter.setOnItemChildClickListener(new DateSectionAdapter.OnItemChildClickListener(){

            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

                List<DateSection> list;
                list = adapter.getData();
                boolean isHeader = list.get(position).isHeader;

                switch (view.getId()){
                    case R.id.delete:
                        deleteRecord(adapter, position);
                        break;
                    case R.id.content:
                        //设置点击事件
                        if(!isHeader) {
                           modifyRecord(position,list);
                        }
                        break;
                }
            }
        });

        dateSectionAdapter.setEmptyView(R.layout.empty_layout,(ViewGroup)record_list.getParent());
        record_list.setAdapter(dateSectionAdapter);
        //刷新的时候加载自定义动画
        if(isOpenRefreshAnimation) {
            dateSectionAdapter.openLoadAnimation(myAnimation);
            isOpenScrollAnimation = true;
        }
    }

    /**
     * 点击修改
     * @param position
     * @param list
     */
    private <T> void  modifyRecord(int position, List<T> list) {
        isOpenRefreshAnimation = true;
        temPosition = position;
        Object record = list.get(position);
        UUID id=null;
        //适配分组列表与简单列表
        if(record instanceof  DateSection){
            id=((DateSection) record).t.getId();
            //因为有头布局，所以减1
            position-=1;
        }else if(record instanceof Record){
            id= ((Record) record).getId();
        }
        Log.e(TAG, "onItemChildClick: id " + id);
        try{
            //弃用 修改日期:2018/8/11
//            Intent intent = ViewPagerRecordActivity.newIntent(getActivity(), id, position);
//            startActivity(intent);
            Intent intent = new Intent(getActivity(),AddRecordActivity.class);
            intent.putExtra(AddRecordActivity.TAG,id);
            startActivityForResult(intent,MODIFY_REQUEST_CODE);

        }catch (NullPointerException e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * 删除记录
     * @param adapter
     * @param position
     */
    private void deleteRecord(BaseQuickAdapter adapter, int position) {
        Object record=adapter.getData().get(position);
        //关闭刷新动画
        isOpenRefreshAnimation = false;
        if(record instanceof DateSection ){
            Boolean isHeader = ((DateSection) record).isHeader;
            if(!isHeader){
                Record IndeedRecord=((DateSection) record).getRecord();
                RecordLab.getRecordLab(getActivity()).removeRecord(IndeedRecord);
                adapter.notifyItemChanged(position);
                record_list.requestFocus();
                try {
                    record_list.removeViewAt(position);
                }catch (Exception e){
                    e.printStackTrace();
                }
                refresh();
            }
        }else if(record instanceof Record){
            RecordLab.getRecordLab(getActivity()).removeRecord((Record) record);
            adapter.notifyItemChanged(position);
            record_list.removeViewAt(position);
            refresh();
        }
        //开启刷新和滑动动画
        isOpenRefreshAnimation = true;
        isOpenScrollAnimation = true;

//        if(!isHeader){
//            EasySwipeMenuLayout easySwipeMenuLayout = (EasySwipeMenuLayout)adapter.getViewByPosition(record_list,position, R.id.easySwipeMenuLayout);
//            easySwipeMenuLayout.resetStatus();
//        }
    }

    /** UI控制------------------------------------------------------------------------------------*/

}
