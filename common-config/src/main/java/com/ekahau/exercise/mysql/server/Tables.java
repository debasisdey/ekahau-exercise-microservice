package com.ekahau.exercise.mysql.server;

public enum Tables {
	USER("user"), BOOK("book");

	private String tableName;

	Tables(String tableName){
		this.tableName = tableName;
	}

	public String getTableName(){
		return this.tableName;
	}
}
