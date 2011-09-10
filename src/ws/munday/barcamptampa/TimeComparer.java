package ws.munday.barcamptampa;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

public class TimeComparer implements Comparator<ScheduleItem> {

	@Override
	public int compare(ScheduleItem item1, ScheduleItem item2) {
		
		
		SimpleDateFormat f = new SimpleDateFormat("hh:mm a");
		
		Date d1 = new Date();
		Date d2 = new Date();
		try {
			d1 = f.parse(item1.startTime);
			d2 = f.parse(item2.startTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
			
		if(d1.after(d2)){
			return 1;
		}else if(d2.after(d1)){
			return -1;
		}else{
			return 0;
		}
		
	}

}
