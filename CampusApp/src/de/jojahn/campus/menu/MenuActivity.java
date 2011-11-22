package de.jojahn.campus.menu;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.jojahn.campus.R;
import de.jojahn.campus.floor.FloorListActivity;
import de.jojahn.campus.map.MapsActivity;
import de.jojahn.campus.mensa.MensaActivity;
import de.jojahn.campus.news.NewsActivity;
import de.jojahn.campus.search.SearchActivity;
import de.jojahn.campus.settings.EditPreferences;
import de.jojahn.campus.tools.TutorialHelper;

public class MenuActivity extends Activity {
	
	private Context mContext;	
	
	private GridView mGridView;
	
	private ArrayList<MenuItem> mMenuItems = new ArrayList<MenuItem>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		
		loadMenuItems();  
		
		setContentView(R.layout.menu);		
						
		mContext = getApplicationContext();	
		
		mGridView = (GridView) findViewById(R.id.grid_view_menu);
		mGridView.setAdapter(new MenuAdapter(this));				
				
		// OnItemClickListener
		mGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent;
				switch (position) {
				// News
				case 0:
					intent = new Intent(mContext, NewsActivity.class);	
					startActivity(intent); 	
					break;
				// Food
				case 1:
					intent = new Intent(mContext, MensaActivity.class);	
					startActivity(intent); 					
					break;
				// Map
				case 2:
					intent = new Intent(mContext, MapsActivity.class);	
					startActivity(intent); 	
					break;
				// Floor
				case 3:
					intent = new Intent(mContext, FloorListActivity.class);	
					startActivity(intent); 					
					break;
				// Search
				case 4:
					intent = new Intent(mContext, SearchActivity.class);	
					startActivity(intent); 		
					break;
				// Library
				case 5:
					intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(Uri.parse("http://katalog.bibliothek.tu-ilmenau.de/fhs"));
					startActivity(intent);
					break;
				// Traffic
				case 6:
					intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(Uri.parse("http://reiseauskunft.bahn.de"));
					startActivity(intent);
					break;
				// Spirit
				case 7:
					intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(Uri.parse("https://spirit.fh-schmalkalden.de/index"));
					startActivity(intent);
					break;
				// Feedback
				case 8:
					intent = new Intent(Intent.ACTION_SEND);	
					intent.setType("plain/text");
					intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"jojahn@gmx.de"}); 
					intent.putExtra(Intent.EXTRA_SUBJECT, "FHS CampusApp on Android"); 
					startActivity(Intent.createChooser(intent, getString(R.string.send_mail)));		            
					break;
				default:
					// ...
					break;
				}					
			}
		}); 		
		// Tutorial       
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.tutorial_main);
        new TutorialHelper(this, frameLayout).start(getString(R.string.tutorial_main));           
	}		
	
	private void loadMenuItems() {
        mMenuItems.add(new MenuItem("Aktuelles", getResources().getDrawable(R.drawable.ic_menu_news))); 
        mMenuItems.add(new MenuItem("Mensa", getResources().getDrawable(R.drawable.ic_menu_food))); 
        mMenuItems.add(new MenuItem("Map", getResources().getDrawable(R.drawable.ic_menu_map))); 
        mMenuItems.add(new MenuItem("Gebäude", getResources().getDrawable(R.drawable.ic_menu_floor)));
        mMenuItems.add(new MenuItem("Suche", getResources().getDrawable(R.drawable.ic_menu_search)));
        mMenuItems.add(new MenuItem("Bibliothek", getResources().getDrawable(R.drawable.ic_menu_library)));        
        mMenuItems.add(new MenuItem("Verkehr", getResources().getDrawable(R.drawable.ic_menu_traffic))); 
        mMenuItems.add(new MenuItem("Spirit", getResources().getDrawable(R.drawable.ic_menu_spirit))); 
        mMenuItems.add(new MenuItem("Feedback", getResources().getDrawable(R.drawable.ic_menu_feedback)));       
    }
	
	// Inner class MenuAdapter
	public class MenuAdapter extends BaseAdapter {
		private Context mContext;
		
		// Constructor
		public MenuAdapter(Context context) {	
			mContext = context;
		}

		@Override
		public int getCount() {
			return mMenuItems.size();
		}

		@Override
		public Object getItem(int position) {
			return mMenuItems.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			MenuView menuView;
			MenuItem menuItem = mMenuItems.get(position);
			
            if (convertView == null) {
            	menuView = new MenuView(mContext, menuItem.getTitle(), menuItem.getDrawable());               
            } else {
            	menuView = (MenuView) convertView;
            	menuView.setTitle(menuItem.getTitle());		
            	menuView.setDrawable(menuItem.getDrawable());	
            }            
            return menuView;
		}		
	}
	
	// Inner class MenuItem
	private class MenuView extends LinearLayout {
		private TextView mTitle;
		private ImageView mImage;			
		
		// Constructor
		public MenuView(Context context, String title, Drawable drawable) {
			super(context);
			
			this.setOrientation(VERTICAL);
			this.setPadding(10, 23, 10, 23);
			
			mImage = new ImageView(context);
			mImage.setBackgroundDrawable(drawable);
			LayoutParams para = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			para.gravity = Gravity.CENTER;
			addView(mImage, para);
			
			mTitle = new TextView(context);
			mTitle.setText(title);
			mTitle.setTextColor(Color.argb(255, 0, 56, 107));
			mTitle.setTypeface(null, Typeface.BOLD);
			mTitle.setGravity(Gravity.CENTER);
			addView(mTitle);						
		}
		
		public void setTitle(String title) {
            mTitle.setText(title);
        }
		
		public void setDrawable(Drawable drawable) {
			mImage.setBackgroundDrawable(drawable);
        }
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		if(item.getItemId() == R.id.prefs) {
			startActivity(new Intent(this, EditPreferences.class));
			return true;
		}		
		return super.onOptionsItemSelected(item);
	}	
}
