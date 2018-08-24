package gz.scau.zhonghaowei.xiaoshoukuaisuan.Model;

import android.support.annotation.DrawableRes;

public class DialogTitleRes {

    private String title;
    @DrawableRes private int resouce;

    public DialogTitleRes(String title, int resouce) {
        this.title = title;
        this.resouce = resouce;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getResouce() {
        return resouce;
    }

    public void setResouce(int resouce) {
        this.resouce = resouce;
    }
}
