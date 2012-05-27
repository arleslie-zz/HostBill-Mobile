package com.leslienetworks.hostbill;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
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
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

public class SupportTicketInfo extends Activity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.supportticketsinfo);
		final Bundle b = this.getIntent().getExtras();
		id = b.getString("ID");
		final AsyncTask<String, Integer, String> progress = new progress(SupportTicketInfo.this);
		progress.execute("");
		title = (EditText) findViewById(R.id.editText1);
		status = (Spinner) findViewById(R.id.spinner1);
		categ = (Spinner) findViewById(R.id.spinner2);
		messa = (ListView) findViewById(R.id.listView1);
		reply2 = (EditText) findViewById(R.id.editText2);
		send = (Button) findViewById(R.id.button1);
		
		statusadapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
		categadapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
		
		statusadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		categadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		status.setAdapter(statusadapter);
		categ.setAdapter(categadapter);
		
		send.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		  final AsyncTask<String, Integer, String> replyprogress = new replyprogress(SupportTicketInfo.this);
        		  replyprogress.execute("");
        	}
        });
	}
	
	public boolean htaccess = main.htaccess;
	public String htuser = main.htuser;
	public String htpass = main.htpass;
	public String url = main.url;
	public String id;
	
	public EditText title;
	public Spinner status;
	public Spinner categ;
	public ListView messa;
	public EditText reply2;
	public Button send;
	
	public ArrayAdapter<String> statusadapter;
	public ArrayAdapter<String> categadapter;
	
	
	CookieStore cookieStore = main.cookieStore;
	
	public String info() {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
	    localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
	    InputStream httpcontent=null;
	    String reply = null;
	    try { 
	    	URL pullurl = new URL(url + "&call=getTicketDetails&id=" + id);
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
	    return reply;
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
        protected void onPostExecute(String info) {
        	Log.d("HBmobile", info);
        	if ( info.contains("\"success\":false")){
        		if(info.contains("API ID")) {
        			progressDialog.hide();
                	AlertDialog alertDialog = new AlertDialog.Builder(SupportTicketInfo.this).create();
        			alertDialog.setTitle("Error");
        			alertDialog.setMessage("The API ID and/or API Key provided is no longer valid.");
        			alertDialog.setButton("Ok", new DialogInterface.OnClickListener() {
        				public void onClick(DialogInterface arg0, int arg1) {
        					setResult(2);
        					finish();
        				}
        			});
        		}
        	} else if ( info.contains("error")) {
        		progressDialog.hide();
            	AlertDialog alertDialog = new AlertDialog.Builder(SupportTicketInfo.this).create();
    			alertDialog.setTitle("Error");
    			alertDialog.setMessage(info);
    			alertDialog.setButton("Ok", new DialogInterface.OnClickListener() {
    				public void onClick(DialogInterface arg0, int arg1) {
    					setResult(2);
    					finish();
    				}
    			});
        	} else {
        		
        		String working = info.substring(info.indexOf("\"subject\":")+11, info.indexOf("\",\"body"));
        		title.setText(working);
        		working = info.substring(info.indexOf("\"status\":")+10, info.indexOf("\",\"priority\""));
        		statusadapter.add(working);
        		working = info.substring(info.indexOf("\"deptname\":")+12, info.indexOf("\",\"viewtime\""));
        		categadapter.add(working);
        		
        		String replies = "";
        		working = info.substring(info.indexOf("\"name\":")+8, info.indexOf("\",\"email"));
        		replies = working;
        		working = info.substring(info.indexOf("\"type\":")+8, info.indexOf("\",\"senderip"));
        		replies = replies + " [" + working + "]:\n";
        		working = info.substring(info.indexOf("\"body\":")+8, info.indexOf("\",\"status\""));
        		replies = replies + working + "~~~~";
        		
        		working = info.substring(info.indexOf("\"replies\":")+12, info.indexOf(",\"attachments\":")-1);
        		String work;
        		working = working.replace("},", "}----");
        		if ( working.contains("id")) {
	        		for ( String reply : working.split("----") ) {
	        			Log.d("HBMobile", reply);
	        			if ( reply.contains("id")) {
		        			work = reply.substring(reply.indexOf("\"status\":")+10, reply.indexOf("\",\"type\":"));
		        			Log.d("HBMobile", work);
		        			if ( work.equals("Sent")) {
			        			work = reply.substring(reply.indexOf("\"name\":")+8, reply.indexOf("\",\"email\""));
			        			Log.d("HBMobile", work);
			        			replies = replies + work;
			        			work = reply.substring(reply.indexOf("\"type\":")+8, reply.length()-2);
			        			Log.d("HBMobile", work);
			        			replies = replies + " [" + work + "]:\n";
			        			work = reply.substring(reply.indexOf("\"body\":")+8, reply.indexOf("\",\"status\":"));
			        			Log.d("HBMobile", work);
			        			replies = replies + work + "~~~~";
		        			}
	        			}
	        		}
        		}
        		Log.d("HBMobile", replies);
        		replies = replies.replace("\\r\\n", "\n");
        		String replies2[] = replies.split("~~~~");
            	messa.setAdapter(new ArrayAdapter<String>(messa.getContext(),android.R.layout.simple_list_item_1, replies2));
        		
            	
        		progressDialog.hide();
        	}
        	
        }

		@Override
		protected String doInBackground(String... params) {
			String populate = info();
			return populate;
		}
    }
    
    public String reply() {
    	DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
	    localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
	    InputStream httpcontent=null;
	    String reply = null;
	    try { 
	    	URL pullurl = new URL(url + "&call=addTicketReply&id=" + id + "&body=" + reply2.getText().toString());
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
	    return reply;
    }
    
    private class replyprogress extends AsyncTask<String, Integer, String> {
        private ProgressDialog progressDialog;
        private Activity m_activity;

        protected replyprogress(Activity activity) {
          setActivity(activity);
        }

        public void setActivity(Activity activity) {
          m_activity = activity;

          progressDialog = new ProgressDialog(m_activity);
          progressDialog.setMessage("Adding Reply...");
          progressDialog.setCancelable(false);
          progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

          progressDialog.show();
          
          
        }

        protected void onProgressUpdate(Integer... values) {
        	
        };

        @Override
        protected void onPostExecute(String info) {          	
        	progressDialog.hide();
        	Log.d("HBmobile", info);
        	if ( info.contains("\"success\":true")) {
        		reply2.setText("");
            	AlertDialog alertDialog = new AlertDialog.Builder(SupportTicketInfo.this).create();
    			alertDialog.setTitle("Success");
    			alertDialog.setMessage("Your reply has been added.");
    			alertDialog.setButton("Ok", new DialogInterface.OnClickListener() {
    				public void onClick(DialogInterface arg0, int arg1) {
    				}
    			});
    			alertDialog.show();
    			final AsyncTask<String, Integer, String> progress = new progress(SupportTicketInfo.this);
    			progress.execute("");
        	} else {
        		AlertDialog alertDialog = new AlertDialog.Builder(SupportTicketInfo.this).create();
    			alertDialog.setTitle("Error");
    			alertDialog.setMessage(info);
    			alertDialog.setButton("Ok", new DialogInterface.OnClickListener() {
    				public void onClick(DialogInterface arg0, int arg1) {
    					setResult(2);
    					finish();
    				}
    			});
    			alertDialog.show();
        	}
        }

		@Override
		protected String doInBackground(String... params) {
			String populate = reply();
			return populate;
		}
		
    }

}
