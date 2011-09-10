package ws.munday.barcamptampa;

import java.util.ArrayList;
import ws.munday.barcamptampa.R.id;
import ws.munday.barcamptampa.R.layout;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

public class SponsorsActivity extends Activity {

	private SponsorAdapter items;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(layout.sponsors);
		new syncTask().execute();
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void onStart() {
			
		super.onStart();
	}
	
	private ArrayList<Sponsor> getItems(){
		try {
			return CSVReader.getSponsors();
		} catch (Exception e) {
			return new ArrayList<Sponsor>();
		}
	}
	
	class syncTask extends UserTask<Void, Void, Void>{

 		@Override
 		public Void doInBackground(Void... params) {
 			items = new SponsorAdapter(getItems(), SponsorsActivity.this);
			return null;
 		}
     	
 		public void onPostExecute(Void result) {
 			Log.d("bctb","sync done");
 			ListView l = (ListView)findViewById(id.sponsorlist);
 			l.setAdapter(items);
 		}
 		
     };
	
}
