package com.android.dz.pullrecyclerview.view;

import static com.android.dz.pullrecyclerview.view.LookMoreLayout.S_VISIBLE_PERCENT;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.android.dz.pullrecyclerview.R;

public class AnimatorView extends RelativeLayout {

    private View mView;
    private LinearLayout mLayout;
    private TextView mTextView;
    private ImageView mIvArrow;
    private int mMove;
    private Path mPath;
    private Paint mBackPaint;
    private int mHeight;
    private int mLayoutHeight;
    private int mLayoutWidth;
    private boolean isRunAnim;

    public AnimatorView(Context context) {
        super(context);
        mPath = new Path();
        mBackPaint = new Paint();
        mBackPaint.setAntiAlias(true);
        mBackPaint.setStyle(Paint.Style.FILL);
        mBackPaint.setColor(0xffF3F3F3);

        mView = View.inflate(context, R.layout.animator_hot, null);
        mLayout = (LinearLayout) mView.findViewById(R.id.animator_ll);
        mTextView = (TextView) mView.findViewById(R.id.animator_text);
        mIvArrow = mView.findViewById(R.id.iv_arrow);
        LayoutParams params = new LayoutParams(-1, -1);
        addView(mView, params);
    }

    public AnimatorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AnimatorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mHeight = getHeight();
        mLayoutHeight = mLayout.getHeight();
        mLayoutWidth = mLayout.getWidth();
    }

    public void setRefresh(int width) {
        mMove += width;
        if (mMove < 0) {
            mMove = 0;
        } else if (mMove > LookMoreLayout.maxWidth) {
            mMove = LookMoreLayout.maxWidth;
        }
//        mView.getLayoutParams().width = mMove;
        mView.getLayoutParams().height = LinearLayout.LayoutParams.MATCH_PARENT;

        if (mMove > LookMoreLayout.maxWidth * S_VISIBLE_PERCENT) {
            mTextView.setText("释放查看");
            if (!isRunAnim) {
                mIvArrow.animate().rotation(180f)
                        .setDuration(100)
                        .withEndAction(() -> isRunAnim = false)
                        .start();
                isRunAnim = true;
            }
        } else {
            mTextView.setText("查看更多");
            if (!isRunAnim) {
                mIvArrow.animate().rotation(0f)
                        .setDuration(100)
                        .withEndAction(() -> isRunAnim = false)
                        .start();
                isRunAnim = true;
            }
        }
        requestLayout();
    }

    public void setRelease() {
        mMove = 0;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        mPath.reset();
//        float marginTop = (mHeight - mLayoutHeight) >> 1;
//        // 右上角x坐标、右上角y坐标
//        mPath.moveTo(mMove - mLayoutWidth, marginTop);
//        // 左边弧形x坐标、左边弧形y坐标、右下角x坐标、右下角y坐标
//        mPath.quadTo(0,  mHeight / 2, mMove - mLayoutWidth, mLayoutHeight + marginTop);
//        canvas.drawPath(mPath, mBackPaint);
    }
}
