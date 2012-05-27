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
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AddClient extends Activity {
	CookieStore cookieStore = main.cookieStore;
	public void onCreate(Bundle home) {
        super.onCreate(home);
        setContentView(R.layout.addclient);
        Bundle b = this.getIntent().getExtras();
		url2 = b.getString("URL");
		htaccess = b.getBoolean("htaccess");
		htuser = b.getString("htuser");
		htpass = b.getString("htpass");
        
	}
	
	
	private String url2;
	private Boolean htaccess;
	private String htuser;
	private String htpass;
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.addclient, menu);
    	return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	case R.id.save:
    		Bundle b = this.getIntent().getExtras();
            String url = b.getString("URL");
            
            EditText first = (EditText) findViewById(R.id.editText1);
            EditText last = (EditText) findViewById(R.id.editText2);
            EditText email = (EditText) findViewById(R.id.editText3);
            EditText password = (EditText) findViewById(R.id.editText4);
            EditText password2 = (EditText) findViewById(R.id.editText5);
            CheckBox send = (CheckBox) findViewById(R.id.checkBox1);
            EditText phone = (EditText) findViewById(R.id.editText6);
            EditText address1 = (EditText) findViewById(R.id.editText7);
            EditText address2 = (EditText) findViewById(R.id.editText8);
            EditText city = (EditText) findViewById(R.id.editText9);
            EditText state = (EditText) findViewById(R.id.editText10);
            EditText zip = (EditText) findViewById(R.id.editText11);
            EditText country = (EditText) findViewById(R.id.editText12);
            EditText company = (EditText) findViewById(R.id.editText13);
            String firstn = first.getText().toString();
            String lastn = last.getText().toString();
            String emailn = email.getText().toString();
            String passwordn = password.getText().toString();
            String password2n = password2.getText().toString();
            Boolean sendn = send.isChecked();
            String phonen = phone.getText().toString();
            String address1n = address1.getText().toString();
            String address2n = address2.getText().toString();
            String cityn = city.getText().toString();
            String staten = state.getText().toString();
            String zipn = zip.getText().toString();
            String countryn = country.getText().toString();
            String companyn = company.getText().toString();
            Log.d("HBMobile", passwordn + password2n);
            if(passwordn.equals(password2n)){
            	url = url + "/admin/api.php?" + "&call=addClient&firstname="+firstn+"&lastname="+lastn+"&email="+emailn+"&password="+password+"&password2="+password2;
            	if ( sendn != true ) {
            		url = url + "&notify=0";
            	} else {
            		url = url + "&notify=1";
            	}
            	if ( phonen != null ) {
            		url = url + "&phone=" + phonen;
            	}
            	if ( address1n != null) {
            		url = url + "&address1=" + address1n;
            	}
            	if ( address2n != null ) {
            		url = url + "&address2=" + address2n;
            	}
            	if ( cityn != null ) {
            		url = url + "&city=" + cityn;
            	}
            	if ( staten != null ) {
            		url = url + "&state=" + staten;
            	}
            	if ( zipn != null ) {
            		url = url + "&zip=" + zipn;
            	}
            	if ( countryn != null ) {
            		url = url + "&country=" + countryn;
            	}
            	if ( companyn != null ) {
            		url = url + "&accounttype=company&companyname=" +companyn;
            	}
            	URL url2 = null;
            	String reply = null;
        		try {
        			url2 = new URL(url);
        		} catch (MalformedURLException e1) {
        			// TODO Auto-generated catch block
        			e1.printStackTrace();
        		}
        		InputStream httpcontent=null;
        		try{
        			HttpGet httpGet = new HttpGet(url2.toURI());
        			DefaultHttpClient httpclient = new DefaultHttpClient();
        			HttpContext localContext = new BasicHttpContext();
        		    localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
        			if ( htaccess = true ) {
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
        			if ( reply.contains("error")) {
        				String a = reply.substring(reply.indexOf("\",\"error\""));
        				a = a.substring(11, a.length() - 3);
        				a = a + ".";
        				Toast.makeText(getBaseContext(), a, Toast.LENGTH_LONG).show();
        				 return false;
        			} else {
        				Toast.makeText(getBaseContext(), "Client added.", Toast.LENGTH_LONG).show();
        				finish();
    		    		return true;
        			}
        		} catch (Exception e) {
        			Toast.makeText(getBaseContext(), "Invalid URL", Toast.LENGTH_SHORT);
        			return false;
        		}
            } else {
            	Toast.makeText(getBaseContext(), "Passwords do not match!", Toast.LENGTH_LONG).show();
            	return false;
            }
           
    	case R.id.item1:
    		finish();
    	default:
    		return false;
    	}
    }
    /*
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	return true;
        }
        return super.onKeyDown(keyCode, event);  
    }*/

}
