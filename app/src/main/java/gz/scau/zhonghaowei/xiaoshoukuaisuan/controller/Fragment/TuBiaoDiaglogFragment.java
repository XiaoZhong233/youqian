package gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import gz.scau.zhonghaowei.xiaoshoukuaisuan.Model.DialogTitleRes;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.R;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Adapter.TuBiaoDialogAdapter;

public class TuBiaoDiaglogFragment extends DialogFragment {

    public static final String TAG = "TuBiaoDiaglogFragment";
    private RecyclerView recyclerView;
    private TuBiaoDialogAdapter adapter;
    List<DialogTitleRes> data;
    int selected = -1;


    public static TuBiaoDiaglogFragment newIntance(int selected){
        TuBiaoDiaglogFragment fragment = new TuBiaoDiaglogFragment();
        Bundle args = new Bundle();
        args.putInt(TAG,selected);
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tubiao_diaglog,container,false);
        initView(view);
        initData();
        initAdapter();
        return view;
    }

    private void initView(View view){
        recyclerView = view.findViewById(R.id.tubiao_dialog_recyclerview);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),2);
        recyclerView.setLayoutManager(gridLayoutManager);
    }

    private void initData(){
        restoreData();
        data = new ArrayList<>();
        data.add(new DialogTitleRes("分类支出",R.mipmap.zhanghu));
        data.add(new DialogTitleRes("分类收入",R.mipmap.zhanghu));
        data.add(new DialogTitleRes("账户支出",R.mipmap.zhanghu));
        data.add(new DialogTitleRes("账户收入",R.mipmap.zhanghu));
        data.add(new DialogTitleRes("总支出",R.mipmap.zhanghu));
        data.add(new DialogTitleRes("总收入",R.mipmap.zhanghu));
    }


    private void restoreData(){
        int selected = getArguments().getInt(TAG,-1);
        if(selected!=-1){
            this.selected = selected;
        }
    }

    private void initAdapter(){

        adapter = new TuBiaoDialogAdapter(getActivity(),R.layout.tubiao_dialog_item,data,selected);

        adapter.setOnItemClickListener((adapter, view, position) -> {
            selected = position;
            sendResult(Activity.RESULT_OK,selected);
        });
        recyclerView.setAdapter(adapter);

    }

    private void sendResult(int resultCode,int selected){
        Intent intent = new Intent();
        intent.putExtra(TAG,selected);
        getTargetFragment().onActivityResult(getTargetRequestCode(),resultCode,intent);
    }

}
