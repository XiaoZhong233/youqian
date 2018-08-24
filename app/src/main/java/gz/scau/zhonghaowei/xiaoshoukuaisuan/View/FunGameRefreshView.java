package gz.scau.zhonghaowei.xiaoshoukuaisuan.View;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.LinearLayout;

/**
 * Created by Hitomis on 2016/3/2.
 */
public class FunGameRefreshView extends LinearLayout implements View.OnTouchListener {
    /**
     * 下拉状态
     */
    public static final int STATUS_PULL_TO_REFRESH = 0;

    /**
     * 释放准备刷新状态
     */
    public static final int STATUS_RELEASE_TO_REFRESH = 1;

    /**
     * 正在刷新状态
     */
    public static final int STATUS_REFRESHING = 2;

    /**
     * 释放后，又按住玩游戏状态
     */
    public static final int STATUS_AGAIN_DOWN = 3;

    /**
     * 刷新完成状态
     */
    public static final int STATUS_REFRESH_FINISHED = 4;

    /**
     * 下拉拖动的黏性比率
     */
    private static final float STICK_RATIO = .65f;

    /**
     * 下拉刷新的回调接口
     */
    private FunGameRefreshListener mListener;

    /**
     * 下拉头的View
     */
    private FunGameHeader header;

    /**
     * 需要去下拉刷新的 View
     */
    private View contentView;

    /**
     * 下拉控件布局参数
     */
    private MarginLayoutParams headerLayoutParams;

    /**
     * 下拉控件高度
     */
    private int hideHeaderHeight;

    /**
     * 当前状态
     */
    private int currentStatus = STATUS_REFRESH_FINISHED;
    ;

    /**
     * 手指按下时屏幕纵坐标
     */
    private float preDownY;

    /**
     * 用于控制onLayout中的初始化只需加载一次
     */
    private boolean once;

    /**
     * 当前是否可以下拉
     */
    private boolean ableToPull;

    /**
     * 刷新子线程任务是否执行完毕
     */
    private boolean isExecComplete;

    private int tempHeaderTopMargin;

    public FunGameRefreshView(Context context) {
        this(context, null);
    }

