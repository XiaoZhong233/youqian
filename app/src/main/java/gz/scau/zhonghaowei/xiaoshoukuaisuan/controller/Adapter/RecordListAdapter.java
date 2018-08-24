package gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.UUID;

import gz.scau.zhonghaowei.xiaoshoukuaisuan.Model.Record;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.R;


public class RecordListAdapter extends RecyclerView.Adapter<RecordListAdapter.viewHolder> implements View.OnClickListener{
    private List<Record> records;
    private Context context;
    private OnItemClickListener onItemClickListener;


    /**
     * 用于列表项点击事件监听的接口
     */
    public interface OnItemClickListener{
        void onItemClick(Context context, int position, UUID id);
    }

    /**
     * 事件监听设置
     * @param onItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener=onItemClickListener;
    }

    public RecordListAdapter(Context context,List<Record> records){
        this.context=context;
        this.records=records;
    }

    class viewHolder extends RecyclerView.ViewHolder{
        TextView title,classes,date,cost;
        ImageView classes_img;
        viewHolder(View view){
            super(view);
            bindingView(view);
        }

        private void bindingView(View view){
            title=view.findViewById(R.id.title);
            classes=view.findViewById(R.id.classes);
            classes_img=view.findViewById(R.id.classes_img);
            date=view.findViewById(R.id.date);
            cost=view.findViewById(R.id.cost);

        }
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.record_list_item,parent,false);
        viewHolder viewHolder=new viewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Record record=records.get(position);
        BindingData(holder,record);
        View view=holder.itemView;
        view.setTag(position);
        //绑定事件监听
        view.setOnClickListener(this);
    }

    private void BindingData(viewHolder holder,Record record){
        holder.title.setText(record.getTitle());
        float cost=record.getCost();
        String costText=String.valueOf(record.getCost());
        if(cost>0.0){
            costText="+"+String.valueOf(record.getCost());
        }
        holder.cost.setText(costText);
        holder.date.setText(record.getDateString());
        holder.classes.setText(record.getClasses());

    }


    /**
     * 监听回调
     * @param v
     */
    @Override
    public void onClick(View v) {
        if(onItemClickListener!=null){
            int position=(int)v.getTag();
            UUID id=records.get(position).getId();
            onItemClickListener.onItemClick(context,position,id);
        }
    }

    @Override
    public int getItemCount() {
        return records.size();
    }
}
