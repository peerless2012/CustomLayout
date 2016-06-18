package com.peerless2012.customlayout.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;

/**
* @Author peerless2012
* @Email peerless2012@126.com
* @DateTime 2016年6月17日 下午1:39:52
* @Version V1.0
* @Description: 自定义Layout，实现列表下拉，底部内容自动全屏
*/
public class AutoFullScreenLayout extends FrameLayout implements NestedScrollingParent{
	
	private View mMainView;
	
	private View mSecondView;
	
	private int padding = 0;
	
	private int paddingLimit = 0;
	
	private int mTouchSlop;
	
	private NestedScrollingParentHelper helper;
	
	public AutoFullScreenLayout(Context context) {
		this(context,null);
	}
	
	public AutoFullScreenLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context,attrs);
	}

	public AutoFullScreenLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	private void init(Context context, AttributeSet attrs) {
		mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
		helper = new NestedScrollingParentHelper(this);
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		int childCount = getChildCount();
		if (childCount != 2) {
			throw new IllegalArgumentException("AutoFullScreenLayout must have two immediate childs");
		}
		mMainView = getChildAt(0);
		mSecondView = getChildAt(1);
		mTarget = mSecondView;
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//		ImageView
//		MeasureSpec.AT_MOST
//		Log.i("AutoFullScreenLayout", "onMeasure " );
	}
	
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		int measuredWidth = mMainView.getMeasuredWidth();
		int measuredHeight = mMainView.getMeasuredHeight();
		paddingLimit = measuredHeight;
		if (padding == 0) padding = measuredHeight;
		Log.i("AutoFullScreenLayout", "onLayout " +padding);
		mSecondView.setPadding(0, padding, 0, 0);
