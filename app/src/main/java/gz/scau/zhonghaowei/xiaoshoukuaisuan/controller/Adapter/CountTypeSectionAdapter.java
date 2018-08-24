package gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Adapter;

import android.content.Context;

import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import gz.scau.zhonghaowei.xiaoshoukuaisuan.Model.CountTypeSection;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.R;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Util.LoadIconHelper;


public class CountTypeSectionAdapter extends BaseSectionQuickAdapter<CountTypeSection,BaseViewHolder> {

    private static final String TAG = "CountTypeSectionAdapter";
    private Context context;

    public CountTypeSectionAdapter(Context context,int layoutResId, int sectionHeadResId, List<CountTypeSection> data) {
        super(layoutResId, sectionHeadResId, data);
        this.context = context;
    }


    /**
     * 加载头布局
     * @param helper
     * @param item
     */
    @Override
    protected void convertHead(BaseViewHolder helper, CountTypeSection item) {
        helper.setText(R.id.count_type,item.header);
        helper.setText(R.id.count,item.getCount_total());
        helper.setText(R.id.liuru,item.getLiuru());
        helper.setText(R.id.liuchu,item.getLiuchu());
    }


    /**
     * 加载子项
     * @param helper
     * @param item
     */
    @Override
    protected void convert(BaseViewHolder helper, CountTypeSection item) {
        helper.setText(R.id.pay_type,item.getPayType());
        helper.setText(R.id.count_num,item.getPayTotal());
        LoadIconHelper.loadIconByClass(context,item.getPayType(),helper.getView(R.id.count_type_img));
    }
}
