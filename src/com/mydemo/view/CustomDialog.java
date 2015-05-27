package com.mydemo.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;

import com.mydemo.R;

public class CustomDialog extends Dialog{

    private Button positiveButton, negativeButton;
    public CustomDialog(Context context) {
        super(context,R.style.Theme_dialog);
        setCustomDialog(context);
    }
    private void setCustomDialog(Context context) {
        View mView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_custom, null);
        positiveButton = (Button) mView.findViewById(R.id.dialog_custom_bt_sure);
//        negativeButton = (Button) mView.findViewById(R.id.negativeButton);
        super.setContentView(mView);
        
    }
//    private void setCustomDialog(Context context, int width, int height) {
//        View mView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_custom, null);
//        title = (TextView) mView.findViewById(R.id.title);
//        editText = (EditText) mView.findViewById(R.id.number);
//        positiveButton = (Button) mView.findViewById(R.id.positiveButton);
//        negativeButton = (Button) mView.findViewById(R.id.negativeButton);
//        
//        Window window =getWindow();
//        WindowManager.LayoutParams params = window.getAttributes();
//        float density = getDensity(context);
//        params.width = (int) (width*density);
//        params.height = (int) (height*density);
//        params.gravity = Gravity.CENTER;
//        window.setAttributes(params);
//        super.setContentView(mView);
//    }
//    private float getDensity(Context context) {
//    	Resources resources = context.getResources();
//    	DisplayMetrics dm = resources.getDisplayMetrics();
//    	return dm.density;
//    	}
     
     @Override
    public void setContentView(int layoutResID) {
    }
 
    @Override
    public void setContentView(View view, LayoutParams params) {
    }
 
    @Override
    public void setContentView(View view) {
    }
 
    /**
     * 确定键监听器
     * @param listener
     */ 
    public void setOnPositiveListener(View.OnClickListener listener){ 
        positiveButton.setOnClickListener(listener); 
    } 
    /**
     * 取消键监听器
     * @param listener
     */ 
//    public void setOnNegativeListener(View.OnClickListener listener){ 
//        negativeButton.setOnClickListener(listener); 
//    }


}