    public FunGameRefreshView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FunGameRefreshView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (getChildCount() > 1)
            throw new RuntimeException("FunGameRefreshView can only contain one View");
        setOrientation(VERTICAL);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        header = new FunGameHeader(context, attrs);
        addView(header, 0);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed && !once) {
            hideHeaderHeight = -header.getHeight();
            headerLayoutParams = (MarginLayoutParams) header.getLayoutParams();
            headerLayoutParams.topMargin = hideHeaderHeight;
            contentView = getChildAt(1);
            contentView.setOnTouchListener(this);
            once = true;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        checkAblePull(event);
        if (!ableToPull) return false;
        if (currentStatus == STATUS_AGAIN_DOWN) {
            return handleAgainDownAction(event);
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                preDownY = event.getRawY();
                if (currentStatus == STATUS_REFRESHING) { //表示释放后处于刷新状态时候，又按住了
                    currentStatus = STATUS_AGAIN_DOWN;
                    setHeaderTopMarign(0);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float currY = event.getRawY();
                float distance = currY - preDownY;
                float offsetY = distance * STICK_RATIO;
                if (distance <= 0 && headerLayoutParams.topMargin <= hideHeaderHeight) {
                    return false;
                }

                if (headerLayoutParams.topMargin > 0) { // 头部全部被下拉出来的时候状态转换为释放刷新
                    currentStatus = STATUS_RELEASE_TO_REFRESH;
                }

                if (headerLayoutParams.topMargin > 0) {
                    currentStatus = STATUS_RELEASE_TO_REFRESH;
                } else {
                    currentStatus = STATUS_PULL_TO_REFRESH;
                }

                // 通过偏移下拉头的topMargin值，来实现下拉效果
                setHeaderTopMarign((int) (offsetY + hideHeaderHeight));

                break;
            case MotionEvent.ACTION_UP:
                if (currentStatus == STATUS_PULL_TO_REFRESH) {
                    rollbackHeader(false);
                }
                if (currentStatus == STATUS_RELEASE_TO_REFRESH) {
                    rollBack2Header(true);
                }
                break;
        }
        if (currentStatus == STATUS_PULL_TO_REFRESH || currentStatus == STATUS_RELEASE_TO_REFRESH) {
            //让 contentView 失去焦点, 不可被点击
            disableContentView();
            return true;
        }
        return false;
    }

    /**
     * 给header设置topMargin参数
     *
     * @param margin
     */
    private void setHeaderTopMarign(int margin) {
        headerLayoutParams.topMargin = margin;
        header.setLayoutParams(headerLayoutParams);
    }

    /**
     * 禁用 contentView，让其失去焦点不可接受点击
     */
    private void disableContentView() {
        contentView.setPressed(false);
        contentView.setFocusable(false);
        contentView.setFocusableInTouchMode(false);
    }

    /**
     * 处理手指第二次按住屏幕玩游戏的事件
     *
     * @param event
     * @return
     */
    private boolean handleAgainDownAction(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                currentStatus = STATUS_AGAIN_DOWN;
                preDownY = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                float currY = event.getRawY();
                float distance = currY - preDownY;
                float offsetY = distance * STICK_RATIO;
                header.moveRacket(offsetY);
                setHeaderTopMarign((int) (offsetY));
                break;
            case MotionEvent.ACTION_UP:
                currentStatus = STATUS_REFRESHING;
                if (isExecComplete) {
                    rollbackHeader(false);
                } else {
                    rollBack2Header(false);
                }
                break;
        }
        disableContentView();
        return true;
    }

    /**
     * 在onTouch中第一个执行，这样可以判断出当前应该是滚动 contentView，还是应该进行下拉。
     *
     * @param event
     */
    private void checkAblePull(MotionEvent event) {
        if (contentView != null) {
            if (!canContentViewScrollUp()) {
                if (!ableToPull) {
                    preDownY = event.getRawY();
                }
                ableToPull = true;
            } else { // 反之
                if (headerLayoutParams.topMargin != hideHeaderHeight) {
                    setHeaderTopMarign(hideHeaderHeight);
                }
                ableToPull = false;
            }
        } else {
            ableToPull = true;
        }
    }

    public boolean canContentViewScrollUp() {
        if (Build.VERSION.SDK_INT < 14) {
            if (contentView instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) contentView;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            } else {
                return ViewCompat.canScrollVertically(contentView, -1) || contentView.getScrollY() > 0;
            }
        } else {
            return ViewCompat.canScrollVertically(contentView, -1);
        }
    }

    /**
     * 给下拉刷新控件注册一个监听器。
     *
     * @param listener 监听器的实现。
     */
    public void setOnRefreshListener(FunGameRefreshListener listener) {
        mListener = listener;
    }

    /**
     * 当所有的刷新逻辑完成后，记录调用一下，否则将一直处于正在刷新状态。
     */
    public void finishRefreshing() {
        header.postComplete();
        isExecComplete = true;
        if (currentStatus != STATUS_AGAIN_DOWN) {
            rollbackHeader(true);
        }
    }

    /**
     * 回滚到头部刷新控件的高度，并触发后台刷新任务
     */
    private void rollBack2Header(boolean isRefresh) {
        ValueAnimator rbToHeaderAnimator = ValueAnimator.ofInt(headerLayoutParams.topMargin, 0);
        long duration = (long) (headerLayoutParams.topMargin * 1.1f) >= 0 ? (long) (headerLayoutParams.topMargin * 1.1f) : 0;
        rbToHeaderAnimator.setDuration(duration);
        rbToHeaderAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        rbToHeaderAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int marginValue = Integer.parseInt(animation.getAnimatedValue().toString());
                setHeaderTopMarign(marginValue);
            }
        });

        if (isRefresh)
            rbToHeaderAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    new AsyncTask<Void, Void, Void>() {

                        @Override
                        protected void onPreExecute() {
                            currentStatus = STATUS_REFRESHING;
                            header.postStart();
                        }

                        @Override
                        protected Void doInBackground(Void... params) {
                            if (mListener != null) {
                                final long minTimes = 1500;
                                long startTimes = System.currentTimeMillis();
                                mListener.onPullRefreshing();
                                long diffTimes = System.currentTimeMillis() - startTimes;
                                if (diffTimes < minTimes) {
                                    SystemClock.sleep(minTimes - diffTimes);
                                }
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            if (mListener != null)
                                mListener.onRefreshComplete();
                            finishRefreshing();
                        }
                    }.execute();
                }
            });
        header.back2StartPoint(duration);
        rbToHeaderAnimator.start();
    }


    /**
     * 回滚下拉刷新头部控件
     */
    private void rollbackHeader(boolean isDelay) {
        tempHeaderTopMargin = headerLayoutParams.topMargin;
        ValueAnimator rbAnimator = ValueAnimator.ofInt(0, header.getHeight() + tempHeaderTopMargin);
        rbAnimator.setDuration(300);
        rbAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        rbAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int marginValue = Integer.parseInt(animation.getAnimatedValue().toString());
                setHeaderTopMarign(-marginValue + tempHeaderTopMargin);
            }
        });
        rbAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (currentStatus == STATUS_PULL_TO_REFRESH || currentStatus == STATUS_REFRESH_FINISHED) {
                    currentStatus = STATUS_REFRESH_FINISHED;
                    return;
                }
                currentStatus = STATUS_REFRESH_FINISHED;
                isExecComplete = false;
                header.postEnd();
            }
        });
        if (isDelay)
            rbAnimator.setStartDelay(500);
        rbAnimator.start();
    }

    /**
     * 设置加载开始文字
     *
     * @param loadingText
     */
    public void setLoadingText(String loadingText) {
        if (TextUtils.isEmpty(loadingText)) return;
        header.setHeaderLodingStr(loadingText);
    }

    /**
     * 设置加载结束文字
     *
     * @param loadingFinishedText
     */
    public void setLoadingFinishedText(String loadingFinishedText) {
        if (TextUtils.isEmpty(loadingFinishedText)) return;
        header.setHeaderLoadingFinishedStr(loadingFinishedText);

    }

    /**
     * 设置游戏结束文字
     *
     * @param gameOverText
     */
    public void setGameOverText(String gameOverText) {
        if (TextUtils.isEmpty(gameOverText)) return;
        header.setHeaderGameOverStr(gameOverText);
    }

    /**
     * 设置上边帷幕中的文字
     *
     * @param topMaskText
     */
    public void setTopMaskText(String topMaskText) {
        if (TextUtils.isEmpty(topMaskText)) return;
        header.setTopMaskViewText(topMaskText);
    }

    /**
     * 设置下边帷幕中的文字
     *
     * @param bottomMaskText
     */
    public void setBottomMaskText(String bottomMaskText) {
        if (TextUtils.isEmpty(bottomMaskText)) return;
        header.setBottomMaskViewText(bottomMaskText);
    }

    /**
     * 下拉刷新的监听器，使用下拉刷新的地方应该注册此监听器来获取刷新回调。
     */
    public interface FunGameRefreshListener {
        /**
         * 刷新时回调方法
         */
        void onPullRefreshing();

        void onRefreshComplete();
    }
}
