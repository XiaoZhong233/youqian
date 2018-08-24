package gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import java.util.Date;
import java.util.GregorianCalendar;

import gz.scau.zhonghaowei.xiaoshoukuaisuan.R;


public class DatePickerFragment extends DialogFragment {

    public static final String TAG = "DatePickerFragment_key";
    private DatePicker datePicker;

    public static DatePickerFragment newIntance(Date date){
        DatePickerFragment dialogFragment = new DatePickerFragment();
        Bundle args = new Bundle();
        args.putSerializable(TAG,date);
        dialogFragment.setArguments(args);
        return dialogFragment;
    }



    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.e(TAG, "onCreateDialog: here" );
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.date_picker_layout,null);
        restoreDate(view);
        return new AlertDialog.Builder(getActivity()).setView(view).setTitle("请选择日期").setPositiveButton(android.R.string.ok
                , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int year = datePicker.getYear();
                        int mouth = datePicker.getMonth();
                        int day = datePicker.getDayOfMonth();
                        Date date = new GregorianCalendar(year,mouth,day).getTime();
                        sendResult(Activity.RESULT_OK,date);
                    }
                }).create();
    }


    private void  restoreDate(View view){
        Date date=(Date)getArguments().getSerializable(TAG);
        if(date!=null){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            Log.e(TAG, "restoreDate: "+date.toString() );
            datePicker = view.findViewById(R.id.date_picker);
            datePicker.init(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH),null);
        }
    }


    private void sendResult(int resultCode,Date date){
        Intent intent = new Intent();
        intent.putExtra(TAG,date);
        getTargetFragment().onActivityResult(getTargetRequestCode(),resultCode,intent);
    }
}
