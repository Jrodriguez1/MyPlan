package com.derek.todolist;


import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.RemoteViews;

public class MyWidget extends AppWidgetProvider {
	
	DatabaseHelper databaseHelper;
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		String strTitle = "未设置",
				strDate = "未设置",
				strTime = "未设置",
				strContent = "未设置";
		databaseHelper = new DatabaseHelper(context, "database.db");
		SQLiteDatabase db = databaseHelper.getReadableDatabase();
		//现在还是对_id 进行排序，没有取time最近的一个 数据库和之前的UI都没有设置好time
		// TO DO
		Cursor cursor = db.query("events",new String[]{"_id","title","date","content"},null,null,null,null,"_id");
		if(cursor.moveToNext()) {
			strTitle = cursor.getString(cursor.getColumnIndex("title"));
			strDate = cursor.getString(cursor.getColumnIndex("date"));
			// get time TO DO
			strContent = cursor.getString(cursor.getColumnIndex("content"));
		}
		
		// 设置跳转的intent
		Intent intent = new Intent(context, MainPageActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
		// 使用RemoteView更新Widget
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
		remoteViews.setTextViewText(R.id.widgetTitle, strTitle);
		remoteViews.setTextViewText(R.id.widgetDate, strDate);
		remoteViews.setTextViewText(R.id.widgetContent, strContent);
		
		// set Text in Widget ViewComButtonOnClickListener
		// 设置widget点击事件
		remoteViews.setOnClickPendingIntent(R.id.relativeWidgetLayout, pendingIntent);
		// 更新AppWidget
		System.out.println(appWidgetIds);
		appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
	}

}
