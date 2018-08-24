package gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Activity;

import android.content.Intent;
import android.support.annotation.BoolRes;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.gyf.barlibrary.ImmersionBar;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import gz.scau.zhonghaowei.xiaoshoukuaisuan.Model.Record;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.Model.RecordLab;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.R;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Fragment.AddRecordFragment;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Fragment.RecordFragment;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Fragment.RecordListFragment;

public class AddRecordActivity extends AppCompatActivity{


    private Toolbar toolbar;
    private ViewPager viewPager;
    private FragmentManager fragmentManager;
    private FragmentPagerAdapter pagerAdapter;
    private TabLayout tabLayout;
    private ImmersionBar mImmersionBar;
    private boolean initFlag = true;
    private static final int ZHICHU = 0;
    private static final int SHOURU = 1;
    @IntDef({ZHICHU,SHOURU})
    @Retention(RetentionPolicy.SOURCE)
    public @interface RecordType{}
    private static final int PAGE_COUNT=2;
    private ViewPager.OnPageChangeListener listener;
    public static final String TAG = "AddRecordActivity";
    List<AddRecordFragment> fragments;
    private int prePage=0;

    private boolean isAdd = true;
    private boolean isInitFlag = true;
    private UUID id = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.add_record);
        Intent intent = getIntent();
        id = (UUID) intent.getSerializableExtra(TAG);
        if(id!=null){
            isAdd = false;
            Log.e(TAG, "onCreate: 修改数据  id:"+ id.toString() );
        }
        fragmentManager = getSupportFragmentManager();
        initView();
        initImmersionBar();
        initAdapter();
        setView();
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

    /*
     * 初始化视图
     */
    private void initView(){
        toolbar =findViewById(R.id.toolbar);
        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tabLayout);
    }

    private void setView(){
        mImmersionBar.statusBarDarkFont(false).titleBar(R.id.toolbar).init();
        listener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //viewpager适配沉浸式
                if(initFlag){
                mImmersionBar.statusBarDarkFont(false).titleBar(R.id.toolbar).keyboardEnable(true).init();
                initFlag = false;
                }
                Log.e(TAG, "onPageScrolled: position "+position );
            }

            @Override
            public void onPageSelected(int position) {
                //mImmersionBar.statusBarDarkFont(false).titleBar(R.id.toolbar).init();
                //在修改页面下滑动,修复不同类型的类别初始化显示问题,此方法只能执行一次,否则空引用
                if((!isAdd) && isInitFlag) {
                    Log.e(TAG, "onPageSelected: position "+position );
                    if(prePage != position){
                        switch (position){
                            case 0:
                                fragments.get(0).setClass_title_TextView("食品酒水","早午晚餐");
                                break;
                            case 1:
                                fragments.get(1).setClass_title_TextView("职业收入","工资收入");
                                break;
                        }
                        isInitFlag = false;
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        };
        viewPager.addOnPageChangeListener(listener);

    }



    private void initAdapter(){
        fragments = new ArrayList<>();
        if(isAdd) {
            AddRecordFragment zhichu = AddRecordFragment.newIntance();
            zhichu.setCurrentType(AddRecordFragment.TYPE_ZHICHU);
            AddRecordFragment shouru = AddRecordFragment.newIntance();
            shouru.setCurrentType(AddRecordFragment.TYPE_SHOURU);
            fragments.add(zhichu);
            fragments.add(shouru);
        }else {
            AddRecordFragment zhichu = AddRecordFragment.newIntance(id);
            zhichu.setCurrentType(AddRecordFragment.TYPE_ZHICHU);
            AddRecordFragment shouru = AddRecordFragment.newIntance(id);
            shouru.setCurrentType(AddRecordFragment.TYPE_SHOURU);
            fragments.add(zhichu);
            fragments.add(shouru);
        }
        pagerAdapter = new FragmentPagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                switch (position){
                    case ZHICHU:
                        return fragments.get(0);
                    case SHOURU:
                        return fragments.get(1);
                        default:
                            return fragments.get(0);
                }
            }

            @Override
            public int getCount() {
                return PAGE_COUNT;
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                switch (position){
                    case ZHICHU:
                        return "支出";
                    case SHOURU:
                        return "收入";
                        default:
                            return "支出";
                }
            }
        };

        viewPager.setAdapter(pagerAdapter);
        //tabLayout绑定ViewPager
        tabLayout.setupWithViewPager(viewPager);


        if(isAdd) {
            viewPager.setCurrentItem(0);
        }else {
            //判断传进来的数据是支出还是收入
            float x = RecordLab.getRecordLab(this).getRecord(id).getCost();
            if(x<=0){
                viewPager.setCurrentItem(0);
                prePage = 0;
            }else {
                viewPager.setCurrentItem(1);
                prePage = 1;
            }
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mImmersionBar.destroy();
    }

}
