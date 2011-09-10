package ws.munday.barcamptampa;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;

import ws.munday.barcamptampa.BarcampTampaContentProvider.barcampDbHelper;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseSyncer {

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
		
		for(ScheduleItem i:schedule){
			upsertScheduleItem(i);
		}
		
		
	}
	
	public void close(){
		db.close();
		dbHelper.close();
	}
	
	private void upsertScheduleItem(ScheduleItem i){
		ContentValues v = new ContentValues();
		v.put(BarcampTampaContentProvider.SCHEDULE_ITEM_SHEET_ID, Integer.parseInt(i.sheetId));
		v.put(BarcampTampaContentProvider.START_TIME, i.startTime);
		v.put(BarcampTampaContentProvider.END_TIME, i.endTime);
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
	
}
