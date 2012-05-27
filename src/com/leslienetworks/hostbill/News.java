package com.leslienetworks.hostbill;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class News extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);
        page = 0;
        newslist = (ListView) findViewById(R.id.listView1);
        final AsyncTask<String, Integer, String> progress = new progress(News.this);
		progress.execute("");
		
		newslist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
  			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
  				String selected = (String) newslist.getItemAtPosition(arg2);
  				if (selected.equals("Previous Page")){
  					page = page -1;
  					final AsyncTask<String, Integer, String> progress = new progress(News.this);
  					progress.execute("");
  				} else if (selected.equals("Next Page")) {
  					page = page +1;
  					final AsyncTask<String, Integer, String> progress = new progress(News.this);
  					progress.execute("");
  				} else {
  					String clientid = selected.substring(1);
					clientid = clientid.replace(clientid.substring(clientid.indexOf(" - "), clientid.length()), "");
					Bundle c = new Bundle();
					c.putString("ID", clientid);
					Intent i = new Intent(News.this, NewsInfo.class);
					i.putExtras(c);
					startActivity(i);
  				}
  			}
  		});
    }
    
	public boolean htaccess = main.htaccess;
	public String htuser = main.htuser;
	public String htpass = main.htpass;
	public String url = main.url;
	CookieStore cookieStore = main.cookieStore;
	public int page;
	
	public ListView newslist;
	
	public String populate(int page) {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
	    localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
	    InputStream httpcontent=null;
	    String reply = null;
	    try { 
	    	URL pullurl = new URL(url + "&call=getNews&page=" + page);
	    	HttpGet httpGet = new HttpGet(pullurl.toURI());
	    	if ( htaccess == true) {
	    		Credentials creds = new UsernamePasswordCredentials(htuser, htpass);
				httpclient.getCredentialsProvider().setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT), creds);
	    	}
			HttpResponse response = httpclient.execute(httpGet, localContext);
			httpcontent = response.getEntity().getContent();
			BufferedReader rd = new BufferedReader(new InputStreamReader(httpcontent), 4096);
			String line;
			StringBuilder sb =  new StringBuilder();
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}
			rd.close();
			reply = sb.toString();
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	return e.getMessage();
	    }
	    Log.d("HBmobile", reply);
	    if ( reply.contains("success\":true")){
	    	reply = reply.substring(25);
	    	Log.d("HBmobile", reply);
	    	StringBuilder complete = new StringBuilder();
	    	String working = null;
	    	reply = reply.replace("},", "~~~");
	    	for ( String info : reply.split("~~~") ){
	    		complete.append("#");
	    		Log.d("HBmobile", info);
	    		working = info.substring(info.indexOf("\"id\"")+6, info.indexOf("\",\"title"));
	    		complete.append(working);
	    		working = info.substring(info.indexOf("\"title\"")+9, info.indexOf("\",\"content\""));
	    		complete.append(" - " + working);
	    		complete.append("---");
	    	}
	    	
	    	reply = null;
		    try { 
		    	page = page-1;
		    	URL pullurl = new URL(url + "&call=getNews&page=" + page);
		    	HttpGet httpGet = new HttpGet(pullurl.toURI());
		    	if ( htaccess == true) {
		    		Credentials creds = new UsernamePasswordCredentials(htuser, htpass);
					httpclient.getCredentialsProvider().setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT), creds);
		    	}
				HttpResponse response = httpclient.execute(httpGet, localContext);
				httpcontent = response.getEntity().getContent();
				BufferedReader rd = new BufferedReader(new InputStreamReader(httpcontent), 4096);
				String line;
				StringBuilder sb =  new StringBuilder();
				while ((line = rd.readLine()) != null) {
					sb.append(line);
				}
				rd.close();
				reply = sb.toString();
		    } catch (Exception e) {
		    	e.printStackTrace();
		    	return e.getMessage();
		    }
		    
		    if ( reply.contains("\"success\":true")){
		    	complete.insert(0, "Previous Page---");
		    }
		    
		    reply = null;
		    try { 
		    	page = page+2;
		    	Log.d("HBMobile", String.valueOf(page));
		    	URL pullurl = new URL(url + "&call=getNews&page=" + page);
		    	HttpGet httpGet = new HttpGet(pullurl.toURI());
		    	if ( htaccess == true) {
		    		Credentials creds = new UsernamePasswordCredentials(htuser, htpass);
					httpclient.getCredentialsProvider().setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT), creds);
		    	}
				HttpResponse response = httpclient.execute(httpGet, localContext);
				httpcontent = response.getEntity().getContent();
				BufferedReader rd = new BufferedReader(new InputStreamReader(httpcontent), 4096);
				String line;
				StringBuilder sb =  new StringBuilder();
				while ((line = rd.readLine()) != null) {
					sb.append(line);
				}
				rd.close();
				reply = sb.toString();
		    } catch (Exception e) {
		    	e.printStackTrace();
		    	return e.getMessage();
		    }
		    Log.d("HBMobile", reply);
		    if ( reply.contains("\"success\":true")){
		    	complete.append("Next Page");
		    }
	    	
	    	return complete.toString();
	    } else {
	    	return reply;
	    }
	}
	
    private class progress extends AsyncTask<String, Integer, String> {
        private ProgressDialog progressDialog;
        private Activity m_activity;

        protected progress(Activity activity) {
          setActivity(activity);
        }

        public void setActivity(Activity activity) {
          m_activity = activity;

          progressDialog = new ProgressDialog(m_activity);
          progressDialog.setMessage("Loading Information...");
          progressDialog.setCancelable(false);
          progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

          progressDialog.show();
          
          
        }

        protected void onProgressUpdate(Integer... values) {
        	
        };

        @Override
        protected void onPostExecute(String clients) {
        	String clients2[] = clients.split("---");
        	newslist.setAdapter(new ArrayAdapter<String>(newslist.getContext(),android.R.layout.simple_list_item_1, clients2));
        	progressDialog.hide();
        }

		@Override
		protected String doInBackground(String... params) {
			String populate = populate(page);
			return populate;
		}
      }	
}
