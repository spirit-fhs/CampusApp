package de.jojahn.campus.floor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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

public class FloorListActivity extends Activity {
	private static final String TAG = "NewsActivity";	
	private static final String PACKAGE = "com.google.zxing.client.android";
	
	private static final int QR_SCAN_REQUEST = 0;
	
	private Context mContext;
	
	private ProgressDialog mProgressDialog;
	
	// ListView and NewsAdapter
	private ListView mListView;
	private FloorAdapter mFloorAdapter;
	
	// Buildings
	private ArrayList<String> mBuildings = new ArrayList<String>();
	
	// Network loading and parsing 	
	private JSONParser mJSONParser;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.floor_list);	
		
		mContext = getApplicationContext();
		
		// ListView and NewsAdapter
		mListView = (ListView) findViewById(R.id.floor_list);
		mFloorAdapter = new FloorAdapter(this);
						
		// OnItemClickListener
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// Send NewsListActivity intent with extra(category)				
				Intent intent = new Intent(mContext, FloorActivity.class);
				intent.putExtra("building", mBuildings.get(position));
				startActivity(intent); 					
			}
		}); 	      
                
        // Create and show ProgressDialog
		mProgressDialog = ProgressDialog.show(this, "", getString(R.string.progress_dialog_text), true);
        
        new DownloadNewsTask().execute("");       
	}
	
	private void prepareFloorList() {
		// Sort ArrayList by name
        Comparator<String> comperator = new Comparator<String>() {
			@Override
			public int compare(String string1, String string2) {				
				return string1.compareToIgnoreCase(string2);
			}
		};
        Collections.sort(mBuildings, comperator);        
       
        mListView.setAdapter(mFloorAdapter);
        
        // Tutorial
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.tutorial_floor);
        new TutorialHelper(this, frameLayout).start(getString(R.string.tutorial_floor));        
	}
	
	private class DownloadNewsTask extends AsyncTask<String, Void, String> {
		
		@Override
		protected String doInBackground(String... arg0) {				        
	        // Parsing network files
	        mJSONParser = new JSONParser(mContext, getString(R.string.type_floor));
	        mBuildings = mJSONParser.parseBuildings();  	       
			return null; 
		}

		@Override
		protected void onPostExecute(String result) {
			if (!mBuildings.isEmpty()) {
				prepareFloorList();								
			} else { 				
				TextView empty = (TextView) findViewById(R.id.floorlist_empty);
				empty.setVisibility(View.VISIBLE);
				Toast.makeText(mContext, getString(R.string.progress_failed), Toast.LENGTH_LONG).show();
			}
			mProgressDialog.dismiss();
			super.onPostExecute(result); 
		}	
	}
	
	private class FloorAdapter extends BaseAdapter {
		private Context mContext;
		
		// Constructor
		public FloorAdapter(Context context) {
            mContext = context;
        }
		
		@Override
		public int getCount() {			
			return mBuildings.size();
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
			textView.setText(mBuildings.get(position));
            return textView;
		}		
	} 
	
	// ButtonClickEvent: QR Code
	public void onQrCodeClicked(View view) {
		Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        intent.setPackage(PACKAGE);
        intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
        // Check weather barcode scanner is already installed
    	try {    		
            startActivityForResult(intent, QR_SCAN_REQUEST);
    	} catch (Exception e){
    		// No barcode scanner on device installed
    		Log.w(TAG, e.toString());
    		showDownloadDialog(this, getString(R.string.dia_qr_title), getString(R.string.dia_qr_message), getString(R.string.dia_qr_btn_yes), getString(R.string.dia_qr_btn_no));
    	}   	
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == QR_SCAN_REQUEST) {
        	// Handle successful scan
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                //String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                               
                // Building scan successful
                if (mBuildings.contains(contents)) {
                	Intent newIntent = new Intent(mContext, FloorActivity.class);
                	newIntent.putExtra("building", contents);
                	newIntent.putExtra("floor", "1");
    				startActivity(newIntent); 	
                // Building scan failed
                } else {
                	Toast.makeText(this, getString(R.string.qr_scan_failed), Toast.LENGTH_LONG).show();
                }
                
             // Handle cancel
            } else if (resultCode == RESULT_CANCELED) {
                
            }
        }
    }
	
	private static AlertDialog showDownloadDialog(final Activity activity, CharSequence stringTitle, CharSequence stringMessage, CharSequence stringButtonYes, CharSequence stringButtonNo) {
		AlertDialog.Builder downloadDialog = new AlertDialog.Builder(activity);
		downloadDialog.setTitle(stringTitle);
		downloadDialog.setMessage(stringMessage);
		downloadDialog.setPositiveButton(stringButtonYes, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int i) {
				Uri uri = Uri.parse("market://search?q=pname:" + PACKAGE);
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				activity.startActivity(intent);
			}
		});
		downloadDialog.setNegativeButton(stringButtonNo, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int i) {}
		});
		return downloadDialog.show();
    }
}
