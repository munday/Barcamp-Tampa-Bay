package ws.munday.barcamptampa;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SponsorAdapter extends BaseAdapter{

	private ArrayList<Sponsor> items;
	private final LayoutInflater inflater;
	private final Context context;
	
	public SponsorAdapter(ArrayList<Sponsor> items, Context c) {
		this.context = c;
		this.inflater = LayoutInflater.from(this.context);
		this.items = items;
	}

	public ArrayList<Sponsor> getItems() {
		if(items!=null)
				return items;
		else
			return null;
	}

	public void setItems(ArrayList<Sponsor> v) {
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
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		final Sponsor s;
		if(items.size()>position)
			s = items.get(position);
		else
			return null;
		// When convertView is not null, we can reuse it directly, there is no
		// need
		// to reinflate it. We only inflate a new View when the convertView
		// supplied
		// by ListView is null.
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.sponsor_item, null);

			// Creates a ViewHolder and store references to the two children
			// views
			// we want to bind data to.
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.sponsor_name);
			holder.img = (ImageView) convertView.findViewById(R.id.sponsor_image);
			holder.desc = (TextView) convertView.findViewById(R.id.sponsor_desc);
			holder.url = (TextView) convertView.findViewById(R.id.sponsor_url);
			
			convertView.setTag(holder);
			
		} else {
			// Get the ViewHolder back to get fast access to the TextView
			// and the ImageView.
			holder = (ViewHolder) convertView.getTag();
		}

		// Bind the data efficiently with the holder.
		
		holder.name.setText(s.name);
		holder.img.setImageDrawable(getWebImageDrawable(s.img));
		holder.desc.setText(s.desc);
		holder.url.setText(s.url);

		return convertView;
		
	}

	final class ViewHolder {
		ImageView img;
		TextView name;
		TextView desc;
		TextView url;
	}

	private Drawable getWebImageDrawable(String url) {
		try {
			InputStream is = (InputStream) this.getFromWeb(url);
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

	public Object getFromWeb(String address) throws MalformedURLException,IOException {
		URL url = new URL(address);
		Object content = url.getContent();
		return content;
	}
	
	@Override
	public long getItemId(int position) {
		return 0;
	}
}
