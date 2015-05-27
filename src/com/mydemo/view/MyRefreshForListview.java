package com.mydemo.view;

import java.util.Date;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mydemo.R;

public class MyRefreshForListview extends ListView implements OnScrollListener {
	private View mHeadView, mFooterView;
	/** 普通状态 */
	private static final int LVS_NORMAL = 0;
	/** 下拉刷新状态 */
	private static final int LVS_PULL_REFRESH = 1;
	/** 松开刷新状态 */
	private static final int LVS_RELEASE_REFRESH = 2;
	/** 加载状态 */
	private static final int LVS_LOADING = 3;
	private RotateAnimation animation;
	private RotateAnimation reverseAnimation;
	// 标记的Y坐标值
	private int mStartY = 0;
	// 当前视图能看到的第一个项的索引
	private int mFirstItemIndex = -1;
	// MOVE时保存的Y坐标值
	private int mMoveY = 0;
	private ImageView mArrowImageView;
	private TextView mRefreshTextview;
	private TextView mLastUpdateTextView;
	private ProgressBar mHeadProgressBar;
	/**
	 * header view height
	 */
	private int mHeaderViewHeight;
	private int mHeadContentWidth;
	private int mHeadContentHeight;
	/** 用于保证startY的值在一个完整的touch事件中只被记录一次 */
	private boolean mIsRecord = false;
	private static int LIST_STATUS = LVS_NORMAL;
	private boolean mBack = false;
	private IOnRefreshListener mOnRefreshListener; // 头部刷新监听器
	private final static int RATIO = 2;
	/** LISTVIEW状态 */
	private int mViewState = IListViewState.LVS_NORMAL;
	
	// 标记的X坐标值
		private int mStartX = 0;
		// MOVE时保存的X坐标值
		private int mMoveX = 0;

	public MyRefreshForListview(Context context) {
		super(context);
		initView(context);
	}

	public MyRefreshForListview(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		this.mFirstItemIndex = firstVisibleItem;

	}

	private void initView(Context context) {
		addHeadView(context);
		setOnScrollListener(this);
	}

