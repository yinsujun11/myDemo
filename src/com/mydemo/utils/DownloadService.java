package com.mydemo.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.RemoteViews;

import com.mydemo.R;
import com.mydemo.activity.MainActivity;
import com.mydemo.entity.Version;

public class DownloadService extends Service{
	private String path="http://115.29.206.228/kuaidituInphone/update/update.xml";
	private Version version;
	private NotificationManager manager;
	private Notification notification;
	private static final int NOTIFY_ID = 0;
	private String savaPath="";
	private Context context;
	private int progress;
	private boolean canceled;
	private ICallbackResult callback;
	private boolean serviceIsDestroy = false;
	private DownloadBinder binder;
	private Handler mHandler=new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				manager.cancel(NOTIFY_ID);
				install();
				break;
			case 1:
				int rate = msg.arg1;
				if (rate < 100) {
					RemoteViews contentview = notification.contentView;
					contentview.setTextViewText(R.id.tv_progress, rate + "%");
					contentview.setProgressBar(R.id.progressbar, 100, rate, false);
				} else {
					System.out.println("下载完毕!!!!!!!!!!!");
					// 下载完毕后变换通知形式
					notification.flags = Notification.FLAG_AUTO_CANCEL;
					notification.contentView = null;
					Intent intent = new Intent(context, MainActivity.class);
					// 告知已完成
					intent.putExtra("completed", "yes");
					// 更新参数,注意flags要使用FLAG_UPDATE_CURRENT
				PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent,
							PendingIntent.FLAG_UPDATE_CURRENT);
				notification.setLatestEventInfo(context, "下载完成", "文件已下载完毕", contentIntent);
					serviceIsDestroy = true;
					stopSelf();// 停掉服务自身
				}
				manager.notify(NOTIFY_ID, notification);
				break;
			case 2:
				manager.cancel(NOTIFY_ID);
				break;

			default:
				break;
			}
		};
	};
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	@Override
	public void onCreate() {
		context=this;
		binder=new DownloadBinder();
		manager=(NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		super.onCreate();
	}
	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	//创建通知栏消息
	private void setNotification(){
		long when = System.currentTimeMillis();
		notification=new Notification(R.drawable.download, "正在下载", when);
		notification.flags=Notification.FLAG_AUTO_CANCEL;
		Intent intent=new Intent(this,MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pendingIntent=PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		notification.contentIntent=pendingIntent;
		manager.notify(NOTIFY_ID, notification);
	}
	//下载apk
	private int lastRate = 0;
	public void getDownLoadApk(){
		canceled = false;
		version=new Version();
		try {
			final URL urlPath=new URL(path);
			new Thread(){
				public void run() {
					try {
						HttpURLConnection connection=(HttpURLConnection)urlPath.openConnection();
						connection.setRequestMethod("GET");
						connection.setConnectTimeout(5000);
						int responseCode = connection.getResponseCode();
						int length = connection.getContentLength();
						if(responseCode==200){
							InputStream is = connection.getInputStream();
							version=VersionUpdateForPull.getVersionXml(is);
							String path = version.getDownloadURL().trim();
//							String newName = path.substring(path.lastIndexOf("/"), path.length());
//							savaPath=Environment.getExternalStorageDirectory() + newName;
							File file = new File(path);
							if(!(Environment.MEDIA_MOUNTED.equals(Environment
									.getExternalStorageState()))){
								file.mkdirs();
							}
							
							File ApkFile = new File(path);
							FileOutputStream fos = new FileOutputStream(ApkFile);
							int count = 0;
							byte buf[] = new byte[1024];
							do {
								int numread = is.read(buf);
								count += numread;
								progress = (int) (((float) count / length) * 100);
								// 更新进度
								Message msg = mHandler.obtainMessage();
								msg.what = 1;
								msg.arg1 = progress;
								if (progress >= lastRate + 1) {
									mHandler.sendMessage(msg);
									lastRate = progress;
									if (callback != null)
										callback.OnBackResult(progress);
								}
								if (numread <= 0) {
									// 下载完成通知安装
									mHandler.sendEmptyMessage(0);
									// 下载完了，cancelled也要设置
									canceled = true;
									break;
								}
								fos.write(buf, 0, numread);
							} while (!canceled);// 点击取消就停止下载.

							fos.close();
							is.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				};
			}.start();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	//安装apk
	private void install(){
		File file=new File(savaPath);
		if(!file.exists()){
			return;
		}
		Intent intent=new Intent(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(Uri.parse("file://" + file.toString()), "application/vnd.android.package-archive");
		context.startActivity(intent);
		canceled = true;
	}
	public interface ICallbackResult {
		public void OnBackResult(Object result);
	}
	public class DownloadBinder extends Binder{
		public void start(){
			progress=0;
			setNotification();
			new Thread(){
				public void run() {
					getDownLoadApk();
				};
			}.start();
		}
	}
}
