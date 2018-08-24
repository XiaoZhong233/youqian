package gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import com.gyf.barlibrary.ImmersionBar;

import gz.scau.zhonghaowei.xiaoshoukuaisuan.R;

public  class BaseFragment extends Fragment {

    protected ImmersionBar mImmersionBar;

    private static final String TAG = "BaseFragment";
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (isImmersionBarEnabled())
            Log.e(TAG, "onViewCreated: " );
            initImmersionBar();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.e(TAG, "onHiddenChanged: " );
        if (!hidden && mImmersionBar != null)
            mImmersionBar.init();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mImmersionBar != null)
            mImmersionBar.destroy();
    }


    /**
     * 是否使用沉浸式
     * 默认使用
     * @return the boolean
     */
    protected boolean isImmersionBarEnabled() {
        return true;
    }


    /**
     * 初始化沉浸式
     */
    protected void initImmersionBar() {
        mImmersionBar = ImmersionBar.with(this);
        mImmersionBar.keyboardEnable(false) //keyboardEnable默认为false,这样就不会弹到编辑框了
                .navigationBarWithKitkatEnable(false)
                .statusBarColor(R.color.colorPrimary)
                .fitsSystemWindows(true)    //解决状态栏和布局的重叠问题
                .init();
    }



}
