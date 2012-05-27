package com.leslienetworks.hostbill;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class OrderList extends Activity {
	CookieStore cookieStore = main.cookieStore;
	@Override
	public void onCreate(Bundle home) {
		super.onCreate(home);
		setContentView(R.layout.list);
		Bundle b = getIntent().getExtras();
		String clientid = b.getString("ID");
		String url2 = main.url;
		Boolean htaccess = main.htaccess;
        final String htuser = main.htuser;
        final String htpass = main.htpass;
		

		URL url = null;

		EditText id = (EditText) findViewById(R.id.editText9);
		EditText firstname = (EditText) findViewById(R.id.editText6);
		EditText lastname = (EditText) findViewById(R.id.editText2);
		EditText ordersta = (EditText) findViewById(R.id.editText3);
		EditText invoicesta = (EditText) findViewById(R.id.editText4);
		EditText date = (EditText) findViewById(R.id.editText5);
		ListView other = (ListView) findViewById(R.id.listView1);

		id.setText(clientid);

		String reply = null;

		try {
			url = new URL(url2 + "&call=getOrderDetails&id=" + clientid);
			Log.e("reply", url.toString());
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		InputStream httpcontent=null;

		try{
			HttpGet httpGet = new HttpGet(url.toURI());
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpContext localContext = new BasicHttpContext();
		    localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
			if ( htaccess == true ) {
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
			reply = new String(sb.toString());
		} catch (Exception e) {
			Toast.makeText(getBaseContext(), "Invalid URL", Toast.LENGTH_SHORT);
		}
		if(reply.contains("success")){
			Log.d("reply", reply);
			reply = reply.substring(reply.indexOf("date_created\""));
			reply = reply.substring(15);
			Log.d("reply", reply);
			date.setText(reply.substring(0,reply.indexOf(",\"status")).replace("\"", ""));
			reply = reply.substring(reply.indexOf("status"));
			reply = reply.substring(9);
			Log.d("reply", reply);
			ordersta.setText(reply.substring(0,reply.indexOf(",\"order_ip")).replace("\"", ""));
			reply = reply.substring(reply.indexOf("firstname"));
			reply = reply.substring(12);
			Log.d("reply", reply);
			firstname.setText(reply.substring(0,reply.indexOf(",\"lastname")).replace("\"", ""));
			reply = reply.substring(reply.indexOf("lastname"));
			reply = reply.substring(11);
			Log.d("reply", reply);
			lastname.setText(reply.substring(0,reply.indexOf(",\"module")).replace("\"", ""));
			reply = reply.substring(reply.indexOf("invstatus"));
			reply = reply.substring(11);
			Log.d("reply", reply);
			invoicesta.setText(reply.substring(0,reply.indexOf("\"currency")).replace("\"", "").replace(",", ""));
			String hosting = reply.substring(reply.indexOf("\"hosting\":["), reply.indexOf("],\"addons"));
			String addons = reply.substring(reply.indexOf("\"addons\":["), reply.indexOf("],\"upgrades\""));
			String updates = reply.substring(reply.indexOf("\"upgrades\":["), reply.indexOf("],\"field"));
			String field = reply.substring(reply.indexOf("\"fieldupgrades\":["), reply.indexOf("],\"domains\""));
			String domains = null;
			if ( reply.contains("fraudout")) {
			domains = reply.substring(reply.indexOf("\"domains\":["), reply.indexOf("],\"fraudout\""));
			String fraud = reply.substring(reply.indexOf("\"fraudout\":"), reply.indexOf("\"call\""));
			} else {
				domains = reply.substring(reply.indexOf("\"domains\":["), reply.indexOf("\"call\""));
			}
			
			if ( domains.contains("id")) {
				
				String domains2 = null;
				domains2 = "Domain:\n";
				String temp = null;
				temp = domains.substring(domains.indexOf("\"id\""));
				temp = temp.substring(6,temp.indexOf("\",\"name\""));
				domains2 = domains2 + "[#" + temp + "]";
				temp = null;
				temp = domains.substring(domains.indexOf("\"name\""));
				temp = temp.substring(8,temp.indexOf("\",\"type\""));
				domains2 = domains2 + " " + temp + "\n";
				temp = null;
				temp = domains.substring(domains.indexOf("\"period\""));
				temp = temp.substring(10,temp.indexOf("\",\"status\""));
				domains2 = domains2 + temp + " year(s) - ";
				temp = null;
				temp = domains.substring(domains.indexOf("\"status\""));
				temp = temp.substring(10,temp.indexOf("\",\"reg"));
				domains2 = domains2 + temp + "---";
				
				
				String domains3[] = domains2.split("---");
				
				
				final ListView orderlist = (ListView) findViewById(R.id.listView1);
	        	orderlist.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, domains3));
	        	orderlist.setTextFilterEnabled(true);
	        	registerForContextMenu(orderlist);
	        	orderlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	        		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					}
	        		
				});
				
			}



		} 

	}
}
