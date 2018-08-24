package gz.scau.zhonghaowei.xiaoshoukuaisuan.View;



import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import gz.scau.zhonghaowei.xiaoshoukuaisuan.R;


/**
 * Created by Hitomis on 2016/3/1.
 */
public class FunGameHeader extends FrameLayout {

    private Context mContext;

    private int headerType;

    private FunGameView funGameView;

    private RelativeLayout curtainReLayout, maskReLayout;

    private TextView topMaskView, bottomMaskView;

    private int halfHitBlockHeight;

    private boolean isStart = false;
    
    private String topMaskViewText = "Pull To Break Out!";
    private String bottomMaskViewText = "Scrooll to move handle";
    private String loadingText = "Loading...";
    private String loadingFinishedText = "Loading Finished";
    private String gameOverText = "Game Over";

    private int topMaskTextSize = 16;

    private int bottomMaskTextSize = 16;

    public FunGameHeader(Context context) {
        this(context, null);
    }

    public FunGameHeader(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FunGameHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FunGameHeader);

        headerType = typedArray.getInt(R.styleable.FunGameHeader_game_type, FunGameFactory.HITBLOCK);

        String topText = typedArray.getString(R.styleable.FunGameHeader_mask_top_text);
        topMaskViewText = topText == null ? topMaskViewText : topText;

        String bottomText = typedArray.getString(R.styleable.FunGameHeader_mask_bottom_text);
        bottomMaskViewText = bottomText == null ? bottomMaskViewText : bottomText;

        String loadingStr = typedArray.getString(R.styleable.FunGameHeader_text_loading);
        loadingText = loadingStr == null ? loadingText : loadingStr;

        String loadingFinishedStr = typedArray.getString(R.styleable.FunGameHeader_text_loading_finished);
        loadingFinishedText = loadingFinishedStr == null ? loadingFinishedText : loadingFinishedStr;

        String gameOverStr = typedArray.getString(R.styleable.FunGameHeader_text_game_over);
        gameOverText = gameOverStr == null ? gameOverText : gameOverStr;

        topMaskTextSize = typedArray.getInt(R.styleable.FunGameHeader_top_text_size, topMaskTextSize);
        bottomMaskTextSize = typedArray.getInt(R.styleable.FunGameHeader_bottom_text_size, bottomMaskTextSize);
        
        typedArray.recycle();

