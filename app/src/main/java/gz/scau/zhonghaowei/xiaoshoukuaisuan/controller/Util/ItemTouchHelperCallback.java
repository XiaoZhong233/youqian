package gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Util;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;


/**
 * 回调类
 * 使用方法，将该实例与recyclerView绑定即可
 * eg.
 *  ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelperCallback());
    mItemTouchHelper.attachToRecyclerView(mRecyclerView);
 */

public class ItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private IItemHelper itemHelperAadapter;

    public ItemTouchHelperCallback(IItemHelper itemHelperAadapter) {
        this.itemHelperAadapter = itemHelperAadapter;
    }

    /**
     * 设置支持的拖拽或滑动方向
     * @param recyclerView
     * @param viewHolder
     * @return
     */
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        //final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        //默认不处理是0
        final int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(0, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        //回调
        itemHelperAadapter.onItemMove(viewHolder.getAdapterPosition(),target.getAdapterPosition());
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        //回调
        itemHelperAadapter.onItemDismiss(viewHolder.getAdapterPosition());
    }
}
