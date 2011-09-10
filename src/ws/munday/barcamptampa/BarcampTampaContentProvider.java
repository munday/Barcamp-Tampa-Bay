package ws.munday.barcamptampa;


import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.net.Uri;
import android.text.TextUtils;

public class BarcampTampaContentProvider extends ContentProvider {

	private static final String barcampAuthority = "ws.munday.provider.barcamptampa"; 
	public static final String barcampURI = "content://ws.munday.provider.barcamp/schedule";
	public static final String barcampAddURI = "content://ws.munday.provider.barcamp/schedule/add";
	
	public static final Uri barcamp_CONTENT_URI = Uri.parse(barcampURI);
	public static final Uri barcamp_ADD_URI = Uri.parse(barcampAddURI);
	
	private static final int ALL_SCHEDULE_ITEMS=0;
	private static final int SINGLE_SCHEDULE_ITEM=1;
	private static final int ADD_SCHEDULE_ITEM=2;
	private static final int UPDATE_SCHEDULE_ITEM=3;
	private static final int DELETE_SCHEDULE_ITEM=4;
	private static final int SCHEDULES_BY_ROOM=5;
	private static final int SCHEDULES_BY_TIME=6;
	private static final int STARRED_SCHEDULE_ITEMS=7;
	private static final int SINGLE_SCHEDULE_ITEM_SHEET_ID=8;
	
	
	private static final UriMatcher uriMatcher;
	private SQLiteDatabase barcampDb;
	public static final String DATABASE_NAME = "barcamp.db";
	public static final String SCHEDULE_TABLE_NAME = "Schedule";
	public static final int DATABASE_VERSION = 1;
	
	public static final String SCHEDULE_ITEM_ID = "_id";
	public static final String SCHEDULE_ITEM_SHEET_ID = "id";
	public static final String ROOM_NAME = "roomName";
	public static final String START_TIME = "startTime";
	public static final String END_TIME = "endTime";
	public static final String TITLE = "title";
	public static final String DESCRIPTION = "description";
	public static final String SPEAKER = "speaker";
	public static final String SPEAKER_TWITTER = "speakerTwitter";
	public static final String SPEAKER_URL = "speakerUrl";
	public static final String SLIDES_URL = "slidesUrl";
	public static final String STARRED = "starred";
	
