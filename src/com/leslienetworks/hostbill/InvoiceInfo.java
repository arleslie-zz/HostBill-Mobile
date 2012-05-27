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
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class InvoiceInfo extends Activity {
	@Override
	public void onCreate(Bundle home) {
		super.onCreate(home);
		setContentView(R.layout.invoiceinfo);
		Bundle b = getIntent().getExtras();
		
		id = b.getString("ID");
		firstname = (EditText) findViewById(R.id.editText6);
		lastname = (EditText) findViewById(R.id.editText2);
		invoicestatus = (EditText) findViewById(R.id.editText3);
		total = (EditText) findViewById(R.id.editText4);
		invoiceid = (EditText) findViewById(R.id.editText9);
		duedate = (EditText) findViewById(R.id.editText5);
		items = (ListView) findViewById(R.id.listView1);
		
		final AsyncTask<String, Integer, String> progress = new progress(InvoiceInfo.this);
		progress.execute("");
		
	}
	
	public String id;
	public EditText firstname;
	public EditText lastname;
	public EditText invoicestatus;
	public EditText total;
	public EditText invoiceid;
	public EditText duedate;
	public ListView items;
	
	public boolean htaccess = main.htaccess;
	public String htuser = main.htuser;
	public String htpass = main.htpass;
	public String url = main.url;
	CookieStore cookieStore = main.cookieStore;
	
	public String populate() {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
	    localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
	    InputStream httpcontent=null;
	    String reply = null;
	    try { 
	    	URL pullurl = new URL(url + "&call=getInvoiceDetails&id=" + id);
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
        protected void onPostExecute(String reply) {
        	String working = null;
        	StringBuilder complete = new StringBuilder();
    	    if ( reply.contains("success\":true")){
    	    	working = reply.substring(reply.indexOf("\"id\"")+6, reply.indexOf("\",\"paid_id\""));
    	    	invoiceid.setText(working);
    	    	working = reply.substring(reply.indexOf("\"firstname\"")+13, reply.indexOf("\",\"lastname\""));
    	    	firstname.setText(working);
    	    	working = reply.substring(reply.indexOf("\"lastname\"")+12, reply.indexOf("\",\"companyname\""));
    	    	lastname.setText(working);
    	    	working = reply.substring(reply.indexOf("\"status\"")+10, reply.indexOf("\",\"client_id"));
    	    	invoicestatus.setText(working);
    	    	working = reply.substring(reply.indexOf("\"total\"")+9, reply.indexOf("\",\"payment_module\""));
    	    	total.setText("$"+working);
    	    	working = reply.substring(reply.indexOf("\"duedate\"")+11, reply.indexOf("\",\"datepaid\""));
    	    	String item = reply.substring(reply.indexOf("\"items\"")+9, reply.indexOf(",\"client\""));
    	    	item = item.replace("{", "~~");
    	    	item = item.replace("\\/", "/");
    	    	String items[] = item.split("~~");
    	    	for(String info : items) {
    	    		if( info.contains("id")) {
	    	    		complete.append("#");
	    	    		Log.d("HBmobile", info);
	    	    		working = info.substring(info.indexOf("\"id\"")+6, info.indexOf("\",\"invoice_id\""));
	    	    		complete.append(working + " - ");
	    	    		working = info.substring(info.indexOf("\"type\"")+8, info.indexOf("\",\"item_id\""));
	    	    		complete.append(working + "\n");
	    	    		working = info.substring(info.indexOf("\"description\"")+15, info.indexOf("\",\"amount\""));
	    	    		complete.append(working + "\n$");
	    	    		working = info.substring(info.indexOf("\"amount\"")+10, info.indexOf("\",\"taxed\""));
	    	    		complete.append(working + "(");
	    	    		working = info.substring(info.indexOf("\"qty\"")+7, info.indexOf("\",\"line"));
	    	    		complete.append(working + ") - ");
	    	    		working = info.substring(info.indexOf("\"linetotal\"")+12, info.length()-2);
	    	    		complete.append(working);
	    	    		complete.append("---");
    	    		}
    	    	}
    	    }
        	String items2[] = complete.toString().split("---");
        	items.setAdapter(new ArrayAdapter<String>(items.getContext(),android.R.layout.simple_list_item_1, items2));
        	progressDialog.hide();
        }

		@Override
		protected String doInBackground(String... params) {
			String populate = populate();
			return populate;
		}
      }
}
