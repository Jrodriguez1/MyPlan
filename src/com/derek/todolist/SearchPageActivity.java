package com.derek.todolist;

import com.derek.todolist.MainPageActivity.EventOnClickListener;
import com.derek.todolist.MainPageActivity.EventOnLongClickListener;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SearchPageActivity extends Activity {
    EditText searchInputBox;
    LinearLayout resultInTitle;
    LinearLayout resultInContent;
    DatabaseHelper databaseHelper;
    Context context;
    int inTitleEventsNum;
    int inContentEventsNum;
    LinearLayout inTitleLowestRow;
    LinearLayout inContentLowestRow;
    Button back;

	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_page);
        context = this;
        searchInputBox = (EditText)findViewById(R.id.searchInputBox);
        resultInTitle = (LinearLayout)findViewById(R.id.resultInTitle);
        resultInContent = (LinearLayout)findViewById(R.id.resultInContent);
        databaseHelper = new DatabaseHelper(context, "database.db");
        inTitleEventsNum = 0;
        inContentEventsNum = 0;
        back = (Button)findViewById(R.id.backBtn2);
        back.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent myIntent = new Intent();
				myIntent.setClass(SearchPageActivity.this, MainPageActivity.class);
				startActivity(myIntent);
				SearchPageActivity.this.finish();
			}
        	
        });
        searchInputBox.addTextChangedListener(new TextWatcher() {			
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				String currentInput = searchInputBox.getText().toString();
				System.out.println(currentInput);
				setPageByInput(currentInput);
			}
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				clearPage();
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
		});
    }
	
	private void setPageByInput(String input) {
		int count1 = searchForTitle(input);
		System.out.println("found in title: "+count1);
		int count2 = searchForContent(input);
		System.out.println("found in content"+count2);
		
	}
	
	private void clearPage() {
		resultInTitle.removeAllViews();
		resultInContent.removeAllViews();
		inContentEventsNum = 0;
		inTitleEventsNum = 0;
	}
	
	//Return the number of how many events have titles contain our input
	private int searchForTitle(String input) {
		int count = 0;
		SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query("events",new String[]{"_id","title","date","content"},null,null,null,null,"_id");
        while (cursor.moveToNext()) {
        	String strTitle = cursor.getString(cursor.getColumnIndex("title"));
        	if(containSubString(input, strTitle)) {
        		int id = cursor.getInt(cursor.getColumnIndex("_id"));
        		String strDate = cursor.getString(cursor.getColumnIndex("date"));
        		String strContent = cursor.getString(cursor.getColumnIndex("content"));
        		addInTitleEvent(strTitle, strDate, strContent, id);
        		count++;
        	}
        	else{
        	}
        }
        return count;
	}
	
	//Return the number of how many events have contents contain our input
	private int searchForContent(String input) {
		int count = 0;
		SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query("events",new String[]{"_id","title","date","content"},null,null,null,null,"_id");
        while (cursor.moveToNext()) {
        	String strContent= cursor.getString(cursor.getColumnIndex("content"));
        	if(containSubString(input, strContent)) {
        		int id = cursor.getInt(cursor.getColumnIndex("_id"));
        		String strTitle = cursor.getString(cursor.getColumnIndex("title"));
        		String strDate = cursor.getString(cursor.getColumnIndex("date"));
        		addInContentEvent(strTitle, strDate, strContent, id);
        		count++;
        	}
        	else{
        	}
        }
        return count;

	}
	
	private void addInTitleEvent(String title, String date, String content, int id) {
		//Using the following if statement to judge if we need a new row
    	if ((inTitleEventsNum % 2) == 0) {
    		//Creating a LinearLayout for a new row.
			inTitleLowestRow = new LinearLayout(context);
			inTitleLowestRow.setOrientation(LinearLayout.HORIZONTAL);
			resultInTitle.addView(inTitleLowestRow);
		}
    	
    	//Creating a LinearLayout for a new event.
		RelativeLayout newEvent = new RelativeLayout(context);
		newEvent.setTag(new String(""+id));
		inTitleLowestRow.addView(newEvent);
		
		//Set the style of newEvent
		LinearLayout.LayoutParams newEventLayoutParams = new LinearLayout.LayoutParams(300,300);
		newEventLayoutParams.setMargins(20, 20, 20, 20);
		newEvent.setLayoutParams(newEventLayoutParams);
		newEvent.setBackgroundResource(R.drawable.event);
		
		//Set OnClickListener on newEvent
		newEvent.setOnClickListener(new EventOnClickListener());
				
		//Creating the descended element for the event.
		TextView newTitle = new TextView(context);
		TextView newDate = new TextView(context);
		TextView newContent = new TextView(context);
		
		newTitle.setId(200);
		newDate.setId(201);
		newContent.setId(202);
		
		newTitle.setText(title);
		newDate.setText(date);
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

		inTitleEventsNum++;
	}
	
	private void addInContentEvent(String title, String date, String content, int id) {
		//Using the following if statement to judge if we need a new row
    	if ((inContentEventsNum % 2) == 0) {
    		//Creating a LinearLayout for a new row.
			inContentLowestRow = new LinearLayout(context);
			inContentLowestRow.setOrientation(LinearLayout.HORIZONTAL);
			resultInContent.addView(inContentLowestRow);
		}
    	
    	//Creating a LinearLayout for a new event.
		RelativeLayout newEvent = new RelativeLayout(context);
		newEvent.setTag(new String(""+id));
		inContentLowestRow.addView(newEvent);
		
		//Set the style of newEvent
		LinearLayout.LayoutParams newEventLayoutParams = new LinearLayout.LayoutParams(300,300);
		newEventLayoutParams.setMargins(20, 20, 20, 20);
		newEvent.setLayoutParams(newEventLayoutParams);
		newEvent.setBackgroundResource(R.drawable.event);
		
		//Set OnClickListener on newEvent
		newEvent.setOnClickListener(new EventOnClickListener());
				
		//Creating the descended element for the event.
		TextView newTitle = new TextView(context);
		TextView newDate = new TextView(context);
		TextView newContent = new TextView(context);
		
		newTitle.setId(300);
		newDate.setId(301);
		newContent.setId(302);
		
		newTitle.setText(title);
		newDate.setText(date);
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
		newDate.setTextColor(Color.rgb(255,0,0));
		newDate.setLayoutParams(newDateLayoutParams);
		
		RelativeLayout.LayoutParams newContentLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
		newContentLayoutParams.addRule(RelativeLayout.BELOW, newDate.getId());
		newContentLayoutParams.setMargins(60, 10, 20, 30);
		newContent.setLayoutParams(newContentLayoutParams);

		inContentEventsNum++;
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
			myIntent.setClass(SearchPageActivity.this,DetailPageActivity.class);
			myIntent.putExtras(myBundle);
			startActivity(myIntent);
			SearchPageActivity.this.finish();
		}
    }
	
	//Search for target string in source string
	//arg1: target string
	//arg2: source string
	//return value: boolean(true for contain, false for not contain)
	private boolean containSubString(String targetString, String sourceString) {
		if (sourceString.indexOf(targetString) != -1 && !targetString.equals(new String(""))) {
			return true;
		}
		else
			return false;
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_page, menu);
        return true;
    }
	
}
