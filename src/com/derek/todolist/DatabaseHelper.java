package com.derek.todolist;

import android.R.integer;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	public final static int VERSION = 1;
	
	/**
	 * constructor method, use parent's constructor.
	 * @param context	context object, just use your current activity.
	 * @param name		name of your database
	 * @param factory
	 * @param version	version of your database, requiring int.
	 */
	public DatabaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		// TODO Auto-generated constructor stub
		super(context, name, factory, version);
	}
	
	public DatabaseHelper(Context context, String name, int version){
		this(context,name,null,version);
	}

	public DatabaseHelper(Context context, String name){
		this(context,name,VERSION);
	}

	//this method will be executed when the first time the sqlite db is created.
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		System.out.println("create a database");
		db.execSQL("CREATE TABLE IF NOT EXISTS events(_id INTEGER PRIMARY KEY AUTOINCREMENT, title VARCHAR(25), date DATE, content VARCHAR(200))");
		db.execSQL("CREATE TABLE IF NOT EXISTS completedEvents(_id INTEGER PRIMARY KEY AUTOINCREMENT, title VARCHAR(25), date DATE, content VARCHAR(200))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		System.out.println("upgrade a database");
	}

}
