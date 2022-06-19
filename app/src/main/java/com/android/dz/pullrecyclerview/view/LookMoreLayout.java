package com.android.dz.pullrecyclerview.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.view.NestedScrollingParent;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;


/**
 * 给RecyclerView提供伸缩效果
 *
 * @author Wudongze
 */
public class LookMoreLayout extends LinearLayout implements NestedScrollingParent {
    private static final String TAG = "DZStickyNavLayouts";
    private NestedScrollingParentHelper mParentHelper;
    private View mHeaderView;
    private AnimatorView mFooterView;
    private RecyclerView mChildView;
    // 解决多点触控问题
    private boolean isRunAnim;
    public static int maxWidth = 0;
    private static final int DRAG = 1;
    private boolean needShowMore = true;
    public static final float S_VISIBLE_PERCENT = 0.8f;

    public interface OnLookMoreListener {
        /**
         * 执行查看更多的回调方法
         */
        void onStart();
    }

    private OnLookMoreListener mLinster;

    public void setOnLookMoreListener(OnLookMoreListener l) {
        mLinster = l;
    }

    public void setNeedShowMore(boolean needShowMore) {
        this.needShowMore = needShowMore;
    }

    public LookMoreLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mHeaderView = new View(context);
        mHeaderView.setBackgroundColor(0xffFFFFFF);
        mFooterView = new AnimatorView(context);
        mFooterView.setBackgroundColor(0xffFFFFFF);
        maxWidth = dp2Px(context, 48);
        mParentHelper = new NestedScrollingParentHelper(this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setOrientation(LinearLayout.HORIZONTAL);

        if (getChildAt(0) instanceof RecyclerView) {
            mChildView = (RecyclerView) getChildAt(0);
            LayoutParams layoutParams = new LayoutParams(maxWidth, LayoutParams.MATCH_PARENT);
            addView(mHeaderView, 0, layoutParams);
            addView(mFooterView, getChildCount(), layoutParams);
            // 左移
            scrollBy(maxWidth, 0);

            mChildView.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // 保证动画状态中 子view不能滑动
                    return isRunAnim;
                }
            });
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mChildView != null) {
            ViewGroup.LayoutParams params = mChildView.getLayoutParams();
            params.width = getMeasuredWidth();
        }
    }

    /**
     * 必须要复写 onStartNestedScroll后调用
     */
    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
        Log.i(TAG, "onNestedScrollAccepted: child=" + child + ",target=" + target + ",axes=" + axes);
        mParentHelper.onNestedScrollAccepted(child, target, axes);
    }

    /**
     * down事件,内部开始滚动之前,外部View准备开始滚动
     * 返回true代表处理本次事件
     * 在执行动画时间里不能处理本次事件
     */
    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        Log.i(TAG, "onStartNestedScroll: ");
        return target instanceof RecyclerView && !isRunAnim && needShowMore;
    }

    /**
     * up事件,当手指抬起时,内部滚动事件,通知外部控件停止滚动
     * 复位初始位置
     * scrollTo 移动到指定坐标
     * scrollBy 在原有坐标上面移动
     */
    @Override
    public void onStopNestedScroll(View target) {
        mParentHelper.onStopNestedScroll(target);
        Log.i(TAG, "onStopNestedScroll: getScrollX()=" + getScrollX());
        // 如果不在RecyclerView滑动范围内
        if (maxWidth != getScrollX()) {
            startAnimation(new ProgressAnimation());
        }
        //当达到指定的区域,放松手指时,触发回调
        if (getScrollX() > maxWidth + maxWidth * S_VISIBLE_PERCENT && mLinster != null) {
            mLinster.onStart();
        }
    }

    /**
     * 回弹动画
     */
    private class ProgressAnimation extends Animation {

        private ProgressAnimation() {
            isRunAnim = true;
        }

        /**
         * 手指松开时,需要将外部View还原.
         * 距离是getScrollX()是已经滑动的距离
         *
         * @param interpolatedTime 0-1 将时间转换为进度的参数
         * @param t                转换算法
         */
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            scrollBy((int) ((maxWidth - getScrollX()) * interpolatedTime), 0);
            //动画执行完毕时,将动画标志置为false,同时调用一个底部View的释放操作
            if (interpolatedTime == 1) {
                isRunAnim = false;
                mFooterView.setRelease();
            }
        }

        @Override
        public void initialize(int width, int height, int parentWidth, int parentHeight) {
            super.initialize(width, height, parentWidth, parentHeight);
            setDuration(300);
            setInterpolator(new AccelerateInterpolator());
        }
    }

    /**
     * 内部View在滑动时,通知外部View是否跟随处理这次滑动
     */
    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {

    }

    /**
     * 内部View在滑动之前通知外部View是否处理这次滑动的距离,外部View处理之后,才交给内部View处理
     *
     * @param dx       水平滑动距离
     * @param dy       垂直滑动距离
     * @param consumed 父类消耗掉的距离
     */
    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed) {
        Log.i(TAG, "onNestedPreScroll:maxWidth= " + maxWidth + ",dx=" + dx + ",dy=" + dy + ",consumed=" + consumed);
        getParent().requestDisallowInterceptTouchEvent(true);
        Log.i(TAG, "onNestedPreScroll: getScrollX()" + getScrollX() + ",can lh =" + target.canScrollHorizontally(-1));
        Log.i(TAG, "onNestedPreScroll: getScrollX()" + getScrollX() + ",can rh =" + target.canScrollHorizontally(1));
        // dx>0 往左滑动 dx<0往右滑动
        boolean hiddenLeft = dx > 0 && getScrollX() < maxWidth && !target.canScrollHorizontally(-1);
        //当手指往右滑动时,并且内部的RV不能往左滑动时,就显示左边的View
        boolean showLeft = dx < 0 && !target.canScrollHorizontally(-1);
        boolean hiddenRight = dx < 0 && getScrollX() > maxWidth && !target.canScrollHorizontally(1);
        boolean showRight = dx > 0 && !target.canScrollHorizontally(1);
        if (showLeft) {
            scrollTo(maxWidth, 0);
            return;
        }
        if (hiddenLeft || hiddenRight || showRight) {
            scrollBy(dx / DRAG, 0);
            consumed[0] = dx;
        }

        if (hiddenRight || showRight) {
            mFooterView.setRefresh(dx / DRAG);
        }

        // 限制错位问题
        if (dx > 0 && getScrollX() > maxWidth && !ViewCompat.canScrollHorizontally(target, -1)) {
            scrollTo(maxWidth, 0);
        }
        if (dx < 0 && getScrollX() < maxWidth && !ViewCompat.canScrollHorizontally(target, 1)) {
            scrollTo(maxWidth, 0);
        }
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        Log.i(TAG, "onNestedFling: ");
        return false;
    }

    /**
     * 子view是否可以有惯性 解决右滑时快速左滑显示错位问题
     *
     * @return true不可以  false可以
     */
    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        Log.i(TAG, "onNestedPreFling: ");
        // 当RecyclerView在界面之内交给它自己惯性滑动
        return getScrollX() != maxWidth;
    }

    @Override
    public int getNestedScrollAxes() {
        return 0;
    }

    /**
     * 限制滑动 移动x轴不能超出最大范围
     */
    @Override
    public void scrollTo(int x, int y) {
        if (x < 0) {
            x = 0;
        } else if (x > maxWidth * 2) {
            x = maxWidth * 2;
        }
        super.scrollTo(x, y);
    }

    private int dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
