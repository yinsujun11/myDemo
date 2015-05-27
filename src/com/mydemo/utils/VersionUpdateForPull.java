package com.mydemo.utils;

import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

import com.mydemo.entity.Version;

public class VersionUpdateForPull {
//	public static Version getVersion(String path){
//		Version version=null;
//		try {
//			URL url=new URL(path);
//			HttpURLConnection connection=(HttpURLConnection) url.openConnection();
//			connection.setRequestMethod("GET");
//			connection.setConnectTimeout(5000);
//			int code=connection.getResponseCode();
//			if(code==200){
//				InputStream is=connection.getInputStream();
//				version=getVersionXml(is);
//			}
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//		}catch (IOException e) {
//			e.printStackTrace();
//		}
//		return version;
//	}
	public static Version getVersionXml(InputStream is){
		XmlPullParser parser = Xml.newPullParser();
		Version version=null;
		try {
			parser.setInput(is, "utf-8");
			int type=parser.getEventType();
			while(type!=XmlPullParser.END_DOCUMENT){
				switch (type) {
				case XmlPullParser.START_TAG:
					if("update".equals(parser.getName())){
						version=new Version();
					}else if("versionCode".equals(parser.getName())){
						version.setVersionCode(parser.nextText());
					}else if("versionName".equals(parser.getName())){
						version.setVersionCode(parser.nextText());
					}else if("downloadURL".equals(parser.getName())){
						version.setVersionCode(parser.nextText());
					}else if("displayMessage".equals(parser.getName())){
						version.setVersionCode(parser.nextText());
					}
					break;
				case XmlPullParser.END_DOCUMENT:
					if("update".equals(parser.getName())){
						version=null;
					}
					break;

				default:
					break;
				}
				type=parser.next();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
		return version;
	}
	
}
