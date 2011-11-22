package de.jojahn.campus.map;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class MapOverlayItem extends OverlayItem {
	
	private int mType;
	private String[] mExtra  = new String[2];	
	
	// Constructor
	public MapOverlayItem(int type, GeoPoint point, String title, String snippet, String[] extra) {
		super(point, title, snippet);
		mType = type;
		mExtra = extra;
	}			
	
	public int getType() {
		return mType;
	}	
	
	public String getExtraIntent() {
		return mExtra[1];
	}	
	
	public String getExtraTitel() {
		return mExtra[0];
	}	
}
