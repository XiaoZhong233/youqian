package gz.scau.zhonghaowei.xiaoshoukuaisuan.View;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;

import gz.scau.zhonghaowei.xiaoshoukuaisuan.R;

public class BeatTextView extends LinearLayout {
    private TextView first;
    private ScrollTextView left;
    private ScrollTextView right;
    private TextView split;
    private int currentSwipeType;
    private static final String TAG = "BeatTextView";
    private boolean enable;

    public BeatTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.beat_textview,this);
        first = findViewById(R.id.first);
        left = findViewById(R.id.left);
        right = findViewById(R.id.right);
        split = findViewById(R.id.split);
        //默认快速滑动
        currentSwipeType = ScrollTextView.FIRSTF_FIRST;
        enable=true;
    }


    /**
     * 设置开头标志
     * @param s
     */
    public void setFirstText(String s){
        first.setText(s);
    }

    private void setText(float num){
        String[] floatString = floatToInt(num);
        left.setText(String.valueOf(floatString[0]));
        right.setText(String.valueOf(floatString[1]));
    }

    /**
     * 设置滚动类型
     * @param swipeType
     * @throws Exception
     */
    public void setPianyilian(int swipeType) throws Exception {
        if(swipeType<=3) {
            this.currentSwipeType = swipeType;
        }else {
            Log.e(TAG, "setPianyilian: swipeType error " );
            throw new Exception("swipeType error");
        }

    }

    /**
     * 开始滚动
     * @param num
     */
    public void startScroll(float num){
        setText(num);
        if(enable) {
            left.setPianyilian(currentSwipeType);
            right.setPianyilian(currentSwipeType);
            left.start();
            right.start();
        }
    }


    public void setTextSize(float size){
        first.setTextSize(size);
        left.setTextSize(size);
        split.setTextSize(size);
        right.setTextSize(size);
    }

    public void setTextColor(@ColorInt int color){
      first.setTextColor(color);
      left.setTextColor(color);
      split.setTextColor(color);
      right.setTextColor(color);
    }

    /**
     * 设置是否可以滚动，默认为真
     * @param enable
     */
    public void setScrollEnable(boolean enable) {
        this.enable = enable;
    }

    private String[] floatToInt(float num){
        num=Math.abs(num);
        DecimalFormat decimalFormat=new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
        String p = decimalFormat.format(num);
        return p.split("\\.");
    }
}
