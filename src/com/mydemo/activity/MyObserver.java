package com.mydemo.activity;

import java.util.Observable;
import java.util.Observer;

import android.util.Log;

import com.mydemo.entity.Person;

public class MyObserver implements Observer{
	private Person person;
	private int i;
	public MyObserver(int i) {
		 Log.e("MyObserver", i+"观察者");
		this.i=i;
	}
	@Override
	public void update(Observable observable, Object data) {
		 this.person = (Person)observable;
		 Log.e("MyObserver", person.toString());
	}
	public Person getPerson() {
		return person;
	}
	public void setPerson(Person person) {
		this.person = person;
	}
	public int getI() {
		return i;
	}
	public void setI(int i) {
		this.i = i;
	}
	
}
