package com.lauraeyal.taskmanager.common;

public class User {
	private String userName;
	private String password;
	private String phoneNumber;
	private int isMailSent;
	private int isAdmin;
	private String teamName;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setMailSent(int isMailSent){this.isMailSent=isMailSent;}

	public int getMailSend() {return isMailSent;}

	public void setPermission(int isAdmin){this.isAdmin=isAdmin;}

	public int getPermission() {return isAdmin;}

	public void setPhoneNumber(String phoneNumber) {this.phoneNumber = phoneNumber;}

	public String getPhoneNumber() {return phoneNumber;}

	public void setTeamName(String teamName){this.teamName = teamName;}

	public String getTeamName(){return teamName;}
}