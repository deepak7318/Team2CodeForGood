package com.example.handsonandroid;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Locale;

import com.example.handsonandroid.custom_calendar.CalendarAdapter;
import com.example.handsonandroid.custom_calendar.CalendarUtility;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author Garrett
 *
 *
 * Activity to use to launch the Calendar for the application
 */
public class CalendarActivity extends Activity {

 public GregorianCalendar month, itemmonth;// calendar instances.

 public CalendarAdapter adapter;// adapter instance
 public Handler handler;// for grabbing some event values for showing the dot
       // marker.
 public ArrayList items; // container to store calendar items which
         // needs showing the event marker
 ArrayList event;
 LinearLayout rLayout;
 ArrayList date;
 ArrayList desc;

 /**
  * What happens for the onCreate method.
  */
 public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	  setContentView(R.layout.activity_calendar);
	  Locale.setDefault(Locale.US);
	
	  rLayout = (LinearLayout) findViewById(R.id.text);
	  month = (GregorianCalendar) GregorianCalendar.getInstance();
	  itemmonth = (GregorianCalendar) month.clone();
	
	  //Create new items arraylist
	  items = new ArrayList();
	
	  adapter = new CalendarAdapter(this, month);
	
	  GridView gridview = (GridView) findViewById(R.id.gridview);
	  gridview.setAdapter(adapter);
	
	  handler = new Handler();
	  handler.post(calendarUpdater);
	
	  TextView title = (TextView) findViewById(R.id.title);
	  title.setText(android.text.format.DateFormat.format("MMMM yyyy", month));
	
	  RelativeLayout previous = (RelativeLayout) findViewById(R.id.previous);
	
	  
	  //Set the button lsiterners
	  previous.setOnClickListener(new OnClickListener() {

   @Override
   public void onClick(View v) {
    setPreviousMonth();
    refreshCalendar();
   }
  });

  RelativeLayout next = (RelativeLayout) findViewById(R.id.next);
  next.setOnClickListener(new OnClickListener() {

   @Override
   public void onClick(View v) {
    setNextMonth();
    refreshCalendar();

   }
  });

  
  /**
   * What happens when one of the calendar items are clicked
   */
  gridview.setOnItemClickListener(new OnItemClickListener() {
   public void onItemClick(AdapterView parent, View v,
	 int position, long id) {
	   // removing the previous view if added
	   if (((LinearLayout) rLayout).getChildCount() > 0) {
		   ((LinearLayout) rLayout).removeAllViews();
	   }
	   desc = new ArrayList();
	   date = new ArrayList();
	   ((CalendarAdapter) parent.getAdapter()).setSelected(v);
	   String selectedGridDate = CalendarAdapter.dayString
			   .get(position);
	   String[] separatedTime = selectedGridDate.split("-");
	   String gridvalueString = separatedTime[2].replaceFirst("^0*",
			   "");// taking last part of date. ie; 2 from 2012-12-02.
	   int gridvalue = Integer.parseInt(gridvalueString);
	   // navigate to next or previous month on clicking offdays.
	   if ((gridvalue > 10) && (position < 8)) {
		   setPreviousMonth();
		   refreshCalendar();
	   } else if ((gridvalue < 7) && (position > 28)) {
		   setNextMonth();
		   refreshCalendar();
	   }
	   v.setBackgroundResource(R.drawable.calendar_cell);
	   ((CalendarAdapter) parent.getAdapter()).setSelected(v);

	   for (int i = 0; i < CalendarUtility.startDates.size(); i++) {
		   if (CalendarUtility.startDates.get(i).equals(selectedGridDate)) {
			   desc.add(CalendarUtility.nameOfEvent.get(i));
		   }
	   }

	   if (desc.size() > 0) {
		   for (int i = 0; i < desc.size(); i++) {
			   TextView rowTextView = new TextView(CalendarActivity.this);
			   rowTextView.setTextSize(16f);
			   rowTextView.setClickable(true);
			   rowTextView.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					TextView view = (TextView) v;
					String title = view.getText().toString().substring(6, view.getText().toString().length());
					
					
					Events event = Events.findByName(title);
					
					boolean queryEventName = true;
					if(event == null)queryEventName = false;
					
					if(queryEventName){
						Intent i = new Intent(CalendarActivity.this, EventActivity.class);
						i.putExtra("Event", event);
					}else{
						//Do nothing
					}
				}
				  
				   
				   
			   });

			   // set some properties of rowTextView or something
			   rowTextView.setText("Event:" + desc.get(i));
			   rowTextView.setTextColor(Color.BLACK);

			   // add the textview to the linearlayout
			   rLayout.addView(rowTextView);
			   
		   }

	   }

	   desc = null;

   	}

  	});
 }

 /**
  * Set the next month when the next button is clicked.
  */
 	protected void setNextMonth() {
 		if (month.get(GregorianCalendar.MONTH) == month
 				.getActualMaximum(GregorianCalendar.MONTH)) {
 			month.set((month.get(GregorianCalendar.YEAR) + 1),
 					month.getActualMinimum(GregorianCalendar.MONTH), 1);
 		} else {
 			month.set(GregorianCalendar.MONTH,
 					month.get(GregorianCalendar.MONTH) + 1);
 		}

 	}

 	/**
 	 * Set the previous month when the previous button is clicked.
 	 */
 	protected void setPreviousMonth() {
 		if (month.get(GregorianCalendar.MONTH) == month
 				.getActualMinimum(GregorianCalendar.MONTH)) {
 			month.set((month.get(GregorianCalendar.YEAR) - 1),
 					month.getActualMaximum(GregorianCalendar.MONTH), 1);
 		} else {
 			month.set(GregorianCalendar.MONTH,
 					month.get(GregorianCalendar.MONTH) - 1);
 		}

 	}

 	/**
 	 * Show the toast
 	 * @param string : String to toast
 	 */
 	protected void showToast(String string) {
 		Toast.makeText(this, string, Toast.LENGTH_SHORT).show();

 	}

 	/**
 	 * Refresh the calendar
 	 */
 	public void refreshCalendar() {
 		TextView title = (TextView) findViewById(R.id.title);

 		adapter.refreshDays();
 		adapter.notifyDataSetChanged();
 		handler.post(calendarUpdater); // generate some calendar items

 		title.setText(android.text.format.DateFormat.format("MMMM yyyy", month));
 	}

 	/**
 	 * Run the calendar Runnable, clears the items, updates the items, currently prints out the contents as well.
 	 */
 	public Runnable calendarUpdater = new Runnable() {

 		@Override
 		public void run() {
 			items.clear();

 			// Print dates of the current week
 			DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
 			String itemvalue;
 			event = CalendarUtility.readCalendarEvent(CalendarActivity.this);
 			Log.d("=====Event====", event.toString());
 			Log.d("=====Date ARRAY====", CalendarUtility.startDates.toString());

 			for (int i = 0; i < CalendarUtility.startDates.size(); i++) {
 				itemvalue = df.format(itemmonth.getTime());
 				itemmonth.add(GregorianCalendar.DATE, 1);
 				items.add(CalendarUtility.startDates.get(i).toString());
 			}
 			adapter.setItems(items);
 			adapter.notifyDataSetChanged();
 		}
 	};
}