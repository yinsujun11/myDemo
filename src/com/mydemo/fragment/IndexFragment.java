package com.mydemo.fragment;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mydemo.R;
import com.mydemo.activity.MainActivity;
import com.mydemo.app.MyApplication;
import com.mydemo.view.CustomDialog;
import com.mydemo.view.ScrollImage;


public class IndexFragment extends Fragment implements OnClickListener{
	private TextView tv_location; 
	private Button bt_custom_dialog;
	private CustomDialog dialog;
	private ScrollImage scrollImage;;
	private ProgressBar activity_head_progress;
	private ImageView activity_head_iv_personal;
	private ListView lv;
	private MyAdapter mAdapter;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View indexFragment=inflater.inflate(R.layout.activity_main, null);
		initView(indexFragment);
		setData();
		setEvent();
		return indexFragment;
	}
	
	  private void initView(View view){
	    	 tv_location=(TextView) view.findViewById(R.id.tv_location);
	         bt_custom_dialog=(Button) view.findViewById(R.id.bt_custom_dialog);
	         dialog=new CustomDialog(getActivity());
	         scrollImage = (ScrollImage) view.findViewById(R.id.simage);
	         activity_head_progress=(ProgressBar) view.findViewById(R.id.activity_head_progress);
	         activity_head_iv_personal=(ImageView) view.findViewById(R.id.activity_head_iv_personal);
	         lv=(ListView) view.findViewById(R.id.lv);
	    }
	    private void setData(){
	    	((MyApplication)getActivity().getApplication()).mLocationResult = tv_location;
	    	List<Bitmap> list = new ArrayList<Bitmap>();
	 		list.add(BitmapFactory.decodeResource(getResources(), R.drawable.a1));
	 		list.add(BitmapFactory.decodeResource(getResources(), R.drawable.a2));
	 		list.add(BitmapFactory.decodeResource(getResources(), R.drawable.a3));
	 		int width = getActivity().getWindowManager().getDefaultDisplay().getWidth();
	 		scrollImage.setHeight((int)(1.0*width*list.get(0).getHeight()/list.get(0).getWidth()));
	 		scrollImage.setBitmapList(list);
	 		scrollImage.start(3000);
	 		scrollImage.setClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					int position = scrollImage.getPosition();
					Toast.makeText(getActivity(), "您点击的是第"+(position+1)+"个图片！", Toast.LENGTH_LONG).show();				
				}
			});
	 		List<String> data=new ArrayList<String>();
			List<String> data2=new ArrayList<String>();
			for(int i=0;i<20;i++){
				String str="哈哈"+i;
				data.add(str);
			}
			for(int i=0;i<7;i++){
				String str="嘻嘻"+i;
				data2.add(str);
			}
			mAdapter=new MyAdapter(data);
			lv.setAdapter(mAdapter);
			mAdapter.notifyDataSetChanged();
	    }
	    private void setEvent(){
	    	bt_custom_dialog.setOnClickListener(this);
	    	activity_head_iv_personal.setOnClickListener(this);
	    	lv.setOnTouchListener(new MyTouchLinster());
	    }
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.bt_custom_dialog:
//						final EditText editText = (EditText) dialog.getEditText();//方法在CustomDialog中实现
					    dialog.setOnPositiveListener(new OnClickListener() {
					        @Override
					        public void onClick(View v) {
//					        	Toast.makeText(MainActivity.this, editText.getText().toString(), Toast.LENGTH_SHORT).show();
					        	dialog.dismiss();
					        }
					    });
					    dialog.show();
					    dialog.getWindow().setLayout(((int)(getActivity().getResources().getDisplayMetrics().widthPixels* 0.85)), LayoutParams.WRAP_CONTENT);  
				break;
			case R.id.activity_head_iv_personal:
//				((MainActivity) getActivity()).showLeft();
				break;
			default:
				break;
			}
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
					convertView=getActivity().getLayoutInflater().inflate(R.layout.test_list_item, null);
					mHolder.tv=(TextView) convertView.findViewById(R.id.tv);
					convertView.setTag(mHolder);
				}
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
					if(dx>20){
						Log.e("xhuadong", dx+"hudong");
					}
					break;
				case MotionEvent.ACTION_UP:
					break;
				default:
					break;
				}
				return true;
			}
			
		}
		@Override
		public void onDestroyView() {
			super.onDestroyView();
			System.out.println("=========onDestroyView");
		}
		@Override
		public void onDestroy() {
			super.onDestroy();
			System.out.println("=========destory");
		}
		@Override
		public void onDetach() {
			super.onDetach();
			System.out.println("=========onDetach");
		}
		
}
