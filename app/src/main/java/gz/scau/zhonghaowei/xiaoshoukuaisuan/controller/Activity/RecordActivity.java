package gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;

import java.util.UUID;

import gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Fragment.RecordFragment;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Util.SingleModeFragmentForActivity;

public class RecordActivity extends SingleModeFragmentForActivity {

    public static final String TAG = "RecordActivity";

    @Override
    public Fragment getFragment() {
        return createFragment();
    }

    @Override
    public String getTAG() {
        return TAG;
    }

    private Fragment createFragment() {
        UUID id = (UUID) getIntent().getSerializableExtra(RecordFragment.TAG);
        return RecordFragment.newIntance(id);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
            FragmentManager fragmentManager=getSupportFragmentManager();
            //获取回退栈中的个数

            int backStackEntryCount = fragmentManager.getBackStackEntryCount();
            Log.e(TAG, "onKeyDown: " +backStackEntryCount);
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