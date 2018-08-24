package gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;

import gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Fragment.RecordListFragment;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Util.SingleModeFragmentForActivity;

public class RecordListActivity extends SingleModeFragmentForActivity{

    public static final String TAG = "RecordListActivity";

    @Override
    public Fragment getFragment() {
        return RecordListFragment.newIntance();
    }

    @Override
    public String getTAG() {
        return TAG;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
            FragmentManager fragmentManager=getSupportFragmentManager();
            //获取回退栈中的个数
            int backStackEntryCount = fragmentManager.getBackStackEntryCount();
            //大于一个就回退
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
