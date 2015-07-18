package com.derek.todolist;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CompletedPageActivity extends Activity {
	static Context context;
	LinearLayout completedEventBoard;
	Button backBtn;
	int eventNum = 0;
	LinearLayout lowestRow;
	DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_page);
		context = this;
		backBtn = (Button)findViewById(R.id.backBtn);
		completedEventBoard = (LinearLayout)findViewById(R.id.completedEventBoard);
		databaseHelper = new DatabaseHelper(context, "database.db");
		initialEvents();
		backBtn.setOnClickListener(new ReturnButtonOnClickListener());
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
        Cursor cursor = db.query("completedEvents",new String[]{"_id","title","date","content"},null,null,null,null,"_id");
       
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
    	completedEventBoard.removeAllViews();
    	//Don't forget to reset the eventNum to 0
    	eventNum = 0;
    }
    
    private void addEvent(String title, String deadline, String content, int id) {
    	//Using the following if statement to judge if we need a new row
    	if ((eventNum % 2) == 0) {
    		//Creating a LinearLayout for a new row.
			lowestRow = new LinearLayout(context);
			lowestRow.setOrientation(LinearLayout.HORIZONTAL);
			completedEventBoard.addView(lowestRow);
		}
    	
    	//Creating a LinearLayout for a new event.
		RelativeLayout newEvent = new RelativeLayout(context);
		newEvent.setTag(new String(""+id));
		lowestRow.addView(newEvent);
		
		//Set the style of newEvent
		LinearLayout.LayoutParams newEventLayoutParams = new LinearLayout.LayoutParams(300,300);
		newEventLayoutParams.setMargins(20, 20, 20, 20);
		newEvent.setLayoutParams(newEventLayoutParams);
		newEvent.setBackgroundResource(R.drawable.grey);
		
		//Set OnClickListener on newEvent
		newEvent.setOnClickListener(new EventOnClickListener());
				
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
		newDateLayoutParams.setMargins(60, 10, 20, 10);
		newDateLayoutParams.addRule(RelativeLayout.BELOW, newTitle.getId());
		newDate.setLayoutParams(newDateLayoutParams);
		
		RelativeLayout.LayoutParams newContentLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
		newContentLayoutParams.setMargins(60, 10, 20, 30);
		newContentLayoutParams.addRule(RelativeLayout.BELOW, newDate.getId());
		newContent.setLayoutParams(newContentLayoutParams);

		eventNum++;
    }
    
    public class EventOnClickListener implements OnClickListener {

		@Override
		public void onClick(View view) {
			// TODO Auto-generated method stub
			String[] choices={"删除备忘"};
			AlertDialog dialog = new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.btn_star)
                .setTitle("操作")
                .setItems(choices, new OperationMenuItemOnClickListener(view)).create();  
			dialog.show();
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
			String currentId = currentView.getTag().toString();
			String whereClause = "_id=?";//删除的条件
			String[] whereArgs = {currentId};//删除的条件参数
			switch(itemNumber) {
				//complete an event.
				case 0:
					wdb.delete("completedEvents",whereClause,whereArgs);//执行删除
					clearEvents();
					initialEvents();
					break;
			}
		}
    	
    }
    
	public class ReturnButtonOnClickListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			Intent myIntent = new Intent();
			myIntent.setClass(CompletedPageActivity.this, MainPageActivity.class);
			startActivity(myIntent);
			CompletedPageActivity.this.finish();
		}
	}
    
}
