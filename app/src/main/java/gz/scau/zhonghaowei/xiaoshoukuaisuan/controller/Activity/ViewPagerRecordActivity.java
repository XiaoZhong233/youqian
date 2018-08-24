package gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.gyf.barlibrary.ImmersionBar;

import java.util.List;
import java.util.UUID;

import gz.scau.zhonghaowei.xiaoshoukuaisuan.Model.Record;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.Model.RecordLab;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.R;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Fragment.RecordFragment;

public class ViewPagerRecordActivity extends AppCompatActivity {


    private ViewPager viewPager;
    List<Record> records;
    private int currentposition;
    private FragmentManager fm;
    private UUID id;

    public static final String ID_KEY = "ViewPagerRecordActivity_ID_KEY";
    public static final String POS_KEY = "ViewPagerRecordActivity_POS_KEY";


    /** 外部接口------------------------------------------------------------------------------------*/
    public static Intent newIntent(Context clientContext, UUID id, int position){
        Intent intent=new Intent(clientContext,ViewPagerRecordActivity.class);
        intent.putExtra(ID_KEY,id);
        intent.putExtra(POS_KEY,position);
        return intent;
    }
    /** 外部接口------------------------------------------------------------------------------------*/



    /** 生命周期------------------------------------------------------------------------------------*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化沉浸式
        ImmersionBar.with(this)
                .keyboardEnable(false)//解决软键盘与底部输入框冲突问题
                .init();
        setContentView(R.layout.activity_view_pager);
        initView();
        initData();
        initAdapter();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //不调用该方法，如果界面bar发生改变，在不关闭app的情况下，退出此界面再进入将记忆最后一次bar改变的状态
        ImmersionBar.with(this).destroy();
    }
    /** 生命周期------------------------------------------------------------------------------------*/


    /** 初始化------------------------------------------------------------------------------------*/
    private void initView(){
        viewPager=findViewById(R.id.viewpager);
    }

    private void initData(){
        fm=getSupportFragmentManager();
        currentposition=getIntent().getIntExtra(POS_KEY,0);
        id=(UUID)getIntent().getSerializableExtra(ID_KEY);
        records= RecordLab.getRecordLab(this).getRecords();
    }

    private void initAdapter(){
        viewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
            @Override
            public Fragment getItem(int position) {
                Record record=records.get(position);
                UUID id=record.getId();
                return RecordFragment.newIntance(id);
            }

            @Override
            public int getCount() {
                return records.size();
            }
        });

        viewPager.setCurrentItem(currentposition);
    }
    /** 初始化------------------------------------------------------------------------------------*/

}
