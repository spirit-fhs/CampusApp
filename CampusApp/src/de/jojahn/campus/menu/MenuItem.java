package de.jojahn.campus.menu;

import android.graphics.drawable.Drawable;

public class MenuItem {
	private String mTitle;
	private Drawable mImage;	
	
	// Constructor
	public MenuItem(String title, Drawable drawable) {
		mTitle = title;
		mImage = drawable;
	}
	
	public String getTitle() {
		return mTitle;
	}
	
	public Drawable getDrawable() {
		return mImage;
	}
}
