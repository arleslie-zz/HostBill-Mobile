package com.leslienetworks.hostbill;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Calendar;

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
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class NewsInfo extends Activity {
	public boolean htaccess = main.htaccess;
	public String htuser = main.htuser;
	public String htpass = main.htpass;
	public String url = main.url;
	CookieStore cookieStore = main.cookieStore;
	public int page;
	public void onCreate(Bundle home) {
        super.onCreate(home);
        setContentView(R.layout.suppotnewsinfo);
        Bundle b = this.getIntent().getExtras();
        final String id = b.getString("ID"); 
        
        String temp = null;
        
        EditText title = (EditText) findViewById(R.id.editText1);
        EditText date = (EditText) findViewById(R.id.editText3);
        CheckBox enabled = (CheckBox) findViewById(R.id.checkBox1);
        EditText content = (EditText) findViewById(R.id.editText2);
        
        if(id == "create"){
        	title.setText("");
        	int month = Calendar.MONTH;
        	int day = Calendar.DAY_OF_MONTH;
        	int year = Calendar.YEAR;
        	date.setText(month + "/" + day + "/" + year);
        	enabled.setChecked(true);
        	content.setText("");
        } else {
        	DefaultHttpClient httpclient = new DefaultHttpClient();
    		HttpContext localContext = new BasicHttpContext();
    	    localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
    	    InputStream httpcontent=null;
    	    String reply = null;
    	    try { 
    	    	URL pullurl = new URL(url + "&call=getNewsItem&id=" + id);
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
    	    }
    		Log.d("reply", reply);
    		reply = reply.replace("},", "---");
    		
    		temp = reply.substring(reply.indexOf("\"id\"")+6, reply.indexOf("\",\"title\""));
    		temp = temp + " - " + reply.substring(reply.indexOf("\"title\"")+9, reply.indexOf("\",\"content\""));
    		temp = "#" + temp;
    		title.setText(temp);
    		temp = null;
    		temp = reply.substring(reply.indexOf("\"content\"")+11, reply.indexOf("\",\"enable\""));
    		temp = temp.replace("&nbsp;", " ");
    		temp = temp.replace("\\/", "/");
    		temp = temp.replace("\\", "\"");
    		content.setText(temp);
    		temp = null;
    		temp = reply.substring(reply.indexOf("\"enable\"")+10, reply.indexOf("\",\"date\""));
    		if ( temp == "1" ) {
    			enabled.setChecked(true);
    		}
    		temp = null;
    		temp = reply.substring(reply.indexOf("\"date\"")+8, reply.indexOf("\",\"tag_title\""));
    		date.setText(temp);
    		
    		
        }
        
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.supportnewsinfo, menu);
    	return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
    	Intent fin = new Intent();
    	switch (item.getItemId()) {
    	case R.id.save:
    		Toast.makeText(getBaseContext(), "Not Impmented Yet!", Toast.LENGTH_SHORT).show();
    		setResult(RESULT_OK, fin);
    		finish();
    		return true;
    	case R.id.delete:
    		Toast.makeText(getBaseContext(), "Not Implmented Yet!", Toast.LENGTH_SHORT).show();
    		setResult(RESULT_OK, fin);
    		finish();
    	default:
    		return false;
    	}
    }

}
