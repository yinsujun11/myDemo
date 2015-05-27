
package com.mydemo.view;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
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
import com.mydemo.view.RefreshListView.IListViewState;


public class SwipeListViewToRefresh extends ListView implements OnScrollListener{
	private Boolean mIsHorizontal;
	private String TAG=this.getClass().getSimpleName();
	private View mPreItemView;

	private View mCurrentItemView;

	private float mFirstX;

	private float mFirstY;

	private int mRightViewWidth;

	// private boolean mIsInAnimation = false;
	private final int mDuration = 100;

	private final int mDurationStep = 10;

	private boolean mIsShown;
	
	/***下拉刷新**/
	private View mHeadView;				
	private TextView mRefreshTextview;
	private TextView mLastUpdateTextView;
	private ImageView mArrowImageView;
	private ProgressBar mHeadProgressBar;
	private int mHeadContentHeight;
	private IOnRefreshListener mOnRefreshListener;			// 头部刷新监听器
	// 用于保证startY的值在一个完整的touch事件中只被记录一次
	private boolean mIsRecord = false;
	// 标记的Y坐标值
	private int mStartY = 0;
	// 当前视图能看到的第一个项的索引
	private int mFirstItemIndex = -1;
	// MOVE时保存的Y坐标值
	private int mMoveY = 0;
	/** LISTVIEW状态*/
	private int mViewState = IListViewState.LVS_NORMAL;
	private final static int RATIO = 2;
	private RotateAnimation animation;
	private RotateAnimation reverseAnimation;
	private boolean mBack = false;
	 private boolean isScrollToBottom; // 是否滑动到底部
	  private int footerViewHeight; // 脚布局的高度
	  private boolean isLoadingMore = false; // 是否正在加载更多中
	public SwipeListViewToRefresh(Context context) {
		this(context,null);
		init(context);
	}

	public SwipeListViewToRefresh(Context context, AttributeSet attrs) {
		this(context, attrs,0);
		init(context);
	}

	public SwipeListViewToRefresh(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
		TypedArray mTypedArray = context.obtainStyledAttributes(attrs,
				R.styleable.swipelistviewstyle);  

		//获取自定义属性和默认值  
		mRightViewWidth = (int) mTypedArray.getDimension(R.styleable.swipelistviewstyle_right_width, 200);   

		mTypedArray.recycle();  
	}
	private void init(Context context)
	{
		initHeadView(context);

		initLoadMoreView(context);

		setOnScrollListener(this);
	}
	// 初始化footview试图
	private void initLoadMoreView(Context context)
	{
		mFootView= LayoutInflater.from(context).inflate(R.layout.loadmore, null);
		mLoadMoreTextView = (TextView) mFootView.findViewById(R.id.load_more_tv);

		mLoadingView = mFootView.findViewById(R.id.loading_layout);
//		measureView(mFootView);
		footerViewHeight = mFootView.getMeasuredHeight();
//		hideFooterView();
		 mFootView.setPadding(0, -1*footerViewHeight, 0, 0);
		 isLoadingMore=false;
		addFooterView(mFootView,null,false);
	}
	// 初始化headview试图
		private void initHeadView(Context context)
		{
			mHeadView = LayoutInflater.from(context).inflate(R.layout.head, null);
			mArrowImageView = (ImageView) mHeadView.findViewById(R.id.head_arrowImageView);
			mArrowImageView.setMinimumWidth(60);
			mHeadProgressBar= (ProgressBar) mHeadView.findViewById(R.id.head_progressBar);

			mRefreshTextview = (TextView) mHeadView.findViewById(R.id.head_tipsTextView);

			mLastUpdateTextView = (TextView) mHeadView.findViewById(R.id.head_lastUpdatedTextView);

			measureView(mHeadView);
			mHeadContentHeight = mHeadView.getMeasuredHeight();
//			mHeadContentWidth = mHeadView.getMeasuredWidth();
			mHeadView.setPadding(0, -1 * mHeadContentHeight, 0, 0);
			//invalidate() 方法,让View无效,如果当前View有效,则发生重画事件. 就是重新绘制
			mHeadView.invalidate();
			addHeaderView(mHeadView, null, false);
			animation = new RotateAnimation(0, -180,
					RotateAnimation.RELATIVE_TO_SELF, 0.5f,
					RotateAnimation.RELATIVE_TO_SELF, 0.5f);
			animation.setInterpolator(new LinearInterpolator());
			animation.setDuration(250);
			animation.setFillAfter(true);

			reverseAnimation = new RotateAnimation(-180, 0,
					RotateAnimation.RELATIVE_TO_SELF, 0.5f,
					RotateAnimation.RELATIVE_TO_SELF, 0.5f);
			reverseAnimation.setInterpolator(new LinearInterpolator());
			reverseAnimation.setDuration(200);
			reverseAnimation.setFillAfter(true);
		}
		// 此方法直接照搬自网络上的一个下拉刷新的demo，计算headView的width以及height
		private void measureView(View child) {
			ViewGroup.LayoutParams p = child.getLayoutParams();
			if (p == null) {
				p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
			}
			int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
			int lpHeight = p.height;
			int childHeightSpec;
			if (lpHeight > 0) {
				childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
						MeasureSpec.EXACTLY);
			} else {
				childHeightSpec = MeasureSpec.makeMeasureSpec(0,
						MeasureSpec.UNSPECIFIED);
			}
			child.measure(childWidthSpec, childHeightSpec);
		}

	/**
	 * return false, can't move any direction. return true, cant't move
	 * vertical, can move horizontal. return super.onTouchEvent(ev), can move
	 * both.
	 */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		float lastX = ev.getX();
		float lastY = ev.getY();
