package com.mydemo.entity;

public class Version {
	/**版本号*/
	public String versionCode;
	/**版本名字*/
	private String versionName;
	/**版本更新url*/
	public String downloadURL;
	/**新版本描述信息*/
	private String displayMessage;
	public String getVersionCode() {
		return versionCode;
	}
	public void setVersionCode(String versionCode) {
		this.versionCode = versionCode;
	}
	public String getVersionName() {
		return versionName;
	}
	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}
	public String getDownloadURL() {
		return downloadURL;
	}
	public void setDownloadURL(String downloadURL) {
		this.downloadURL = downloadURL;
	}
	public String getDisplayMessage() {
		return displayMessage;
	}
	public void setDisplayMessage(String displayMessage) {
		this.displayMessage = displayMessage;
	}
	@Override
	public String toString() {
		return "Version [versionCode=" + versionCode + ", versionName=" 
				+ versionName  + ", downloadURL=" + downloadURL
				+ ", displayMessage=" + displayMessage + "]";
	}
	
}
