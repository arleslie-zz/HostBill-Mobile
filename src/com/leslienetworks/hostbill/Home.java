package com.leslienetworks.hostbill;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class Home extends Activity {
	
	public void onCreate(Bundle home) {
	        super.onCreate(home);
	        setContentView(R.layout.home);
	        Bundle b = this.getIntent().getExtras();	        
	        final String url = b.getString("url");
	        final Boolean htaccess = b.getBoolean("htaccess", false);
	        final String htuser = b.getString("htuser");
	        final String htpass = b.getString("htpass");
	        
	        ImageButton clients = (ImageButton) findViewById(R.id.imageButton1);
	        clients.setOnClickListener(new OnClickListener() {
	        	public void onClick(View v) {
	        		Intent i = new Intent(Home.this, Clients.class);
	        		startActivity(i);
	        	}
	        });
	        
	        ImageButton support = (ImageButton) findViewById(R.id.imageButton2);
	        support.setOnClickListener(new OnClickListener() {
	        	public void onClick(View v) {
	        		Intent i = new Intent(Home.this, SupportTickets.class);
	        		startActivity(i);
	        	}
	        });
	        
	        ImageButton news = (ImageButton) findViewById(R.id.imageButton3);
	        news.setOnClickListener(new OnClickListener() {
	        	public void onClick(View v) {
	        		Bundle c = new Bundle();
	        		c.putString("URL", url);
	        		c.putBoolean("htaccess", htaccess);
	        		c.putString("htuser", htuser);
	        		c.putString("htpass", htpass);
	        		Intent i = new Intent(Home.this, News.class);
	        		i.putExtras(c);
	        		startActivity(i);
	        	}
	        });
	        
	        ImageButton Order = (ImageButton) findViewById(R.id.imageButton5);
	        Order.setOnClickListener(new OnClickListener() {
	        	public void onClick(View v) {
	        		Bundle c = new Bundle();
	        		c.putString("URL", url);
	        		c.putBoolean("htaccess", htaccess);
	        		c.putString("htuser", htuser);
	        		c.putString("htpass", htpass);
	        		Intent i = new Intent(Home.this, Order.class);
	        		i.putExtras(c);
	        		startActivity(i);
	        	}
	        });
	        
	        ImageButton invoices = (ImageButton) findViewById(R.id.imageButton4);
	        invoices.setOnClickListener(new OnClickListener() {
	        	public void onClick(View v) {
	        		Intent i = new Intent(Home.this, Invoices.class);
	        		startActivity(i);
	        	}
	        });
	 }
	public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.home, menu);
    	return true;
    }
	public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	case R.id.logout:
    		Intent in = new Intent();
    		setResult(Activity.RESULT_CANCELED,in);
    		finish();
    	default:
    		return false;
    	}
    }

}
