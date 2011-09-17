package ws.munday.barcamptampa;

import java.io.IOException;
import java.util.Date;

import org.apache.http.client.ClientProtocolException;

import ws.munday.barcamptampa.BarcampTampaContentProvider.barcampDbHelper;
import ws.munday.barcamptampa.R.anim;
import ws.munday.barcamptampa.R.id;
import ws.munday.barcamptampa.R.layout;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ScheduleItemActivity extends Activity {

	private Handler handler;
	private DatabaseSyncer dbSyncer;
	private barcampDbHelper dbHelper;
	private SQLiteDatabase db;
	private int itemId;
	Animation refreshAnim;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(layout.talk);
		handler = new Handler();
        dbSyncer = new DatabaseSyncer(getApplicationContext());		
        dbHelper = new barcampDbHelper(getApplicationContext(), BarcampTampaContentProvider.DATABASE_NAME, null, BarcampTampaContentProvider.DATABASE_VERSION);
		db = dbHelper.getWritableDatabase();
	
		refreshAnim = AnimationUtils.loadAnimation(getApplicationContext(),anim.rotate);
		 
		ImageView refresh = (ImageView) findViewById(id.refresh);
		refresh.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				new syncTask().execute();
				
			}
		});
		
		Intent i = getIntent();
		itemId = i.getIntExtra("ITEM_ID", -1);
		
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void onStart() {
		loadItem(itemId);
		super.onStart();
	}
	
	public void loadItem(int id){
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
				BarcampTampaContentProvider.SCHEDULE_ITEM_SHEET_ID + "=" + id, null, null, null,BarcampTampaContentProvider.START_TIME);
		
		if(c!=null){
			
			final ScheduleItem i = new ScheduleItem();
			
			while(c.moveToNext()){
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
				
			}
		
			TextView title = (TextView) findViewById(R.id.talk_title);
			title.setText(i.title);
			
			TextView desc = (TextView) findViewById(R.id.talk_desc);
			desc.setText(i.description);
			
			TextView speaker = (TextView) findViewById(R.id.talk_speaker);
			speaker.setText("by " + i.speaker);
			
			if(i.speakerTwitter!=null){
				speaker.setText("by " + i.speaker + "(" + i.speakerTwitter + ")");
				
			}
			
			CheckBox star = (CheckBox) findViewById(R.id.starred);
			star.setChecked(i.isStarred);
			star.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					starItem(itemId, isChecked);
				}
			});
			
			ImageView share = (ImageView) findViewById(R.id.share_icon);
			share.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
					shareIntent.setType("text/plain");
					shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Barcamp Tampa Bay");
					shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "I'm attending " + i.title + " by " + i.speaker + " at #barcamptampabay");

					 startActivity(Intent.createChooser(shareIntent, "Share Using..."));
				}
			});
			
			
			ImageView url = (ImageView) findViewById(R.id.url_icon);
			LinearLayout url_wrap = (LinearLayout) findViewById(R.id.url);
			if(i.speakerWebsite.trim().length()<=0){
				url.setVisibility(View.GONE);
				url_wrap.setVisibility(View.GONE);
			}else{
				url.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						String url = i.speakerWebsite;
						if(!url.toLowerCase().startsWith("http://")){
							url = "http://" + url;
						}
						Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setData(Uri.parse(url));
						startActivity(intent);
					}
				});
			}
			
			ImageView slides = (ImageView) findViewById(R.id.slides_icon);
			LinearLayout slides_wrap = (LinearLayout) findViewById(R.id.slides);
			if(i.slidesUrl.trim().length()<=0){
				slides.setVisibility(View.GONE);
				slides_wrap.setVisibility(View.GONE);
			}else{
				slides.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						String url = i.slidesUrl;
						if(!url.toLowerCase().startsWith("http://")){
							url = "http://" + url;
						}
						Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setData(Uri.parse(url));
						startActivity(intent);
					}
				});
			}
			
			
			
			c = db.query(BarcampTampaContentProvider.SCHEDULE_TABLE_NAME, columns, 
					BarcampTampaContentProvider.SCHEDULE_ITEM_SHEET_ID + "=" + id, null, null, null,BarcampTampaContentProvider.START_TIME);
			
			if(c!=null){
				
				ScheduleItem itm = new ScheduleItem();
				
				while(c.moveToNext()){
					
						itm.id = c.getLong(BarcampTampaContentProvider.SCHEDULE_ITEM_ID_COLUMN);
						itm.sheetId = c.getString(BarcampTampaContentProvider.SHEET_ID_COLUMN);
						itm.roomName = c.getString(BarcampTampaContentProvider.ROOM_NAME_COLUMN);
						itm.startTime = new Date(c.getLong(BarcampTampaContentProvider.START_TIME_COLUMN));
						itm.endTime = new Date(c.getLong(BarcampTampaContentProvider.END_TIME_COLUMN));
						itm.title = c.getString(BarcampTampaContentProvider.TITLE_COLUMN);
						itm.description = c.getString(BarcampTampaContentProvider.DESCRIPTION_COLUMN);
						itm.speaker = c.getString(BarcampTampaContentProvider.SPEAKER_COLUMN);
						itm.speakerTwitter = c.getString(BarcampTampaContentProvider.SPEAKER_TWITTER_COLUMN);
						itm.speakerWebsite = c.getString(BarcampTampaContentProvider.SPEAKER_URL_COLUMN);
						itm.slidesUrl = c.getString(BarcampTampaContentProvider.SLIDES_URL_COLUMN);
						itm.isStarred = c.getInt(BarcampTampaContentProvider.STARRED_COLUMN)==1;
					
						if(!itm.sheetId.equals(i.sheetId) && itm.startTime.equals(itm)){
							i.conflictingItems.add(itm);
						}
				}

				c.close();
			}
			
		}
		
		
		
	}
	
	protected void onDestroy() {
		dbSyncer.close();
		db.close();
		dbHelper.close();
		super.onDestroy();
	}
	
	private boolean starItem(int id, boolean star){
		ContentValues v = new ContentValues();
		v.put(BarcampTampaContentProvider.STARRED, star?1:0);
		
		int ret = db.update(BarcampTampaContentProvider.SCHEDULE_TABLE_NAME, v, BarcampTampaContentProvider.SCHEDULE_ITEM_SHEET_ID + "=" + id, null);
		
		return ret==0?false:true;
	}
	
	class syncTask extends UserTask<Void, Void, Void>{

		final ImageView refresh = (ImageView) findViewById(id.refresh);
		
 		@Override
 		public Void doInBackground(Void... params) {
 			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					refresh.startAnimation(refreshAnim);
				}
			});
 			try {
				dbSyncer.syncData();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
 			return null;
 		}
     	
 		public void onPostExecute(Void result) {
 			Log.d("bctb","sync done");
 			
			loadItem(itemId);
			
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					refresh.clearAnimation();
				}
			}, 600); 
			
			
		}
 		
     };
	
}
