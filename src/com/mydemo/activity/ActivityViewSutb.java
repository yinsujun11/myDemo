package com.mydemo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.TextView;

import com.mydemo.R;

public class ActivityViewSutb extends Activity{
	private ViewStub view_stub;
	private TextView textView1;
	private Button button1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_stub);
		view_stub=(ViewStub) findViewById(R.id.view_stub);
		textView1=(TextView) findViewById(R.id.textView1);
		view_stub.setVisibility(View.VISIBLE);
		button1=(Button) findViewById(R.id.button1);
		button1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				textView1.setText("拜拜，小白");
				view_stub.setVisibility(View.GONE);
			}
		});
		
	}
}
