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
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.Scroller;

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
	private boolean mReturningToStart;
	private int mActivePointerId = INVALID_POINTER;
	private boolean mIsBeingDragged;
	private float mInitialMotionY;
    private float mInitialDownY;
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return true;
		/*final int action = MotionEventCompat.getActionMasked(ev);
		
		if (padding < mHeightLimit) {
			return true;
		}
		
		if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
            mReturningToStart = false;
        }
		
		if (!isEnabled() || mReturningToStart || canChildScrollUp()) {
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

        return mIsBeingDragged;*/
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
		Log.i("AutoFullScreenLayout", "onTouchEvent : "+ event.getAction());
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
			}
			mVelocityTracker.clear();
			mVelocityTracker.addMovement(event);
			preY = downY = event.getY();
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
				// fling
				if (yVelocity > 0) {// 向下
					Log.i("UP", "需要fling   "+"yVelocity = "+ yVelocity + " m = "+ mYOffset +"  ,h = "+ mHeightLimit);
					mScroller.fling(0, mYOffset, 0, (int)yVelocity, 0, 0, mYOffset, mHeightLimit);
				}else {// 向上
					Log.i("UP", "需要fling   "+"yVelocity = "+ yVelocity + " m = "+ 0 +"  ,h = "+ mYOffset);
					mScroller.fling(0, mYOffset, 0, (int)yVelocity, 0, 0, 0, mYOffset);
				}
				invalidate();
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
	
	@Override
	public void computeScroll() {
		super.computeScroll();
		if (mScroller.computeScrollOffset()) {
			int currY = mScroller.getCurrY();
			Log.i("UP", "computeScroll " + currY);
			mYOffset = currY;
			mSecondView.layout(0, mYOffset, mSecondView.getMeasuredWidth(), mYOffset + mSecondView.getMeasuredHeight());
//			mSecondView.offsetTopAndBottom(mYOffset);
			requestLayout();
//			invalidate();
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}

	private boolean flag = false;
	
//	private void updatePadding(float y) {
//		int scrollY = mSecondView.getScrollY();
//		Log.i("AutoFullScreenLayout updatePadding","   scrollY = "+scrollY+"    y = "+y);
//		if (scrollY > 0) {
//			// 如果不在顶端则不处理
//			return;
//		}
//		padding += y;
//		if (padding <= paddingLimit) {
//			padding = paddingLimit;
//			flag = true;
//		}else if (padding >= getMeasuredHeight()) {
//			padding = getMeasuredHeight();
//			flag = true;
//		}else {
//			mSecondView.requestLayout();
//		}
//		if (flag) {
//			Log.i("AutoFullScreenLayout updatePadding", "padding = "+padding);
//		}
//	}
	
	
	@Override
	public boolean onStartNestedScroll(View child, View target,
			int nestedScrollAxes) {
		// 是否配合子View滑动,如果是垂直则配合
//		Log.i("AutoFullScreenLayout", "onStartNestedScroll");
//		return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
		return false;
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
//		if (velocityY < -1000) {
//			int paddingTop = mSecondView.getPaddingTop();
//			if (paddingTop != paddingLimit) {
//				paddingTop = paddingLimit;
//				mSecondView.setPadding(0, paddingLimit, 0, 0);
//			}
//		}
		return false;
	}

	@Override
	public int getNestedScrollAxes() {
		Log.i("AutoFullScreenLayout", "getNestedScrollAxes     : ");
		return helper.getNestedScrollAxes();
	}
	
	private void obtainVelocityTracker(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    private void initOrResetVelocityTracker() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        } else {
            mVelocityTracker.clear();
        }
    }

    private void releaseVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }
	
	public void fling(int velocityY) {
        mScroller.abortAnimation();
        mScroller.fling(0, mSecondView.getTop(), 0, velocityY, 0, 0,
                0, mMainView.getMeasuredHeight() - mSecondView.getTop(),
                0, 0);
        ViewCompat.postInvalidateOnAnimation(this);
    }

    private int moveBy(float deltaY) {
//    	Log.i("AutoFullScreenLayout", "deltaY = "+deltaY);
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
