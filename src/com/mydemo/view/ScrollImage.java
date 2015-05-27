package com.mydemo.view;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.mydemo.R;

public class ScrollImage extends RelativeLayout {	
	
	private ImageScrollView imageScrollView = null;
	private PageControlView pageControlView = null;

	public ScrollImage(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.scroll_image, ScrollImage.this);
		
		imageScrollView = (ImageScrollView) this.findViewById(R.id.myImageScrollView);

		pageControlView = (PageControlView) this.findViewById(R.id.myPageControlView);
	}
	
	/**
	 * 设置显示图片
	 * @param list 图片集合
	 */
	public void setBitmapList(List<Bitmap> list){
		int num = list.size();
		imageScrollView.removeAllViews();
		for(int i = 0; i < num; i++){
			ImageView imageView = new ImageView(getContext());
			imageView.setImageBitmap(list.get(i));
			imageScrollView.addView(imageView);
		}
		/** 设置圆圈的数量 **/
		pageControlView.setCount(imageScrollView.getChildCount());
		/** 初始化圆圈 **/
		pageControlView.generatePageControl(0);
		/** 设置视图切换回调函数实现 **/
		imageScrollView.setScrollToScreenCallback(pageControlView);
	}
	
	/**
	 * 设置组件的高
	 * @param height 高
	 */
	public void setHeight(int height){
		android.view.ViewGroup.LayoutParams la = getLayoutParams();
		la.height = height;
		android.view.ViewGroup.LayoutParams lap = imageScrollView.getLayoutParams();
		lap.height = height;
	}
	
	/**
	 * 设置组件的宽
	 * @param height 宽
	 */
	public void setWidth(int width){
		android.view.ViewGroup.LayoutParams la = getLayoutParams();
		la.width = width;
		android.view.ViewGroup.LayoutParams lap = imageScrollView.getLayoutParams();
		lap.width = width;
	}
	
	/**
	 * 图片开始滚动
	 * @param time 滚动频率，单位：毫秒
	 */
	public void start(int time){
		imageScrollView.start(time);
	}
	
	/**
	 * 图片停止滚动
	 */
	public void stop(){
		imageScrollView.stop();
	}
	
	public int getPosition(){
		return imageScrollView.getCurrentScreenIndex();
	}

	/**
	 * 设置监听方法
	 * @param clickListener
	 */
	public void setClickListener(OnClickListener clickListener) {
		imageScrollView.setClickListener(clickListener);
	}
}
