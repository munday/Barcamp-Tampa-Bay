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
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageView;
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
       
        buildHomeIcons();
        
        createButtonGrid(R.id.main_menu, homeIcons);
        
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
    	
    	buildHomeIcons();
    	createButtonGrid(R.id.main_menu, homeIcons);
      
    	
    	super.onConfigurationChanged(newConfig);
    }

    public void buildHomeIcons(){
    	
    	homeIcons = new ArrayList<View>();
    	
    	LinearLayout l = new LinearLayout(this);
        l.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        l.setGravity(Gravity.CENTER);
        l.setPadding(10, 10, 10, 10);
        ImageView img = new ImageView(this);
        img.setBackgroundDrawable(getResources().getDrawable(R.drawable.schedule));
        img.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent();
				i.setClass(getApplicationContext(), ScheduleActivity.class);
				startActivity(i);
			}
		});
        
        l.addView(img);
        
        homeIcons.add(l);
        
        LinearLayout l2 = new LinearLayout(this);
        l2.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        l2.setGravity(Gravity.CENTER);
        l2.setPadding(10, 10, 10, 10);
        ImageView img2 = new ImageView(this);
        img2.setBackgroundDrawable(getResources().getDrawable(R.drawable.starred));
        
        img2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent();
				i.setClass(getApplicationContext(), UpcomingScheduleActivity.class);
				startActivity(i);
			}
		});
        
        l2.addView(img2);
        
        homeIcons.add(l2);
        
        LinearLayout l3 = new LinearLayout(this);
        l3.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        l3.setGravity(Gravity.CENTER);
        l3.setPadding(10, 10, 10, 10);
        ImageView img3 = new ImageView(this);
        img3.setBackgroundDrawable(getResources().getDrawable(R.drawable.sponsors));
        
        img3.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent();
				i.setClass(getApplicationContext(), SponsorsActivity.class);
				startActivity(i);
			}
		});
        
        l3.addView(img3);
        
        homeIcons.add(l3);
        
        LinearLayout l4 = new LinearLayout(this);
        l4.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        l4.setGravity(Gravity.CENTER);
        l4.setPadding(10, 10, 10, 10);
        ImageView img4 = new ImageView(this);
        img4.setBackgroundDrawable(getResources().getDrawable(R.drawable.location));
        img4.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String url = "http://maps.google.com/maps?daddr=1001+East+Palm+Ave+Tampa+FL+33605+(Kforce+Professional+Staffing)";
				Intent intent = new Intent(android.content.Intent.ACTION_VIEW,  Uri.parse(url));
				startActivity(intent);
			}
		});
        l4.addView(img4);
        
        homeIcons.add(l4);
        /*
        
        LinearLayout l6 = new LinearLayout(this);
        l6.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        l6.setGravity(Gravity.CENTER);
        l6.setPadding(10, 10, 10, 10);
        ImageView img6 = new ImageView(this);
        img6.setBackgroundDrawable(getResources().getDrawable(R.drawable.tweets));
        l6.addView(img6);
        
        homeIcons.add(l6);
    	*/
    }
    
    private void createButtonGrid(int tableId, ArrayList<View> items ) {
    	
    	int viewWidth = this.getResources().getDisplayMetrics().widthPixels;
    	
    	int buttonWidth = getResources().getDrawable(R.drawable.icon).getIntrinsicWidth() + 40;
    	        
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
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						Toast.makeText(getApplicationContext(),"Unable to sync data, check your internet connection.", Toast.LENGTH_LONG).show();			
					}
				});
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
