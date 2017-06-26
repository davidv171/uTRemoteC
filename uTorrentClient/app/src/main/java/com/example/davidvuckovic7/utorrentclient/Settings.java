package com.example.davidvuckovic7.utorrentclient;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by davidvuckovic7 on 21.11.2016.
 */
public class Settings extends PreferenceActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

    }


}