//		if (mOnRefreshListener != null)
//		{
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			doActionDown(ev);
			mIsHorizontal = null;
			mFirstX = lastX;
			mFirstY = lastY;
			int motionPosition = pointToPosition((int)mFirstX, (int)mFirstY);
			Log.i(TAG, "motionPosition:"+motionPosition);
			if(mIsShown){
				hiddenRight(mCurrentItemView);
				return false;
			}
			if (motionPosition >= 0) {
				View currentItemView = getChildAt(motionPosition - getFirstVisiblePosition());
				mPreItemView = mCurrentItemView;
				mCurrentItemView = currentItemView;
			}else{
				System.out.println("0---> hiddenRight");
				hiddenRight(mCurrentItemView);
				mCurrentItemView=null;
			}
			break;

		case MotionEvent.ACTION_MOVE:
			float dx = lastX - mFirstX;
			float dy = lastY - mFirstY;
//			doActionMove(ev);
			// confirm is scroll direction
			if (mIsHorizontal == null) {
				if (!judgeScrollDirection(dx, dy)) {
					doActionMove(ev);
					break;
				}
			}

			if (mIsHorizontal) {
				if (mIsShown && mPreItemView != mCurrentItemView) {
					System.out.println("2---> hiddenRight");
					/**
					 * 情况二：
					 * <p>
					 * 一个Item的右边布局已经显示，
					 * <p>
					 * 这时候左右滑动另外一个item,那个右边布局显示的item隐藏其右边布局
					 * <p>
					 * 向左滑动只触发该情况，向右滑动还会触发情况五
					 */
					hiddenRight(mPreItemView);
				}

				if (mIsShown && mPreItemView == mCurrentItemView) {
					dx = dx - mRightViewWidth;
					System.out.println("======dx " + dx);
				}


				// can't move beyond boundary
				if (dx < 0 && dx > -mRightViewWidth) {
					if(mCurrentItemView!=null)
						mCurrentItemView.scrollTo((int)(-dx), 0);
				}

				return true;
			} else {
				doActionMove(ev);
				if (mIsShown) {
					/**
					 * 情况三：
					 * <p>
					 * 一个Item的右边布局已经显示，
					 * <p>
					 * 这时候上下滚动ListView,那么那个右边布局显示的item隐藏其右边布局
					 */
					hiddenRight(mPreItemView);
				}
			}

			break;

		case MotionEvent.ACTION_UP:
			doActionUp(ev);
		case MotionEvent.ACTION_CANCEL:
			clearPressedState();
			if (mIsShown) {
				System.out.println("4---> hiddenRight");
				/**
				 * 情况四：
				 * <p>
				 * 一个Item的右边布局已经显示，
				 * <p>
				 * 这时候左右滑动当前一个item,那个右边布局显示的item隐藏其右边布局
				 */
				hiddenRight(mPreItemView);
				return true;//如果有一个Item已经show了，直接返回true不让执行单击
			}

			if (mIsHorizontal != null && mIsHorizontal) {
				if (mFirstX - lastX >= mRightViewWidth / 2) {
					showRight(mCurrentItemView);
				} else {
					System.out.println("5---> hiddenRight");
					/**
					 * 情况五：
					 * <p>
					 * 向右滑动一个item,且滑动的距离超过了右边View的宽度的一半，隐藏之。
					 */
					hiddenRight(mCurrentItemView);
				}

				return true;
			}

			break;
		}
