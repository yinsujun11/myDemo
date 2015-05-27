package com.mydemo.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.mydemo.R;

public class MyAdapter extends BaseAdapter{

	private List<String> listData=new ArrayList<String>();
	private int rightWidth;
	private Context context;
	public MyAdapter(Context context,List<String> listData) {
		this.listData=listData;
		this.context=context;
	}
	@Override
	public int getCount() {
		return listData.size();
	}

	@Override
	public Object getItem(int position) {
		return listData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder mHolder=null;
		if(convertView==null){
			mHolder=new ViewHolder();
			convertView=LayoutInflater.from(getContext()).inflate(R.layout.swipe_list_item, null);
			mHolder.swipe_list_tv_text=(TextView) convertView.findViewById(R.id.swipe_list_tv_text);
			mHolder.swipe_list_tv_delete=(TextView) convertView.findViewById(R.id.swipe_list_tv_delete);
			LinearLayout.LayoutParams lp2 = new LayoutParams(rightWidth, LayoutParams.MATCH_PARENT);
			mHolder.swipe_list_tv_delete.setLayoutParams(lp2);
			mHolder.deleteLinster=new MyOnDeleteClickListener();
			convertView.setTag(mHolder);
		}
		mHolder=(ViewHolder) convertView.getTag();
		mHolder.swipe_list_tv_text.setText(listData.get(position));
		mHolder.deleteLinster.setPosition(position);
		mHolder.swipe_list_tv_delete.setOnClickListener(mHolder.deleteLinster);
//		mHolder.swipe_list_tv_delete.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				listData.remove(position);
//				notifyDataSetChanged();
//				Toast.makeText(MySwipeListView.this, "delete"+position, Toast.LENGTH_SHORT).show();
//			}
//		});
		return convertView;
	}
	public void addAll(List<String> listData){
		this.listData.addAll(listData);
		notifyDataSetChanged();
	}
	private class ViewHolder{
		private TextView swipe_list_tv_text,swipe_list_tv_delete;
		private MyOnDeleteClickListener deleteLinster;
	}
	public void setRightWidth(int rightWidth) {
		this.rightWidth = rightWidth;
	}
	private class MyOnDeleteClickListener implements OnClickListener{
		private int position;
		
		public void setPosition(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			if(onListener!=null){
				onListener.delete(v, position);
			}
		}
		
	}
	private onDeleteListener onListener;
	public void setOnDeleteLinsener(onDeleteListener onListener){
		this.onListener=onListener;
	}
	public interface onDeleteListener{
		void delete(View view,int position);
	}
	public Context getContext() {
		return context;
	}


}
