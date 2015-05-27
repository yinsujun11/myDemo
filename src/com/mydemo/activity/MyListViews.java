package com.mydemo.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mydemo.R;
import com.mydemo.adapter.MyListAdapters;
import com.mydemo.entity.Person;
import com.mydemo.utils.Tools;
import com.mydemo.view.MyRefreshForListview;
//implements RefreshListView.IOnRefreshListener
public class MyListViews extends Activity implements MyRefreshForListview.IOnRefreshListener
				,MyListAdapters.setButtonClickLinster{
	private MyRefreshForListview myList;
	private MyListAdapters mAdapter;
	private MyAdapter2 mAdapter2;
	private Person person;
	private List<MyObserver> myObserver;
	List<Object> dataList=new ArrayList<Object>();
	private int i=0;
	private RefreshDataAsynTask refresh;
	private Handler mHandler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_listview_refresh);
		myList=(MyRefreshForListview) findViewById(R.id.my_list_view);
//		person=new Person();
//		myObserver=new ArrayList<MyObserver>();
//		for(int j=0;j<3;j++){
//			MyObserver my=new MyObserver(i);
//			i++;
//			person.setAge(20);
//			person.setId(j);
//			person.setName("yin"+j);
//			person.setAlias("小傻逼"+j);
//			person.addObserver(my);
//			myObserver.add(my);
//		}
//		mAdapter2=new MyAdapter2(myObserver);
//		myList.setAdapter(mAdapter);
//		mAdapter2.notifyDataSetChanged();
		for(int i=0;i<20;i++){
			String str="哈哈"+i;
			dataList.add(str);
		}
		mAdapter=new MyListAdapters(dataList,this);
		myList.setAdapter(mAdapter);
		mAdapter.setOnClick(this);
		myList.setOnRefreshListener(this);
	}
//	@Override
//	public void OnRefresh() {
//		for(int i=0;i<5;i++){
//			data.add("刷新加载"+i);
//			mAdapter=new MyAdapter(data);
//			myList.setAdapter(mAdapter);
//			mAdapter.notifyDataSetChanged();
////			myList.onRefreshComplete();
//		}
//		mRefreshAsynTask = new RefreshDataAsynTask();
//		mRefreshAsynTask.execute();
//	}
@Override
public void OnRefresh() {
//	for(int i=0;i<5;i++){
//		dataList.add("刷新加载"+i);
//		mAdapter=new MyAdapter(dataList);
//		myList.setAdapter(mAdapter);
//		mAdapter.notifyDataSetChanged();
//		myList.onRefreshComplete();
//	}
	refresh=new RefreshDataAsynTask();
	refresh.execute();
}
private int index = 0;

class RefreshDataAsynTask extends AsyncTask<Void , Void, Void>
{
	List<String> data=new ArrayList<String>();
	@Override
	protected Void doInBackground(Void... arg0) {
		
		
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		index++;
		data.add("Kevin2" + index);
		data.add("Kevin2" + index);
		data.add("Kevin2" + index);
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		dataList.addAll(data);
		mAdapter.notifyDataSetChanged();
		myList.onRefreshComplete();
	}
}
private class MyAdapter2 extends BaseAdapter{
	private List<MyObserver> data=new ArrayList<MyObserver>();
	
	public MyAdapter2(List<MyObserver> data) {
		this.data=data;
		Log.e("adapter", data.get(0).getPerson().toString());
	}
	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public MyObserver getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		String name=null;
		name=getItem(position).getPerson().getName();
		ViewHolder mHolder=null;
		if(convertView==null){
			mHolder=new ViewHolder();
			convertView=MyListViews.this.getLayoutInflater().inflate(R.layout.test_list_item, null);
			mHolder.tv=(TextView) convertView.findViewById(R.id.tv);
			convertView.setTag(mHolder);
		}
		mHolder=(ViewHolder) convertView.getTag();
		Log.e("adapter", name);
		mHolder.tv.setText(data.get(0).getPerson().getName());
		return convertView;
	}
	private class ViewHolder{
		private TextView tv;
	}
}
private void saveEditedSms2(List<String> smsContent){
    SharedPreferences preferences = getApplicationContext().getSharedPreferences("editedSmsContents", Context.MODE_PRIVATE); 
    Editor editor = preferences.edit(); 
    editor.putInt("listSize", smsContent.size());
    for(int i=0;i<smsContent.size();i++){
   	 editor.putString("editedSms"+i, smsContent.get(i));
    }
    editor.commit(); 
}
private List<String> getPreferences(){
  List<String> params = new ArrayList<String>();
  SharedPreferences preference = getApplicationContext().getSharedPreferences("editedSmsContents", Context.MODE_PRIVATE);
  int listSize=preference.getInt("listSize", 0);
  for(int i=0;i<listSize;i++){
	   params.add(preference.getString("editedSms"+i, "default"));
  }
  return params;
}
@Override
public void onClick(View view, int position) {
	Tools.showTextToast(MyListViews.this, "点击了"+position+"位置");
}
}
