package de.jojahn.campus.news;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import de.jojahn.campus.R;
import de.jojahn.campus.tools.JSONParser;

public class NewsListActivity extends Activity {
	//private static final String TAG = "NewsActivity";	
	
	private Context mContext;	
	
	private ProgressDialog mProgressDialog;
	
	// ListView and NewsListAdapter
	private ListView mListView;
	private NewsListAdapter mNewsListAdapter;
	
	// NewsItems
	private ArrayList<NewsItem> mNewsItems = new ArrayList<NewsItem>();
	
	// JSON parser 	
	private JSONParser mJSONParser;		
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.news_list);		
		
		mContext = getApplicationContext();
		
		// InfoText
		TextView textView = (TextView) findViewById(R.id.infotext_newsgroup);
		textView.setText(getIntent().getExtras().getString("category"));
		
		// ListView and NewsListAdapter
		mListView = (ListView) findViewById(R.id.news_list);		
		
		// OnItemClickListener
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// Send web intent if NewsItem has URL
				String url = mNewsItems.get(position).getUrl();
				if (url.contains("http")) {
					Intent i = new Intent(Intent.ACTION_VIEW);
					i.setData(Uri.parse(url));
					startActivity(i);	
				}
			}
		}); 	
		
		// Create and show ProgressDialog
		mProgressDialog = ProgressDialog.show(this, "", getString(R.string.progress_dialog_text), true);
	
        new DownloadMensaTask().execute(""); 	
	}	
	
	public void prepareNewsList() {
		// Sort ArrayList by date
        Comparator<NewsItem> comperator = new Comparator<NewsItem>() {
			@Override
			public int compare(NewsItem item1, NewsItem item2) {				
				return item1.getDateValue().compareToIgnoreCase(item2.getDateValue());
			}
		};
        Collections.sort(mNewsItems, comperator);
        Collections.reverse(mNewsItems);
        
        mNewsListAdapter = new NewsListAdapter(mContext);	
        mListView.setAdapter(mNewsListAdapter);
	}
	
	private class DownloadMensaTask extends AsyncTask<String, Void, String> {
		
		@Override
		protected String doInBackground(String... arg0) {	
	        // Parsing network files		
	        mJSONParser = new JSONParser(mContext, getString(R.string.type_news));
	        mNewsItems = mJSONParser.parseNews(getIntent().getExtras().getString("category"));   
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			if (!mNewsItems.isEmpty()) {
				prepareNewsList();		    	
				mProgressDialog.dismiss();
			} else {
				mProgressDialog.dismiss();			
				TextView empty = (TextView) findViewById(R.id.newslist_empty);
				empty.setVisibility(View.VISIBLE);
				Toast.makeText(mContext, getString(R.string.progress_failed), Toast.LENGTH_LONG).show();
			}
			super.onPostExecute(result); 
		}	
	}
	
	// Inner Class NewsListAdapter
	private class NewsListAdapter extends BaseAdapter {
		private Context mContext;
		
		// Constructor
		public NewsListAdapter(Context context) {
			mContext = context;
		}

		@Override
		public int getCount() {
			return mNewsItems.size();
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
			NewsView newsView;
			NewsItem newsItem = mNewsItems.get(position);
			
			if (convertView == null) {
				newsView = new NewsView(mContext, newsItem.getTitle(), newsItem.getDate(), newsItem.getDescription());
			} else {
				newsView = (NewsView) convertView;
				newsView.setTitle(newsItem.getTitle());
				newsView.setDate(newsItem.getDate());
				newsView.setDescription(newsItem.getDescription());
			}							
			return newsView;
		}	
	}
	
	// Inner class NewsView  
	private class NewsView extends LinearLayout {
		private TextView mDate; 
		private TextView mTitle; 
		private TextView mDescription; 
		
		public NewsView(Context context, String title, String date, String description) {
			super(context);
			
			this.setOrientation(VERTICAL);
			this.setPadding(15, 15, 15, 15);
			
			mTitle = new TextView(context);
			mTitle.setText(title);
			mTitle.setTextColor(Color.argb(255, 0, 56, 107));			
			mTitle.setTypeface(null, Typeface.BOLD);
			mTitle.setTextSize(17);
			addView(mTitle); 
			
			mDate = new TextView(context);
			mDate.setText(date);
			mDate.setTextSize(13);
			mDate.setTextColor(Color.argb(255, 125, 125, 125));
			addView(mDate);
			
			mDescription = new TextView(context);
			mDescription.setText(description);
			mDescription.setTextSize(15);
			mDescription.setTextColor(Color.argb(255, 0, 0, 0));
			addView(mDescription);
		}
		
		public void setTitle(String title) {
            mTitle.setText(title);
        }
		
		public void setDate(String date) {
			mDate.setText(date);
        }
		
		public void setDescription(String description) {
			mDescription.setText(description);
        }	
	}
}
