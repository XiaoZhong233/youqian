package gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import gz.scau.zhonghaowei.xiaoshoukuaisuan.Model.DataSchema.RecordTable;


/**
 * @author Zhong.H.W
 */
public class RecordBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "recordbase.db";

    public RecordBaseHelper(Context context){
        super(context,DATABASE_NAME,null,VERSION);
    }

    /**
     * 在这个方法中创建表
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+ RecordTable.NAME + "("
                + "_id integer primary key autoincrement,"
                + RecordTable.Cols.UUID + ","
                + RecordTable.Cols.TITLE + ","
                + RecordTable.Cols.CLASSES + ","
                + RecordTable.Cols.COST + ","
                + RecordTable.Cols.DATE + ","
                + RecordTable.Cols.DETAIL +","
                + RecordTable.Cols.ZHANGHU +","
                + RecordTable.Cols.PAY_TYPE+
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
