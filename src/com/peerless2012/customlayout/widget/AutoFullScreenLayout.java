package com.peerless2012.customlayout.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ScrollerCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;

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
	
	private int mYOffset = -1;
	
	private int mTouchSlop;
	
	private NestedScrollingParentHelper helper;
	
	private ScrollerCompat mScroller;
	
	private VelocityTracker mVelocityTracker = VelocityTracker.obtain();
	
	private int mMaxVelocity; 
	
	private int mMinVelocity; 
	
	private int mHeightLimit;
	
	private int mInitY;
	
	private int mCanScrollY;
	
	private int mCurrentY;
	
	private int mInitHeight;
	
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
		mScroller = ScrollerCompat.create(getContext());
		ViewConfiguration viewConfiguration = ViewConfiguration.get(getContext());
		mMaxVelocity = viewConfiguration.getScaledMaximumFlingVelocity();
		mMinVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
		mYOffset = mInitHeight = 300;
	}
	
	
	private int cumputeY(int y) {
		return (int) (((y - mInitHeight) / ((getMeasuredHeight() - mInitHeight) * 1.0f)) * mCanScrollY) + mInitY;
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
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mHeightLimit = getMeasuredHeight();
		mInitY =  -( getMeasuredHeight() / 2 - mInitHeight / 2 );
		mCanScrollY = -mInitY;
		Log.i("onMeasure", "mInitHeight = "+mInitHeight);
	}
	
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		int cumputeY = cumputeY(mYOffset);
		mMainView.layout(0, cumputeY, mMainView.getMeasuredWidth(), cumputeY + mMainView.getMeasuredHeight());
		mSecondView.layout(0, mYOffset, mSecondView.getMeasuredWidth(), mYOffset + mSecondView.getMeasuredHeight());
		flag = false;
	}
	private static final int INVALID_POINTER = -1;
	private int mActivePointerId = INVALID_POINTER;
	private boolean mIsBeingDragged;
	private float mInitialMotionY;
    private float mInitialDownY;
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		int action = ev.getAction();
		
		if (!isEnabled() || mYOffset <= 0) {
            // Fail fast if we're not in a state where a swipe is possible
			Log.i("computeScroll", "onInterceptTouchEvent  忽略");
			
            return false;
        }
		
		Log.i("computeScroll", "onInterceptTouchEvent  不忽略");
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                mIsBeingDragged = false;
                final float initialDownY = ev.getY();
                if (initialDownY == -1) {
                    return false;
                }
                mInitialDownY = initialDownY;
                preY = downY = ev.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                if (mActivePointerId == INVALID_POINTER) {
                    return false;
                }

                final float y = ev.getY();
                if (y == -1) {
                    return false;
                }
                final float yDiff = y - mInitialDownY;
                if (Math.abs(yDiff) > mTouchSlop && !mIsBeingDragged) {
                    mInitialMotionY = mInitialDownY + mTouchSlop;
                    mIsBeingDragged = true;
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsBeingDragged = false;
                mActivePointerId = INVALID_POINTER;
                break;
        }

        return mIsBeingDragged;
	}
    
	private float preY,downY;
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.i("computeScroll", "onTouchEvent  e = "+event.getAction());
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
			}
			mVelocityTracker.clear();
			mVelocityTracker.addMovement(event);
			preY = downY;
			break;
			
		case MotionEvent.ACTION_MOVE:
			mVelocityTracker.addMovement(event);
			float y2 = event.getY();
			moveBy(preY - y2);
			preY = y2;
			break;
			
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			mVelocityTracker.addMovement(event);
			mVelocityTracker.computeCurrentVelocity(1000,mMaxVelocity);
			float yVelocity = mVelocityTracker.getYVelocity();
			if (Math.abs(yVelocity) >= mMinVelocity) {
				dispatchFling(yVelocity);
			}else {
				// 不需要fling
				Log.i("UP", "不需要fling");
				moveBy(preY - event.getY());
			}
			preY = downY = -1;
			break;

		default:
			break;
		}
		return true;
	}
	
	private void dispatchFling(float velocityY) {
		//- height ---- H
		int current = mYOffset - mSecondView.getScrollY();
		Log.i("fling", "current   "+current+"  ,mYOffset = "+ mYOffset + " ,ScrollY = "+ mSecondView.getScrollY());
		// fling
		if (velocityY > 0) {// 向下滑动
			int min = current;
			int max = mHeightLimit;
			Log.i("fling", "需要fling   "+"yVelocity = "+ velocityY + " m = "+ min +"  ,h = "+ max);
			mScroller.fling(0, current, 0, (int)velocityY, 0, 0, min, max);
		}else {// 向上
			int min = -((ViewGroup)mSecondView).getChildAt(0).getHeight();
			int max = current;
			Log.i("fling", "需要fling   "+"yVelocity = "+ velocityY  + " m = "+ min +"  ,h = "+ max);
			mScroller.fling(0, current, 0, (int)velocityY, 0, 0, min, max);
		}
		invalidate();
	}
	
	@Override
	public void computeScroll() {
		super.computeScroll();
		if (mScroller.computeScrollOffset()) {
			int oldYOffset = mYOffset;
			int currY = mScroller.getCurrY();
			Log.i("computeScroll", "computeScroll    currY =  " + currY+"   ,mYoffset = "+mYOffset);
				if (currY <= 0) {
					Log.i("computeScroll", "Scroll");
					if (mYOffset > 0) {
						mYOffset = 0;
//						mSecondView.layout(0, mYOffset, mSecondView.getMeasuredWidth(), mYOffset + mSecondView.getMeasuredHeight());
						requestLayout();
					}
					mSecondView.scrollTo(0,-currY);
				}else if (currY <= mHeightLimit) {
					Log.i("computeScroll", "Layout ");
					mYOffset = currY;
//					mSecondView.layout(0, mYOffset, mSecondView.getMeasuredWidth(), mYOffset + mSecondView.getMeasuredHeight());
					requestLayout();
			}else {
				// 不会到这一行
				Log.i("computeScroll", "invalidate    不应该到这一行 ");
			}
			postInvalidate();
			Log.i("computeScroll", "                                                          ");
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}

	private boolean flag = false;
	
	@Override
	public boolean onStartNestedScroll(View child, View target,
			int nestedScrollAxes) {
		// 是否配合子View滑动,如果是垂直则配合
//		Log.i("AutoFullScreenLayout", "onStartNestedScroll");
		return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
	}

	@Override
	public void onNestedScrollAccepted(View child, View target,
			int nestedScrollAxes) {
		helper.onNestedScrollAccepted(child, target, nestedScrollAxes);
//		Log.i("AutoFullScreenLayout", "onNestedScrollAccepted");
	}

	@Override
	public void onStopNestedScroll(View target) {
//		Log.i("AutoFullScreenLayout", "onStopNestedScroll");
		helper.onStopNestedScroll(target);
	}

	@Override
	public void onNestedScroll(View target, int dxConsumed, int dyConsumed,
			int dxUnconsumed, int dyUnconsumed) {
//		Log.i("AutoFullScreenLayout", "onNestedScroll     : dxConsumed = " 
//			+ dxConsumed +" ,dyConsumed =" + dyConsumed
//			+ " ,dxUnconsumed = "+ dxUnconsumed +" ,dyUnconsumed = "+dyUnconsumed);
//		Log.i("AutoFullScreenLayout", "onNestedScroll");
//		int moveBy = moveBy(dyUnconsumed);
	}

	// 往下 dy是负值（当子View不可再往下滑动的时候）
	// 往上dy是正值
	@Override
	public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
//		Log.i("AutoFullScreenLayout", "onNestedPreScroll");
		boolean canScrollDown = mSecondView.canScrollVertically(-1);
		boolean canScrollUp = mSecondView.canScrollVertically(1);
		Log.i("AutoFullScreenLayout", "canScrollDown : " + canScrollDown + "   ,canScrollUp = "+canScrollUp +"   "+dy);
		// 不可以向下滑动 并且 Padding要在限定范围内
		Log.i("computeScroll", "onNestedPreScroll  mYOffset"+mYOffset+"   ,dy = "+dy);
		if (dy < 0) {// 向下
			if (!canScrollDown) {
				consumed[1] = moveBy(dy);
			}
		}else if (dy > 0) { //向上
			if (mYOffset < getMeasuredHeight()) {
				consumed[1] = moveBy(dy);
			}
		}
	}

	@Override
	public boolean onNestedFling(View target, float velocityX, float velocityY,
			boolean consumed) {
		Log.i("fling", "onNestedFling     : velocityY = "+velocityY);
		return false;
	}

	@Override
	public boolean onNestedPreFling(View target, float velocityX,
			float velocityY) {
		Log.i("fling", "onNestedPreFling     : velocityY = "+velocityY);
		dispatchFling(-velocityY);
		return true;
	}
	
	@Override
	public int getNestedScrollAxes() {
		Log.i("AutoFullScreenLayout", "getNestedScrollAxes     : ");
		return helper.getNestedScrollAxes();	
	}

    private int moveBy(float deltaY) {
    	Log.i("UP", "deltaY = "+deltaY);
    	int oldYOffset = mYOffset;
        int consumed = 0;
        float canScrollY = mYOffset - deltaY;
        if (canScrollY > mHeightLimit) {
			canScrollY = mHeightLimit;
		}else if (canScrollY < 0) {
			canScrollY = 0;
		}
        consumed = (int) (canScrollY - mYOffset);
        if (consumed != 0) {
        	Log.i("computeScroll", "moveBy  pre  "+"mYoffset = "+mYOffset);
        	mYOffset += consumed;
        	Log.i("computeScroll", "moveBy  aft  "+"mYoffset = "+mYOffset);
//        	mSecondView.layout(0, mYOffset, mSecondView.getMeasuredWidth(), mYOffset + mSecondView.getMeasuredHeight());
        	requestLayout();
        	postInvalidate();
		}
        return -consumed;
    }
}
