package de.jojahn.campus.settings;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import de.jojahn.campus.R;

public class EditPreferences extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.preferences);
	}
}