//		mMainView.layout(0, 0, getMeasuredWidth(), measuredHeight);
//		mSecondView.layout(0, padding, getMeasuredWidth(), getMeasuredHeight());
		super.onLayout(changed, left, top, right, bottom);
		flag = false;
	}
	private static final int INVALID_POINTER = -1;
	private boolean mReturningToStart;
	private int mActivePointerId = INVALID_POINTER;
	private boolean mIsBeingDragged;
	private float mInitialMotionY;
    private float mInitialDownY;
	/*@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		
		final int action = MotionEventCompat.getActionMasked(ev);
		
		if (padding < paddingLimit) {
			return true;
		}
		
		if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
            mReturningToStart = false;
        }
		
		if (!isEnabled() || mReturningToStart || canChildScrollUp()
                || mRefreshing || mNestedScrollInProgress) {
            // Fail fast if we're not in a state where a swipe is possible
            return false;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                mIsBeingDragged = false;
                final float initialDownY = getMotionEventY(ev, mActivePointerId);
                if (initialDownY == -1) {
                    return false;
                }
                mInitialDownY = initialDownY;
                break;

            case MotionEvent.ACTION_MOVE:
                if (mActivePointerId == INVALID_POINTER) {
                    return false;
                }

                final float y = getMotionEventY(ev, mActivePointerId);
                if (y == -1) {
                    return false;
                }
                final float yDiff = y - mInitialDownY;
                if (yDiff > mTouchSlop && !mIsBeingDragged) {
                    mInitialMotionY = mInitialDownY + mTouchSlop;
                    mIsBeingDragged = true;
//                    mProgress.setAlpha(STARTING_PROGRESS_ALPHA);
                }
                break;

            case MotionEventCompat.ACTION_POINTER_UP:
//                onSecondaryPointerUp(ev);
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsBeingDragged = false;
                mActivePointerId = INVALID_POINTER;
                break;
        }

        return mIsBeingDragged;
	}*/
	private View mTarget;
	 public boolean canChildScrollUp() {
	        if (android.os.Build.VERSION.SDK_INT < 14) {
	            if (mTarget instanceof AbsListView) {
	                final AbsListView absListView = (AbsListView) mTarget;
	                return absListView.getChildCount() > 0
	                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
	                                .getTop() < absListView.getPaddingTop());
	            } else {
	                return ViewCompat.canScrollVertically(mTarget, -1) || mTarget.getScrollY() > 0;
	            }
	        } else {
	            return ViewCompat.canScrollVertically(mTarget, -1);
	        }
	    }

	
	private float preY,downY;
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.i("AutoFullScreenLayout", "onTouchEvent : "+ event.getAction());
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			preY = downY = event.getY();
			break;
			
		case MotionEvent.ACTION_MOVE:
			float y2 = event.getY();
			if (preY > 0) {
				updatePadding(y2 - preY);
			}
			preY = y2;
			break;
			
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			preY = downY = -1;
			break;

		default:
			break;
		}
		return true;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}

	private boolean flag = false;
	
	private void updatePadding(float y) {
		int scrollY = mSecondView.getScrollY();
		Log.i("AutoFullScreenLayout updatePadding","   scrollY = "+scrollY+"    y = "+y);
		if (scrollY > 0) {
			// 如果不在顶端则不处理
			return;
		}
		padding += y;
		if (padding <= paddingLimit) {
			padding = paddingLimit;
			flag = true;
		}else if (padding >= getMeasuredHeight()) {
			padding = getMeasuredHeight();
			flag = true;
		}else {
			mSecondView.requestLayout();
		}
		if (flag) {
			Log.i("AutoFullScreenLayout updatePadding", "padding = "+padding);
		}
	}
	
	@Override
	public boolean onStartNestedScroll(View child, View target,
			int nestedScrollAxes) {
		// 是否配合子View滑动
		Log.i("AutoFullScreenLayout", "onStartNestedScroll   : Axes = " + nestedScrollAxes);
		return true;
	}

	@Override
	public void onNestedScrollAccepted(View child, View target,
			int nestedScrollAxes) {
		helper.onNestedScrollAccepted(child, target, nestedScrollAxes);
		Log.i("AutoFullScreenLayout", "onNestedScrollAccepted     : Axes = " + nestedScrollAxes);
	}

	@Override
	public void onStopNestedScroll(View target) {
		Log.i("AutoFullScreenLayout", "onStopNestedScroll     : ");
		helper.onStopNestedScroll(target);
	}

	@Override
	public void onNestedScroll(View target, int dxConsumed, int dyConsumed,
			int dxUnconsumed, int dyUnconsumed) {
//		Log.i("AutoFullScreenLayout", "onNestedScroll     : dxConsumed = " 
//			+ dxConsumed +" ,dyConsumed =" + dyConsumed
//			+ " ,dxUnconsumed = "+ dxUnconsumed +" ,dyUnconsumed = "+dyUnconsumed);
	}

	@Override
	public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
//		Log.i("AutoFullScreenLayout", "onNestedPreScroll     : dx = "+ dx+" ,dy = "+dy +" ,consumed = " + consumed);
		updatePadding(-dy);
		
	}

	@Override
	public boolean onNestedFling(View target, float velocityX, float velocityY,
			boolean consumed) {
		Log.i("AutoFullScreenLayout", "onNestedFling     : velocityY = "+velocityY);
		return false;
	}

	@Override
	public boolean onNestedPreFling(View target, float velocityX,
			float velocityY) {
		Log.i("AutoFullScreenLayout", "onNestedPreFling     : velocityY = "+velocityY);
		if (velocityY < -1000) {
			int paddingTop = mSecondView.getPaddingTop();
			if (paddingTop != paddingLimit) {
				paddingTop = paddingLimit;
				mSecondView.setPadding(0, paddingLimit, 0, 0);
			}
		}
		return false;
	}

	@Override
	public int getNestedScrollAxes() {
		Log.i("AutoFullScreenLayout", "getNestedScrollAxes     : ");
		return helper.getNestedScrollAxes();
	}
}
