package gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import gz.scau.zhonghaowei.xiaoshoukuaisuan.R;

public class QiTaListAdapter extends RecyclerView.Adapter<QiTaListAdapter.myHolder> implements View.OnClickListener {

    private Context context;
    private List<String> data;
    private OnItemClickListener onItemClickListener;

    public QiTaListAdapter(Context context,List<String> data) {
        this.context = context;
        this.data = data;
    }

    public interface OnItemClickListener{
        void onItemClick(Context context, int position);
    }


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public class myHolder extends RecyclerView.ViewHolder{
        private TextView textView;

        public myHolder(View itemView) {
            super(itemView);
            bindingData(itemView);
        }

        private void bindingData(View view){
            textView = view.findViewById(R.id.text);
        }
    }


    @NonNull
    @Override
    public myHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.qita_item,parent,false);
        myHolder holder = new myHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull myHolder holder, int position) {
        String s = data.get(position);
        holder.textView.setText(s);
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onClick(View v) {
        if(onItemClickListener!=null){
            int position = (int)v.getTag();
            onItemClickListener.onItemClick(context,position);
        }
    }
}
