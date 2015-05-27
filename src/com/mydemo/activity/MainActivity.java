package com.mydemo.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;

import com.mydemo.R;
import com.mydemo.fragment.IndexFragment;
import com.mydemo.fragment.LeftFragment;
import com.mydemo.sildingmenu.view.SlidingMenu;

public class MainActivity extends FragmentActivity{
	private SlidingMenu mSlidingMenu;
	private LeftFragment leftFragment;
	private IndexFragment indexFragment;
	private AtyViewpagerMain viewPagerFragment;
    @SuppressLint("ClickableViewAccessibility") @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
//        mSlidingMenu = (SlidingMenu) findViewById(R.id.slidingMenu);
//    	mSlidingMenu.setLeftView(getLayoutInflater().inflate(
//				R.layout.left_frame, null));
//    	mSlidingMenu.setCenterView(getLayoutInflater().inflate(
//				R.layout.center_frame, null));
    	FragmentTransaction t = this.getSupportFragmentManager()
				.beginTransaction();
//		leftFragment = new LeftFragment();
//		t.replace(R.id.left_frame, leftFragment);
//		indexFragment=new IndexFragment();
//		t.replace(R.id.center_frame, indexFragment);
//		t.commit();
//		viewPagerFragment=new AtyViewpagerMain();
//		t.replace(R.id.center_frame, viewPagerFragment);
//		t.commit();
    	indexFragment=new IndexFragment();
		t.replace(R.id.content, indexFragment);
		t.commit();
    }
    
	private void showLeft() {
		mSlidingMenu.showLeftView();
	}
    }

