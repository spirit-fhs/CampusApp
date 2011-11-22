package de.jojahn.campus.search;

import java.util.ArrayList;
import java.util.Comparator;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.Toast;
import de.jojahn.campus.R;
import de.jojahn.campus.tools.JSONParser;
import de.jojahn.campus.tools.TutorialHelper;

public class SearchActivity extends Activity {
	
	private Context mContext;
	
	private ProgressDialog mProgressDialog;
	
	private AutoCompleteTextView mAutoTextView;
	
	private ArrayList<String[]> mRooms = null;	
	
	// Network loading and parsing 	
	private JSONParser mJSONParser;	
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(android.R.style.Theme);
        setContentView(R.layout.search);
        
        mContext = getApplicationContext();       
        
        mAutoTextView = (AutoCompleteTextView) findViewById(R.id.search_edit);
                   
        // Create and show ProgressDialog
		mProgressDialog = ProgressDialog.show(this, "", getString(R.string.progress_dialog_text), true);
        
        new DownloadRoomsTask().execute("");     
    }
    
    private void prepareRoomList() { 
    	
    	ArrayList<String> mNames = new ArrayList<String>();
    	for (String[] r : mRooms) {
    		mNames.add(r[0]);
    	}
    	
    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, mNames);
    	mAutoTextView.setAdapter(adapter);  
    	
    	// Sort the adapter elements by name
        adapter.sort(new Comparator<String>() {
        	public int compare(String object1, String object2) {
        		return object1.compareTo(object2); 
        	}
		});
        
        // Tutorial
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.tutorial_search);
        new TutorialHelper(this, frameLayout).start(getString(R.string.tutorial_search));        
    }
    
    private class DownloadRoomsTask extends AsyncTask<String, Void, String> {
		
		@Override
		protected String doInBackground(String... arg0) {				        
	        // Parsing network files
	        mJSONParser = new JSONParser(mContext, getString(R.string.type_floor));
	        mRooms = mJSONParser.parseRooms();  	       
			return null; 
		}

		@Override
		protected void onPostExecute(String result) {
			if (!mRooms.isEmpty()) {
				prepareRoomList();				
				mProgressDialog.dismiss();
			} else {
				mProgressDialog.dismiss();				
				Toast.makeText(mContext, getString(R.string.progress_failed), Toast.LENGTH_LONG).show();
			}
			super.onPostExecute(result); 
		}	
	}
    
    // Check whether ArrayList contains searched room
	private boolean contains(String room) {
		for (String[] string : mRooms) {
			if(string[0].equals(room)) return true;
		}
		return false;
	}
	
	// ButtonClickEvent : Search
	public void onSearchClicked(View view) {
		if(contains(mAutoTextView.getText().toString())) {
			// Searched room exists            
            Intent newIntent = new Intent(mContext, SearchResultActivity.class);
        	newIntent.putExtra("room_name", mAutoTextView.getText().toString());
        	
        	for (int i = 0; i < mRooms.size(); i++) {
        		if (mRooms.get(i)[0].equals(mAutoTextView.getText().toString())) {
        			newIntent.putExtra("room_description", mRooms.get(i)[1]);
        			break;
        		}        			 
        	}        	        	
        	newIntent.putExtra("room_searched", true);
			startActivity(newIntent); 	
		} else {
			// Searched room doesn´t exists
			Toast.makeText(this, getString(R.string.search_failed), Toast.LENGTH_SHORT).show();	
		}
	}
}
