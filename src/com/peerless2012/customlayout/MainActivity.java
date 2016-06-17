package com.peerless2012.customlayout;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.FrameLayout;

public class MainActivity extends Activity {

	private int[] mLayoutRess = {
			R.layout.view_auto_full_screen,
			R.layout.view_percent_layout
	};
	
	private ViewPager mViewPager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mViewPager = new ViewPager(this);
		FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
		mViewPager.setLayoutParams(layoutParams);
		setContentView(mViewPager);
		
		LayoutAdapter layoutAdapter = new LayoutAdapter(mLayoutRess);
		mViewPager.setAdapter(layoutAdapter);
	}
	
}
