package com.leslienetworks.hostbill;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;

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
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class SupportTickets extends Activity {
	public void onCreate(Bundle home){
		super.onCreate(home);
		setContentView(R.layout.supporttickets);
		page = 0;
		clientlist = (ListView) findViewById(R.id.listView1);
		final AsyncTask<String, Integer, String> progress = new progress(SupportTickets.this);
		  progress.execute("");
		  
		  registerForContextMenu(clientlist);
	    	clientlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
  			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
  				String selected = (String) clientlist.getItemAtPosition(arg2);
  				if (selected.equals("Previous Page")){
  					page = page -1;
  					final AsyncTask<String, Integer, String> progress = new progress(SupportTickets.this);
  					progress.execute("");
  				} else if (selected.equals("Next Page")) {
  					page = page +1;
  					final AsyncTask<String, Integer, String> progress = new progress(SupportTickets.this);
  					progress.execute("");
  				} else {
  					String clientid = selected.substring(1);
					clientid = clientid.replace(clientid.substring(clientid.indexOf(" - "), clientid.length()), "");
					Bundle c = new Bundle();
					c.putString("ID", clientid);
					Intent i = new Intent(SupportTickets.this, SupportTicketInfo.class);
					i.putExtras(c);
					startActivityForResult(i,code);
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
	public ListView clientlist;
	public int code;
	
	public EditText deptname;
	public EditText deptemail;
	
	public String populate(int page) {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
	    localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
	    InputStream httpcontent=null;
	    String reply = null;
	    try { 
	    	URL pullurl = new URL(url + "&call=getTickets&page=" + page);
	    	Log.d("HBmobile", pullurl.toString());
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
	    if ( reply.contains("success\":true")){
	    	reply = reply.substring(26);
	    	StringBuilder complete = new StringBuilder();
	    	String working = null;
	    	reply = reply.replace("}", "~~~");
	    	for ( String info : reply.split("~~~") ){
	    		if(info.contains("id")) {
	    			working = info.substring(info.indexOf("\"tsubject\":\"")+12, info.indexOf("\",\"deptname\""));
	    			complete.append("" + working + "\n");
	    			working = info.substring(info.indexOf("\"deptname\":\"")+12, info.indexOf("\",\"priority\""));
	    			complete.append("(" + working + ")");
	    			working = info.substring(info.indexOf("\"status\":\"")+10, info.indexOf("\",\"ticket_number\""));
	    			complete.append(" " + working + "\n");
	    			working = info.substring(info.indexOf("\"id\":\"")+6, info.indexOf("\",\"type\""));
	    			complete.append("#" + working);
	    			working = info.substring(info.indexOf("\"firstname\":\"")+13, info.indexOf("\",\"lastname\""));
	    			complete.append(" - " + working);
	    			working = info.substring(info.indexOf("\"lastname\":\"")+12, info.indexOf("\",\"date\""));
	    			complete.append(" " + working);
	    			complete.append("---");
	    		}
	    	}
	    	
	    	reply = null;
		    try { 
		    	page = page-1;
		    	URL pullurl = new URL(url + "&call=getTickets&page=" + page);
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
		    	URL pullurl = new URL(url + "&call=getTickets&page=" + page);
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
        	clientlist.setAdapter(new ArrayAdapter<String>(clientlist.getContext(),android.R.layout.simple_list_item_1, clients2));
        	progressDialog.hide();
        }

		@Override
		protected String doInBackground(String... params) {
			String populate = populate(page);
			return populate;
		}
    }
	
	private class deptadd extends AsyncTask<String, Integer, String> {
        private ProgressDialog progressDialog;
        private Activity m_activity;

        protected deptadd(Activity activity) {
          setActivity(activity);
        }

        public void setActivity(Activity activity) {
          m_activity = activity;

          progressDialog = new ProgressDialog(m_activity);
          progressDialog.setMessage("Creating Department...");
          progressDialog.setCancelable(false);
          progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

          progressDialog.show();
          
          
        }

        protected void onProgressUpdate(Integer... values) {
        	
        };

        @Override
        protected void onPostExecute(String reply) {
        	progressDialog.hide();
        	if ( reply.contains("\"success\":true")) {
	        	AlertDialog alertDialog = new AlertDialog.Builder(SupportTickets.this).create();
				alertDialog.setTitle("Success");
				alertDialog.setMessage("The support department has been created successfully.");
				alertDialog.setButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
					}
				});
				alertDialog.show();
        	} else {
	        	AlertDialog alertDialog = new AlertDialog.Builder(SupportTickets.this).create();
				alertDialog.setTitle("Error");
				alertDialog.setMessage(reply);
				alertDialog.setButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
					}
				});
				alertDialog.show();
        	}
        }

		@Override
		protected String doInBackground(String... params) {
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpContext localContext = new BasicHttpContext();
		    localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
		    InputStream httpcontent=null;
		    String reply = null;
		    try { 
		    	page = page+2;
		    	String deptdemail = URLEncoder.encode(deptemail.getText().toString());
		    	String deptdname = URLEncoder.encode(deptname.getText().toString());
		    	URL pullurl = new URL(url + "&call=addTicketDept&email=" + deptdemail + "&name=" + deptdname);
		    	Log.d("HBMobile", pullurl.toString());
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
		    
		    return reply;
		}
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if ( resultCode == 2) {
    		finish();
    	} else if ( resultCode == 1 ) {
    		final AsyncTask<String, Integer, String> progress = new progress(SupportTickets.this);
			progress.execute("");
    	}
    	
    }
    
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.supporttickets, menu);
    	return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	case R.id.AddDept:
    		final Dialog dialog = new Dialog(SupportTickets.this);
			dialog.setContentView(R.layout.supportticketdept);
			dialog.setTitle("Create a Department");
			dialog.setCancelable(true);
			deptname = (EditText) dialog.findViewById(R.id.editText1);
			deptemail = (EditText) dialog.findViewById(R.id.editText2);
			
			Button create = (Button) dialog.findViewById(R.id.button2);
			create.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					
					new deptadd(SupportTickets.this).execute("");
					dialog.dismiss();
				}
			});
			Button cancel = (Button) dialog.findViewById(R.id.button1);
			cancel.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			dialog.show();
    		return true;
    	default:
    		return false;
    	}
    }
    
}
