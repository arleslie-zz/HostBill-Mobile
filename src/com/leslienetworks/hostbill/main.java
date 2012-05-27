package com.leslienetworks.hostbill;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import android.widget.EditText;

public class main extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        
        final String prefuser = preferences.getString("HB_username", null);
        final String prefpass = preferences.getString("HB_password", null);
        firsttime = preferences.getBoolean("HB_first", false);
        
        final Button button = (Button) findViewById(R.id.button1);
        final EditText username = (EditText) findViewById(R.id.editText1);
        final EditText password = (EditText) findViewById(R.id.editText2);
        final CheckBox remember = (CheckBox) findViewById(R.id.checkBox1);
        
        apiid = preferences.getString("apiid", "NONE");
        apikey = preferences.getString("apikey", "NONE");
        
        /*
        if(firsttime != true){
        	Intent i = new Intent(main.this, FirstTime.class);
        	startActivity(i);
        }*/
        
        
        if (apiid.equals("NONE") || apikey.equals("NONE") ) {
        	AlertDialog alertDialog = new AlertDialog.Builder(main.this).create();
			alertDialog.setTitle("Missing Information!");
			alertDialog.setMessage("The API ID and/or API Key has not been set.");
			alertDialog.setButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface arg0, int arg1) {
					Intent i = new Intent(main.this, Settings.class);
		    		startActivity(i);
				}
			});
        }
        
        if(prefuser != null && prefpass != null){
        	username.setText(prefuser);
        	password.setText(prefpass);
        }
        
        if(prefuser != null){
        	remember.setChecked(true);
        }
        
        remember.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
				if (remember.isChecked()){

        			AlertDialog alertDialog = new AlertDialog.Builder(main.this).create();
        			alertDialog.setTitle("Security Warning!");
        			alertDialog.setMessage("When using the remember me feature, your username and password is stored in plaintext in an Android data vault.");
        			alertDialog.setButton("Ok", new DialogInterface.OnClickListener() {
        				public void onClick(DialogInterface arg0, int arg1) {
                			preferences.edit().putString("HB_username", username.getText().toString()).putString("HB_password", password.getText().toString()).commit();
        				}
        			});
        			alertDialog.setButton2("Cancel", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							remember.setChecked(false);
						}
					});
        			alertDialog.show();
        		} else {
        			preferences.edit().remove("HB_username").remove("HB_password").commit();
        			
        		}
			}
        });
      
        button.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		apiid = preferences.getString("apiid", "NONE");
                apikey = preferences.getString("apikey", "NONE");
                htaccess = preferences.getBoolean("htcheck", false);
                htuser = preferences.getString("htuser", null);
                htpass = preferences.getString("htpass", null);
        		  final AsyncTask<String, Integer, String> progress = new progress(main.this);
        		  progress.execute("");
        	}
        });
        
   
    }
    
    public DefaultHttpClient httpClient = new DefaultHttpClient();
    public static CookieStore cookieStore = new BasicCookieStore();
    SharedPreferences preferences;
    public static boolean htaccess;
    public static String htuser;
    public static String htpass;
    public boolean firsttime;
    public String apiid;
    public String apikey;
    public static String url;
    
	
    private String login(final String username, final String password, final String url) {
        Credentials creds = null;
        if ( htaccess == true ) {
        	creds = new UsernamePasswordCredentials(htuser, htpass);
        }
        
	    if ( htaccess == true ){ httpClient.getCredentialsProvider().setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT), creds); }
	    
	    StringBuilder response = new StringBuilder();
	    HttpContext localContext = new BasicHttpContext();
	    localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
	    
	    try {
	        HttpPost post = new HttpPost();
	        URI urlLogin = new URI(url + "/admin/index.php");
	        post.setURI(urlLogin);
	        
	        List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>();
	        nameValuePairs.add(new BasicNameValuePair("action", "login"));
	        nameValuePairs.add(new BasicNameValuePair("username", username));
	        nameValuePairs.add(new BasicNameValuePair("password", password));
	        nameValuePairs.add(new BasicNameValuePair("language", "english"));
	        nameValuePairs.add(new BasicNameValuePair("rememberme", "false"));
	        post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	        
	        HttpResponse httpResponse = httpClient.execute(post, localContext);
	        int status = httpResponse.getStatusLine().getStatusCode();
	        if (status == 200) {
	            HttpEntity messageEntity = httpResponse.getEntity();
	            InputStream is = messageEntity.getContent();
	            BufferedReader br = new BufferedReader(new InputStreamReader(is));
	            String line;
	            while ((line = br.readLine()) != null) {
	                response.append(line);
	            }
	        } else if( status == 404) {
				return "notfound";
			} else if (status == 401) {
				return "auth";
			}
	    } catch (Exception e) {
	    	e.printStackTrace();
	        return e.getMessage();
	    }
	    
	    String phpsess = cookieStore.getCookies().toString();
	    try {
		    phpsess = phpsess.substring(38);
		    phpsess = phpsess.replace(phpsess.substring(phpsess.indexOf("][domain")), "");
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	return "session error";
	    }
	    Log.d("HBmobile", response.toString());
	    if ( response.toString().contains("invalid username")) {
			return "Incorrect Login";
	    }
	    
	    response.setLength(0);
	    int status;

	    try{
	        URI urlLogin = new URI(url + "/admin/api.php?&api_id="+apiid+"&api_key=" + apikey + "&call=getHostBillversion");
			HttpGet httpGet = new HttpGet();
			httpGet.setURI(urlLogin);
			HttpResponse loginrespo = httpClient.execute(httpGet);
			status = loginrespo.getStatusLine().getStatusCode();
			Log.d("HBmobile", String.valueOf(status));
			if( status == 404) {
				return "notfound";
			} else if (status == 200) {
	            HttpEntity messageEntity = loginrespo.getEntity();
	            InputStream is = messageEntity.getContent();
	            BufferedReader br = new BufferedReader(new InputStreamReader(is));
	            String line;
	            while ((line = br.readLine()) != null) {
	                response.append(line);
	            }
			} else if (status == 401) {
				return "auth";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "notfound";
		}
		Log.d("HBmobile", response.toString());
		if( response.toString().contains("success")){
			int requestCode = 0;
			Intent i= new Intent(main.this, Home.class);
			Bundle b = new Bundle();
			if ( htaccess == true ) {
				b.putBoolean("htaccess", true);
				b.putString("htuser", htuser);
				b.putString("htpass", htpass);
			} else {
				b.putBoolean("htaccess", false);
			}
			b.putString("phpsess", phpsess);
			b.putString("url", url + "/admin/api.php?&api_id="+apiid+"&api_key=" + apikey);
			main.url = url + "/admin/api.php?&api_id="+apiid+"&api_key=" + apikey;
			i.putExtras(b);
			startActivityForResult(i, requestCode);
			return "Login Complete";
		} else if( response.toString().contains("error")){
			if( response.toString().contains("ip_banned_login")){
				return "IP Ban";
			} else if ( response.toString().contains("Wrong API")){
				return "api";
			} else {
    			return "Unknown";
			}
		} else {
			return "Unknown";
		}
	    
    }
    
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.menu, menu);
    	return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	case R.id.MenuSettings:
    		Intent i = new Intent(main.this, Settings.class);
    		startActivity(i);
    		return true;
    	case R.id.item1:
    		finish();
    	default:
    		return false;
    	}
    }
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED){
        	final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        	preferences.edit().remove("HB_TOKEN").commit();
        	final Button button = (Button) findViewById(R.id.button1);
            final EditText username2 = (EditText) findViewById(R.id.editText1);
            final EditText password2 = (EditText) findViewById(R.id.editText2);
			username2.setEnabled(true);
    		password2.setEnabled(true);
    		button.setEnabled(true);
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
          progressDialog.setMessage("Logging In");
          progressDialog.setCancelable(false);
          progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

          progressDialog.show();
          
          
        }

        protected void onProgressUpdate(Integer... values) {
          progressDialog.setProgress((int) ((values[0] / (float) values[1]) * 100));
        };

        @Override
        protected void onPostExecute(String login) {
        	final String url = preferences.getString("URL", "none");
			if ( login == "auth" ) {
				final Dialog dialog = new Dialog(main.this);
				dialog.setContentView(R.layout.htaccessdialog);
				dialog.setTitle("Error - 401 - Authorization Required.");
				dialog.setCancelable(true);
				final EditText htusername = (EditText) dialog.findViewById(R.id.editText1);
				final EditText htpassword = (EditText) dialog.findViewById(R.id.editText2);
				
				Button login2 = (Button) dialog.findViewById(R.id.button1);
				login2.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						htaccess = true;
						htuser = htusername.getText().toString();
						htpass = htpassword.getText().toString();
						new progress(main.this).execute("");
						dialog.dismiss();
					}
				});
				Button cancel = (Button) dialog.findViewById(R.id.button2);
				cancel.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				dialog.show();
			} else if ( login == "Incorrect Login") {
				AlertDialog alertDialog = new AlertDialog.Builder(main.this).create();
				alertDialog.setTitle("Error");
				alertDialog.setMessage("You have entered an invalid username or password.");
				alertDialog.setButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
					}
				});
				alertDialog.show();
			} else if ( login == "notfound" ) {
				AlertDialog alertDialog = new AlertDialog.Builder(main.this).create();
				alertDialog.setTitle("Error - 404 - Not Found");
				alertDialog.setMessage("An invalid URL has been set.\nPlease reset the URL and include http(s)://");
				alertDialog.setButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						Intent i = new Intent(main.this, Settings.class);
			    		startActivity(i);
					}
				});
			} else if ( login == "IP Ban" ) {
				AlertDialog alertDialog = new AlertDialog.Builder(main.this).create();
				alertDialog.setTitle("Error");
				alertDialog.setMessage("Your IP has been banned from the HostBill Installation.");
				alertDialog.setButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
					}
				});
				alertDialog.show();
			} else if ( login == "Unknown" ) {
				AlertDialog alertDialog = new AlertDialog.Builder(main.this).create();
				alertDialog.setTitle("Unknown Error");
				alertDialog.setMessage("An unknown error has occured, please check all settings and network configuration. If you are still receiveing this error please check the HostBill installation.");
				alertDialog.setButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
					}
				});
				alertDialog.show();
			} else if ( login.contains(url.substring(8)) ) {
				AlertDialog alertDialog = new AlertDialog.Builder(main.this).create();
				alertDialog.setTitle("Connection Error");
				alertDialog.setMessage("The URL provided does not resolve.");
				alertDialog.setButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
					}
				});
				alertDialog.show();
			} else if ( login.contains("timed out")) {
				AlertDialog alertDialog = new AlertDialog.Builder(main.this).create();
				alertDialog.setTitle("Connection Error");
				alertDialog.setMessage("The connection timed out when attempting to login.");
				alertDialog.setButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
					}
				});
				alertDialog.show();
			} else if (login == "session error" ) {
				AlertDialog alertDialog = new AlertDialog.Builder(main.this).create();
				alertDialog.setTitle("Connection Error");
				alertDialog.setMessage("An error occured when attempting to process the session, please check the URL supplied.");
				alertDialog.setButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
					}
				});
				alertDialog.show();
			} else if (login.equals("api")) {
				AlertDialog alertDialog = new AlertDialog.Builder(main.this).create();
				alertDialog.setTitle("Error");
				alertDialog.setMessage("The supplied API ID and API Key is incorrect.");
				alertDialog.setButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
					}
				});
				alertDialog.show();
			} else if (login != "Login Complete") {
				AlertDialog alertDialog = new AlertDialog.Builder(main.this).create();
				alertDialog.setTitle("Unknown Error");
				alertDialog.setMessage("-"+login + "-");
				alertDialog.setButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
					}
				});
				alertDialog.show();
			}
          progressDialog.hide();
        }

		@Override
		protected String doInBackground(String... params) {
			final EditText username = (EditText) findViewById(R.id.editText1);
	        final EditText password = (EditText) findViewById(R.id.editText2);
	          
	  		final String url = preferences.getString("URL", "none");
	  		String login = null;
	  		if ( !url.equals(null) && !url.equals("http://") ) {
	  			login = login(username.getText().toString(), password.getText().toString(), url);
	  		} else {
	  			login = "404";
	  		}
			return login;
		}
      }

    
}