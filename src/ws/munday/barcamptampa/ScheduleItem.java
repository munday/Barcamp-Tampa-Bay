package ws.munday.barcamptampa;

import java.util.ArrayList;

public class ScheduleItem {

	public long id;
	public String sheetId;
	public String startTime;
	public String endTime;
	public String title;
	public String description;
	public String speaker;
	public String speakerTwitter;
	public String speakerWebsite;
	public String slidesUrl;
	public String roomName;
	public boolean isStarred;
	public ArrayList<ScheduleItem> conflictingItems = new ArrayList<ScheduleItem>();

	public String toString(){
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("sheet-id: " + sheetId);
		sb.append("\n");
		
		if(startTime!=null){
			sb.append("start-time: " + startTime.toString());
			sb.append("\n");
		}
		
		if(endTime!=null){
			sb.append("end-time: " + endTime.toString());
			sb.append("\n");
		}
		
		sb.append("room: " + roomName.toString());
		sb.append("\n");
		sb.append("title: " + title.toString());
		sb.append("\n");
		sb.append("description: " + description.toString());
		sb.append("\n");
		sb.append("speaker: " + speaker.toString());
		sb.append("\n");
		sb.append("speaker-twitter: " + speakerTwitter.toString());
		sb.append("\n");
		sb.append("speaker-website: " + speakerWebsite.toString());
		sb.append("\n");
		sb.append("slides-url: " + slidesUrl.toString());
		
		return sb.toString();
		
	}
	
}
