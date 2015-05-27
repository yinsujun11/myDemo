package com.mydemo.activity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mydemo.R;

public class AtyTestTime extends Activity{
	private TextView tvCount;
	private Button btCount;
	private EditText etTime;
	long time;
	Long timeSava;
@Override
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.time_test_activity);
	initView();
	time = new Date().getTime();
//	saveWithdrawDepositTime("7",time);
	
	Map<String, Object> preferences = getPreferences();
	final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	timeSava = (Long) preferences.get("withdrawDepositTime");
	String courierId = (String) preferences.get("courierId");
	etTime.setText(sdf.format(new Date(timeSava)));
	
	tvCount.setText(sdf.format(new Date(timeSava)));
	btCount.setOnClickListener(new OnClickListener() {
		
		@SuppressLint("SimpleDateFormat") @Override
		public void onClick(View v) {
			String format = sdf.format(time);
//			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			try
//			{
//			    Date d1 = df.parse("2015-04-13 15:42:05");
//			    Date d2 = df.parse(etTime.getText().toString());
//			    long diff = d1.getTime() - d2.getTime();
//			    long days = diff / (1000 * 60 * 60 * 24);
//			    tvCount.setText("时间差为："+days);
//			}
//			catch (Exception e)
//			{
//			}
			tvCount.setText(sdf.format(new Date(timeSava))+"与上次时间差为："+getDistanceDays(etTime.getText().toString(), format));
			
			
		}
	});
	
	
	List<String> pList=new ArrayList<String>();
	for(int i=0;i<5;i++){
		pList.add("yin"+1);
	}
	saveEditedSms2("yin",pList);
	
	Map<String, List<String>> preferences2 = getPreferences2();
	Log.e("保存数据", preferences2.size()+"");
	
}
private void initView(){
	tvCount=(TextView) findViewById(R.id.tv_time_count);
	btCount=(Button) findViewById(R.id.bt_count);
	etTime=(EditText) findViewById(R.id.et_time);
}
/**
 * 获得两个时间差
 * @param str1
 * @param str2
 * @return
 */
public  long getDistanceDays(String str1, String str2){  
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
    Date one;  
    Date two;  
    long days=0;  
    try {  
        one = df.parse(str1);  
        two = df.parse(str2);  
        long time1 = one.getTime();  
        long time2 = two.getTime();  
        long diff ;  
        if(time1<time2) {  
            diff = time2 - time1;  
        } else {  
            diff = time1 - time2;  
        }  
        days = diff / (1000 * 60 * 60 * 24);  
    } catch (ParseException e) {  
        e.printStackTrace();  
    }  
    return days;  
}
private void saveWithdrawDepositTime(String courierId, long time){
    SharedPreferences preferences = getApplicationContext().getSharedPreferences("withdrawDepositTime", Context.MODE_PRIVATE); 
    Editor editor = preferences.edit(); 
    editor.putString("courierId", courierId);
    editor.putLong("withdrawDepositTime", time);
    editor.commit(); 
}
private Map<String, Object> getPreferences(){
  Map<String, Object> params = new HashMap<String, Object>();
  SharedPreferences preference = getApplicationContext().getSharedPreferences("withdrawDepositTime", Context.MODE_PRIVATE);
  params.put("courierId", preference.getString("courierId", "default"));
  params.put("withdrawDepositTime", preference.getLong("withdrawDepositTime",0L));
  return params;
}
public String getDistanceTime(String str1, String str2){  
	SimpleDateFormat  df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
    Date one;  
    Date two;  
    long day = 0;  
    long hour = 0;  
    long min = 0;  
    long sec = 0;  
    try {  
        one = df.parse(str1);  
        two = df.parse(str2);  
        long time1 = one.getTime();  
        long time2 = two.getTime();  
        long diff ;  
        if(time1<time2) {  
            diff = time2 - time1;  
        } else {  
            diff = time1 - time2;  
        }  
        day = diff / (24 * 60 * 60 * 1000);  
        hour = (diff / (60 * 60 * 1000) - day * 24);  
        min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);  
        sec = (diff/1000-day*24*60*60-hour*60*60-min*60);  
    } catch (ParseException e) {  
        e.printStackTrace();  
    }  
    return day + "天" + hour + "小时" + min + "分" + sec + "秒";  
}  

/**
 * 获得两个时间差数组
 * @param str1
 * @param str2
 * @return
 */
public long[] getDistanceTimes(String str1, String str2) {  
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
    Date one;  
    Date two;  
    long day = 0;  
    long hour = 0;  
    long min = 0;  
    long sec = 0;  
    try {  
        one = df.parse(str1);  
        two = df.parse(str2);  
        long time1 = one.getTime();  
        long time2 = two.getTime();  
        long diff ;  
        if(time1<time2) {  
            diff = time2 - time1;  
        } else {  
            diff = time1 - time2;  
        }  
        day = diff / (24 * 60 * 60 * 1000);  
        hour = (diff / (60 * 60 * 1000) - day * 24);  
        min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);  
        sec = (diff/1000-day*24*60*60-hour*60*60-min*60);  
    } catch (ParseException e) {  
        e.printStackTrace();  
    }  
    long[] times = {day, hour, min, sec};  
    return times;  
} 


private void saveEditedSms2(String courierId,List<String> smsContent){
    SharedPreferences preferences = getApplicationContext().getSharedPreferences("editedSmsContents", Context.MODE_PRIVATE); 
    Editor editor = preferences.edit(); 
    editor.putString("courierId", courierId);
    editor.putInt("listSize", smsContent.size());
    for(int i=0;i<smsContent.size();i++){
   	 editor.putString("editedSms"+i, smsContent.get(i));
    }
    editor.commit(); 
}
private Map<String,List<String>> getPreferences2(){
  List<String> paramsList = new ArrayList<String>();
  Map<String,List<String>> params=new HashMap<String, List<String>>();
  SharedPreferences preference = getApplicationContext().getSharedPreferences("editedSmsContents", Context.MODE_PRIVATE);
  int listSize=preference.getInt("listSize", 0);
  for(int i=0;i<listSize;i++){
	   paramsList.add(preference.getString("editedSms"+i, "default"));
	   params.put(preference.getString("courierId", "default"),paramsList);
  }
  return params;
}
}
