package com.leslienetworks.hostbill;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

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
import org.apache.http.util.ByteArrayBuffer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ClientInfo extends Activity {
	CookieStore cookieStore = main.cookieStore;
	public void onCreate(Bundle home) {
		Log.d("HBmobile", "ClientInfo");
        super.onCreate(home);
        setContentView(R.layout.clientinfo);
        Bundle b = this.getIntent().getExtras();
        clientid = b.getString("ID");
        url2 = b.getString("URL");
        htaccess = b.getBoolean("htaccess", false);
        htuser = b.getString("htuser");
        htpass = b.getString("htpass");
        TextView id = (TextView) findViewById(R.id.textView4);
        EditText first = (EditText) findViewById(R.id.editText1);
        EditText last = (EditText) findViewById(R.id.editText2);
        EditText company = (EditText) findViewById(R.id.editText3);
        EditText address = (EditText) findViewById(R.id.editText4);
        EditText city = (EditText) findViewById(R.id.editText5);
        EditText state = (EditText) findViewById(R.id.editText6);
        EditText zip = (EditText) findViewById(R.id.editText7);
        EditText country = (EditText) findViewById(R.id.editText8);
        EditText phone = (EditText) findViewById(R.id.editText10);
        EditText notes = (EditText) findViewById(R.id.editText9);
        String reply = null;
        id.setText("Client Information - #" + clientid);
		
		URL url = null;
		try {
			url = new URL(url2 + "&call=getClientDetails&id=" + clientid);
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
		
		if (reply.contains("\"success\":true")){
			reply = reply.substring(42);
			String working = reply.substring(reply.indexOf("\"firstname\":\"")+13, reply.indexOf("\",\"lastname"));
			first.setText(working);
			working = reply.substring(reply.indexOf("\"lastname\":\"")+12, reply.indexOf("\",\"companyname\""));
			last.setText(working);
			working = reply.substring(reply.indexOf("\"companyname\":\"")+15, reply.indexOf("\",\"address1\""));
			company.setText(working);
			working = reply.substring(reply.indexOf("\"address1\":\"")+12, reply.indexOf("\",\"address2\""));
			address.setText(working);
			working = reply.substring(reply.indexOf("\"city\":\"")+8, reply.indexOf("\",\"state\""));
			city.setText(working);
			working = reply.substring(reply.indexOf("\"state\":\"")+9, reply.indexOf("\",\"postcode\""));
			state.setText(working);
			working = reply.substring(reply.indexOf("\"postcode\":\"")+12, reply.indexOf("\",\"country\""));
			zip.setText(working);
			working = reply.substring(reply.indexOf("\"countryname\":\"")+15, reply.indexOf("\"},"));
			country.setText(working);
			working = reply.substring(reply.indexOf("\"phonenumber\":\"")+15, reply.indexOf("\",\"datecreated\""));
			phone.setText(working);
			working = reply.substring(reply.indexOf("\"notes\":\"")+9, reply.indexOf("\",\"language\""));
			notes.setText(working);
		} else {
			Log.d("HBmobile", reply);
			Toast.makeText(getBaseContext(), "An Error Occured", Toast.LENGTH_SHORT).show();
			setResult(RESULT_CANCELED);
			finish();
		}
	}
	
	
	private String clientid;
	private String url2;
	private Boolean htaccess;
	private String htuser;
	private String htpass;
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.clientinfo, menu);
    	return true;
    }
    
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		String reply = null;
    	switch (item.getItemId()) {
    	case R.id.delete:
    		URL url = null;
        	InputStream httpcontent=null;
        	DefaultHttpClient httpclient = new DefaultHttpClient();
        	HttpContext localContext = new BasicHttpContext();
            localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
        try{
        	url = new URL(url2 + "&call=deleteClient&id=" + clientid);
        	HttpGet httpGet = new HttpGet(url.toURI());
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
    			Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();
    		}
            if (reply.contains("true")){
    		Toast.makeText(getBaseContext(), "Client Deleted.", Toast.LENGTH_SHORT).show();
    		setResult(RESULT_OK);
    		finish();
    		return true;
            } else {
            	Toast.makeText(getBaseContext(), "An error occurred.", Toast.LENGTH_SHORT).show();
            	finish();
            	return false;
            }
    	case R.id.save:
    		EditText first = (EditText) findViewById(R.id.editText1);
            EditText last = (EditText) findViewById(R.id.editText2);
            EditText company = (EditText) findViewById(R.id.editText3);
            EditText address = (EditText) findViewById(R.id.editText4);
            EditText city = (EditText) findViewById(R.id.editText5);
            EditText state = (EditText) findViewById(R.id.editText6);
            EditText zip = (EditText) findViewById(R.id.editText7);
            EditText country = (EditText) findViewById(R.id.editText8);
            EditText phone = (EditText) findViewById(R.id.editText10);
            EditText notes = (EditText) findViewById(R.id.editText9);
            URL url2 = null;
        	InputStream httpcontent2=null;
        	DefaultHttpClient httpclient2 = new DefaultHttpClient();
        	HttpContext localContext2 = new BasicHttpContext();
            localContext2.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
    		/*try { 
    			URL myURL = new URL(url2 + "/admin/api.php?" + "&call=saveClient&firstname=" + first + "&lastname=" + last + "&company=" + company + "&address=" + address + "&city=" + city + "&state=" + state + "&zip=" + zip + "&country=" + country + "&phone=" + phone + "&notes=" + notes);
    			HttpGet httpGet = new HttpGet(url.toURI());
    			
    			BufferedInputStream bis = new BufferedInputStream(is);
    			ByteArrayBuffer baf = new ByteArrayBuffer(50);
    			int current = 0;
    			while ((current = bis.read()) != -1){
    				baf.append((byte)current);
    			}
    			reply = new String(baf.toByteArray());
    		} catch (Exception e) {
    			Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();
    		}*/
            //if (reply.contains("true")){
    		Toast.makeText(getBaseContext(), "Not Implemented Yet.", Toast.LENGTH_SHORT).show();
    		
    		setResult(RESULT_OK);
    		finish();
    		return true;
            /*} else {
            	Toast.makeText(getBaseContext(), "An error occurred.", Toast.LENGTH_SHORT).show();
            	Log.d("Debug", "Finished");
            	finish();
            	return false;
            }*/
    	/*case R.id.item1:
    		finish();*/
    	default:
    		return false;
    	}
    }
	
	/*@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	return true;
        }
        return super.onKeyDown(keyCode, event);  
    }*/
}
