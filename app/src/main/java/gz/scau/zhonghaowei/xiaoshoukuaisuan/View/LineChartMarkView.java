package gz.scau.zhonghaowei.xiaoshoukuaisuan.View;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.MPPointF;

import java.text.DecimalFormat;
import java.util.List;

import gz.scau.zhonghaowei.xiaoshoukuaisuan.Model.DataServer;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.R;

public class LineChartMarkView extends MarkerView {

    private TextView tvDate;
    private TextView tvValue_shouru;
    private TextView tvValue_zhihcu;
    private IAxisValueFormatter xAxisValueFormatter;

    public LineChartMarkView(Context context, IAxisValueFormatter xAxisValueFormatter) {
        super(context, R.layout.layout_markview);
        this.xAxisValueFormatter = xAxisValueFormatter;

        tvDate = findViewById(R.id.tv_date);
        tvValue_zhihcu = findViewById(R.id.tv_value0);
        tvValue_shouru = findViewById(R.id.tv_value1);
        tvValue_shouru.setVisibility(GONE);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        Chart chart = getChartView();
        if (chart instanceof LineChart) {
            LineData lineData = ((LineChart) chart).getLineData();
            //获取到图表中的所有曲线
            List<ILineDataSet> dataSetList = lineData.getDataSets();
            for (int i = 0; i < dataSetList.size(); i++) {
                LineDataSet dataSet = (LineDataSet) dataSetList.get(i);
                //获取到曲线的所有在Y轴的数据集合，根据当前X轴的位置 来获取对应的Y轴值
                float y;
                try {
                    //可能收入为空
                    y = dataSet.getValues().get((int) e.getX()).getY();
                }catch (Exception e1){
                    y=0f;
                }
                if (i == 0) {
                    //支出曲线
                    tvValue_zhihcu.setText("我的支出:" + "¥" +DataServer.floatToInt(y));
                }
                if (i == 1) {
                    //收入曲线
                    tvValue_shouru.setText("我的收入:" + "¥" + DataServer.floatToInt(y));
                }
            }
            tvDate.setText(xAxisValueFormatter.getFormattedValue(e.getX(), null));
        }
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }

}
