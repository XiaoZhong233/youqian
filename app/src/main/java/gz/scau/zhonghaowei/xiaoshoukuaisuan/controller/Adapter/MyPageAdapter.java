package gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Adapter;

import android.app.Fragment;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;

import java.util.List;

public class MyPageAdapter extends PagerAdapter {
    private Context context;
    private List<Fragment> fragments;

    public MyPageAdapter(Context context,List<Fragment> fragments){
        this.context = context;
        this.fragments = fragments;
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return false;
    }
}