	private void addHeadView(Context context) {
		mHeadView = LayoutInflater.from(context).inflate(R.layout.head, null);
		mArrowImageView = (ImageView) mHeadView
				.findViewById(R.id.head_arrowImageView);
		mArrowImageView.setMinimumWidth(60);
		mHeadProgressBar = (ProgressBar) mHeadView
				.findViewById(R.id.head_progressBar);
		mRefreshTextview = (TextView) mHeadView
				.findViewById(R.id.head_tipsTextView);
		mLastUpdateTextView = (TextView) mHeadView
				.findViewById(R.id.head_lastUpdatedTextView);
		measureView(mHeadView);
		// mHeaderViewHeight = mHeadView.getMeasuredHeight();
		// topPadding(-mHeaderViewHeight);
		// this.addHeaderView(mHeadView);
		mHeadContentHeight = mHeadView.getMeasuredHeight();
		mHeadContentWidth = mHeadView.getMeasuredWidth();

		mHeadView.setPadding(0, -1 * mHeadContentHeight, 0, 0);
		//invalidate()的调用是把之前的旧的view从主UI线程队列中pop掉。 
		            //--但是Invalidate不能直接在线程中调用，因为他是违背了单线程模型：Android UI操作并不是线程安全的，并且这些操作必须在UI线程中调用。
		mHeadView.invalidate();
		addHeaderView(mHeadView, null, false);
		/**
		 * public RotateAnimation (float fromDegrees, float toDegrees, int pivotXType, float pivotXValue, int pivotYType, float pivotYValue)
		 * 
		 *  fromDegrees：旋转的开始角度。
			toDegrees：旋转的结束角度。
			pivotXType：X轴的伸缩模式，可以取值为ABSOLUTE、RELATIVE_TO_SELF、RELATIVE_TO_PARENT。
			pivotXValue：X坐标的伸缩值。
			pivotYType：Y轴的伸缩模式，可以取值为ABSOLUTE、RELATIVE_TO_SELF、RELATIVE_TO_PARENT。
			pivotYValue：Y坐标的伸缩值
		 */
		// 设置旋转的开始位置和结束位置；以及旋转的圆心位置。  0.5f一半
		animation = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animation.setInterpolator(new LinearInterpolator());
		// 设置旋转的时间  
		animation.setDuration(250);
		// 如果不设置该属性，系统默认在View完成旋转之后，会回到最初的状态。设置为true之后，就可以保持旋转之后的状态。  
		animation.setFillAfter(true);

		reverseAnimation = new RotateAnimation(-180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		reverseAnimation.setInterpolator(new LinearInterpolator());
		reverseAnimation.setDuration(200);
		reverseAnimation.setFillAfter(true);
	}

	// 设置header布局的上边距
	private void topPadding(int topPadding) {
		mHeadView.setPadding(mHeadView.getPaddingLeft(), topPadding,
				mHeadView.getPaddingRight(), mHeadView.getPaddingBottom());
		mHeadView.invalidate();
	}

	// 通知父布局占用的宽，高
	private void measureView(View view) {
		ViewGroup.LayoutParams p = view.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int width = ViewGroup.getChildMeasureSpec(0, 0, p.width);
		int height;
		int tempHeight = p.height;
		if (tempHeight > 0) {
			/**
			 * 1、MeasureSpec.EXACTLY是精确尺寸，
			 * 当我们将控件的layout_width或layout_height指定为具体数值时如andorid
			 * :layout_width="50dip"， 或者为FILL_PARENT是，都是控件大小已经确定的情况，都是精确尺寸。
			 * 
			 * 2、MeasureSpec.AT_MOST是最大尺寸，
			 * 当控件的layout_width或layout_height指定为WRAP_CONTENT时
			 * ，控件大小一般随着控件的子空间或内容进行变化，
			 * 此时控件尺寸只要不超过父控件允许的最大尺寸即可。因此，此时的mode是AT_MOST，size给出了父控件允许的最大尺寸。
			 * 
			 * 3、MeasureSpec.UNSPECIFIED是未指定尺寸，这种情况不多，一般都是父控件是AdapterView，
			 * 通过measure方法传入的模式。
			 */
			height = MeasureSpec.makeMeasureSpec(tempHeight,
					MeasureSpec.EXACTLY);
		} else {
			height = MeasureSpec.makeMeasureSpec(tempHeight,
					MeasureSpec.UNSPECIFIED);
		}
		view.measure(width, height);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			doActionDown(ev);
			break;
		case MotionEvent.ACTION_MOVE:
			doActionMove(ev);
			break;
		case MotionEvent.ACTION_UP:
			doActionUp(ev);
			break;
		default:
			break;
		}
		return super.onTouchEvent(ev);
	}

	/**
	 * 按下
	 * 
	 * @param ev
	 */
	private void doActionDown(MotionEvent ev) {
		if (mIsRecord == false && mFirstItemIndex == 0) {
			mStartY = (int) ev.getY();
			mIsRecord = true;
		}
	}

	/**
	 * 移动
	 * 
	 * @param ev
	 */
	private void doActionMove(MotionEvent ev) {
		mMoveY = (int) ev.getY();

		if (mIsRecord == false && mFirstItemIndex == 0) {
			mStartY = (int) ev.getY();
			mIsRecord = true;
		}
		if (mIsRecord == false || mViewState == IListViewState.LVS_LOADING) {
			return;
		}
		// ????????????
		int offset = (mMoveY - mStartY) / RATIO;

		switch (mViewState) {
		case IListViewState.LVS_NORMAL: {
			if (offset > 0) { // 滑到屏幕中间 开始下拉刷新事件
				mHeadView.setPadding(0, offset - mHeadContentHeight, 0, 0);
				switchViewState(IListViewState.LVS_PULL_REFRESH);
			}
		}
			break;
		case IListViewState.LVS_PULL_REFRESH: {
			setSelection(0);
			mHeadView.setPadding(0, offset - mHeadContentHeight, 0, 0);
			if (offset < 0) {
				switchViewState(IListViewState.LVS_NORMAL);
			} else if (offset > mHeadContentHeight) {
				switchViewState(IListViewState.LVS_RELEASE_REFRESH);
			}
		}
			break;
		case IListViewState.LVS_RELEASE_REFRESH: {
			setSelection(0);
			mHeadView.setPadding(0, offset - mHeadContentHeight, 0, 0);
			if (offset >= 0 && offset <= mHeadContentHeight) {
				mBack = true;
				switchViewState(IListViewState.LVS_PULL_REFRESH);
			} else if (offset < 0) {
				switchViewState(IListViewState.LVS_NORMAL);
			} else {

			}

		}
			break;
		default:
			return;
		}
		;

	}

