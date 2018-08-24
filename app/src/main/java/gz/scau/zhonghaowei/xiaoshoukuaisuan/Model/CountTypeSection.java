package gz.scau.zhonghaowei.xiaoshoukuaisuan.Model;

import com.chad.library.adapter.base.entity.SectionEntity;

import gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Util.TimeUtil;

public class CountTypeSection extends SectionEntity<String> {
    private String countType = "null";
    private String count_total = "null";
    private String payType = "null";
    private String payTotal = "null";
    private String liuru = "null";
    private String liuchu = "null";


    public CountTypeSection(boolean isHeader, String countType,String count,String liuru,String liuchu) {
        super(isHeader, countType);
        this.countType = countType;
        count_total = count;
        this.liuru = liuru;
        this.liuchu = liuchu;
    }

    public CountTypeSection(String payType,String payTotal) {
        super(payType);
        this.payType = payType;
        this.payTotal = payTotal;
    }


    public String getCountType() {
        return countType;
    }

    public String getCount_total() {
        return count_total;
    }

    public String getPayType() {
        return payType;
    }

    public String getPayTotal() {
        return payTotal;
    }

    public String getLiuru() {
        return liuru;
    }

    public String getLiuchu() {
        return liuchu;
    }
}
