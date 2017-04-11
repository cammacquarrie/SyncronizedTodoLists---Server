package edu.carleton.comp2601Final;

import java.io.Serializable;
import java.util.ArrayList;

public class List implements Serializable{
	private ArrayList<Item> items;
	private ArrayList<User> users;
	private String admin;
	private String name;
	private int id;
	
	public List(String name, int id) {
		this.name = name;
		this.id = id;
		users = new ArrayList<User>();
		items = new ArrayList<Item>();
	}
	
	public ArrayList<Item> getItems() {
		return items;
	}
	public void setItems(ArrayList<Item> items) {
		this.items = items;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public ArrayList<User> getUsers() {
		return users;
	}

	public void setUsers(ArrayList<User> users) {
		this.users = users;
	}
	
	public boolean isAdmin(User user){
		if(user.equals(user)){
			return true;
		}
		return false;
	}

	public String getAdmin() {
		return admin;
	}

	public void setAdmin(String admin) {
		this.admin = admin;
	}
}
