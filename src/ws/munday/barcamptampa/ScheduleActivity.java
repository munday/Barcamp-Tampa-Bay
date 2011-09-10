package ws.munday.barcamptampa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import ws.munday.barcamptampa.BarcampTampaContentProvider.barcampDbHelper;
import ws.munday.barcamptampa.R.anim;
import ws.munday.barcamptampa.R.id;
import ws.munday.barcamptampa.R.layout;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

public class ScheduleActivity extends Activity implements StarCheckListener {

	private Handler handler;
	private DatabaseSyncer dbSyncer;
	private barcampDbHelper dbHelper;
	private SQLiteDatabase db;
	ScheduleItemAdapter items;
	private Animation refreshAnim;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(layout.schedule);
       
        refreshAnim = AnimationUtils.loadAnimation(getApplicationContext(),anim.rotate);
        
        handler = new Handler();
        dbSyncer = new DatabaseSyncer(getApplicationContext());		
        dbHelper = new barcampDbHelper(getApplicationContext(), BarcampTampaContentProvider.DATABASE_NAME, null, BarcampTampaContentProvider.DATABASE_VERSION);
		db = dbHelper.getWritableDatabase();
	
		ImageView refresh = (ImageView) findViewById(id.refresh);
		refresh.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new syncTask().execute();
			}
		});
		
		
		
	}
	
	@Override
	protected void onStart() {
		ListView l = (ListView)findViewById(id.scheduleitems);
		new syncTask().execute();
		l.setOnItemClickListener( new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				Intent i = new Intent(getApplicationContext(),ScheduleItemActivity.class);
				ScheduleItem itm = (ScheduleItem)items.getItem(arg2);
				i.putExtra("ITEM_ID", Integer.valueOf(itm.sheetId));
				startActivity(i);
			}
			
		});
		
		super.onStart();
	}
	
	@Override
	protected void onDestroy() {
		dbSyncer.close();
		db.close();
		dbHelper.close();
		super.onDestroy();
	}
	
	private ArrayList<ScheduleItem> getItems(){
		ArrayList<ScheduleItem> itms = new ArrayList<ScheduleItem>();
		String[] columns = {BarcampTampaContentProvider.SCHEDULE_ITEM_ID,
							BarcampTampaContentProvider.SCHEDULE_ITEM_SHEET_ID,
							BarcampTampaContentProvider.ROOM_NAME,
							BarcampTampaContentProvider.START_TIME,
							BarcampTampaContentProvider.END_TIME,
							BarcampTampaContentProvider.TITLE,
							BarcampTampaContentProvider.DESCRIPTION,
							BarcampTampaContentProvider.SPEAKER,
							BarcampTampaContentProvider.SPEAKER_TWITTER,
							BarcampTampaContentProvider.SPEAKER_URL,
							BarcampTampaContentProvider.SLIDES_URL,
							BarcampTampaContentProvider.STARRED};
		
		Cursor c = db.query(BarcampTampaContentProvider.SCHEDULE_TABLE_NAME, columns, 
				null, null, null, null,BarcampTampaContentProvider.START_TIME);
		
		if(c!=null){
			while(c.moveToNext()){
				ScheduleItem i = new ScheduleItem();
				i.id = c.getLong(BarcampTampaContentProvider.SCHEDULE_ITEM_ID_COLUMN);
				i.sheetId = c.getString(BarcampTampaContentProvider.SHEET_ID_COLUMN);
				i.roomName = c.getString(BarcampTampaContentProvider.ROOM_NAME_COLUMN);
				i.startTime = new Date(c.getLong(BarcampTampaContentProvider.START_TIME_COLUMN));
				i.endTime = new Date(c.getLong(BarcampTampaContentProvider.END_TIME_COLUMN));
				i.title = c.getString(BarcampTampaContentProvider.TITLE_COLUMN);
				i.description = c.getString(BarcampTampaContentProvider.DESCRIPTION_COLUMN);
				i.speaker = c.getString(BarcampTampaContentProvider.SPEAKER_COLUMN);
				i.speakerTwitter = c.getString(BarcampTampaContentProvider.SPEAKER_TWITTER_COLUMN);
				i.speakerWebsite = c.getString(BarcampTampaContentProvider.SPEAKER_URL_COLUMN);
				i.slidesUrl = c.getString(BarcampTampaContentProvider.SLIDES_URL_COLUMN);
				i.isStarred = c.getInt(BarcampTampaContentProvider.STARRED_COLUMN)==1;
				itms.add(i);
			}
		}
		
		
		Collections.sort(itms, new TimeComparer());
		return itms;
	}
	
	private boolean starItem(long id, boolean star){
		ContentValues v = new ContentValues();
		v.put(BarcampTampaContentProvider.STARRED, (star?1:0));
		
		int ret = db.update(BarcampTampaContentProvider.SCHEDULE_TABLE_NAME, v, BarcampTampaContentProvider.SCHEDULE_ITEM_ID + "=" + id, null);
		Log.d("bctb", "star success:" + ret);
		return ret==0?false:true;
	}

	@Override
	public boolean OnItemStarred(long id, boolean star) {
		return starItem(id, star);
	}
	
	class syncTask extends UserTask<Void, Void, ArrayList<ScheduleItem>>{

		final ImageView refresh = (ImageView) findViewById(id.refresh);
		
 		@Override
 		public ArrayList<ScheduleItem> doInBackground(Void... params) {
 			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					refresh.startAnimation(refreshAnim);
				}
			});
 			return getItems();
 		}
     	
 		public void onPostExecute(ArrayList<ScheduleItem> result) {
 			Log.d("bctb","sync done");
 				if(items==null){
 					items = new ScheduleItemAdapter(result, ScheduleActivity.this, getApplicationContext());
 					ListView l = (ListView)findViewById(id.scheduleitems);
 					l.setAdapter(items);
 				}else{
 					items.setItems(result);
 					items.notifyDataSetChanged();
 				}
 			
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						refresh.clearAnimation();
					}
				}, 600); 
			
			
		}
 		
     };
	
}
