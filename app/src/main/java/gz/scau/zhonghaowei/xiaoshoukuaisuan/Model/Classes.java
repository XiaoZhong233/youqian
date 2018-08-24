package gz.scau.zhonghaowei.xiaoshoukuaisuan.Model;

public class Classes {

    private int position;
    private String label;

    public Classes(int position,String label){
        this.position = position;
        this.label = label;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
