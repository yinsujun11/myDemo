package com.mydemo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.mydemo.R;
import com.mydemo.activity.AtyViewpagerMain;
 public class LeftFragment extends Fragment{
	 private RelativeLayout my_message_relative;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View leftView=inflater.inflate(R.layout.main_silding_left, null);
		my_message_relative=(RelativeLayout) leftView.findViewById(R.id.my_message_relative);
		my_message_relative.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getActivity(),AtyViewpagerMain.class));
			}
		});
		return leftView;
	}

}
