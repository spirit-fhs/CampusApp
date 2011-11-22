package de.jojahn.campus.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.google.android.maps.GeoPoint;

import de.jojahn.campus.R;
import de.jojahn.campus.floor.FloorItem;
import de.jojahn.campus.floor.Room;
import de.jojahn.campus.map.MapOverlayItem;
import de.jojahn.campus.mensa.MensaItem;
import de.jojahn.campus.news.NewsItem;

public class JSONParser {
	private static final String TAG = "JSONParser";
	
	// Context and expected type of the invoking activity
	private Context mContext;		
	private String mType; 
	
	// Constructor
	public JSONParser(Context context, String type) {
		mContext = context;	
		mType = type;
	}	
	
	// Convert FileInputStream to String via Buffered Reader
	private static String convertStreamToString(InputStream is) {      
       
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
 
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
	
	// Parse JSON Object to NewsItemList
	public ArrayList<NewsItem> parseNews(String category) {
		
		ArrayList<NewsItem> newsItems = new ArrayList<NewsItem>();		
		
		try {
			// Establish HttpUrlconnection
			String url = mContext.getString(R.string.url_path) + mType + ".txt"; 
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(url);
			HttpResponse response;
			
			// Receive data from network
			response = httpclient.execute(httpget);
			Log.w(TAG, response.getStatusLine().toString());				
			HttpEntity entity = response.getEntity();
				
			if (entity != null) {
				
				// Open FileInputStream
				InputStream fis = entity.getContent();				
					
				// Get JSON String
				String jsonText = convertStreamToString(fis);					
			
				// Parse JSON String
				JSONArray entries = new JSONArray(jsonText);			
				for (int i = 0; i < entries.length(); i++) {
					JSONObject post = entries.getJSONObject(i); 						
					
					// Parse only requested news (category)
					if (category.equals(mContext.getString(R.string.all_news)) || category.equals(post.getString("category"))) {				
											
						// Date (year, month, day)
						JSONArray jsonDate = post.getJSONArray("date");
						int[] date = {
								jsonDate.getInt(0), 
								jsonDate.getInt(1), 
								jsonDate.getInt(2)
						};
					
						// New NewsItem
						NewsItem newsItem = new NewsItem(post.getString("category"), date, post.getString("title"), post.getString("description"), post.getString("url"));
													
						// Add NewsItem
						newsItems.add(newsItem);	
					}
				}			
				fis.close();	
			}
		} 
		catch (Exception e) {
			// Parsing failed
			Log.e(TAG, e.toString());
			Log.w(TAG, mType + " parsing failed");			
			return newsItems;
		}		
		// Parsing successful
		Log.w(TAG, mType + " parsing successful");   
		return newsItems;
	}
	
	// Parse JSON Object to NewsCategories
	public ArrayList<String> parseNewsCategories() {
		
		ArrayList<String> newsCategories = new ArrayList<String>();		
		
		try {
			// Establish HttpUrlconnection
			String url = mContext.getString(R.string.url_path) + mType + ".txt"; 
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(url);
			HttpResponse response;
			
			// Receive data from network
			response = httpclient.execute(httpget);
			Log.w(TAG, response.getStatusLine().toString());				
			HttpEntity entity = response.getEntity();
				
			if (entity != null) {
				
				// Open FileInputStream
				InputStream fis = entity.getContent();				
					
				// Get JSON String
				String jsonText = convertStreamToString(fis);			
			
				// Parse JSON String
				JSONArray entries = new JSONArray(jsonText);			
				for (int i = 0; i < entries.length(); i++) {
					JSONObject post = entries.getJSONObject(i); 						
								
					// Add NewsCategorie
					if (!newsCategories.contains(post.getString("category"))) {
						newsCategories.add(post.getString("category"));
					}					
				}			
				fis.close();	
			}
		} 
		catch (Exception e) {
			// Parsing failed
			Log.e(TAG, e.toString());
			Log.w(TAG, mType + " parsing failed");			
			return newsCategories;
		}		
		// Parsing successful
		Log.w(TAG, mType + " parsing successful");   
		return newsCategories;
	}	
	
	// Parse JSON Object to MensaItemList
	public ArrayList<MensaItem> parseMensa() {
		
		ArrayList<MensaItem> mensaItems = new ArrayList<MensaItem>();		
		
		try {
			// Establish HttpUrlconnection
			String url = mContext.getString(R.string.url_path) + mType + ".txt"; 
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(url);
			HttpResponse response;
			
			// Receive data from network
			response = httpclient.execute(httpget);
			Log.w(TAG, response.getStatusLine().toString());				
			HttpEntity entity = response.getEntity();
				
			if (entity != null) {
				
				// Open FileInputStream
				InputStream fis = entity.getContent();				
					
				// Get JSON String
				String jsonText = convertStreamToString(fis);				
			
				// Parse JSON String
				JSONArray entries = new JSONArray(jsonText);			
				for (int i = 0; i < entries.length(); i++) {
					JSONObject post = entries.getJSONObject(i); 						
								
					// Date (year, month, day)
					JSONArray jsonDate = post.getJSONArray("date");
					int[] date = {
							jsonDate.getInt(0), 
							jsonDate.getInt(1), 
							jsonDate.getInt(2)
					};				
					/*				
					Date d = new Date(System.currentTimeMillis());
					final Calendar c = GregorianCalendar.getInstance();
					
					Log.w("Time", "" + c.get(Calendar.DAY_OF_MONTH) + "." + c.get(Calendar.MONTH) + "." + c.get(Calendar.YEAR));
					Log.w("Time", "" + c.get(Calendar.DAY_OF_WEEK));
					*/
					for (int j = 0; j < post.getJSONArray("meal").length(); j++) {
						JSONArray jsonMeal = post.getJSONArray("meal").getJSONArray(j);
						// Add MensaItem
						mensaItems.add(new MensaItem(date, jsonMeal.getString(0), jsonMeal.getString(1), jsonMeal.getString(2)));
					}													
				}			
				fis.close();	
			}
		} 
		catch (Exception e) {
			// Parsing failed
			Log.e(TAG, e.toString());
			Log.w(TAG, mType + " parsing failed");		
			return mensaItems;
		}		
		// Parsing successful
		Log.w(TAG, mType + " parsing successful");   
		return mensaItems;
	}		
	
	// Parse JSON Object to FloorItemList
	public ArrayList<FloorItem> parseFloor(String building) {
		
		ArrayList<FloorItem> floorItems = new ArrayList<FloorItem>();		
		
		try {
			// Establish HttpUrlconnection
			String url = mContext.getString(R.string.url_path) + mType + ".txt"; 
			
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(url);
			HttpResponse response;
			
			// Receive data from network
			response = httpclient.execute(httpget);
			Log.w(TAG, response.getStatusLine().toString());				
			HttpEntity entity = response.getEntity();
				
			if (entity != null) {
				
				// Open FileInputStream
				InputStream fis = entity.getContent();				
					
				// Get JSON String
				String jsonText = convertStreamToString(fis);				
			
				// Parse JSON String
				JSONArray entries = new JSONArray(jsonText);			
				for (int i = 0; i < entries.length(); i++) {
					JSONObject post = entries.getJSONObject(i); 						
					
					// Parse only requested floors (building)
					if (building.equals(post.getString("building"))) {				
					
						ArrayList<Room> rooms = new ArrayList<Room>();
						
						for (int j = 0; j < post.getJSONArray("rooms").length(); j++) {
							JSONArray jsonRoom = post.getJSONArray("rooms").getJSONArray(j); 
													
							float[] points = {
									(float) jsonRoom.getJSONArray(1).getDouble(0),
									(float) jsonRoom.getJSONArray(1).getDouble(1),
									(float) jsonRoom.getJSONArray(1).getDouble(2),
									(float) jsonRoom.getJSONArray(1).getDouble(3)
							};							
							rooms.add(new Room(jsonRoom.getString(0), points, jsonRoom.getString(2)));						
													
						}								
						floorItems.add(new FloorItem(post.getString("floor"), post.getString("building"), post.getString("image"), rooms));													
					}
				}			
				fis.close();	
			}
		} 
		catch (Exception e) {
			// Parsing failed
			Log.e(TAG, e.toString());
			Log.w(TAG, mType + " parsing failed");		
			return floorItems;
		}		
		// Parsing successful
		Log.w(TAG, mType + " parsing successful");   
		return floorItems;
	}			
	 
	// Parse JSON Object to Buildings
	public ArrayList<String> parseBuildings() {
		
		ArrayList<String> buildings = new ArrayList<String>();		
		
		try {
			// Establish HttpUrlconnection
			String url = mContext.getString(R.string.url_path) + mType + ".txt"; 
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(url);
			HttpResponse response;
			
			// Receive data from network
			response = httpclient.execute(httpget);
			Log.w(TAG, response.getStatusLine().toString());				
			HttpEntity entity = response.getEntity();
				
			if (entity != null) {
				
				// Open FileInputStream
				InputStream fis = entity.getContent();				
					
				// Get JSON String
				String jsonText = convertStreamToString(fis);			
			
				// Parse JSON String
				JSONArray entries = new JSONArray(jsonText);			
				for (int i = 0; i < entries.length(); i++) {
					JSONObject post = entries.getJSONObject(i); 						
								
					// Add NewsCategorie
					if (!buildings.contains(post.getString("building"))) {
						buildings.add(post.getString("building"));
					}					
				}			
				fis.close();	
			}
		} 
		catch (Exception e) {
			// Parsing failed
			Log.e(TAG, e.toString());
			Log.w(TAG, mType + " parsing failed");			
			return buildings;
		}		
		// Parsing successful
		Log.w(TAG, mType + " parsing successful");   
		return buildings;
	}	
	
	// Parse JSON Object to Buildings
	public ArrayList<String[]> parseRooms() {
		
		ArrayList<String[]> rooms = new ArrayList<String[]>();		
		
		try {
			// Establish HttpUrlconnection
			String url = mContext.getString(R.string.url_path) + mType + ".txt"; 
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(url);
			HttpResponse response;
			
			// Receive data from network
			response = httpclient.execute(httpget);
			Log.w(TAG, response.getStatusLine().toString());				
			HttpEntity entity = response.getEntity();
				
			if (entity != null) {
				
				// Open FileInputStream
				InputStream fis = entity.getContent();				
					
				// Get JSON String
				String jsonText = convertStreamToString(fis);			
			
				// Parse JSON String
				JSONArray entries = new JSONArray(jsonText);			
				for (int i = 0; i < entries.length(); i++) {
					JSONObject post = entries.getJSONObject(i); 						
							
					JSONArray jsonRoom = post.getJSONArray("rooms");
					
					for (int j = 0; j < jsonRoom.length(); j++) {						
						// Add Room
						if (!rooms.contains(jsonRoom.getJSONArray(j).getString(0))) {
							String[] room = new String[2];
							room[0] = jsonRoom.getJSONArray(j).getString(0);
							room[1] = jsonRoom.getJSONArray(j).getString(2);
							rooms.add(room);
						}	
					}
				}			
				fis.close();	
			}
		} 
		catch (Exception e) {
			// Parsing failed
			Log.e(TAG, e.toString());
			Log.w(TAG, mType + " parsing failed");			
			return rooms;
		}		
		// Parsing successful
		Log.w(TAG, mType + " parsing successful");   
		return rooms;
	}		
	
	// Parse JSON Object to MapItemList
	public ArrayList<MapOverlayItem> parseMap() {
		
		ArrayList<MapOverlayItem> mapOverlayItems = new ArrayList<MapOverlayItem>();		
		
		try {
			// Establish HttpUrlconnection
			String url = mContext.getString(R.string.url_path) + mType + ".txt"; 
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(url);
			HttpResponse response;
			
			// Receive data from network
			response = httpclient.execute(httpget);
			Log.w(TAG, response.getStatusLine().toString());				
			HttpEntity entity = response.getEntity();
				
			if (entity != null) {
				
				// Open FileInputStream
				InputStream fis = entity.getContent();				
					
				// Get JSON String
				String jsonText = convertStreamToString(fis);				
				
				// Parse JSON String			
				JSONArray entries = new JSONArray(jsonText);			
				for (int i = 0; i < entries.length(); i++) {
				JSONObject post = entries.getJSONObject(i);						
					
					// GeoPoint 
					JSONArray jsonGeo = post.getJSONArray("location");
					GeoPoint geoPoint = new GeoPoint((int) (jsonGeo.getDouble(0) * 1E6), (int) (jsonGeo.getDouble(1) * 1E6));
					
					// Extras
					JSONArray jsonExtra = post.getJSONArray("extra");
					String[] extra = {
						jsonExtra.getString(0),	
						jsonExtra.getString(1),	
					};				
					
					// New MapOverlayItem
					MapOverlayItem mapOverlayItem = new MapOverlayItem(post.getInt("type"), geoPoint, post.getString("name"), post.getString("description"), extra);
												
					// Add MapOverlayItem
					mapOverlayItems.add(mapOverlayItem);				
				}			
				fis.close(); 
			}	
		} 
		catch (Exception e) {
			// Parsing failed
			Log.e(TAG, e.toString());
			Log.w(TAG, mType + " parsing failed");			
			return mapOverlayItems;
		}		
		// Parsing successful
		Log.w(TAG, mType + " parsing successful");   
		return mapOverlayItems;
	}
}
