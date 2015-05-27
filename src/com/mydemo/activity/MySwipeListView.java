package com.mydemo.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.mydemo.R;
import com.mydemo.adapter.MyAdapter;
import com.mydemo.utils.Tools;
import com.mydemo.view.SwipeListViewToRefresh;
/**
 * 滑动删除+上拉刷新 下拉加载
 * @author kuaiditu
 *
 */
public class MySwipeListView extends Activity implements SwipeListViewToRefresh.IOnRefreshListener,SwipeListViewToRefresh.IOnLoadMoreListener,MyAdapter.onDeleteListener{
	private SwipeListViewToRefresh swipeListView;
	private List<String> listData=new ArrayList<String>();
	private MyAdapter mAdapter;
	private LoadMoreDataAsynTask loadMore;
	private RefreshDataAsynTask refresh;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_swipelistview);
		swipeListView=(SwipeListViewToRefresh) findViewById(R.id.my_swipeListView);
		for(int i=0;i<20;i++){
			listData.add("我的listview"+i);
		}
		mAdapter=new MyAdapter(MySwipeListView.this,listData);
		mAdapter.setRightWidth(swipeListView.getRightViewWidth());
		swipeListView.setAdapter(mAdapter);
		mAdapter.setOnDeleteLinsener(this);
		swipeListView.setOnRefreshListener(this);
		swipeListView.setOnLoadMoreListener(this);
		swipeListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Tools.showTextToast(MySwipeListView.this, position+"内容"+mAdapter.getItem(position-(swipeListView.getHeaderViewsCount())).toString());
			}
			
		});
		
	}
	@Override
	public void OnRefresh() {
		refresh=new RefreshDataAsynTask();
		refresh.execute();
	}
	@Override
	public void delete(View view, int position) {
		listData.remove(position);
		mAdapter.notifyDataSetChanged();
		swipeListView.hiddenRight();
	}
	@Override
	public void OnLoadMore() {
//		List<String> addList=new ArrayList<String>();
//		for(int i=0;i<5;i++){
//			addList.add("加载更多"+i);
//		}
//		mAdapter.addAll(addList);
//		swipeListView.onLoadMoreComplete(false);
//		swipeListView.hideFooterView();
		loadMore=new LoadMoreDataAsynTask();
		loadMore.execute();
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
			mAdapter.addAll(data);
			swipeListView.onRefreshComplete();
		}
	}
	
	
	
	private int pos = 0;
	
	class LoadMoreDataAsynTask extends AsyncTask<Void , Void, Void>
	{
		List<String> data=new ArrayList<String>();
		@Override
		protected Void doInBackground(Void... arg0) {
			
			
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			pos++;
			data.add("Kevin" + pos);
			
			
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			mAdapter.addAll(data);
			if (pos > 5)
			{
				swipeListView.onLoadMoreComplete(true);
//				Toast.makeText(MySwipeListView.this, "没有更多数据！", duration)
			}else{
				swipeListView.onLoadMoreComplete(false);
			}
		}
	}
}
