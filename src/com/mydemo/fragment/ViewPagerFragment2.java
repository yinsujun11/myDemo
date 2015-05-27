package com.mydemo.fragment;

import com.mydemo.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ViewPagerFragment2 extends Fragment{
	private TextView text;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view2=inflater.inflate(R.layout.viewpager_fragment, null);
		text=(TextView) view2.findViewById(R.id.tv_viewpager);
		text.setText("我是第二个页面");
		text.setTextColor(getResources().getColor(R.color.blue));
		return view2;
	}
}
