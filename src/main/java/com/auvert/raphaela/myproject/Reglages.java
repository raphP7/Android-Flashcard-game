package com.auvert.raphaela.myproject;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by Raph on 23/12/2016.
 */

public class Reglages extends PreferenceFragment {

    public Reglages() {
        // Empty constructor required for fragment subclasses
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferencescreen);
    }
}
