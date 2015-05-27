package com.mydemo.activity;

import android.app.Activity;
import android.os.Bundle;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.mydemo.R;

public class AtyMapView extends Activity  implements BaiduMap.OnMapClickListener{
	//地图
	MapView mMapView = null;  
	BDLocation location=new BDLocation();
//	private LocationMode mCurrentMode;
	BitmapDescriptor mCurrentMarker;
	boolean isFirstLoc = true;// 是否首次定位
	// 定位相关
	LocationClient mLocClient;
	private MyLocationListenner myListener = new MyLocationListenner();
	/***路线描绘*/
	boolean useDefaultIcon = false;
	//地图相关，使用继承MapView的MyRouteMapView目的是重写touch事件实现泡泡处理
	//如果不处理touch事件，则无需继承，直接使用MapView即可
	// 地图View
	BaiduMap mBaiduMap=null;
	//搜索相关
	RoutePlanSearch mSearch = null;    // 搜索模块，也可去掉地图模块独立使用
	String addr;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext()); 
		setContentView(R.layout.aty_mapview);
		//获取地图控件引用  
		mMapView = (MapView) findViewById(R.id.bmapView);  
		mBaiduMap = mMapView.getMap();
		// 开启定位图层  
		mBaiduMap.setMyLocationEnabled(true); 
		setDefaultLocation();
		// 地图初始化
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(14.0f);
		mBaiduMap.setMapStatus(msu);
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		mLocClient.start();
	}
	private void setDefaultLocation(){
		LatLng cenpt = new LatLng(31.213704,121.488327); 
		//定义地图状态
		MapStatus mMapStatus = new MapStatus.Builder()
		.target(cenpt)
		.build();
		//定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
		MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
		//改变地图状态
		mBaiduMap.setMapStatus(mMapStatusUpdate); 
	}
	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null)
				return;
//			LatLng llA = new LatLng(location.getLatitude(), location.getLongitude());
//			OverlayOptions ooA = new MarkerOptions().position(llA).icon(bdA).zIndex(9).draggable(true);
//			mMarkerA = (Marker) (mBaiduMap.addOverlay(ooA));
//			if(order.getAddressLng().equals("null")||order.getAddressLat().equals("null")){
//				return;
//			}
//			LatLng llB = new LatLng( Double.parseDouble(order.getAddressLat()),Double.parseDouble(order.getAddressLng()));
//			OverlayOptions ooB = new MarkerOptions().position(llB).icon(bdB).zIndex(5);
//			mMarkerB = (Marker) (mBaiduMap.addOverlay(ooB));
			if (isFirstLoc) {
				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(),
						location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(u);
			}
		}
		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

	@Override  
	protected void onDestroy() {  
		super.onDestroy();  
		//在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理  
		// 退出时销毁定位
		mLocClient.stop();
		// 关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;
//		bdA.recycle();
//		bdB.recycle();
	}

	@Override  
	protected void onResume() {  
		super.onResume();  
		//在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理  
		mMapView.onResume();  
	}  
	@Override  
	protected void onPause() {  
		super.onPause();  
		//在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理  
		mMapView.onPause();  
	}  
	@Override
	public void onMapClick(LatLng point) {
		mBaiduMap.hideInfoWindow();
	}

	@Override
	public boolean onMapPoiClick(MapPoi poi) {
		return false;
	}
	@Override
	protected void onStop() {
		super.onStop();
		mLocClient.stop();
	}
}