//		}
		boolean result=super.onTouchEvent(ev);//onTouchEvent中处理onItemClick事件
		Log.i(TAG, "onTouchEvent return->"+result);
		
		return result;
	}
	//	private boolean isHitCurItemLeft(float x) {
	//		return x < getWidth() - mRightViewWidth;
	//	}

	/**
	 * @param dx
	 * @param dy
	 * @return judge if can judge scroll direction
	 */
	private boolean judgeScrollDirection(float dx, float dy) {
		boolean canJudge = true;

		if (Math.abs(dx) > 30 && Math.abs(dx) > 2 * Math.abs(dy)) {
			mIsHorizontal = true;
			System.out.println("mIsHorizontal---->" + mIsHorizontal);
		} else if (Math.abs(dy) > 30 && Math.abs(dy) > 2 * Math.abs(dx)) {
			mIsHorizontal = false;
			canJudge=false;
			System.out.println("mIsHorizontal---->" + mIsHorizontal);
		} else {
			System.out.println("canJudged---->" + false);
			canJudge = false;
		}
		if(canJudge){
			Log.e(TAG, "judgeScrollDirection()->lock()");
			//			pullToRereshView.lock();
		}
		return canJudge;
	}



	private void clearPressedState() {
		// TODO current item is still has background, issue
		if(mCurrentItemView==null){
			return;
		}
		mCurrentItemView.setPressed(false);
		setPressed(false);
		refreshDrawableState();
		// invalidate();
	}

	private void showRight(View view) {
		if(view==null){
			return;
		}
		System.out.println("=========showRight");
		Message msg = new MoveHandler().obtainMessage();
		msg.obj = view;
		msg.arg1 = view.getScrollX();
		msg.arg2 = mRightViewWidth;
		msg.sendToTarget();
		mIsShown = true;
	}

	private void hiddenRight(View view) {
		if(view==null){
			return;
		}
		System.out.println("=========hiddenRight");
		if (mCurrentItemView == null) {
			return;
		}
		Message msg = new MoveHandler().obtainMessage();//
		msg.obj = view;
		msg.arg1 = view.getScrollX();
		msg.arg2 = 0;

		msg.sendToTarget();

		mIsShown = false;
	}
	public void hiddenRight(){
		System.out.println("1---> hiddenRight");
		if(mCurrentItemView!=null){
			mCurrentItemView.scrollTo(0, 0);
		}
	}
	/**
	 * show or hide right layout animation
	 */
	@SuppressLint("HandlerLeak")
	class MoveHandler extends Handler {
		int stepX = 0;

		int fromX;

		int toX;

		View view;

		private boolean mIsInAnimation = false;

		private void animatioOver(boolean right) {

			mIsInAnimation = false;
			stepX = 0;
			Log.e(TAG, "animatioOver()->unlock()");
			//			if(right){
			//				pullToRereshView.unlock();
			//			}
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (stepX == 0) {
				if (mIsInAnimation) {
					return;
				}
				mIsInAnimation = true;
				view = (View)msg.obj;
				fromX = msg.arg1;
				toX = msg.arg2;
				Log.i(TAG, "fromX:"+fromX);
				Log.i(TAG, "toX:"+toX);
				stepX = (int)((toX - fromX) * mDurationStep * 1.0 / mDuration);
				Log.i(TAG, "stepX:"+stepX);
				if (stepX < 0 && stepX > -1) {
					stepX = -1;
				} else if (stepX > 0 && stepX < 1) {
					stepX = 1;
				}
				if (Math.abs(toX - fromX) < 10) {
					view.scrollTo(toX, 0);
					animatioOver(toX==0);
					return;
				}
			}

			fromX += stepX;
			boolean isLastStep = (stepX > 0 && fromX > toX) || (stepX < 0 && fromX < toX);
			if (isLastStep) {
				fromX = toX;
			}

			view.scrollTo(fromX, 0);
			invalidate();

			if (!isLastStep) {
				this.sendEmptyMessageDelayed(0, mDurationStep);
			} else {
				animatioOver(toX==0);
			}
		}
	}

	public int getRightViewWidth() {
		return mRightViewWidth;
	}

	public void setRightViewWidth(int mRightViewWidth) {
		this.mRightViewWidth = mRightViewWidth;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
//		this.scrollState=scrollState;
		if (scrollState == SCROLL_STATE_IDLE
		        || scrollState == SCROLL_STATE_FLING) {
		      // 判断当前是否已经到了底部
		      if (isScrollToBottom && !isLoadingMore) {
		        isLoadingMore = true;
		        // 当前到底部
		        Log.i(TAG, "加载更多数据");
//		        mFootView.setPadding(0, 0, 0, 0);
		        this.setSelection(this.getCount());
		        if (mLoadMoreListener != null && mLoadMoreState == IListViewState.LVS_NORMAL)
				{
					updateLoadMoreViewState(ILoadMoreViewState.LMVS_LOADING);
					mLoadMoreListener.OnLoadMore();
				}
		      }
		    }
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		mFirstItemIndex = firstVisibleItem;
		   if (getLastVisiblePosition() == (totalItemCount - 1)) {
			      isScrollToBottom = true;
			    } else {
			      isScrollToBottom = false;
			    }
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


	// 切换headview视图
	private void switchViewState(int state)
	{	

		switch(state)
		{
		case IListViewState.LVS_NORMAL:
		{//正在状态
			Log.e("!!!!!!!!!!!", "convert to IListViewState.LVS_NORMAL");
			mArrowImageView.clearAnimation();
			mArrowImageView.setImageResource(R.drawable.ic_pulltorefresh_arrow);
		}
		break;
		case IListViewState.LVS_PULL_REFRESH:
		{
			//下拉刷新状态
			Log.e("!!!!!!!!!!!", "convert to IListViewState.LVS_PULL_REFRESH");
			mHeadProgressBar.setVisibility(View.GONE);
			mArrowImageView.setVisibility(View.VISIBLE);
			mRefreshTextview.setText("下拉可以刷新");
			mArrowImageView.clearAnimation();

			// 是由RELEASE_To_REFRESH状态转变来的
			if (mBack)
			{	//下拉的高度等于head高度
				mBack = false;
				mArrowImageView.clearAnimation();
				mArrowImageView.startAnimation(reverseAnimation);
			}
		}
		break;
		case IListViewState.LVS_RELEASE_REFRESH:
		{
			//下拉松开状态
			Log.e("!!!!!!!!!!!", "convert to IListViewState.LVS_RELEASE_REFRESH");
			mHeadProgressBar.setVisibility(View.GONE);
			mArrowImageView.setVisibility(View.VISIBLE);
			mRefreshTextview.setText("松开获取更多");
			mArrowImageView.clearAnimation();
			mArrowImageView.startAnimation(animation);
		}
		break;
		case IListViewState.LVS_LOADING:
		{	
			//下拉刷新松开加载状态
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
	public void setOnRefreshListener(IOnRefreshListener listener)
	{
		mOnRefreshListener = listener;
	}

	private void onRefresh()
	{
		if (mOnRefreshListener != null)
		{
			mOnRefreshListener.OnRefresh();
		}
	}

	public void onRefreshComplete()
	{
		mHeadView.setPadding(0,  -1 * mHeadContentHeight, 0, 0);
//		mLastUpdateTextView.setText("最近更新:" + new Date().toLocaleString());
		mLastUpdateTextView.setText("正在刷新...");
		switchViewState(IListViewState.LVS_NORMAL);
	}
	public interface IOnRefreshListener
	{
		void OnRefresh();
	}
	private View mFootView;								
	private TextView mLoadMoreTextView;
	private View mLoadingView;
	private IOnLoadMoreListener mLoadMoreListener;						// 加载更多监听器
	private int mLoadMoreState = IListViewState.LVS_NORMAL;
	public void setOnLoadMoreListener(IOnLoadMoreListener listener)
	{
		mLoadMoreListener = listener;
	}
	// flag 数据是否已全部加载完毕
		public void onLoadMoreComplete(boolean flag)
		{
			if (flag)
			{//加载完了
				updateLoadMoreViewState(ILoadMoreViewState.LMVS_OVER);
				hideFooterView();
			}else{
				updateLoadMoreViewState(ILoadMoreViewState.LMVS_NORMAL);
				hideFooterView();
			}

		}
	// 更新footview视图
		private void updateLoadMoreViewState(int state)
		{
			switch(state)
			{
			case ILoadMoreViewState.LMVS_NORMAL:
				mLoadingView.setVisibility(View.GONE);
				mLoadMoreTextView.setVisibility(View.VISIBLE);
//				mLoadMoreTextView.setText("查看更多");
				break;
			case ILoadMoreViewState.LMVS_LOADING:
				mLoadingView.setVisibility(View.VISIBLE);
				mLoadMoreTextView.setVisibility(View.GONE);
				break;
			case ILoadMoreViewState.LMVS_OVER:
				mLoadingView.setVisibility(View.GONE);
				mLoadMoreTextView.setVisibility(View.VISIBLE);
				mLoadMoreTextView.setText("加载完了！");
				break;
			default:
				break;
			}

			mLoadMoreState = state;
		}


		public void removeFootView()
		{
			removeFooterView(mFootView);
		}
	public interface ILoadMoreViewState
	{
		int LMVS_NORMAL= 0;					//  普通状态
		int LMVS_LOADING = 1;				//  加载状态
		int LMVS_OVER = 2;					//  结束状态
	}

	public interface IOnLoadMoreListener
	{
		void OnLoadMore();
	}
	  /**
	   * 隐藏脚布局
	   */
	  public void hideFooterView() {
//	    mFootView.setPadding(0, -footerViewHeight, 0, 0);
		  mFootView.setPadding(0, 0, 0, -footerViewHeight);
	    isLoadingMore = false;
	  }
}
