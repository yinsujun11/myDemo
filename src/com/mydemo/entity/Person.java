package com.mydemo.entity;

import java.util.Observable;

public class Person extends Observable{
	private int id;
	private int age;
	private String name;
	private String alias;
	public Person() {
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
		setChanged();
        notifyObservers();
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public Person(int id, int age, String name, String alias) {
		super();
		this.id = id;
		this.age = age;
		this.name = name;
		this.alias = alias;
	}
	@Override
	public String toString() {
		return "Persion [id=" + id + ", age=" + age + ", name=" + name
				+ ", alias=" + alias + "]";
	}
	

}
