package gz.scau.zhonghaowei.xiaoshoukuaisuan.Model;

public class DataSchema {
    //数据库信息类
    public static final class RecordTable{
        //表名
        public static final String NAME = "records";
        //数据名
        public static final class Cols{
            public static final String UUID = "uuid";
            public static final String COST = "cost";
            public static final String DATE = "date";
            public static final String CLASSES = "classes";
            public static final String DETAIL = "detail";
            public static final String TITLE = "title";
            public static final String ZHANGHU = "zhanghu";
            public static final String PAY_TYPE = "type";
        }
    }

}
