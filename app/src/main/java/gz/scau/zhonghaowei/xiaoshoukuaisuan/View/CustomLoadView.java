package gz.scau.zhonghaowei.xiaoshoukuaisuan.View;

import com.chad.library.adapter.base.loadmore.LoadMoreView;

import gz.scau.zhonghaowei.xiaoshoukuaisuan.R;

public class CustomLoadView extends LoadMoreView {


    @Override
    public int getLayoutId() {
        return R.layout.load_more_view;
    }

    @Override
    protected int getLoadingViewId() {
        return R.id.load_more_loading_view;
    }

    @Override
    protected int getLoadFailViewId() {
        return R.id.load_more_load_fail_view;
    }

    @Override
    protected int getLoadEndViewId() {
        return R.id.load_more_load_end_view;
    }
}
