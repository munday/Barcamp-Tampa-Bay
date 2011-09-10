package ws.munday.barcamptampa;

import java.util.Comparator;

public class TimeComparer implements Comparator<ScheduleItem> {

	@Override
	public int compare(ScheduleItem item1, ScheduleItem item2) {
		
		
			
		if(item1.startTime.after(item2.startTime)){
			return 1;
		}else if(item2.startTime.after(item1.startTime)){
			return -1;
		}else{
			return 0;
		}
		
	}

}
