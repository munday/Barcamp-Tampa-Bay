package ws.munday.barcamptampa;

import java.io.IOException;
import java.util.ArrayList;
import org.apache.http.client.ClientProtocolException;


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
			i.sheetId = data[0];
			i.startTime = data[1];
			i.endTime = data[2];
			i.roomName = data[3];
			i.title = data[4];
			i.description = data[5];
			i.speaker = data[6];
			i.speakerTwitter = data[7];
			i.speakerWebsite = data[8];
			i.slidesUrl = data[9];
			
			items.add(i);
			
		}
		
		return items;
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
