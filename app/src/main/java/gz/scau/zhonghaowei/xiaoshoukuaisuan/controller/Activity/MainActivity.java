package gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Activity;

import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import gz.scau.zhonghaowei.xiaoshoukuaisuan.R;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Fragment.RecordFragment;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Fragment.RecordListFragment;

public class MainActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private final int PAGE_COUNT=3;
    public static final int RECORD_LIST=0;
    public static final int CALCULATE=1;
    public static final int OTHERS=2;
    private static final String TAG = "MainActivity";
    public static FragmentPagerAdapter fragmentPagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_layout);
        viewPager=findViewById(R.id.viewpager);
        tabLayout=findViewById(R.id.tablayout);
        final FragmentManager fm=getSupportFragmentManager();

        fragmentPagerAdapter =new FragmentPagerAdapter(fm) {

            @Override
            public Fragment getItem(int position) {
                switch (position){
                    case RECORD_LIST:
                        return RecordListFragment.newIntance();
                    case CALCULATE:
                        return RecordFragment.newIntance();
                    case OTHERS:
                        return RecordListFragment.newIntance();
                    default:
                        return RecordFragment.newIntance();
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
                    case RECORD_LIST:
                        return "记账";
                    case CALCULATE:
                        return "计算";
                    case OTHERS:
                        return "其他(待定)";
                    default:
                        return "记账";
                }
            }
        };


        viewPager.setAdapter(fragmentPagerAdapter);
        //tabLayout绑定ViewPager
        tabLayout.setupWithViewPager(viewPager);


        int backStackEntryCount = getFragmentManager().getBackStackEntryCount();
        //大于一个就回退
        Log.e(TAG, "onKeyDown: " +backStackEntryCount);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
            FragmentManager fragmentManager=getSupportFragmentManager();
            //获取回退栈中的个数
            int backStackEntryCount = fragmentManager.getBackStackEntryCount();
            //大于一个就回退
            Log.e(TAG, "onKeyDown: " +backStackEntryCount);
            while (backStackEntryCount>1) {
                if (backStackEntryCount > 1) {
                    fragmentManager.popBackStackImmediate();
                    backStackEntryCount--;
                }
            }
            finish();
        }
        return true;
    }
}
