package gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Adapter;

import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import gz.scau.zhonghaowei.xiaoshoukuaisuan.Model.AAItem;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.R;

public class AAAdapter extends BaseQuickAdapter<AAItem,BaseViewHolder> {

    private List<AAItem> data;
    private int count = 0;

    public AAAdapter(int layoutResId, @Nullable List<AAItem> data) {
        super(layoutResId, data);
        this.data = data;
    }

    @Override
    protected void convert(BaseViewHolder helper, AAItem item) {
        //helper.setText(R.id.name,item.getName());
        //helper.setText(R.id.price,String.valueOf(item.getPrice()));
        //注册子控件点击事件
        helper.addOnClickListener(R.id.delete);
        helper.addOnClickListener(R.id.content);
        EditText countEdit = helper.getView(R.id.price);
        EditText nameEdit = helper.getView(R.id.name);

        countEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try{
                    double c=Double.valueOf(s.toString());
                    item.setPrice(c);
                }catch (Exception e){
                    countEdit.getText().clear();
                }
            }
        });

        nameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                item.setName(s.toString());
            }
        });

    }
}