        initView(attrs);
    }

    private void initView(AttributeSet attrs) {
        funGameView = FunGameFactory.createFunGameView(mContext, attrs, headerType);
        setHeaderLodingStr(loadingText);
        setHeaderLoadingFinishedStr(loadingFinishedText);
        setHeaderGameOverStr(gameOverText);
        funGameView.postStatus(FunGameView.STATUS_GAME_PREPAR);
        addView(funGameView);

        curtainReLayout = new RelativeLayout(mContext);
        maskReLayout = new RelativeLayout(mContext);
        maskReLayout.setBackgroundColor(Color.parseColor("#3A3A3A"));

        topMaskView = createMaskTextView(topMaskViewText, topMaskTextSize, Gravity.BOTTOM);
        bottomMaskView = createMaskTextView(bottomMaskViewText, bottomMaskTextSize, Gravity.TOP);

        coverMaskView();

        funGameView.getViewTreeObserver().addOnGlobalLayoutListener(new MeasureListener());
    }

    private TextView createMaskTextView(String text, int textSize, int gravity) {
        TextView maskTextView = new TextView(mContext);
        maskTextView.setTextColor(Color.BLACK);
        maskTextView.setBackgroundColor(Color.WHITE);
        maskTextView.setGravity(gravity | Gravity.CENTER_HORIZONTAL);
        maskTextView.setTextSize(textSize);
        maskTextView.setText(text);
        return maskTextView;
    }

    private void coverMaskView() {
        LayoutParams maskLp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        maskLp.topMargin = (int) FunGameView.DIVIDING_LINE_SIZE;
        maskLp.bottomMargin = (int) FunGameView.DIVIDING_LINE_SIZE;

        addView(maskReLayout, maskLp);
        addView(curtainReLayout, maskLp);
    }

    private class MeasureListener implements ViewTreeObserver.OnGlobalLayoutListener {

        @Override
        public void onGlobalLayout() {
            halfHitBlockHeight = (int) ((funGameView.getHeight() - 2 * FunGameView.DIVIDING_LINE_SIZE) * .5f);
            RelativeLayout.LayoutParams topRelayLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, halfHitBlockHeight);
            RelativeLayout.LayoutParams bottomRelayLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, halfHitBlockHeight);
            bottomRelayLayoutParams.topMargin = halfHitBlockHeight;

            curtainReLayout.removeAllViews();
            curtainReLayout.addView(topMaskView, 0, topRelayLayoutParams);
            curtainReLayout.addView(bottomMaskView, 1, bottomRelayLayoutParams);

            getViewTreeObserver().removeGlobalOnLayoutListener(this);
        }
    }

    private void doStart(long delay) {
        ObjectAnimator topMaskAnimator = ObjectAnimator.ofFloat(topMaskView, "translationY", topMaskView.getTranslationY(), -halfHitBlockHeight);
        ObjectAnimator bottomMaskAnimator = ObjectAnimator.ofFloat(bottomMaskView, "translationY", bottomMaskView.getTranslationY(), halfHitBlockHeight);
        ObjectAnimator maskShadowAnimator = ObjectAnimator.ofFloat(maskReLayout, "alpha", maskReLayout.getAlpha(), 0);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(topMaskAnimator).with(bottomMaskAnimator).with(maskShadowAnimator);
        animatorSet.setDuration(800);
        animatorSet.setStartDelay(delay);
        animatorSet.start();

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                topMaskView.setVisibility(View.GONE);
                bottomMaskView.setVisibility(View.GONE);
                maskReLayout.setVisibility(View.GONE);

                funGameView.postStatus(FunGameView.STATUS_GAME_PLAY);
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int width = 0, height = 0;

        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View childView = getChildAt(i);
            measureChild(childView, widthMeasureSpec, heightMeasureSpec);
            if (childView instanceof FunGameView) {
                width = childView.getMeasuredWidth();
                height = childView.getMeasuredHeight();
            }
        }

        if (heightMode == MeasureSpec.UNSPECIFIED || heightMode == MeasureSpec.EXACTLY) {
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void postStart() {
        if (!isStart) {
            doStart(200);
            isStart = true;
        }
    }

    public void postEnd() {
        isStart = false;
        funGameView.postStatus(FunGameView.STATUS_GAME_PREPAR);

        topMaskView.setTranslationY(topMaskView.getTranslationY() + halfHitBlockHeight);
        bottomMaskView.setTranslationY(bottomMaskView.getTranslationY() - halfHitBlockHeight);
        maskReLayout.setAlpha(1.f);

        topMaskView.setVisibility(View.VISIBLE);
        bottomMaskView.setVisibility(View.VISIBLE);
        maskReLayout.setVisibility(View.VISIBLE);
    }

    public void postComplete() {
        funGameView.postStatus(FunGameView.STATUS_GAME_FINISHED);
    }

    public void moveRacket(float distance) {
        if (isStart)
            funGameView.moveController(distance);
    }

    public void back2StartPoint(long duration) {
        funGameView.moveController2StartPoint(duration);
    }

    public int getGameStatus() {
        return funGameView.getCurrStatus();
    }

    public void setTopMaskViewText(String topMaskViewText) {
        this.topMaskViewText = topMaskViewText;
        topMaskView.setText(topMaskViewText);
    }

    public void setBottomMaskViewText(String bottomMaskViewText) {
        this.bottomMaskViewText = bottomMaskViewText;
        bottomMaskView.setText(bottomMaskViewText);
    }

    public void setHeaderLodingStr(String loadingStr) {
        funGameView.setTextLoading(loadingStr);
    }

    public void setHeaderGameOverStr(String gameOverStr) {
        funGameView.setTextGameOver(gameOverStr);
    }

    public void setHeaderLoadingFinishedStr(String loadingFinishedStr) {
        funGameView.setTextLoadingFinished(loadingFinishedStr);
    }

}
