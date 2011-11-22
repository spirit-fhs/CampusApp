package de.jojahn.campus.map;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;

import de.jojahn.campus.R;
import de.jojahn.campus.tools.JSONParser;
import de.jojahn.campus.tools.TutorialHelper;

public class MapsActivity extends MapActivity {
	//private static final String TAG = "MapsActivity";
	private static final GeoPoint GEOSTART = new GeoPoint((int) (50.716501 * 1E6), (int) (10.465915 * 1E6));
	
	private Context mContext;
	
	private ProgressDialog mProgressDialog;
	
	// LinearLayout
	private LinearLayout mLinearLayout;
	
	// MapView and MapController
	private MapView mMapView = null;	
	private MapController mMapController;
	
	// ArrayList for overlays
	private List<Overlay> mMapOverlays;	
	private ArrayList<MapItemizedOverlay> mItemizedOverlays = new ArrayList<MapItemizedOverlay>();
	private ArrayList<MapOverlayItem> mMapOverlayItems = new ArrayList<MapOverlayItem>();
		
	// LocationOverlay and locating status
	private MyLocationOverlay mLocationOverlay;
	private boolean isLocating = false;	
	
	// Network loading and parsing 
	private JSONParser mJSONParser;			
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_view);
        
        mContext = getApplicationContext();
        
        mLinearLayout = (LinearLayout) findViewById(R.id.marker_menu); 
        
        mMapView = (MapView) findViewById(R.id.mapview);
        mMapController = mMapView.getController();       
        
        mMapView.setBuiltInZoomControls(true); 
        mMapController.setZoom(17);       
        mMapController.setCenter(GEOSTART);
        
        mLocationOverlay = new MyLocationOverlay(this, mMapView);
        
        // Create and show ProgressDialog
		mProgressDialog = ProgressDialog.show(this, "", getString(R.string.progress_dialog_text), true);
		
		new DownloadMapItemsTask().execute("");                      
    }
    
    public void prepareMapItems() {
    	// Define MapItem images   
        Drawable[] pics = {
        		this.getResources().getDrawable(R.drawable.ic_poi_building),
        		this.getResources().getDrawable(R.drawable.ic_poi_food),
        		this.getResources().getDrawable(R.drawable.ic_poi_sports),
        		this.getResources().getDrawable(R.drawable.ic_poi_traffic),
        		this.getResources().getDrawable(R.drawable.ic_poi_parking)
        };   
        
        mMapOverlays = mMapView.getOverlays();
        for (int i = 0; i < 5; i++) {
        	mItemizedOverlays.add(new MapItemizedOverlay(pics[i], this)); 
        }
                           
        // Assign OverlayItems to related ItemmizedOverlay (consider mapItem type)       
        for (int i = 0; i < mMapOverlayItems.size(); i++) {
        	mItemizedOverlays.get(mMapOverlayItems.get(i).getType()).addOverlay(mMapOverlayItems.get(i));
        }
        
        // Add sub-overlays to main-overlay
        mMapOverlays.add(mItemizedOverlays.get(0));   
        mMapOverlays.add(mLocationOverlay);  
        
        mMapView.invalidate();  
        
        // Tutorial    
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.tutorial_map);
        new TutorialHelper(this, frameLayout).start(getString(R.string.tutorial_map));
    }
    
    private class DownloadMapItemsTask extends AsyncTask<String, Void, String> {
			
		@Override
		protected String doInBackground(String... arg0) {		
	        // Parsing network files
	        mJSONParser = new JSONParser(mContext, getString(R.string.type_map));
	        mMapOverlayItems = mJSONParser.parseMap();      
			return null; 
		}

		@Override
		protected void onPostExecute(String result) {
			if (!mMapOverlayItems.isEmpty()) {
				prepareMapItems();     			
				mProgressDialog.dismiss();				
			} else {
				mProgressDialog.dismiss();			
				Toast.makeText(mContext, getString(R.string.progress_failed), Toast.LENGTH_LONG).show();
			}
			super.onPostExecute(result); 
		}	
	}
    
    // Start locating
    private void startLocationOverlay() {     	
        mLocationOverlay.enableMyLocation();              
        mLocationOverlay.runOnFirstFix(new Runnable() {			
        	@Override
        	public void run() {        		
        		mMapController.animateTo(mLocationOverlay.getMyLocation());        		
        		mMapController.setZoom(17);        		
        	}
        });              
        mMapView.invalidate();
        isLocating = true;
    }
    
    // Stop locating
    private void stopLocationOverlay() {    	
        mLocationOverlay.disableMyLocation();
        mMapView.invalidate();
        isLocating = false;
    }
    
    // Refresh MapItems
    public void refreshOverlay(int type) {
    	mLinearLayout.setVisibility(View.INVISIBLE);    	
    	mMapOverlays.clear();
    	mMapOverlays.add(mItemizedOverlays.get(type));
    	mMapOverlays.add(mLocationOverlay);     
    	mMapView.invalidate();
    }
    
    // ClickEvent: GeoButton
	public void onGeoClicked(View view) {    	
    	if (!mLocationOverlay.isMyLocationEnabled()) startLocationOverlay();
    	else stopLocationOverlay();
    }
    
	// ClickEvent: POIButton
    public void onPOIClicked(View view) {    	
    	if (!mLinearLayout.isShown()) mLinearLayout.setVisibility(View.VISIBLE);
    	else mLinearLayout.setVisibility(View.INVISIBLE);
    }
    
    // ClickEvent: BuildingsButton
    public void onBuildingsClicked(View view) {
    	refreshOverlay(0);    	
    }
    
    // ClickEvent: FootButton
    public void onFootClicked(View view) {
    	refreshOverlay(1);    
    }
    
    // ClickEvent: SportsButton
    public void onSportsClicked(View view) {
    	refreshOverlay(2);    
    }
    
    // ClickEvent: TrafficButton
    public void onTrafficClicked(View view) {
    	refreshOverlay(3);    
    }    
    
 // ClickEvent: ParkingButton
    public void onParkingClicked(View view) {
    	refreshOverlay(4);    
    }   

    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
        
    @Override
	protected void onPause() {
    	mLocationOverlay.disableMyLocation();
		super.onPause();
	}

	@Override
	protected void onResume() {
		if (isLocating) startLocationOverlay();
		super.onResume();
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {        
        switch (item.getItemId()) {
        case R.id.street:
        	mMapView.setSatellite(false);       
            mMapView.setStreetView(true);           
            return true;    
        case R.id.satelite:
        	mMapView.setStreetView(false);         
        	mMapView.setSatellite(true);       
            return true;   
        default:
            return super.onOptionsItemSelected(item);
        }
    }
}