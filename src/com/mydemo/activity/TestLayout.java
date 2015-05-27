package com.mydemo.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.mydemo.R;

public class TestLayout extends Activity{
	private ListView lv;
	private MyAdapter mAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		lv.setOnTouchListener(new MyTouchLinster());
	}
	private void initView(){
		lv=(ListView) findViewById(R.id.lv);
		List<String> data=new ArrayList<String>();
		for(int i=0;i<20;i++){
			String str="哈哈"+i;
			data.add(str);
		}
		mAdapter=new MyAdapter(data);
		lv.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();
	}
	private class MyAdapter extends BaseAdapter{
		private List<String> data=new ArrayList<String>();
		public MyAdapter(List<String> data) {
			this.data=data;
		}
		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			String str="";
			str=(String) getItem(position);
			ViewHolder mHolder=null;
			if(convertView==null){
				mHolder=new ViewHolder();
				convertView=TestLayout.this.getLayoutInflater().inflate(R.layout.test_list_item, null);
				mHolder.tv=(TextView) convertView.findViewById(R.id.tv);
				convertView.setTag(mHolder);
			}
//			else if(convertView!=null&& convertView instanceof LinearLayout){
//			}
			mHolder=(ViewHolder) convertView.getTag();
			mHolder.tv.setText(str);
			return convertView;
		}
		private class ViewHolder{
			private TextView tv;
		}
	}
	private class MyTouchLinster implements OnTouchListener{
		float mDownX;
		 float mDownY;
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mDownX=event.getX();
				mDownY=event.getY();
				break;
			case MotionEvent.ACTION_MOVE:
				float dy = Math.abs((event.getY() - mDownY));
				float dx = Math.abs((event.getX() - mDownX));
				if(dy>200){
					InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);  
					boolean isOpen=imm.isActive();
					if(isOpen==true){
						((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(TestLayout.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
						Log.e("yhuadong", dy+"hudong"+"隐藏");
						return true;
					}else{
						return false;
					}
				}
				if(dx>20){
					Log.e("xhuadong", dx+"hudong");
					return true;
				}
				break;
			case MotionEvent.ACTION_UP:
				break;
			default:
				break;
			}
			return false;
		}
		
	}
}
