package gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Adapter;

import android.content.Context;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import gz.scau.zhonghaowei.xiaoshoukuaisuan.Model.Record;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.R;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Util.LoadIconHelper;

public class EasyRecordListAdapter extends BaseQuickAdapter<Record,BaseViewHolder>{



    public EasyRecordListAdapter(Context context,int layoutResId, List<Record> data){
        super(layoutResId,data);
        this.mContext = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, Record item) {
        helper.setText(R.id.title,item.getTitle());
        helper.setText(R.id.classes,item.getClasses());
        //helper.setImageResource(R.id.classes_img,R.drawable.ic_launcher_background);
        LoadIconHelper.loadIconByClass(mContext,item.getClasses(),helper.getView(R.id.classes_img));
        helper.setText(R.id.date,item.getDateString());
        float cost=item.getCost();
        String costText=String.valueOf(item.getCost());
        if(cost>0.0){
            costText="+"+String.valueOf(item.getCost());
        }
        helper.setText(R.id.cost,costText);
        //注册子控件点击事件
        helper.addOnClickListener(R.id.delete);
        helper.addOnClickListener(R.id.content);

    }


    /** 实现ItemTouchActionCallback接口的方法*/


}