	private void doActionUp(MotionEvent ev) {
		mIsRecord = false;
		mBack = false;

		if (mViewState == IListViewState.LVS_LOADING) {
			return;
		}

		switch (mViewState) {
		case IListViewState.LVS_NORMAL:
			break;
		case IListViewState.LVS_PULL_REFRESH:
			mHeadView.setPadding(0, -1 * mHeadContentHeight, 0, 0);
			switchViewState(IListViewState.LVS_NORMAL);
			break;
		case IListViewState.LVS_RELEASE_REFRESH:
			mHeadView.setPadding(0, 0, 0, 0);
			switchViewState(IListViewState.LVS_LOADING);
			onRefresh();
			break;
		}

	}

	/**
	 * 切换head视图
	 * 
	 * @param state
	 */
	// 切换headview视图
	private void switchViewState(int state) {

		switch (state) {
		case IListViewState.LVS_NORMAL: {// 正在状态
			Log.e("!!!!!!!!!!!", "convert to IListViewState.LVS_NORMAL");
			mArrowImageView.clearAnimation();
			mArrowImageView.setImageResource(R.drawable.ic_pulltorefresh_arrow);
		}
			break;
		case IListViewState.LVS_PULL_REFRESH: {
			// 下拉刷新状态
			Log.e("!!!!!!!!!!!", "convert to IListViewState.LVS_PULL_REFRESH");
			mHeadProgressBar.setVisibility(View.GONE);
			mArrowImageView.setVisibility(View.VISIBLE);
			mRefreshTextview.setText("下拉可以刷新");
			mArrowImageView.clearAnimation();

			// 是由RELEASE_To_REFRESH状态转变来的
			if (mBack) { // 下拉的高度等于head高度
				mBack = false;
				mArrowImageView.clearAnimation();
				mArrowImageView.startAnimation(reverseAnimation);
			}
		}
			break;
		case IListViewState.LVS_RELEASE_REFRESH: {
			// 下拉松开状态
			Log.e("!!!!!!!!!!!",
					"convert to IListViewState.LVS_RELEASE_REFRESH");
			mHeadProgressBar.setVisibility(View.GONE);
			mArrowImageView.setVisibility(View.VISIBLE);
			mRefreshTextview.setText("松开获取更多");
			mArrowImageView.clearAnimation();
			mArrowImageView.startAnimation(animation);
		}
			break;
		case IListViewState.LVS_LOADING: {
			// 下拉刷新松开加载状态
			Log.e("!!!!!!!!!!!", "convert to IListViewState.LVS_LOADING");
			mHeadProgressBar.setVisibility(View.VISIBLE);
			mArrowImageView.clearAnimation();
			mArrowImageView.setVisibility(View.GONE);
			mRefreshTextview.setText("载入中...");
		}
			break;
		default:
			return;
		}

		mViewState = state;

	}

	/*************************************************
	 * 相关接口
	 *************************************************/

	public interface IListViewState {
		/** 普通状态 */
		int LVS_NORMAL = 0;
		/** 下拉刷新状态 */
		int LVS_PULL_REFRESH = 1;
		/** 松开刷新状态 */
		int LVS_RELEASE_REFRESH = 2;
		/** 加载状态 */
		int LVS_LOADING = 3;
	}

	private void onRefresh() {
		if (mOnRefreshListener != null) {
			mOnRefreshListener.OnRefresh();
		}
	}

	public interface IOnRefreshListener {
		void OnRefresh();
	}

	public void setOnRefreshListener(IOnRefreshListener listener) {
		mOnRefreshListener = listener;
	}

	public void onRefreshComplete() {
		mHeadView.setPadding(0, -1 * mHeadContentHeight, 0, 0);
		mLastUpdateTextView.setText("最近更新:" + new Date().toLocaleString());
		switchViewState(IListViewState.LVS_NORMAL);
	}
}
