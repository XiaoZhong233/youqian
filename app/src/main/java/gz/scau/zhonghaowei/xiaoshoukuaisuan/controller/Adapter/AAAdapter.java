package gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Adapter;

import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
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

            }
        });

        countEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    String s = countEdit.getText().toString();
                    try{
                        double c=Double.valueOf(s.toString());
                        item.setPrice(c);
                    }
                    catch (Exception e1){
                        try{
                            String ss = countEdit.getText().toString();
                            String[] counts = ss.split(",+");
                            double c = 0d;
                            if(counts.length>0){
                                for(int i =0;i<counts.length;i++){
                                    c += Double.valueOf(counts[i]);
                                }
                                item.setPrice(c);
                                countEdit.setText(String.valueOf(c));
                            }
                        }catch (Exception e2){
                            try {
                                String ss = countEdit.getText().toString();
                                String[] counts = ss.split("\\s+");
                                double c = 0d;
                                if(counts.length>0){
                                    for(int i =0;i<counts.length;i++){
                                        c += Double.valueOf(counts[i]);
                                    }
                                    item.setPrice(c);
                                    countEdit.setText(String.valueOf(c));
                                }
                            }catch (Exception e3){
                                try {
                                    String ss = countEdit.getText().toString();
                                    String[] counts = ss.split("，+");
                                    double c = 0d;
                                    if(counts.length>0){
                                        for(int i =0;i<counts.length;i++){
                                            c += Double.valueOf(counts[i]);
                                        }
                                        item.setPrice(c);
                                        countEdit.setText(String.valueOf(c));
                                    }
                                }catch (Exception e4){
                                    countEdit.getText().clear();
                                }

                            }
                        }

                    }
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
