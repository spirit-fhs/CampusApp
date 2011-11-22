package de.jojahn.campus.news;

public class NewsItem {
	private static final String[] MONTH = {
		"Januar",
		"Februar",
		"März",
		"April",
		"Mai",
		"Juni",
		"Juli",
		"August",
		"September",
		"Oktober",
		"November",
		"Dezember"
	};
	
	private String mCategory;
	private int[] mDate; 
	private String mTitle;  
	private String mDescription;
	private String mUrl;
	
	// Value for sorting dates
	private int mDateValue;
	
	// Constructor
	public NewsItem(String category, int[] date, String title, String description, String url) {
		mCategory = category;
		mDate = date;		
		mTitle = title;
		mDescription = description;
		mUrl = url;
		mDateValue = ((mDate[0] * 365) + (mDate[1] * 30) + mDate[2]);
	}
	
	public String getCategory() {
		return mCategory;
	}
	
	public String getDate() {
		int dayInt = mDate[2];
		String dayString = "" + dayInt;
		if (dayInt < 10) dayString = "0" + dayInt;
		String date = dayString + ". " + MONTH[mDate[1] - 1] + " " + mDate[0];
		return date;		
	}
	
	public String getTitle() {
		return mTitle;
	}
	
	public String getDescription() {
		return mDescription;
	}
	
	public String getUrl() {
		return mUrl;
	}
	
	public String getDateValue() {
		return "" + mDateValue;
	}
}
