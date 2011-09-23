package ws.munday.barcamptampa;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.client.ClientProtocolException;

import android.util.Log;


public class CSVReader {

	public static final String SPREADSHEET_URL="https://docs.google.com/spreadsheet/pub?hl=en_US&hl=en_US&key=0AjPtlyH0E2lEdDNUdUtJbjlhcGgtQmpsSW96VW1qOFE&single=true&gid=0&output=txt";
	public static final String SPONSORS_SPREADSHEET_URL="https://docs.google.com/spreadsheet/pub?hl=en_US&hl=en_US&key=0AjPtlyH0E2lEdEFDMjF1c25ER0llQWdDTjk0MW1lUXc&output=txt";
			
	public static ArrayList<ScheduleItem> getSchedule() throws ClientProtocolException, IOException{
		ArrayList<ScheduleItem> items = new ArrayList<ScheduleItem>();
		
		WebRequest req = new WebRequest();
		String csv = req.Get(SPREADSHEET_URL);
		String[] lines = csv.split("\n");
		for(int x=1; x<lines.length;x++){
			String[] data = lines[x].split("\\t");
			
			ScheduleItem i = new ScheduleItem();
			i.sheetId = getData(data,0);
			i.startTime = new Date(DatabaseSyncer.CONFERENCE_DATE_WITHOUT_TIME + getData(data,2));
			Log.d("bctb", i.startTime.toString());
			i.endTime = new Date(DatabaseSyncer.CONFERENCE_DATE_WITHOUT_TIME + getData(data,3));
			i.roomName = getData(data,4);
			i.title = getData(data,5);
			i.description = getData(data,6);
			i.speaker = getData(data,7);
			i.speakerTwitter = getData(data,8);
			i.speakerWebsite = getData(data,9);
			i.slidesUrl = getData(data,10);
			
			items.add(i);
			
		}
		
		return items;
	}
	
	public static String getData(String[] data, int idx){
		if(data.length-1<idx)
			return "";
		else
			return data[idx];
	}
	
	
	
	public static ArrayList<Sponsor> getSponsors() throws ClientProtocolException, IOException{
		ArrayList<Sponsor> items = new ArrayList<Sponsor>();
		
		WebRequest req = new WebRequest();
		String csv = req.Get(SPONSORS_SPREADSHEET_URL);
		String[] lines = csv.split("\n");
		for(int x=1; x<lines.length;x++){
			String[] data = lines[x].split("\\t");
			Sponsor i = new Sponsor();
			i.name = data[0];
			i.img = data[1];
			i.desc = data[2];
			i.url = data[3];
			i.order = Integer.parseInt(data[4]);
			items.add(i);
			
		}
		
		return items;
	}
	
}
