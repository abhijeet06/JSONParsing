package com.abhi.jsonparsing;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelp extends SQLiteOpenHelper {
	private static final String DATABASE_NAME="Utube.db";
	private static final int SCHEMA_VERSION=1;
	public SQLiteHelp(Context context) {
	super(context, DATABASE_NAME, null, SCHEMA_VERSION);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE Utube (_id INTEGER PRIMARY KEY AUTOINCREMENT," +
				"Title TEXT, Duration TEXT, Author TEXT);");
		
		db.execSQL("CREATE TABLE Contact (_id TEXT," +
				"Name TEXT, Email TEXT, Mobile TEXT);");
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
	
	
}