package com.peerless2012.customlayout;

import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
* @Author peerless2012
* @Email  peerless2012@126.com
* @HomePage http://peerless2012.github.io
* @DateTime 2015年7月13日 下午2:15:42
* @Version V1.0
* @Description: 自定义Layout展示的适配器
*/
public class LayoutAdapter extends PagerAdapter {

	private LayoutInflater mLayoutInflater;
	
	private int[] mLayoutRess;
	
	public LayoutAdapter(int[] mLayoutRess) {
		super();
		this.mLayoutRess = mLayoutRess;
	}

	@Override
	public int getCount() {
		return mLayoutRess == null ? 0 : mLayoutRess.length;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}
	
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		if (mLayoutInflater == null) {
			mLayoutInflater = LayoutInflater.from(container.getContext());
		}
		View page = mLayoutInflater.inflate(mLayoutRess[position], container, false);
		container.addView(page);
		return page;
	}
}
