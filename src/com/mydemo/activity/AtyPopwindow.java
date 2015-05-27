package com.mydemo.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListPopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.mydemo.R;

public class AtyPopwindow extends Activity{
	private TextView txt;  
    private ListPopupWindow lpw;  
    private EditText et;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.aty_window);
    	et=(EditText) findViewById(R.id.window_et);
    	et.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				if(s.length()>4){
					showWindow(et);
				}
			}
		});
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB) @SuppressLint("NewApi") private void showWindow(View v){
    	lpw = new ListPopupWindow(AtyPopwindow.this);  
        final String[] strs = { "编辑", "清空" };  
        lpw.setOnItemClickListener(new OnItemClickListener() {  
            @SuppressLint("NewApi") @Override  
            public void onItemClick(AdapterView<?> parent, View view,  
                    int position, long id) {  
                Toast.makeText(AtyPopwindow.this, strs[position],  
                        Toast.LENGTH_SHORT).show();  
            }  
        });  

        lpw.setAdapter(new ArrayAdapter<String>(AtyPopwindow.this,  
                R.layout.test_window,R.id.test_tv, strs));  
        View anchor = v;  
        if (anchor != null) {  
            lpw.setAnchorView(anchor);// 设置ListPopupWindow漂浮于anchor上，不写会导致空指针  
        }  
    	lpw.setWidth(LayoutParams.MATCH_PARENT);
    	lpw.setHeight(LayoutParams.WRAP_CONTENT);
        lpw.show(); 
    }
    
//	private void setPopwindow(){
//		 lpw = new ListPopupWindow(AtyPopwindow.this);  
//         final String[] strs = { "编辑", "清空" };  
//         lpw.setOnItemClickListener(new OnItemClickListener() {  
//             @Override  
//             public void onItemClick(AdapterView<?> parent, View view,  
//                     int position, long id) {  
//                 Toast.makeText(AtyPopwindow.this, strs[position],  
//                         Toast.LENGTH_SHORT).show();  
//             }  
//         });  
//
//         lpw.setAdapter(new ArrayAdapter<String>(AtyPopwindow.this,  
//                 R.layout.test_window, strs));  
//         View anchor = v;  
//         if (anchor != null) {  
//             lpw.setAnchorView(anchor);// 设置ListPopupWindow漂浮于anchor上，不写会导致空指针  
//         }  
//         lpw.setWidth(100);  
//         lpw.show();  
//	}

}
