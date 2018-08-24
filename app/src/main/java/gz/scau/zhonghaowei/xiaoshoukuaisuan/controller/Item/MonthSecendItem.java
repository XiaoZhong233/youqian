package gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Item;

import android.content.Context;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.Date;
import java.util.List;

import gz.scau.zhonghaowei.xiaoshoukuaisuan.Model.Record;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Adapter.YMRExpandableItemAdapter;

public class MonthSecendItem extends AbstractExpandableItem<Record> implements MultiItemEntity {

    private Context context;
    private int month;
    private List<Record> data;

    public MonthSecendItem(Context context,int month,List<Record> data) {
        this.context = context;
        this.month = month;
        this.data = data;
    }

    public List<Record> getData() {
        return data;
    }

    @Override
    public int getLevel() {
        return 1;
    }

    @Override
    public int getItemType() {
        return YMRExpandableItemAdapter.TYPE_LEVEL_1;
    }

    public int getMonth() {
        return month;
    }
}
