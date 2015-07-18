
package com.derek.todolist;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainPageActivity extends Activity {
	static Context context;
	LinearLayout eventBoard;
	Button addBtn;
	Button searchBtn;
	Button completedBtn;
	int eventNum = 0;
	LinearLayout lowestRow;
	DatabaseHelper databaseHelper;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        
        context = this;
        eventBoard = (LinearLayout)findViewById(R.id.eventBoard);
        addBtn = (Button)findViewById(R.id.addBtn);
        searchBtn = (Button)findViewById(R.id.searchBtn);
        completedBtn = (Button)findViewById(R.id.completedBtn);
        databaseHelper = new DatabaseHelper(context, "database.db");
        
        initialEvents();
        
        addBtn.setOnClickListener(new AddButtonOnClickListener());
        searchBtn.setOnClickListener(new SearchButtonOnClickListener());
        completedBtn.setOnClickListener(new ViewComButtonOnClickListener());
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	updateWidget();
    }
    
    private void updateWidget() {
    	AppWidgetManager mAppWidgetManager = AppWidgetManager.getInstance(this);
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
		
		// 使用RemoteView更新Widget
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
		remoteViews.setTextViewText(R.id.widgetTitle, strTitle);
		remoteViews.setTextViewText(R.id.widgetDate, strDate);
		//remoteViews.setTextViewText(R.id.widgetTime, strTime);
		remoteViews.setTextViewText(R.id.widgetContent, strContent);
		System.out.println(R.xml.widget_provider);
		// 更新 did not comprehend
    	mAppWidgetManager.updateAppWidget(new ComponentName(this.getPackageName(), MyWidget.class.getName()),
    			remoteViews);
    	
    }
    
    private void initialEvents() {
    	SQLiteDatabase db = databaseHelper.getReadableDatabase();
    	// 调用SQLiteDatabase对象的query方法进行查询，返回一个Cursor对象：由数据库查询返回的结果集对象  
        // 第一个参数String：表名  
        // 第二个参数String[]:要查询的列名  
        // 第三个参数String：查询条件  
        // 第四个参数String[]：查询条件的参数  
        // 第五个参数String:对查询的结果进行分组  
        // 第六个参数String：对分组的结果进行限制  
        // 第七个参数String：对查询的结果进行排序  
        Cursor cursor = db.query("events",new String[]{"_id","title","date","content"},null,null,null,null,"_id");
       
        // 将光标移动到下一行，从而判断该结果集是否还有下一条数据，如果有则返回true，没有则返回false  
        while (cursor.moveToNext()) {
        	int id = cursor.getInt(cursor.getColumnIndex("_id"));
        	String strTitle = cursor.getString(cursor.getColumnIndex("title"));
        	String strDate = cursor.getString(cursor.getColumnIndex("date"));
        	String strContent = cursor.getString(cursor.getColumnIndex("content"));
        	System.out.println("query and get"+id);
        	addEvent(strTitle, strDate, strContent, id);
        }
    }
    
    private void clearEvents() {
    	eventBoard.removeAllViews();
    	//Don't forget to reset the eventNum to 0
    	eventNum = 0;
    }
    
    private void addEvent(String title, String deadline, String content, int id) {
    	//Using the following if statement to judge if we need a new row
    	if ((eventNum % 2) == 0) {
    		//Creating a LinearLayout for a new row.
			lowestRow = new LinearLayout(context);
			lowestRow.setOrientation(LinearLayout.HORIZONTAL);
			eventBoard.addView(lowestRow);
		}
    	
    	//Creating a LinearLayout for a new event.
		RelativeLayout newEvent = new RelativeLayout(context);
		newEvent.setTag(new String(""+id));
		lowestRow.addView(newEvent);
		
		//Set the style of newEvent
		LinearLayout.LayoutParams newEventLayoutParams = new LinearLayout.LayoutParams(300,300);
		newEventLayoutParams.setMargins(20, 20, 20, 20);
		newEvent.setLayoutParams(newEventLayoutParams);
		newEvent.setBackgroundResource(R.drawable.event);
		
		//Set OnClickListener on newEvent
		newEvent.setOnClickListener(new EventOnClickListener());
		newEvent.setOnLongClickListener(new EventOnLongClickListener());
				
		//Creating the descended element for the event.
		TextView newTitle = new TextView(context);
		TextView newDate = new TextView(context);
		TextView newContent = new TextView(context);
		
		newTitle.setId(100);
		newDate.setId(101);
		newContent.setId(102);
		
		newTitle.setText(title);
		newDate.setText(deadline);
		newContent.setText(content);

		newEvent.addView(newTitle);
		newEvent.addView(newDate);
		newEvent.addView(newContent);
		
		//Set the Style of descended elements of newEvent
		RelativeLayout.LayoutParams newTitleLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
		newTitleLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		newTitleLayoutParams.setMargins(60, 50, 20, 10);
		newTitle.setLayoutParams(newTitleLayoutParams);

		RelativeLayout.LayoutParams newDateLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
		newDateLayoutParams.addRule(RelativeLayout.BELOW, newTitle.getId());
		newDateLayoutParams.setMargins(60, 10, 20, 10);
		newDate.setLayoutParams(newDateLayoutParams);
		newDate.setTextColor(Color.rgb(255,0,0));
		
		RelativeLayout.LayoutParams newContentLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
		newContentLayoutParams.addRule(RelativeLayout.BELOW, newDate.getId());
		newContentLayoutParams.setMargins(60, 10, 20, 30);
		newContent.setLayoutParams(newContentLayoutParams);

		eventNum++;
    }
    
    private void createNewEvent() {
    	int id = insertEventToDB(new String("新备忘"), new String("尚未编辑时间"), new String("尚未编辑内容"));
		addEvent(new String("新备忘"), new String("尚未编辑时间"), new String("尚未编辑内容"), id);
		clearEvents();
		initialEvents();
    }
    
    private int insertEventToDB(String title, String date, String content) {
    	int returnId = -2;
    	SQLiteDatabase wdb = databaseHelper.getWritableDatabase();
    	ContentValues values = new ContentValues();
    	values.put("title", title);
    	values.put("date", date);
    	values.put("content", content);
    	wdb.insert("events", null, values);
    	Cursor cursor = wdb.rawQuery("SELECT last_insert_rowid()", null);
    	//This will return id of the latest row we insert. 

    	if (cursor.moveToFirst()) {
    		returnId = cursor.getInt(0);
    		System.out.println("insertingid is:"+returnId);
    	}
		return returnId;
    }

    
    public class AddButtonOnClickListener implements OnClickListener {
    	@Override
		public void onClick(View arg0) {
			createNewEvent();
		}
    }
    
    public class SearchButtonOnClickListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			Intent myIntent = new Intent();
			myIntent.setClass(MainPageActivity.this, SearchPageActivity.class);
			startActivity(myIntent);
			MainPageActivity.this.finish();
		}
    	
    }
    
    public class ViewComButtonOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Intent myIntent = new Intent();
			myIntent.setClass(MainPageActivity.this, CompletedPageActivity.class);
			startActivity(myIntent);
		}
    	
    }
    
    public class EventOnClickListener implements OnClickListener {

		@Override
		public void onClick(View view) {
			// TODO Auto-generated method stub
			String viewTag = view.getTag().toString();
			System.out.println("currentTag is: "+ viewTag);
			Bundle myBundle = new Bundle();
			myBundle.putString("tag", viewTag);
			Intent myIntent = new Intent();
			myIntent.setClass(MainPageActivity.this,DetailPageActivity.class);
			myIntent.putExtras(myBundle);
			startActivity(myIntent);
			MainPageActivity.this.finish();
		}
    }
    
    public class EventOnLongClickListener implements OnLongClickListener {

		@Override
		public boolean onLongClick(View view) {
			// TODO Auto-generated method stub
			String[] choices={"标记为已完成","删除备忘"};
			AlertDialog dialog = new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.btn_star)
                .setTitle("操作")
                .setItems(choices, new OperationMenuItemOnClickListener(view)).create();  
			dialog.show();
			return true;
		}
    	
    }
    
    
    //Listener for the operation menu which appears after long click on an event
    public class OperationMenuItemOnClickListener implements android.content.DialogInterface.OnClickListener {
    	
    	View currentView;
    	
		public OperationMenuItemOnClickListener(View view) {
			// TODO Auto-generated constructor stub
			super();
			currentView = view;
		}

		@Override
		public void onClick(DialogInterface dialog, int itemNumber) {
			SQLiteDatabase wdb = databaseHelper.getWritableDatabase();
			SQLiteDatabase rdb = databaseHelper.getReadableDatabase();
			String currentId = currentView.getTag().toString();
			String whereClause = "_id=?";//删除的条件
			String[] whereArgs = {currentId};//删除的条件参数
			switch(itemNumber) {
				//complete an event.
				case 0:
					// get this data
			        Cursor cursor = rdb.query("events",new String[]{"_id","title","date","content"},"_id=?", new String[]{currentId}, null, null, null);
			        // 将光标移动到下一行，从而判断该结果集是否还有下一条数据，如果有则返回true，没有则返回false
			        int num = cursor.getCount();
			        System.out.println(num);
			        cursor.moveToFirst();
			        String title = cursor.getString(cursor.getColumnIndex("title"));
			        String date = cursor.getString(cursor.getColumnIndex("date"));
			        String content = cursor.getString(cursor.getColumnIndex("content"));
			        ContentValues values = new ContentValues();
			    	values.put("title", title);
			    	values.put("date", date);
			    	values.put("content", content);
			    	wdb.insert("completedEvents", null, values);
					wdb.delete("events",whereClause,whereArgs);//执行删除
					clearEvents();
					initialEvents();
					break;
				//delete an event
				case 1:
					System.out.println(currentId);
					//Delete this event from the database
					wdb.delete("events",whereClause,whereArgs);//执行删除
					//clear the eventBoard then re initial it in order to sort.
					clearEvents();
					initialEvents();
					break;
			}
		}
    	
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_page, menu);
        return true;
    }
}