	public static final int SCHEDULE_ITEM_ID_COLUMN = 0;
	public static final int SHEET_ID_COLUMN = 1;
	public static final int ROOM_NAME_COLUMN = 2;
	public static final int START_TIME_COLUMN = 3;
	public static final int END_TIME_COLUMN = 4;
	public static final int TITLE_COLUMN = 5;
	public static final int DESCRIPTION_COLUMN = 6;
	public static final int SPEAKER_COLUMN = 7;
	public static final int SPEAKER_TWITTER_COLUMN = 8;
	public static final int SPEAKER_URL_COLUMN = 9;
	public static final int SLIDES_URL_COLUMN = 10;
	public static final int STARRED_COLUMN = 11;
	
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(barcampAuthority, "schedule", ALL_SCHEDULE_ITEMS);
		uriMatcher.addURI(barcampAuthority, "schedule/starred", STARRED_SCHEDULE_ITEMS);
		uriMatcher.addURI(barcampAuthority, "schedule/sheetid/#", SINGLE_SCHEDULE_ITEM_SHEET_ID);
		uriMatcher.addURI(barcampAuthority, "schedule/#", SINGLE_SCHEDULE_ITEM);
		uriMatcher.addURI(barcampAuthority, "schedule/room/#", SCHEDULES_BY_ROOM);
		uriMatcher.addURI(barcampAuthority, "schedule/time/*", SCHEDULES_BY_TIME);
		uriMatcher.addURI(barcampAuthority, "schedule/update/#", UPDATE_SCHEDULE_ITEM);
		uriMatcher.addURI(barcampAuthority, "schedule/add/#", ADD_SCHEDULE_ITEM);
	}
	
	
	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {

		int count = 0;
		switch (uriMatcher.match(uri)){
			case ALL_SCHEDULE_ITEMS:
				count = barcampDb.delete(SCHEDULE_TABLE_NAME, where, whereArgs);
				break;
			case DELETE_SCHEDULE_ITEM:
				String scheduleId = uri.getPathSegments().get(2);
				count = barcampDb.delete(SCHEDULE_TABLE_NAME, 
						SCHEDULE_ITEM_ID + "=" + scheduleId + (!TextUtils.isEmpty(where) ? "( AND " + where + ")" : "" ), 
						whereArgs);
				break;
			default:
				throw new IllegalArgumentException("Invalid Uri " + uri);
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public String getType(Uri uri) {

		switch(uriMatcher.match(uri)){
		case ALL_SCHEDULE_ITEMS:
			return "vnd.android.cursor.item/vnd.munday.barcamp.schedule";
		case STARRED_SCHEDULE_ITEMS:
			return "vnd.android.cursor.item/vnd.munday.barcamp.schedule";
		case SINGLE_SCHEDULE_ITEM:
			return "vnd.android.cursor.item/vnd.munday.barcamp.schedule";
		case SINGLE_SCHEDULE_ITEM_SHEET_ID:
			return "vnd.android.cursor.item/vnd.munday.barcamp.schedule";
		case ADD_SCHEDULE_ITEM:
			return "vnd.android.cursor.item/vnd.munday.barcamp.schedule";
		case UPDATE_SCHEDULE_ITEM:
			return "vnd.android.cursor.item/vnd.munday.barcamp.schedule";
		case DELETE_SCHEDULE_ITEM:
			return "vnd.android.cursor.item/vnd.munday.barcamp.schedule";
		case SCHEDULES_BY_ROOM:
			return "vnd.android.cursor.item/vnd.munday.barcamp.schedule";
		case SCHEDULES_BY_TIME:
			return "vnd.android.cursor.item/vnd.munday.barcamp.schedule";
		default:
			throw new IllegalArgumentException("Invalid Uri " + uri);
		}
	
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		switch (uriMatcher.match(uri)){
		case ADD_SCHEDULE_ITEM:
			long id = barcampDb.insert(SCHEDULE_TABLE_NAME, "", values);
			if(id>0){
				Uri newUri = Uri.parse(barcampAddURI + "/" + id);
				getContext().getContentResolver().notifyChange(uri, null);
				return newUri;
			}
		default:
			throw new SQLException("Failed to add schedule item into " + uri);	
		}
	}

	@Override
	public boolean onCreate() {

		Context c = getContext();
		barcampDbHelper dbHelper;
		dbHelper = new barcampDbHelper(c, DATABASE_NAME, null, DATABASE_VERSION);
		barcampDb = dbHelper.getWritableDatabase();
		return (barcampDb == null)?false:true;
		
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
			String sort) {

		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(SCHEDULE_TABLE_NAME);

		switch (uriMatcher.match(uri)){

			case SINGLE_SCHEDULE_ITEM:
				qb.appendWhere(SCHEDULE_ITEM_ID + "=" + uri.getPathSegments().get(1) );
				break;
			case SINGLE_SCHEDULE_ITEM_SHEET_ID:
				qb.appendWhere(SCHEDULE_ITEM_SHEET_ID + "=" + uri.getPathSegments().get(2) );
				break;
			case SCHEDULES_BY_ROOM:
					qb.appendWhere(ROOM_NAME + "='" +  uri.getPathSegments().get(2) + "'" );
					break;
			case SCHEDULES_BY_TIME:
				qb.appendWhere(START_TIME + "='" +  uri.getPathSegments().get(2) + "'" );
				break;
			case STARRED_SCHEDULE_ITEMS:
				qb.appendWhere(STARRED + "=1" );
				break;
			case ALL_SCHEDULE_ITEMS:
				break;
			default: 
				throw new IllegalArgumentException("Invalid Uri " + uri);
			
		}
		
		String orderBy;
		if(TextUtils.isEmpty(sort)){
			orderBy = SCHEDULE_ITEM_ID;
		}else{
			orderBy = sort;
		}
		
		
		Cursor c = qb.query(barcampDb, projection, selection, selectionArgs, null, null, orderBy);
		
		c.setNotificationUri(getContext().getContentResolver(), uri);
		
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
		int count = 0;
		String scheduleId;
		switch (uriMatcher.match(uri)){
			case UPDATE_SCHEDULE_ITEM:
				scheduleId = uri.getPathSegments().get(2);
				count = barcampDb.update(SCHEDULE_TABLE_NAME, values, SCHEDULE_ITEM_ID + "=" + scheduleId + (!TextUtils.isEmpty(where)? "( AND " + where + ")" : ""), whereArgs);
				break;
			default:
				throw new IllegalArgumentException("Unknown Uri " + uri);
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		
		return count;
	}

	public static class barcampDbHelper extends SQLiteOpenHelper{
		private static final String DATABASE_CREATE = "Create Table If Not Exists "
			+ SCHEDULE_TABLE_NAME + "("
			+ SCHEDULE_ITEM_ID + " integer primary key autoincrement, "
			+ SCHEDULE_ITEM_SHEET_ID + " integer,"
			+ ROOM_NAME + " text, "
			+ START_TIME + " date, "
			+ END_TIME + " date, "
			+ TITLE + " text, "
			+ DESCRIPTION + " text, "
			+ SPEAKER + " text, "
			+ SPEAKER_TWITTER + " text, "
			+ SPEAKER_URL + " text, "
			+ SLIDES_URL + " text, "
			+ STARRED + " integer"
			+ ")";
		
		public barcampDbHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			
		}
	
	}
	
}
