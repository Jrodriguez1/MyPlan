package com.derek.todolist;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

public class DetailPageActivity extends Activity {
	DatabaseHelper databaseHelper;
	static Context context;
	TextView detailTitle;
	TextView detailDate;
	TextView detailContent;
	RelativeLayout mainBodyLayout;
	RelativeLayout bottomLayout;
	Button returnBtn;
	Button editBtn;
	Button alarmBtn;
	Button confrimBtn;
	Button cancelBtn;
	EditText editDetailTitle;
	DatePicker editDetailDate;
	EditText editDetailContent;
	String currentId;
	
	//These three string are used as a temporarily storage when the event is being edit. So it can recover to the previous state when we cancel editing.
	String previousTitle;
	String previousDate;
	String previousContent;
	
	//These variable is used to store event date.
	Calendar calendar = Calendar.getInstance();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail_page);
		Bundle myBundle = this.getIntent().getExtras();
        currentId = myBundle.getString("tag");
        context = this;
        databaseHelper = new DatabaseHelper(context, "database.db");
        detailTitle = (TextView)findViewById(R.id.detailTitle);
        detailDate = (TextView)findViewById(R.id.detailDate);
        detailContent = (TextView)findViewById(R.id.detailContent);
        mainBodyLayout = (RelativeLayout)findViewById(R.id.mainBodyLayout);
        bottomLayout = (RelativeLayout)findViewById(R.id.bottomLayout);
        returnBtn = (Button)findViewById(R.id.returnBtn);
        editBtn = (Button)findViewById(R.id.editBtn);
        alarmBtn = (Button)findViewById(R.id.alarmBtn);
        editBtn.setOnClickListener(new EditButtonOnClickListener());
        returnBtn.setOnClickListener(new ReturnButtonOnClickListener());
        alarmBtn.setOnClickListener(new AlarmButtonOnClickListener());
        initialPageByTag(currentId);
        detailDate.setTextColor(Color.rgb(255,0,0));
        
        //set the position of return button
		/*RelativeLayout.LayoutParams returnButtonLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
		returnButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		returnBtn.setLayoutParams(returnButtonLayoutParams);*/
		
		//Set the position of alarm Btn
		/*RelativeLayout.LayoutParams alarmButtonLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
		alarmButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		alarmBtn.setLayoutParams(alarmButtonLayoutParams);*/
	}
	
	private void initialPageByTag(String viewTag) {
		SQLiteDatabase db = databaseHelper.getReadableDatabase();
    	// 调用SQLiteDatabase对象的query方法进行查询，返回一个Cursor对象：由数据库查询返回的结果集对象  
        // 第一个参数String：表名  
        // 第二个参数String[]:要查询的列名  
        // 第三个参数String：查询条件  
        // 第四个参数String[]：查询条件的参数  
        // 第五个参数String:对查询的结果进行分组  
        // 第六个参数String：对分组的结果进行限制  
        // 第七个参数String：对查询的结果进行排序  
        Cursor cursor = db.query("events",new String[]{"title","date","content"},"_id=?", new String[]{viewTag},null,null,null);
        // 将光标移动到下一行，从而判断该结果集是否还有下一条数据，如果有则返回true，没有则返回false
        int num = cursor.getCount();
        System.out.println(num);
        cursor.moveToFirst();
        String title = cursor.getString(cursor.getColumnIndex("title"));
    	String date = cursor.getString(cursor.getColumnIndex("date"));
    	String content = cursor.getString(cursor.getColumnIndex("content"));
    	System.out.println(title+date+content);
    	detailTitle.setText(title);
    	detailDate.setText(date);
    	detailContent.setText(content);        
	}
	
	public class EditButtonOnClickListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			//Store current state.
			System.out.println("edit id: "+arg0.getId());
			previousTitle = detailTitle.getText().toString();
			previousDate = detailDate.getText().toString();
			previousContent = detailContent.getText().toString();
			
			//Change the buttons in bottomLayout
			bottomLayout.removeAllViews();
			confrimBtn = new Button(context);
			cancelBtn = new Button(context);
			
			confrimBtn.setText("Confirm");
			cancelBtn.setText("Cancel");
			
			confrimBtn.setOnClickListener(new ConfirmButtonOnClickListener());
			cancelBtn.setOnClickListener(new CancelButtonOnClickListener());
			
			bottomLayout.addView(cancelBtn);
			bottomLayout.addView(confrimBtn);
			
			//Set positions of the new buttons
			RelativeLayout.LayoutParams confirmBtnLayoutParams = new RelativeLayout.LayoutParams(80,80);			
			confirmBtnLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			confirmBtnLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
			confrimBtn.setLayoutParams(confirmBtnLayoutParams);
			confrimBtn.setBackground(getResources().getDrawable(R.drawable.done));
			confrimBtn.setText("");
			
			RelativeLayout.LayoutParams cancelBtnLayoutParams = new RelativeLayout.LayoutParams(80,80);
			cancelBtnLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			cancelBtnLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
			cancelBtn.setBackground(getResources().getDrawable(R.drawable.cancel));
			cancelBtn.setLayoutParams(cancelBtnLayoutParams);			
			cancelBtn.setText("");
			//Change the elements in mainBodyLayout
			mainBodyLayout.removeAllViews();
			editDetailTitle = new EditText(context);
			editDetailDate = new DatePicker(context);
			editDetailContent = new EditText(context);
			
			editDetailTitle.setText(previousTitle);
			editDetailDate.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new OnDateChangedListener() {

				@Override
				public void onDateChanged(DatePicker view, int year, int month,
						int day) {
					// TODO Auto-generated method stub
					calendar.set(year,month,day);
				}
				
			});
			editDetailContent.setText(previousContent);
			
			editDetailTitle.setId(1000);
			editDetailDate.setId(1001);
			editDetailContent.setId(1002);
			
			mainBodyLayout.addView(editDetailTitle);
			mainBodyLayout.addView(editDetailDate);
			mainBodyLayout.addView(editDetailContent);
			
			//Set the position of editDetailTitle
			RelativeLayout.LayoutParams editDetailTitleLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
			editDetailTitleLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			editDetailTitle.setLayoutParams(editDetailTitleLayoutParams);
			
			//Set the position of editDetailDate
			RelativeLayout.LayoutParams editDetailDateLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
			editDetailDateLayoutParams.addRule(RelativeLayout.BELOW, editDetailTitle.getId());
			editDetailDate.setLayoutParams(editDetailDateLayoutParams);
			
			//Set the position of editDetailContent
			RelativeLayout.LayoutParams editDetailContentLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
			editDetailContentLayoutParams.addRule(RelativeLayout.BELOW, editDetailDate.getId());
			editDetailContent.setLayoutParams(editDetailContentLayoutParams);
			editDetailContent.setTextColor(Color.rgb(255,255,255));
		}
		
	}
	
	public class ConfirmButtonOnClickListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			//Get the current values
			String currentTitle = editDetailTitle.getText().toString();
			SimpleDateFormat tempDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String currentDate = tempDateFormat.format(calendar.getTime());
			String currentContent = editDetailContent.getText().toString();
			
			//Update these value to the database
			ContentValues values = new ContentValues();
			values.put("title", currentTitle);
			values.put("date", currentDate);
			values.put("content", currentContent);
			SQLiteDatabase db = databaseHelper.getWritableDatabase();
			db.update("events", values, "_id=?", new String[]{currentId});
			initialPageByTag(currentId);
			
			//Change the elements to the origin state
			bottomLayout.removeAllViews();
			
			bottomLayout.addView(returnBtn);
			bottomLayout.addView(editBtn);
			bottomLayout.addView(alarmBtn);
			
			//Set the position of editBtn
			/*RelativeLayout.LayoutParams editButtonLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
			editButtonLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
			editBtn.setLayoutParams(editButtonLayoutParams);*/
			
			mainBodyLayout.removeAllViews();
			
			detailTitle.setText(currentTitle);
			detailDate.setText(currentDate);
			detailContent.setText(currentContent);
		
			mainBodyLayout.addView(detailTitle);
			mainBodyLayout.addView(detailDate);
			mainBodyLayout.addView(detailContent);
			
			//Set the position of detailTitle
			RelativeLayout.LayoutParams detailTitleLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
			detailTitleLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			detailTitle.setLayoutParams(detailTitleLayoutParams);
			
			//Set the position of detailDate
			RelativeLayout.LayoutParams detailDateLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
			detailDateLayoutParams.addRule(RelativeLayout.BELOW, detailTitle.getId());
			detailDate.setLayoutParams(detailDateLayoutParams);
			detailDate.setTextColor(Color.rgb(255,0,0));
			
			//Set the position of editDetailContent
			RelativeLayout.LayoutParams detailContentLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
			detailContentLayoutParams.addRule(RelativeLayout.BELOW, detailDate.getId());
			detailContent.setLayoutParams(detailContentLayoutParams);
			

		}
		
	}
	
	public class AlarmButtonOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent(context, OnAlarmReceiver.class);
			intent.putExtra("tags", currentId);
			intent.putExtra("title", detailTitle.getText().toString());
			intent.putExtra("time", detailDate.getText().toString());
			intent.putExtra("content", detailContent.getText().toString());
			PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
			Calendar time = Calendar.getInstance();
			time.setTimeInMillis(System.currentTimeMillis());

			Date date = new Date();
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				date = sdf.parse(detailDate.getText().toString() + " 00:00:00");
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			time.setTime(date);
			System.out.println(time.getTimeInMillis() - System.currentTimeMillis());
			am.set(AlarmManager.RTC_WAKEUP, time.getTimeInMillis() - System.currentTimeMillis(), pendingIntent);
			System.out.println("alarm id: "+v.getId());
		}
		
	}
	public class CancelButtonOnClickListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub			
			//Change the elements to the origin state
			bottomLayout.removeAllViews();
			
			bottomLayout.addView(returnBtn);
			bottomLayout.addView(editBtn);
			bottomLayout.addView(alarmBtn);
			
			//Set the position of editBtn
			/*RelativeLayout.LayoutParams editButtonLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
			editButtonLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
			editBtn.setLayoutParams(editButtonLayoutParams);*/
			
			mainBodyLayout.removeAllViews();
			
			detailTitle.setText(previousTitle);
			detailDate.setText(previousDate);
			detailContent.setText(previousContent);
		
			mainBodyLayout.addView(detailTitle);
			mainBodyLayout.addView(detailDate);
			mainBodyLayout.addView(detailContent);
			
			//Set the position of detailTitle
			RelativeLayout.LayoutParams detailTitleLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
			detailTitleLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			detailTitle.setLayoutParams(detailTitleLayoutParams);
			
			//Set the position of detailDate
			RelativeLayout.LayoutParams detailDateLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
			detailDateLayoutParams.addRule(RelativeLayout.BELOW, detailTitle.getId());
			detailDate.setLayoutParams(detailDateLayoutParams);
			
			//Set the position of editDetailContent
			RelativeLayout.LayoutParams detailContentLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
			detailContentLayoutParams.addRule(RelativeLayout.BELOW, detailDate.getId());
			detailContent.setLayoutParams(detailContentLayoutParams);
		}
		
	}
	
	public class ReturnButtonOnClickListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			System.out.println("return id: " + arg0.getId());
			Intent myIntent = new Intent();
			myIntent.setClass(DetailPageActivity.this, MainPageActivity.class);
			startActivity(myIntent);
			DetailPageActivity.this.finish();
		}
		
	}
}
