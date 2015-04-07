package com.abhi.jsonparsing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class JsonActivity extends Activity {

	String json;
	private String TAG = "JsonActivity";
	private ArrayList<String> uTubeVideoList=new ArrayList<String>();
	private ArrayList<Long> uTubeVideoDurList=new ArrayList<Long>();
	private ArrayList<String> authorList=new ArrayList<String>();
	ListView list;
	private	String title,duration,author;	
	SQLiteDatabase db=null;
	Cursor model=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_json);
		list=(ListView)findViewById(R.id.restaurants);
		db=(new SQLiteHelp(this)).getWritableDatabase();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open("pars.json")));
			StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            json = sb.toString();
            Log.e("Abhijeet","json value:::="+json);	

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			JSONObject jObj = new JSONObject(json);
			JSONObject feed=jObj.getJSONObject("feed");
			JSONArray entry=feed.getJSONArray("entry");
			
			Log.e(TAG,"Array of JSON="+entry.toString());
			for (int i = 0; i < entry.length(); i++) {
				
				JSONObject jsonObj = entry.getJSONObject(i);
				
				title=jsonObj.getJSONObject("media$group").getJSONObject("media$title").getString("$t");
				uTubeVideoList.add(title);
				
				duration =jsonObj.getJSONObject("media$group").getJSONObject("yt$duration").getString("seconds");
				uTubeVideoDurList.add(Long.parseLong(duration));
				
				author=jsonObj.getJSONArray("author").getJSONObject(0).getJSONObject("name").getString("$t");
				authorList.add(author);
				
				save(db);
				
				Log.i(TAG, i+" :Video: "+title+ ", uTubeVideoDurList: "+uTubeVideoDurList+ ", author: "+author);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		
		list.setAdapter(new ListAdp(this));
		
		
	}
	
	private class ListAdp extends BaseAdapter{
		
		Context context = null;
		public ListAdp(Context jsonActivity) {
			context = jsonActivity;
		}

		@Override
		public int getCount() {
			return uTubeVideoList.size();
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
	        t1.setText(uTubeVideoList.get(position));
	        t2.setText(uTubeVideoDurList.get(position)+"");
	        t3.setText(authorList.get(position));
			
			return v;
		}
		
	}
	
	private void save(SQLiteDatabase db) {
		ContentValues cv=new ContentValues();
		cv.put("Title", title);
		cv.put("Duration", duration);
		cv.put("Author", author);
		db.insert("Utube", "name", cv);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		db.close();
	}

	
}
