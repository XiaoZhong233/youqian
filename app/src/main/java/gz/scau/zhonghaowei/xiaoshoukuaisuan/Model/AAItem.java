package gz.scau.zhonghaowei.xiaoshoukuaisuan.Model;

import java.math.BigDecimal;

public class AAItem {

    private String name;
    private double price;
    public static int count = 0;
    private   final int id = count++;

    public AAItem(String name, double price) {
        this.name = name;
        this.price = price;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getId() {
        return id;
    }
}
