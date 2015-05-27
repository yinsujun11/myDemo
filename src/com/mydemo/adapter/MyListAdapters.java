package com.mydemo.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mydemo.R;

public class MyListAdapters extends BaseAdapter{
	private List<Object> data=new ArrayList<Object>();
	private Context context;
	public int getSelectPosition() {
		return selectPosition;
	}
	public void setSelectPosition(int selectPosition) {
		this.selectPosition = selectPosition;
	}
	private int selectPosition;
	public MyListAdapters(List<Object> data,Context context) {
		this.data=data;
		this.context=context;
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
			convertView=LayoutInflater.from(context).inflate(R.layout.test_list_item, null);
			mHolder.tv=(TextView) convertView.findViewById(R.id.tv);
			mHolder.bt=(Button) convertView.findViewById(R.id.list_item_bt);
			mHolder.myClick=new MyClick();
			convertView.setTag(mHolder);
		}
		mHolder=(ViewHolder) convertView.getTag();
		mHolder.myClick.setPosition(position);
		mHolder.bt.setOnClickListener(mHolder.myClick);
		mHolder.tv.setText(str);
		
//		mHolder.iv.setBackgroundResource(R.drawable.my_message);
		
		return convertView;
	}
	private class ViewHolder{
		private TextView tv;
		private Button bt;
		private MyClick myClick;
	}
	
	
	
	private class MyClick implements OnClickListener{
		private int position;

		public void setPosition(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			if(click!=null){
				click.onClick(v,position);
			}
			
		}
	}
	private setButtonClickLinster click;
	public void setOnClick(setButtonClickLinster click){
		this.click=click;
	}
	public interface setButtonClickLinster{
		void onClick(View view,int position);
	};
}
