package com.mydemo.entity;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

/**
 * Ӧ�ó������ʵ����
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class Update implements Serializable{
	
	public final static String UTF8 = "UTF-8";
	public final static String NODE_ROOT = "oschina";
	
	private int versionCode;
	private String versionName;
	private String downloadUrl;
	private String updateLog;
	
	public int getVersionCode() {
		return versionCode;
	}
	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}
	public String getVersionName() {
		return versionName;
	}
	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}
	public String getDownloadUrl() {
		return downloadUrl;
	}
	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}
	public String getUpdateLog() {
		return updateLog;
	}
	public void setUpdateLog(String updateLog) {
		this.updateLog = updateLog;
	}
	
	public static Update parse(InputStream inputStream) throws IOException {
		Update update = null;
        //���XmlPullParser������
        XmlPullParser xmlParser = Xml.newPullParser();
        try {        	
            xmlParser.setInput(inputStream, UTF8);
            //��ý��������¼���������п�ʼ�ĵ��������ĵ�����ʼ��ǩ��������ǩ���ı��ȵ��¼���
            int evtType=xmlParser.getEventType();
			//һֱѭ����ֱ���ĵ�����    
			while(evtType!=XmlPullParser.END_DOCUMENT){ 
	    		String tag = xmlParser.getName(); 
			    switch(evtType){ 
			    	case XmlPullParser.START_TAG:			    		
			            //֪ͨ��Ϣ
			            if(tag.equalsIgnoreCase("android"))
			    		{
			            	update = new Update();
			    		}
			            else if(update != null)
			    		{
			    			if(tag.equalsIgnoreCase("versionCode"))
				            {			      
			    				update.setVersionCode( Integer.parseInt(xmlParser.nextText(),0));
				            }
				            else if(tag.equalsIgnoreCase("versionName"))
				            {			            	
				            	update.setVersionName(xmlParser.nextText());
				            }
				            else if(tag.equalsIgnoreCase("downloadUrl"))
				            {			            	
				            	update.setDownloadUrl(xmlParser.nextText());
				            }
				            else if(tag.equalsIgnoreCase("updateLog"))
				            {			            	
				            	update.setUpdateLog(xmlParser.nextText());
				            }
			    		}
			    		break;
			    	case XmlPullParser.END_TAG:		    		
				       	break; 
			    }
			    //���xmlû�н������򵼺�����һ���ڵ�
			    evtType=xmlParser.next();
			}		
        } catch (XmlPullParserException e) {
        	e.printStackTrace();
        } finally {
        	inputStream.close();	
        }      
        return update;       
	}
}
