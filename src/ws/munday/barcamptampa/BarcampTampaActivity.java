package ws.munday.barcamptampa;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ws.munday.barcamptampa.R.id;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class BarcampTampaActivity extends Activity {

	private ScheduledThreadPoolExecutor tweetGetter;
	private ArrayList<View> homeIcons;
	private DatabaseSyncer dbSyncer;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.main);
       
        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        
        if(display.getOrientation()==1){
        	buildHomeIcons(5);
        	createButtonGrid(R.id.main_menu,10, homeIcons);
        }else{
    		buildHomeIcons(10);
    		createButtonGrid(R.id.main_menu,40, homeIcons);	
        }
    	
        Runnable tweetrefresher = new Runnable() {
			
			@Override
			public void run() {
				getBarcampTweets();
			}
		}; 
		
		if(tweetGetter==null){
			tweetGetter = new ScheduledThreadPoolExecutor(1);
			tweetGetter.scheduleAtFixedRate(tweetrefresher, 0, 30,TimeUnit.SECONDS);
		}
	
		dbSyncer = new DatabaseSyncer(getApplicationContext());		
		
    }

    @Override
    protected void onStart() {
    	new syncTask().execute();
    	super.onStart();
    }
    
    @Override
    protected void onDestroy() {
    	dbSyncer.close();
    	super.onDestroy();
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    	
    	
    	/*
    	TextView tweetHead=(TextView) findViewById(id.tweets_head);
		LinearLayout tweets = (LinearLayout) findViewById(id.tweets);
		ImageView logo = (ImageView) findViewById(id.logo);
		*/
    	if (newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE){
    		buildHomeIcons(5);
    		createButtonGrid(R.id.main_menu,10, homeIcons);
        	/*
    		logo.setScaleType(ScaleType.FIT_START);
    		tweetHead.setVisibility(View.GONE);
    		tweets.setVisibility(View.GONE);
    		*/
    	}else{
    		buildHomeIcons(10);
    		createButtonGrid(R.id.main_menu,40, homeIcons);
        	/*
    		logo.setScaleType(ScaleType.FIT_CENTER);
    		tweetHead.setVisibility(View.VISIBLE);
    		tweets.setVisibility(View.VISIBLE);
    		*/
    	}
    	
    	super.onConfigurationChanged(newConfig);
    }

    public void buildHomeIcons(int padding){
    	
    	homeIcons = new ArrayList<View>();
        homeIcons.add(makeIcon(R.drawable.schedule, padding, ScheduleActivity.class));
        homeIcons.add(makeIcon(R.drawable.starred, padding, StarredScheduleActivity.class));
        
        homeIcons.add(makeIcon(R.drawable.schedule, padding, UpcomingScheduleActivity.class));
        homeIcons.add(makeIcon(R.drawable.starred, padding, StarredUpcomingScheduleActivity.class));
        
        homeIcons.add(makeIcon(R.drawable.sponsors, padding, SponsorsActivity.class));

        /*
        LinearLayout l = new LinearLayout(this);
        l.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        l.setGravity(Gravity.CENTER);
        l.setPadding(padding, padding, padding, padding);
        ImageView img = new ImageView(this);
        img.setBackgroundDrawable(getResources().getDrawable(R.drawable.location));
        img.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String url = "http://maps.google.com/maps?daddr=1001+East+Palm+Ave+Tampa+FL+33605+(Kforce+Professional+Staffing)";
				Intent intent = new Intent(android.content.Intent.ACTION_VIEW,  Uri.parse(url));
				startActivity(intent);
			}
		});
        l.addView(img);
        homeIcons.add(l);
        */
    }
    
    private LinearLayout makeIcon(int drawable, int padding, final Class<?> c){
    	LinearLayout l = new LinearLayout(this);
        l.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        l.setGravity(Gravity.CENTER);
        l.setPadding(padding, padding, padding, padding);
        ImageView img = new ImageView(this);
        img.setBackgroundDrawable(getResources().getDrawable(drawable));
        img.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent();
				i.setClass(getApplicationContext(), c);
				startActivity(i);
			}
		});
        
        l.addView(img);
        return l;
    }
    
    private void createButtonGrid(int tableId, int padding, ArrayList<View> items ) {
    	
    	int viewWidth = this.getResources().getDisplayMetrics().widthPixels;
    	
    	int buttonWidth = getResources().getDrawable(R.drawable.icon).getIntrinsicWidth() + padding;
    	        
        TableLayout grid = (TableLayout) findViewById(tableId);
 
        grid.removeAllViews();
        
        int numColumns = (viewWidth / buttonWidth);
        
        int colNum = 0;
 
        TableRow row = new TableRow(this);
 
        for (int i = 0; i < items.size(); i++) {
 
 
                if (colNum == 0) {
 
                    row = new TableRow(this);
 
                }
 
                row.addView(
                    items.get(i),
                    new TableRow.LayoutParams()
                );
 
                colNum++;
                
                if (colNum == numColumns || i == items.size()-1) {
 
                    grid.addView(row);
                    grid.setStretchAllColumns(true);
 
                    grid.postInvalidate();
 
                    grid.forceLayout();
 
                    colNum = 0;
 
                }
 
            
 
        }
 
    }
    
    public void getBarcampTweets(){
    	ArrayList<Tweet> tweets = getTweets("#barcamptampa", 1);
    	if(tweets != null && tweets.size()>0){
    		int r = new Random().nextInt(tweets.size());
    		final Tweet t = tweets.get(r);
    		
    		runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					ImageView img = (ImageView) findViewById(R.id.tweeter);
		    		TextView tweet = (TextView) findViewById(R.id.tweet);
		    	
		    		img.setBackgroundDrawable(GetImageFromWeb(BarcampTampaActivity.this,t.image));
		    		tweet.setText(t.tweet);
		    		
		    		img.invalidate();
		    		tweet.invalidate();
		    		img.forceLayout();
		    		tweet.forceLayout();
				}
			});
    		
    	}
    }   
    
     public ArrayList<Tweet> getTweets(String searchTerm, int page) {
    	String searchUrl = "http://search.twitter.com/search.json?q=" + Uri.encode(searchTerm) + "&rpp=10&page=" + page;
    
    	ArrayList<Tweet> tweets = new ArrayList<Tweet>();

		WebRequest w = new WebRequest();
		JSONObject obj = null;
    	  
		try {
			obj = w.GetJSON(searchUrl);
		} catch (Exception e) {
			e.printStackTrace();
		}
     	 
    	JSONArray arr = null;
    	    
    	  try {
    		  Object j = obj.get("results");
    		  arr = (JSONArray)j;
    	  } catch(Exception ex){
    		  Log.v("bctb","Exception: " + ex.toString());
    	  }

    	  if(arr!=null){
    		  for(int x=0; x< arr.length(); x++) {
    			  JSONObject t;
    			  try {
    				  t = (JSONObject)arr.getJSONObject(x);
    				  Tweet tweet = new Tweet(
    						  t.get("from_user").toString(),
				    	      t.get("text").toString(),
				    	      t.get("profile_image_url").toString());
					
    				  tweets.add(tweet);
				
				} catch (JSONException e) {
					e.printStackTrace();
				}
	    	    
	    	}
    	}
    	    
    	  return tweets;
    }
    
     class syncTask extends UserTask<Void, Void, Void>{

 		@Override
 		public Void doInBackground(Void... params) {
 			try{
 				Log.d("bctb","syncing");
 				dbSyncer.syncData();
			}catch (Exception e) {
				e.printStackTrace();

			}
			return null;
 			
 		}
     	
 		public void onPostExecute(Void result) {
 			Log.d("bctb","sync done");
 		}
 		
     };
     
    public static Drawable GetImageFromWeb(Context ctx, String url) {
		try {
			InputStream is = (InputStream) fetch(url);
			Drawable d = Drawable.createFromStream(is, "src");
			return d;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Object fetch(String address) throws MalformedURLException,IOException {
		URL url = new URL(address);
		Object content = url.getContent();
		return content;
	}
    

}
