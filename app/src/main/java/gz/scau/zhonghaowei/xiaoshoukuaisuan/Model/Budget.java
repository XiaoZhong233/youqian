package gz.scau.zhonghaowei.xiaoshoukuaisuan.Model;

import java.util.Date;

public class Budget {
    private float budget;
    private Date date;
    private int type;
    public static final int YEAR=0;
    public static final int MONTH=1;

    public Budget(float budget, int type) {
        this.budget = budget;
        this.type = type;
        date = new Date();
    }

    public float getBudget() {
        return budget;
    }

    public void setBudget(float budget) {
        this.budget = budget;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}
