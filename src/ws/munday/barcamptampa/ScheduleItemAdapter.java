package ws.munday.barcamptampa;

import java.util.ArrayList;

import ws.munday.barcamptampa.BarcampTampaContentProvider.barcampDbHelper;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ScheduleItemAdapter extends BaseAdapter{

	private ArrayList<ScheduleItem> items;
	private final LayoutInflater inflater;
	private final Context context;
	private final StarCheckListener checkListener;
	private barcampDbHelper dbHelper;
	private SQLiteDatabase db;
	
	
	public ScheduleItemAdapter(ArrayList<ScheduleItem> items, StarCheckListener l, Context c) {
		this.context = c;
		this.inflater = LayoutInflater.from(this.context);
		this.items = items;
		this.checkListener = l;
		dbHelper = new barcampDbHelper(this.context, BarcampTampaContentProvider.DATABASE_NAME, null, BarcampTampaContentProvider.DATABASE_VERSION);
		db = dbHelper.getWritableDatabase();
	
	}

	public ArrayList<ScheduleItem> getItems() {
		if(items!=null)
				return items;
		else
			return null;
	}

	public void setItems(ArrayList<ScheduleItem> v) {
		items = v;
	}
	
	public void clearItems() {
		items.clear();
	}

		
	@Override
	public int getCount() {
		if(items!=null)
				return items.size();
		else
			return 0;
	}

	@Override
	public Object getItem(int position) {
		if(items!=null){
				if(items.size()>=position)
					return items.get(position);
		}
		
		return null;
	}
	

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		final ScheduleItem s;
		//if(items.size()>=position)
			s = items.get(position);
		//else
		//	return null;
		// When convertView is not null, we can reuse it directly, there is no
		// need
		// to reinflate it. We only inflate a new View when the convertView
		// supplied
		// by ListView is null.
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.schedule_item, null);

			// Creates a ViewHolder and store references to the two children
			// views
			// we want to bind data to.
			holder = new ViewHolder();
			holder.time = (TextView) convertView.findViewById(R.id.hour_slot);
			holder.room = (TextView) convertView.findViewById(R.id.talk_room);
			holder.title = (TextView) convertView.findViewById(R.id.talk_title);
			holder.speaker = (TextView) convertView.findViewById(R.id.talk_speaker);
			holder.speakerTwitter = (TextView) convertView.findViewById(R.id.talk_speaker_twitter);
			holder.star = (CheckBox) convertView.findViewById(R.id.starred);
			holder.conflict = (TextView) convertView.findViewById(R.id.conflict);
			convertView.setTag(holder);
			
		} else {
			// Get the ViewHolder back to get fast access to the TextView
			// and the ImageView.
			holder = (ViewHolder) convertView.getTag();
		}

		// Bind the data efficiently with the holder.
		holder.time.setText(s.startTime);
		holder.room.setText(s.roomName);
		holder.title.setText(s.title);
		holder.star.setChecked(s.isStarred);
		holder.speaker.setText("by " + s.speaker + " ");
		holder.speakerTwitter.setText(s.speakerTwitter);
		holder.star.setChecked(isItemStarred(s.id));
		
		holder.star.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				CheckBox c =(CheckBox) v;
				checkListener.OnItemStarred(s.id, c.isChecked());
				
			}
		});
		
		if(s.conflictingItems != null && s.conflictingItems.size()>0){
			holder.conflict.setVisibility(View.VISIBLE);
			holder.conflict.setText("conflict - " + s.conflictingItems.size() + " other talk" + (s.conflictingItems.size()>1?"s":"") + " starred at " + s.startTime);
		}else{
			holder.conflict.setVisibility(View.GONE);
		}
		
		return convertView;
		
	}

	final class ViewHolder {
		TextView time;
		TextView room;
		TextView title;
		TextView speaker;
		TextView speakerTwitter;
		TextView conflict;
		CheckBox star;
		LinearLayout share;
		LinearLayout slides;
		LinearLayout url;
		
	}

	public boolean isItemStarred(long id){
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
					BarcampTampaContentProvider.SCHEDULE_ITEM_ID + "=" + id, null, null, null,BarcampTampaContentProvider.START_TIME);
				
				if(c!=null){
					c.moveToFirst();
					return c.getInt(BarcampTampaContentProvider.STARRED_COLUMN)==1;
				}else{
					return false;
				}
	}
	
	@Override
	protected void finalize() throws Throwable {
		db.close();
		dbHelper.close();
		super.finalize();
	}
	
	@Override
	public long getItemId(int position) {
		return items.get(position).id;
	}
}
