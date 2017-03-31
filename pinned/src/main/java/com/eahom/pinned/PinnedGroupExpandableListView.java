package com.eahom.pinned;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.ExpandableListView;

/**
 * Created by eahom on 17/1/3.
 */
public class PinnedGroupExpandableListView extends ExpandableListView {

    private final int MAX_DIVIDER_HEIGHT = 3;

    private int mTouchSlop;
    private int mTranslateY;
    private PinnedGroup mPinnedGroup;
    private PinnedGroup mRecyclerGroup;
    /**
     * The divider height must be not beyond {@link #MAX_DIVIDER_HEIGHT}
     * The function init adjust the divider height if it beyond the {@link #MAX_DIVIDER_HEIGHT}.
     */
    private int mDividerHeight;
    private Drawable mPinnedGroupDivider;
    private final Rect mTouchRect = new Rect();
    private boolean isDownPointInPinnedGroup = false;

    private OnScrollListener mDelegateScrollListener;

    private final OnScrollListener mOnScrollListener = new OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (mDelegateScrollListener != null)
                mDelegateScrollListener.onScrollStateChanged(view, scrollState);
        }

        @Override
        public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (mDelegateScrollListener != null)
                mDelegateScrollListener.onScroll(absListView, firstVisibleItem, visibleItemCount, totalItemCount);

            int firstVisibleGroupPosition = getPackedPositionGroup(getExpandableListPosition(firstVisibleItem));
            int nextGroupPosition = getPackedPositionGroup(getExpandableListPosition(firstVisibleItem + 1));

            if (firstVisibleGroupPosition >= 0) {
                refreshPinnedGroup(firstVisibleGroupPosition);
            }
            else
                destroyPinnedGroup();

            if (nextGroupPosition != firstVisibleGroupPosition && mPinnedGroup != null) {
                View nextGroupView = getChildAt(1);
                if (nextGroupView == null)
                    return;

                final int bottom = mPinnedGroup.view.getBottom() + getPaddingTop();
                int mGroupDistanceY = nextGroupView.getTop() - bottom;
                if (mGroupDistanceY < 0)
                    mTranslateY = mGroupDistanceY;
                else
                    mTranslateY = 0;
            }
        }
    };


    public PinnedGroupExpandableListView(Context context) {
        this(context, null);
    }

    public PinnedGroupExpandableListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PinnedGroupExpandableListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setOnScrollListener(mOnScrollListener);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        adjustDividerHeight(context);
    }

    private void adjustDividerHeight(Context context) {
        // The unit is pixel
        int dividerHeight = getDividerHeight();
        if (dividerHeight > MAX_DIVIDER_HEIGHT) {
            setDividerHeight(UiUtil.px2dip(context, MAX_DIVIDER_HEIGHT));
            dividerHeight = MAX_DIVIDER_HEIGHT;
        }
        mDividerHeight = dividerHeight;
    }

    private void refreshPinnedGroup(int groupPosition) {
        if (mPinnedGroup == null) {
            if (mRecyclerGroup != null) {
                mPinnedGroup = mRecyclerGroup;
            }
            else {
                mPinnedGroup = new PinnedGroup();
                if (mPinnedGroupDivider != null)
                    mPinnedGroup.divider = mPinnedGroupDivider;
                else
                    mPinnedGroup.divider = getDivider();
            }
        }
        View pinnedView = getExpandableListAdapter().getGroupView(groupPosition, false,
                mPinnedGroup.view, this);
        LayoutParams layoutParams = (LayoutParams) pinnedView.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = (LayoutParams) generateDefaultLayoutParams();
            pinnedView.setLayoutParams(layoutParams);
        }
        int heightMode = MeasureSpec.getMode(layoutParams.height);
        int heightSize = MeasureSpec.getSize(layoutParams.height);
        if (heightMode == MeasureSpec.UNSPECIFIED)
            heightMode = MeasureSpec.EXACTLY;
        int maxHeight = getHeight() - getListPaddingTop() - getListPaddingBottom();
        if (heightSize > maxHeight)
            heightSize = maxHeight;

        int ws = MeasureSpec.makeMeasureSpec(getWidth() - getListPaddingLeft() -
                getListPaddingRight(), MeasureSpec.EXACTLY);
        int hs = MeasureSpec.makeMeasureSpec(heightSize, heightMode);
        pinnedView.measure(ws, hs);
        pinnedView.layout(0, 0, pinnedView.getMeasuredWidth(), pinnedView.getMeasuredHeight());

        mTranslateY = 0;
        mPinnedGroup.view = pinnedView;
        mPinnedGroup.groupPosition = groupPosition;
    }

    private void destroyPinnedGroup() {
        if (mPinnedGroup != null) {
            mRecyclerGroup = mPinnedGroup;
            mPinnedGroup = null;
        }
    }

    public void setPinnedGroupDivider(Drawable divider) {
        mPinnedGroupDivider = divider;
    }

    @Override
    public void setOnScrollListener(OnScrollListener l) {
        if (l == mOnScrollListener)
            super.setOnScrollListener(l);
        else
            mDelegateScrollListener = l;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
        post(new Runnable() {
            @Override
            public void run() {
                int firstVisibleItem = getFirstVisiblePosition();
                int firstVisibleGroupPosition = getPackedPositionGroup(
                        getExpandableListPosition(firstVisibleItem));
                if (firstVisibleGroupPosition >= 0) {
                    refreshPinnedGroup(firstVisibleGroupPosition);
                }
            }
        });
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mPinnedGroup != null) {
            int pinnedLeft = getListPaddingLeft();
            int pinnedRight = pinnedLeft + mPinnedGroup.view.getWidth();
            int pinnedTop = getListPaddingTop();
            int clipHeight = mPinnedGroup.view.getHeight() + mDividerHeight;

            canvas.save();
            canvas.clipRect(pinnedLeft, pinnedTop, pinnedRight, pinnedTop + clipHeight);
            canvas.translate(pinnedLeft, pinnedTop + mTranslateY);
            drawChild(canvas, mPinnedGroup.view, getDrawingTime());
            if (mPinnedGroup.divider != null) {
                int dividerLeft = pinnedLeft;
                int dividerRight = pinnedRight;
                int dividerTop = mPinnedGroup.view.getBottom() ;
                int dividerBottom = dividerTop + mDividerHeight;
                mPinnedGroup.divider.setBounds(dividerLeft, dividerTop, dividerRight, dividerBottom);
                mPinnedGroup.divider.draw(canvas);
            }
            canvas.restore();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        final float x = ev.getX();
        final float y = ev.getY();
        final int action = ev.getAction();
        boolean isPinnedGroupTouched = false;
        if (mPinnedGroup != null && mPinnedGroup.view != null)
            isPinnedGroupTouched = isPinnedGroupTouched(mPinnedGroup.view, x, y);

        if (action == MotionEvent.ACTION_DOWN) {
            isDownPointInPinnedGroup = isPinnedGroupTouched;
        }

        if (isDownPointInPinnedGroup) {
            mPinnedGroup.view.dispatchTouchEvent(ev);
            if (isPinnedGroupTouched) {
                if (action == MotionEvent.ACTION_UP) {
                    performPinnedGroupClick();
                }
                else {
                    if (action == MotionEvent.ACTION_MOVE) {
                    }
                    return true;
                }
            }
            else {
                isDownPointInPinnedGroup = false;
                // 如果希望手指在移出pinnedView之后触摸事件恢复为可操控ListView滚动的事件，则将以下这段代码注释放开即可
                /*
                if (mDownEvent == null) {
                    mDownEvent = MotionEvent.obtain(ev);
                    ev.setAction(MotionEvent.ACTION_CANCEL);
                    dispatchTouchEvent(ev);
                    mDownEvent.setAction(MotionEvent.ACTION_DOWN);
                    dispatchTouchEvent(mDownEvent);
                    mDownEvent.recycle();
                    mDownEvent = null;
                    return true;
                }
                */
            }
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }



    private boolean isPinnedGroupTouched(View view, float x, float y) {
        if (view == null)
            return false;
        view.getHitRect(mTouchRect);
        mTouchRect.top += mTranslateY;
        mTouchRect.bottom += mTranslateY + getPaddingTop();
        mTouchRect.left += getPaddingLeft();
        mTouchRect.right -= getPaddingRight();
        return mTouchRect.contains((int)x, (int)y);
    }

    private boolean performPinnedGroupClick() {
        int flatPosition = getFlatListPosition(getPackedPositionForGroup(mPinnedGroup.groupPosition));
        performItemClick(this, flatPosition, getAdapter().getItemId(flatPosition));
//        smoothScrollToPosition(flatPosition);
        return false;
    }

}
