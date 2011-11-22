package de.jojahn.campus.floor;

import android.graphics.RectF;

public class Room {
	private String mName;
	private RectF mRect;
	private String mDescription;	
	
	// Constructor
	public Room(String name, float[] points, String description) {
		mName = name;
		mRect = new RectF(points[0], points[1], points[2], points[3]);
		mDescription = description;
	}
	
	public String getName() {
		return mName;
	}
	
	public RectF getRect(float width, float height) {
		RectF rect = new RectF(width * (mRect.left / 100), height * (mRect.top / 100), width * (mRect.right / 100), height * (mRect.bottom / 100));
		return rect;
	}	
	
	public String getDescription() {
		return mDescription;
	}
}
