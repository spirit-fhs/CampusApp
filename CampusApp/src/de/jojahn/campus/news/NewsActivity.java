package de.jojahn.campus.news;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import de.jojahn.campus.R;
import de.jojahn.campus.tools.JSONParser;
import de.jojahn.campus.tools.TutorialHelper;

public class NewsActivity extends Activity {

	private Context mContext;
	
	private ProgressDialog mProgressDialog;
	
	// ListView and NewsAdapter
	private ListView mListView;
	private NewsAdapter mNewsAdapter;
	
	// News categories
	private ArrayList<String> mNewsCategories = new ArrayList<String>();
	
	// Network loading and parsing 	
	private JSONParser mJSONParser;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.news);	
		
		mContext = getApplicationContext();
		
		// ListView and NewsAdapter
		mListView = (ListView) findViewById(R.id.news_list);
		mNewsAdapter = new NewsAdapter(this);
						
		// OnItemClickListener
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// Send NewsListActivity intent with extra(category)				
				Intent intent = new Intent(mContext, NewsListActivity.class);
				intent.putExtra("category", mNewsCategories.get(position));
				startActivity(intent); 					
			}
		}); 	      
                
        // Create and show ProgressDialog
		mProgressDialog = ProgressDialog.show(this, "", getString(R.string.progress_dialog_text), true);
        
        new DownloadNewsTask().execute("");       
	}
	
	private void prepareNews() {
		// Sort ArrayList by name
        Comparator<String> comperator = new Comparator<String>() {
			@Override
			public int compare(String string1, String string2) {				
				return string1.compareToIgnoreCase(string2);
			}
		};
        Collections.sort(mNewsCategories, comperator);
        
        mNewsCategories.add(0, getString(R.string.all_news)); 
        mListView.setAdapter(mNewsAdapter);
        
        // Tutorial
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.tutorial_news);
        new TutorialHelper(this, frameLayout).start(getString(R.string.tutorial_news));        
	}
	
	private class DownloadNewsTask extends AsyncTask<String, Void, String> {
		
		@Override
		protected String doInBackground(String... arg0) {				        
	        // Parsing network files
	        mJSONParser = new JSONParser(mContext, getString(R.string.type_news));
	        mNewsCategories = mJSONParser.parseNewsCategories();  	       
			return null; 
		}

		@Override
		protected void onPostExecute(String result) {
			if (!mNewsCategories.isEmpty()) {
				prepareNews();				
				mProgressDialog.dismiss();
			} else {
				mProgressDialog.dismiss();
				TextView empty = (TextView) findViewById(R.id.news_empty);
				empty.setVisibility(View.VISIBLE);
				Toast.makeText(mContext, getString(R.string.progress_failed), Toast.LENGTH_LONG).show();
			}
			super.onPostExecute(result); 
		}	
	}
	
	private class NewsAdapter extends BaseAdapter {
		private Context mContext;
		
		// Constructor
		public NewsAdapter(Context context) {
            mContext = context;
        }
		
		@Override
		public int getCount() {			
			return mNewsCategories.size();
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
			TextView textView;
			if (convertView == null) {
				textView = (TextView) LayoutInflater.from(mContext).inflate(R.layout.news_category, parent, false);
            } else {
            	textView = (TextView) convertView; 
            }
			textView.setText(mNewsCategories.get(position));
            return textView;
		}		
	} 
}
