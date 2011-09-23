package ws.munday.barcamptampa;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import org.apache.http.client.ClientProtocolException;

import ws.munday.barcamptampa.BarcampTampaContentProvider.barcampDbHelper;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DatabaseSyncer {

	public static String CONFERENCE_DATE_WITHOUT_TIME = "9/24/2011 ";
	private SQLiteDatabase db;
	private barcampDbHelper dbHelper;
	private Context context;
	
	public DatabaseSyncer(Context c){
		context = c;
		dbHelper = new barcampDbHelper(context, BarcampTampaContentProvider.DATABASE_NAME, null, BarcampTampaContentProvider.DATABASE_VERSION);
		db = dbHelper.getReadableDatabase();
	}
	
	public void syncData() throws ClientProtocolException, IOException{
		
		ArrayList<ScheduleItem> schedule = CSVReader.getSchedule();
		ArrayList<ScheduleItem> dbschedule = getDBSchedule();
		
		removeDeleted(schedule, dbschedule);
		
		for(ScheduleItem i:schedule){
			upsertScheduleItem(i);
		}
		
		
	}
	
	public void close(){
		db.close();
		dbHelper.close();
	}
	
	@Override
	protected void finalize() throws Throwable {
		close();
		super.finalize();
	}
	
	private void upsertScheduleItem(ScheduleItem i){
		ContentValues v = new ContentValues();
		v.put(BarcampTampaContentProvider.SCHEDULE_ITEM_SHEET_ID, Integer.parseInt(i.sheetId));
		v.put(BarcampTampaContentProvider.START_TIME, i.startTime.getTime());
		v.put(BarcampTampaContentProvider.END_TIME, i.endTime.getTime());
		v.put(BarcampTampaContentProvider.ROOM_NAME, i.roomName);
		v.put(BarcampTampaContentProvider.TITLE, i.title);
		v.put(BarcampTampaContentProvider.DESCRIPTION, i.description);
		v.put(BarcampTampaContentProvider.SPEAKER, i.speaker);
		v.put(BarcampTampaContentProvider.SPEAKER_TWITTER, i.speakerTwitter);
		v.put(BarcampTampaContentProvider.SPEAKER_URL, i.speakerWebsite);
		v.put(BarcampTampaContentProvider.SLIDES_URL, i.slidesUrl);
		
		int updated = db.update(BarcampTampaContentProvider.SCHEDULE_TABLE_NAME, v, BarcampTampaContentProvider.SCHEDULE_ITEM_SHEET_ID + "=" + i.sheetId, null);
		
		if(updated == 0){
			db.insert(BarcampTampaContentProvider.SCHEDULE_TABLE_NAME, "", v);
		}
		
	}
	
	private ArrayList<ScheduleItem> getDBSchedule(){
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
			c.close();
		}
		
		
		Collections.sort(itms, new TimeComparer());
		return itms;
	}
	
	public boolean deleteItem(ScheduleItem i){
		int num = db.delete(BarcampTampaContentProvider.SCHEDULE_TABLE_NAME, BarcampTampaContentProvider.SCHEDULE_ITEM_SHEET_ID + "=" + i.sheetId, null);
		if(num==1)
			return true;
		else
			return false;
	}
	
	public void removeDeleted(ArrayList<ScheduleItem> csvItems, ArrayList<ScheduleItem> dbItems){
		//remove any items no longer in the csv
		for(ScheduleItem d : dbItems){
			boolean remove = true;
			for(ScheduleItem c : csvItems){
				if(c.sheetId.equals(d.sheetId)){
					remove = false;
				}
			}
			
			if(remove){
				Log.d("bctb","delete:" + d.sheetId);
				deleteItem(d);
			}
		}
	}
	
}
