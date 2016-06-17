package com.peerless2012.customlayout.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
* @Author peerless2012
* @Email peerless2012@126.com
* @DateTime 2016年6月17日 下午1:39:52
* @Version V1.0
* @Description: 自定义Layout，实现列表下拉，底部内容自动全屏
*/
public class AutoFullScreenLayout extends FrameLayout{
	
	private View mMainView;
	
	private View mSecondView;
	
	private int padding = 0;
	
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
	}
	
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		int measuredWidth = mMainView.getMeasuredWidth();
		int measuredHeight = mMainView.getMeasuredHeight();
		mSecondView.setPadding(0, measuredHeight, 0, 0);
//		mMainView.layout(0, 0, getMeasuredWidth(), measuredHeight);
//		mSecondView.layout(0, padding + measuredHeight, getMeasuredWidth(), getMeasuredHeight());
		super.onLayout(changed, left, top, right, bottom);
	}
	
	
	private float preX,downX;
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			preX = downX = event.getX();
			break;
			
		case MotionEvent.ACTION_MOVE:
			
			break;
			
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			
			break;

		default:
			break;
		}
		return super.onTouchEvent(event);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}
}
