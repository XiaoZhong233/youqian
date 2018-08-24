package gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Item;

import android.content.Context;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.Date;
import java.util.List;

import gz.scau.zhonghaowei.xiaoshoukuaisuan.Model.Record;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Adapter.ExpandableItemAdapter;

public class MonthfirstItem extends AbstractExpandableItem<Record> implements MultiItemEntity {

    private Context context;
    private Date date;
    private List<Record> records;


    public MonthfirstItem(Context context, Date date, List<Record> records) {
        this.context = context;
        this.date = date;
        this.records = records;
    }


    @Override
    public int getItemType() {
        return ExpandableItemAdapter.TYPE_LEVEL_0;
    }

    @Override
    public int getLevel() {
        return 0;
    }


    public Context getContext() {
        return context;
    }

    public Date getDate() {
        return date;
    }

    public List<Record> getRecords() {
        return records;
    }
}
