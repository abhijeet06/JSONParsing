package com.abhi.jsonparsing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class JsonUrlActivity  extends Activity {

	private static String url = "http://api.androidhive.info/contacts/";
	private String TAG = "JsonUrlActivity";
	private ArrayList<String> IdList=new ArrayList<String>();
	private ArrayList<String> NameList=new ArrayList<String>();
	private ArrayList<String> EmailList=new ArrayList<String>();
	private ArrayList<String> PhoneList=new ArrayList<String>();
	ListView list;
	SQLiteDatabase db=null;
	Cursor model=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_json);
		
		list=(ListView)findViewById(R.id.restaurants);
		db=(new SQLiteHelp(this)).getWritableDatabase();
				
		new Async().execute();
	}
	
	
	private class ListAdp extends BaseAdapter{
		
		Context context = null;
		public ListAdp(Context jsonActivity) {
			context = jsonActivity;
		}

		@Override
		public int getCount() {
			return IdList.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			View v=null;
			if (convertView == null) {
				LayoutInflater inflater=getLayoutInflater();
				v=inflater.inflate(R.layout.inflatexml, null);
			}else{
				v = convertView;
			}
			TextView t1 = (TextView) v.findViewById(R.id.text1);
			TextView t2 = (TextView) v.findViewById(R.id.text2);
			TextView t3 = (TextView) v.findViewById(R.id.text3);
	        t1.setText(NameList.get(position));
	        t2.setText(EmailList.get(position));
	        t3.setText(PhoneList.get(position));
			
			return v;
		}
		
	}
	
		
	public class Async extends AsyncTask<String, String, JSONObject> 
	{

		@Override
		protected JSONObject doInBackground(String... params) {
//			JSONParser jParser = new JSONParser();
			JSONObject jString = getJSONFromUrl(url);
			Log.e(TAG, "Inside doInBackground: "+jString);
			return jString;
		}
		
		protected void onPostExecute(JSONObject obj) {
			Log.e(TAG, "Inside onPostExecute");
			try {
				// Getting Array of Contacts
				JSONArray contacts = obj.getJSONArray("contacts");
	             
	            // looping through All Contacts
	            for(int i = 0; i < contacts.length(); i++){
	                JSONObject c = contacts.getJSONObject(i);
	                 
	                // Storing each json item in variable
	                String id = c.getString("id");
	                String name = c.getString("name");
	                String email = c.getString("email");
	                String address = c.getString("address");
	                String gender = c.getString("gender");
	                 
	                // Phone number is agin JSON Object
	                JSONObject phone = c.getJSONObject("phone");
	                String mobile = phone.getString("mobile");
	                String home = phone.getString("home");
	                String office = phone.getString("office");
	                 
	                ContentValues cv=new ContentValues();
	        		cv.put("_id", id);
	        		cv.put("name", name);
	        		cv.put("email", email);
	        		cv.put("mobile", mobile);
	        		db.insert("Contact", null, cv);
	        		
	        		Log.i(TAG, i+" :id: "+id+ ", name: "+name+ ", gender: "+gender);

	            }
	            getAllContacts();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public JSONObject getJSONFromUrl(String url) {
		InputStream is = null;
		JSONObject jObj = null;
		String json = "";
		try {
			// defaultHttpClient
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);

			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();	
			
			BufferedReader reader=new BufferedReader(new InputStreamReader(is));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			json = sb.toString();
			jObj = new JSONObject(json);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jObj;
	}
	
	public void getAllContacts() {
	    // Select All Query
	    String selectQuery = "SELECT * FROM Contact";
	    Cursor cursor = db.rawQuery(selectQuery, null);
	 
	    // looping through all rows and adding to list
	    if (cursor.moveToFirst()) {
	        do {
	            IdList.add(cursor.getString(0));
	            NameList.add(cursor.getString(1));
	            EmailList.add(cursor.getString(2));
	            PhoneList.add(cursor.getString(3));
	        } while (cursor.moveToNext());
	    }
	    
	    list.setAdapter(new ListAdp(JsonUrlActivity.this));
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		db.close();
	}

	
}
