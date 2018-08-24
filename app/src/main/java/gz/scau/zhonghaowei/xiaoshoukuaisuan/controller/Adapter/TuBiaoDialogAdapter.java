package gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Adapter;

import android.content.Context;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import gz.scau.zhonghaowei.xiaoshoukuaisuan.Model.DialogTitleRes;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.R;

public class TuBiaoDialogAdapter extends BaseQuickAdapter<DialogTitleRes,BaseViewHolder> {

    private Context context;
    private int selected;


    public TuBiaoDialogAdapter(Context context,int layoutResId, @Nullable List<DialogTitleRes> data,int selected) {
        super(layoutResId, data);
        this.context = context;
        this.selected = selected;
    }


    @Override
    protected void convert(BaseViewHolder helper, DialogTitleRes item) {
        helper.setText(R.id.dialog_text,item.getTitle());
        helper.setImageResource(R.id.diaglog_img,item.getResouce());
        if(selected!=-1){
            //设置背景色
        }
    }
}
