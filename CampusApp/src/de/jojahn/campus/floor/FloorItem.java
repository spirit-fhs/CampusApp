package de.jojahn.campus.floor;

import java.util.ArrayList;

public class FloorItem {
	private String mName;
	private String mBuilding;
	private String mImageUrl;
	private ArrayList<Room> mRooms = new ArrayList<Room>();	
	
	// Constructor
	public FloorItem(String name, String building, String imageUrl, ArrayList<Room> rooms) {
		mName = name;
		mBuilding = building;
		mImageUrl = imageUrl;
		mRooms = rooms;
	}
	
	public String getName() {
		return mName;
	}
	
	public String getBuilding() {
		return mBuilding;
	}
	
	public String getImageUrl() {
		return mImageUrl;
	}
	
	public ArrayList<Room> getRooms() {		
		return mRooms;
	}
}
