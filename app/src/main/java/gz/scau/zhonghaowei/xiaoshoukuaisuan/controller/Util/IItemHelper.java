package gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Util;


/**
 * ItemHelper与Adapter 适配接口
 * 使用方法，让Adapter实现此接口,在CallBack中调用
 */
public interface IItemHelper {
    void onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);
}
