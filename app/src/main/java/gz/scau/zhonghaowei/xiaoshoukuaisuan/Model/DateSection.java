package gz.scau.zhonghaowei.xiaoshoukuaisuan.Model;

import com.chad.library.adapter.base.entity.SectionEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Util.TimeUtil;


/**
 * @author zhong
 * 包装类，用于分组列表
 */
public class DateSection extends SectionEntity<Record> {

    private Record record;
    private TimeUtil.DateType dateType;

    public DateSection(boolean isHeader, String header, TimeUtil.DateType dateType) {
        super(isHeader, header);
        this.dateType = dateType;
    }

    public DateSection(Record record, TimeUtil.DateType dateType) {
        super(record);
        this.record = record;
        this.dateType = dateType;
    }

    public Record getRecord() {
        return record;
    }

    public TimeUtil.DateType getDateType() {
        return dateType;
    }

}
