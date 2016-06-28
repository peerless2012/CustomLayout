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
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.widget.AbsListView;
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
		mHeightLimit = getMeasuredHeight();
	}
	
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		int measuredWidth = mMainView.getMeasuredWidth();
		int measuredHeight = mMainView.getMeasuredHeight();
		if (mYOffset == -1) mYOffset = measuredHeight;
		mMainView.layout(0, 0, getMeasuredWidth(), measuredHeight);
		mSecondView.layout(0, mYOffset, mSecondView.getMeasuredWidth(), mSecondView.getMeasuredHeight());
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
		
		if (!isEnabled() || mYOffset < 0) {
            // Fail fast if we're not in a state where a swipe is possible
            return false;
        }

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
		Log.i("onTouchEvent", "onTouchEvent : "+ event.getAction());
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
			int currY = mScroller.getCurrY();
			Log.i("fling", "computeScroll    当前Y =  " + currY+"   ,mYoffset = "+mYOffset);
			if (currY <= 0) {
				Log.i("fling", "computeScroll    Scroll " + (-currY));
				if (mYOffset > 0) {
					mYOffset = 0;
					mSecondView.layout(0, mYOffset, mSecondView.getMeasuredWidth(), mYOffset + mSecondView.getMeasuredHeight());
				}
				mSecondView.scrollTo(0,-currY);
				invalidate();
			}else if (currY <= mHeightLimit) {
				Log.i("fling", "computeScroll    Layout " + mYOffset);
				mYOffset = currY;
				mSecondView.layout(0, mYOffset, mSecondView.getMeasuredWidth(), mYOffset + mSecondView.getMeasuredHeight());
				requestLayout();
				invalidate();
			}else {
				// 不会到这一行
				Log.i("fling", "computeScroll    不应该到这一行 ");
				/*if (mYOffset > mHeightLimit) {
					Log.i("fling", "computeScroll    Layout 修复");
					mYOffset = mHeightLimit;
					mSecondView.layout(0, mYOffset, mSecondView.getMeasuredWidth(), mYOffset + mSecondView.getMeasuredHeight());
				}
				Log.i("fling", "computeScroll    Scroll " + (currY - mHeightLimit));
				mSecondView.scrollTo(0,currY - mHeightLimit);
				invalidate();*/
			}
//			
//			
//			mYOffset = currY;
//			if (mYOffset < 0) {
//				mYOffset = 0;
//				Log.i("UP", "computeScroll    reset " + mYOffset);
//				mSecondView.layout(0, mYOffset, mSecondView.getMeasuredWidth(), mYOffset + mSecondView.getMeasuredHeight());
////				requestLayout();
//			}
//			
//			if (currY < 0 ) {
//				Log.i("UP", "computeScroll    Scroll " + currY);
//				mSecondView.scrollTo(0, -currY);
//				invalidate();
//			}else if (currY > mHeightLimit) {
//				mSecondView.scrollTo(0, currY - mHeightLimit);
//				invalidate();
//			}else {
//				Log.i("UP", "computeScroll    Layout " + mYOffset);
//				mSecondView.layout(0, mYOffset, mSecondView.getMeasuredWidth(), mYOffset + mSecondView.getMeasuredHeight());
//				requestLayout();
//			}
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
        int consumed = 0;
        float canScrollY = mYOffset - deltaY;
        if (canScrollY > mHeightLimit) {
			canScrollY = mHeightLimit;
		}else if (canScrollY < 0) {
			canScrollY = 0;
		}
        consumed = (int) (canScrollY - mYOffset);
        if (consumed != 0) {
        	mYOffset += consumed;
//        	Log.i("AutoFullScreenLayout", "consumed = "+consumed+ "   ,padding = " + mYOffset);
        	mSecondView.layout(0, mYOffset, mSecondView.getMeasuredWidth(), mYOffset + mSecondView.getMeasuredHeight());
        	requestLayout();
        	
		}
        return -consumed;
    }
}
