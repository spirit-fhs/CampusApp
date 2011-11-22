package de.jojahn.campus.mensa;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import de.jojahn.campus.R;
import de.jojahn.campus.tools.JSONParser;
import de.jojahn.campus.tools.SeparatedListAdapter;
import de.jojahn.campus.tools.TutorialHelper;

public class MensaActivity extends Activity{
	//private static final String TAG = "NewsActivity";
	
	private Context mContext;
	
	private ProgressDialog mProgressDialog;
	
	private ListView mListView;
	
	// MensaItems
	private ArrayList<MensaItem> mMensaItems = new ArrayList<MensaItem>();
	
	// SeparateList adapter
	private SeparatedListAdapter mAdapter;
	
	// Network loading and parsing 
	private JSONParser mJSONParser;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.mensa); 
		
		mContext = getApplicationContext();
		
		mListView = (ListView) findViewById(R.id.mensa_list);
				
		// Create and show ProgressDialog
		mProgressDialog = ProgressDialog.show(this, "", getString(R.string.progress_dialog_text), true);
	
        new DownloadMensaTask().execute("");        
	}
	
	public void prepareMensaList() {
		// Split MensaItemList into separate lists (by date)        
        ArrayList<ArrayList<MensaItem>> groupedMensaItems = new ArrayList<ArrayList<MensaItem>>();
        int group = 0;
        for (int i = 0; i < mMensaItems.size(); i++) {
        	if (i == 0) {
        		groupedMensaItems.add(new ArrayList<MensaItem>());
        		groupedMensaItems.get(group).add(mMensaItems.get(i));
        	}
        	else {
        		if (mMensaItems.get(i - 1).getDate().equals(mMensaItems.get(i).getDate())) groupedMensaItems.get(group).add(mMensaItems.get(i));
        		else {
        			group += 1;
        			groupedMensaItems.add(new ArrayList<MensaItem>());
            		groupedMensaItems.get(group).add(mMensaItems.get(i));    
        		}        		    		
        	}
        }            
  
        // Create separated ListView 
        mAdapter = new SeparatedListAdapter(this);  
        for (int i = 0; i < groupedMensaItems.size(); i++) {
        	mAdapter.addSection(groupedMensaItems.get(i).get(0).getDate(), new MensaAdapter(this, groupedMensaItems.get(i)));
		}
        
        mListView.setAdapter(mAdapter);		
		
		// Tutorial      	
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.tutorial_mensa);
        new TutorialHelper(this, frameLayout).start(getString(R.string.tutorial_mensa));
	}
	
	private class DownloadMensaTask extends AsyncTask<String, Void, String> {
	
		@Override
		protected String doInBackground(String... arg0) {			
	        // Parsing network files
	        mJSONParser = new JSONParser(mContext, getString(R.string.type_mensa));
	        mMensaItems = mJSONParser.parseMensa();      
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			if (!mMensaItems.isEmpty()) {
				prepareMensaList();						
			} else {				
				TextView empty = (TextView) findViewById(R.id.mensa_empty);
				empty.setVisibility(View.VISIBLE);
				Toast.makeText(mContext, getString(R.string.progress_failed), Toast.LENGTH_LONG).show();
			}
			mProgressDialog.dismiss();			
			super.onPostExecute(result); 
		}	
	}
	
	private class MensaAdapter extends BaseAdapter {
		private Context mContext;
		private ArrayList<MensaItem> mList;
		
		// Constructor
		public MensaAdapter(Context context, ArrayList<MensaItem> list) {
            mContext = context;
            mList = list;
        }
		
		@Override
		public int getCount() {			
			return mList.size();
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
			MensaView mensaView;
			MensaItem mensaItem = mList.get(position);
			
			if (convertView == null) {
				mensaView = new MensaView(mContext, mensaItem.getTitle(), mensaItem.getDescription(), mensaItem.getPrice());
			} else {
				mensaView = (MensaView) convertView;
				mensaView.setTitle(mensaItem.getTitle());				
				mensaView.setDescription(mensaItem.getDescription());	
				mensaView.setPrice(mensaItem.getPrice());	
			}							
			return mensaView; 
		}		
	} 
	
	// Inner class MensaView  
	private class MensaView extends LinearLayout {
		private TextView mTitle; 
		private TextView mDescription; 
		private TextView mPrice;
		
		public MensaView(Context context, String title, String description, String price) {
			super(context);
			
			this.setOrientation(VERTICAL);
			this.setPadding(15, 15, 15, 15);
			
			mTitle = new TextView(context);
			mTitle.setText(title);
			mTitle.setTextColor(Color.argb(255, 0, 56, 107));			
			mTitle.setTypeface(null, Typeface.BOLD);
			mTitle.setTextSize(17);
			addView(mTitle); 		
			
			mDescription = new TextView(context);
			mDescription.setText(description);
			mDescription.setTextSize(15);
			addView(mDescription);
			
			mPrice = new TextView(context);
			mPrice.setText(price);
			mPrice.setTextSize(13);
			mPrice.setTextColor(Color.argb(255, 125, 125, 125));
			addView(mPrice);			
		}
		
		public void setTitle(String title) {
            mTitle.setText(title);
        }	
		
		public void setDescription(String description) {
			mDescription.setText(description);
        }
		
		public void setPrice(String price) {
			mPrice.setText(price);
        }
	}
	
	public void onTutorialClicked(View view) {
		view.setVisibility(View.GONE);
	}
}
