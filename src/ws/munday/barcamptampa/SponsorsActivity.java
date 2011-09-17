package ws.munday.barcamptampa;

import java.util.ArrayList;

import ws.munday.barcamptampa.R.anim;
import ws.munday.barcamptampa.R.id;
import ws.munday.barcamptampa.R.layout;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;

public class SponsorsActivity extends Activity {

	private SponsorAdapter items;
	private Animation refreshAnim;
	private Handler handler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(layout.sponsors);
		new syncTask().execute();
		
		handler = new Handler();
		
		refreshAnim = AnimationUtils.loadAnimation(getApplicationContext(),anim.rotate);
        
		ImageView refresh = (ImageView) findViewById(id.refresh);
		refresh.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new syncTask().execute();
			}
		});
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
		
		final ImageView refresh = (ImageView) findViewById(id.refresh);
		
 		@Override
 		public Void doInBackground(Void... params) {
 			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					if(refresh.getAnimation()==null)
						refresh.startAnimation(refreshAnim);
				}
			});
 			items = new SponsorAdapter(getItems(), SponsorsActivity.this);
			return null;
 		}
     	
 		public void onPostExecute(Void result) {
 			Log.d("bctb","sync done");
 			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					refresh.clearAnimation();
				}
			}, 600); 
 			ListView l = (ListView)findViewById(id.sponsorlist);
 			if(items!=null && l!=null)
 				l.setAdapter(items);
 		}
 		
     };
	
}
