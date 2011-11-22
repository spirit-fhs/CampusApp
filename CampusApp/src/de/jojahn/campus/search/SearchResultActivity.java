package de.jojahn.campus.search;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import de.jojahn.campus.R;
import de.jojahn.campus.floor.FloorActivity;

public class SearchResultActivity extends Activity {
	private String mName;
	private String mDescription;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.search_result);
        
        // Get View elements
        TextView name = (TextView) findViewById(R.id.search_result_name);
        TextView building = (TextView) findViewById(R.id.search_result_building);
        TextView subname = (TextView) findViewById(R.id.search_result_subname);
        TextView description = (TextView) findViewById(R.id.search_result_description);
        
        Button button = (Button) findViewById(R.id.search_result_button);
    
        // Get intent extras
        mName = getIntent().getExtras().getString("room_name");
        mDescription = getIntent().getExtras().getString("room_description");
        
        // Set View elements
        name.setText(mName.substring(0, 5));
        
        if (String.valueOf(mName.charAt(2)).equals("0")) building.setText("Haus " + mName.charAt(0) + ", " + "Erdgeschoss");  
        else building.setText("Haus " + mName.charAt(0) + ", " + mName.charAt(2) + ". Obergeschoss");  
        
        if (mName.length() > 5) {
        	subname.setText(mName.substring(8));
        } else {
        	// Hide empty subname
        	subname.setVisibility(View.GONE);
        }
        	
        
        description.setText(mDescription);
              
        // Show Button if SearchActivity was sending intent
        if (getIntent().getExtras().getBoolean("room_searched")) {
        	button.setVisibility(View.VISIBLE);
        }
    }
    
    // ButtonClickEvent: gotoFloor
    public void onFloorClicked(View view) {
    	Intent intent = new Intent(this, FloorActivity.class);
		intent.putExtra("building", "Haus " + mName.charAt(0));
		intent.putExtra("floor", Integer.parseInt(String.valueOf(mName.charAt(2))));
        startActivity(intent);
    }
}
