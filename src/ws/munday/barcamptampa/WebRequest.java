/*
 * Copyright (C) 2009 Matt Munday
 *
 * This program is free software; you can redistribute it and/or modify it under the terms 
 * of the GNU General Public License as published by the Free Software Foundation; either 
 * version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program; 
 * if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/
package ws.munday.barcamptampa;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class WebRequest {
    
	DefaultHttpClient _client;
	HttpGet _get;
	HttpPost _post;
	HttpResponse _response;
	
	public DefaultHttpClient getClient(){
		return _client;
	}
	
	public WebRequest() {
		_client = new DefaultHttpClient();
		HttpParams p= _client.getParams();
		HttpConnectionParams.setConnectionTimeout(p, 30000);
	}
	
	public WebRequest(int timeout) {
		int msTimeout = timeout*1000;
		_client = new DefaultHttpClient();
		HttpParams p= _client.getParams();
		HttpConnectionParams.setConnectionTimeout(p, msTimeout);
	}
	
	public HttpResponse Post(String uri, HttpEntity postData ) throws ClientProtocolException, IOException{
		_post = new HttpPost(uri);
		_post.setEntity(postData);
		_response = _client.execute(_post);
		return _response;
	}
	
	public String Get(String uri) throws ClientProtocolException, IOException{
		_get = new HttpGet(uri);
		_response = _client.execute(_get);
		return GetResponseText(_response.getEntity().getContent());
	}

	public String Get(String uri, Header[] headers) throws ClientProtocolException, IOException{
		_get = new HttpGet(uri);
		for(int x=0;x<headers.length;x++){
			_get.addHeader(headers[x]);
		}
		_response = _client.execute(_get);
		return GetResponseText(_response.getEntity().getContent());
	}
	
	public String Get(String uri, List<Cookie> cookies) throws ClientProtocolException, IOException{
		_get = new HttpGet(uri);
		
		for(Cookie c:cookies){
				
			Log.d("torrent-fu", "Cookie: " + c.getName() + " : " + c.getValue());
			_client.getCookieStore().addCookie(c);
			
		}
		
		_response = _client.execute(_get);
		return GetResponseText(_response.getEntity().getContent());
	}

	
	public String GetWithBasicAuthorization(String uri, String Username, String Password) throws ClientProtocolException, IOException{
		_get = new HttpGet(uri);
		_get.addHeader("Authorization", "Basic " + Base64.encodeString(Username + ":" + Password)  );
		_response = _client.execute(_get);
		return GetResponseText(_response.getEntity().getContent());
	}
	
	public String GetWithBasicAuthorization(String uri, String Username, String Password, List<Cookie> cookies) throws ClientProtocolException, IOException{
		_get = new HttpGet(uri);
		_get.addHeader("Authorization", "Basic " + Base64.encodeString(Username + ":" + Password)  );
		for(Cookie c:cookies){
			
			Log.d("torrent-fu", "Cookie: " + c.getName() + " : " + c.getValue());
			_client.getCookieStore().addCookie(c);
			
		}
		_response = _client.execute(_get);
		return GetResponseText(_response.getEntity().getContent());
	}
	
	public String PostWithBasicAuthorization(String uri, String Username, String Password, HttpEntity postData, String[][] headers) throws ClientProtocolException, IOException{
		_post = new HttpPost(uri);
		for(int x=0;x<headers.length;x++){
			_post.addHeader(headers[x][0], headers[x][1]);
		}
		_post.addHeader("Authorization", "Basic " + Base64.encodeString(Username + ":" + Password)  );
		_post.setEntity(postData);
		_response = _client.execute(_post);
		return GetResponseText(_response.getEntity().getContent());
	}
	
	public HashMap<String, String> PostWithBasicAuthorizationAndReturnHeaders(String uri, String Username, String Password,  HttpEntity postData, String[][] headers) throws ClientProtocolException, IOException{
		_post = new HttpPost(uri);
		for(int x=0;x<headers.length;x++){
			_post.addHeader(headers[x][0], headers[x][1]);
		}
		_post.addHeader("Authorization", "Basic " + Base64.encodeString(Username + ":" + Password)  );
		_post.setEntity(postData);
		_response = _client.execute(_post);
		Header[] h = _response.getAllHeaders();
		
		HashMap<String, String> ret = new HashMap<String, String>();
		
		for(int y=0; y<h.length;y++){
			ret.put(h[y].getName(), h[y].getValue());
		}
		
		ret.put("responsetext", GetResponseText(_response.getEntity().getContent()));
		return ret;
	}
	
	public static String GetResponseText(InputStream in) throws IOException{
		String out = "";
		BufferedReader r = new BufferedReader(new InputStreamReader(in));
		StringBuilder sb = new StringBuilder();
		String line = null;

		while((line = r.readLine()) != null){
			sb.append(line + "\n");
		}
		
		out = sb.toString();
		
		in.close();
		r.close();

		return out;
		
	}
	
	public JSONObject GetJSON(String uri) throws ClientProtocolException, IOException, JSONException{
		String ret = Get(uri);
		JSONObject o = new JSONObject(ret);
		return o;
	}
	
}