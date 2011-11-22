package de.jojahn.campus.map;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

import de.jojahn.campus.R;

public class MapItemizedOverlay extends ItemizedOverlay {
	private ArrayList<MapOverlayItem> mMapOverlays = new ArrayList<MapOverlayItem>();
	private Context mContext;	
	
	public MapItemizedOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));		
	}
	
	public MapItemizedOverlay(Drawable defaultMarker, Context context) {
		super(boundCenterBottom(defaultMarker));
		mContext = context;
	}

	@Override
	protected OverlayItem createItem(int i) {
		return mMapOverlays.get(i);		
	}

	@Override
	public int size() {
		return mMapOverlays.size();		
	}
	
	public void addOverlay(MapOverlayItem overlay) {
		mMapOverlays.add(overlay);
	    populate();
	}
	
	@Override
	protected boolean onTap(int index) {
	  final MapOverlayItem item = mMapOverlays.get(index);
	  
	  Dialog dialog = new Dialog(mContext);
	  
	  dialog.setContentView(R.layout.map_dialog);
	  dialog.setTitle(item.getTitle());
	  	  	        
      // Description
	  TextView text = (TextView) dialog.findViewById(R.id.snippet);
	  text.setText(item.getSnippet());
	  
	  // Button	  
	  Button button = (Button) dialog.findViewById(R.id.btn_1);
	  button.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View arg0) {			
			resolveIntent(item.getExtraIntent());
		}
	  });	  
	 
	  if (item.getExtraIntent() != "null") {		
		  button.setVisibility(View.VISIBLE);
		  button.setText(item.getExtraTitel());
	  } else button.setVisibility(View.GONE); 
	  	  
	  dialog.show();
	  
	  return true;
	}
	
	// Resolve URL string and starting intent
	private void resolveIntent(String s) {
		// Website
		if (s.contains("http")) {
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(s));
			mContext.startActivity(i);
		// Activity
		} else {
			
		}
	}
}
