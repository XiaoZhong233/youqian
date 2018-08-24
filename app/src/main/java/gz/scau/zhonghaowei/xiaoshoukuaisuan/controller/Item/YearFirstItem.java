package gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Item;

import android.content.Context;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;

import gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Adapter.YMRExpandableItemAdapter;

public class YearFirstItem extends AbstractExpandableItem<MonthSecendItem> implements MultiItemEntity{

    private Context context;
    private int curYear;

    public YearFirstItem(Context context,int year) {
        this.context = context;
        this.curYear = year;
    }

    @Override
    public int getLevel() {
        return 0;
    }

    @Override
    public int getItemType() {
        return YMRExpandableItemAdapter.TYPE_LEVEL_0;
    }

    public int getYear() {
        return curYear;
    }
}
