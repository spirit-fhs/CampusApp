package de.jojahn.campus.mensa;

public class MensaItem {
	/*
	private static final String[] DAY = {
		"Mo",
		"Di",
		"Mi",
		"Do",
		"Fr",
		"Sa",
		"So"
	};
	*/
	private int[] mDate;
	private String mTitle;
	private String mDescription;	
	private String mPrice;
	
	// Constructor
	public MensaItem(int[] date, String title, String description, String price) {
		mDate = date;
		mTitle = title;
		mDescription = description;
		mPrice = price;  
	}	
	
	public String getDate() {		
		String dayString = "" + mDate[2];
		String monthString = "" + mDate[1];
		
		if (mDate[2] < 10) dayString = "0" + mDate[2];
		if (mDate[1] < 10) monthString = "0" + mDate[1];
		
		String date = dayString + "." + monthString + "." + mDate[0];		
		return date;			
	}
	
	public String getTitle() {
		return mTitle;
	}
	
	public String getDescription() {
		return mDescription;
	}
	
	public String getPrice() {
		return mPrice + " €";
	}
}
