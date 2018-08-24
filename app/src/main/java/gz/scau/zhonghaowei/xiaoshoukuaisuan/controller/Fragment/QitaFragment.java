package gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.gyf.barlibrary.ImmersionBar;

import java.util.ArrayList;
import java.util.List;

import gz.scau.zhonghaowei.xiaoshoukuaisuan.R;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.View.FunGameRefreshView;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Activity.AddRecordActivity;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Adapter.QiTaListAdapter;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Util.Contact;

public class QitaFragment extends BaseFragment {

    private static Fragment mfragment;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private FunGameRefreshView refreshView;
    private List<String> data;
    private QiTaListAdapter adapter;

    public static Fragment newIntance(){
        mfragment = new QitaFragment();
        return mfragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.qita_fragment,container,false);
        initData();
        initView(view);
        setView();
        initAdapter();
        return view;
    }

    private void initView(View view){
        toolbar = view.findViewById(R.id.toolbar);
        recyclerView = view.findViewById(R.id.recyclerView);
        refreshView = view.findViewById(R.id.refresh_hit_block);
    }

    private void setView(){
        toolbar.inflateMenu(R.menu.add_record_menu);
        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()){
                case  R.id.add_record_icon:
                    showAddRecordFragment();
                    break;
            }
            return true;
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
        refreshView.setOnRefreshListener(new FunGameRefreshView.FunGameRefreshListener() {
            @Override
            public void onPullRefreshing() {
                //刷新
                SystemClock.sleep(2000);
            }

            @Override
            public void onRefreshComplete() {
                //刷新完成
            }
        });
        refreshView.setGameOverText("GAME OVER!");
        refreshView.setLoadingText("加载中");
        refreshView.setLoadingFinishedText("加载完成");
        refreshView.setTopMaskText("放松一下");
        refreshView.setBottomMaskText("游戏开始");
    }

    private void initAdapter(){
        adapter = new QiTaListAdapter(getActivity(),data);
        adapter.setOnItemClickListener((context, position) -> {
            if(data.get(position).equals("联系开发者")){
                Contact.onContact(getActivity());
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void initData(){
        data = new ArrayList<>();
        data.add("A");
        data.add("B");
        data.add("C");
        data.add("D");
        data.add("E");
        data.add("G");
        data.add("H");
        data.add("联系开发者");
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

    /**
     * 显示增加记录fragment
     */
    private void showAddRecordFragment(){
        Intent intent = new Intent(getActivity(), AddRecordActivity.class);
        startActivity(intent);
    }
}
