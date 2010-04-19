package nl.terr.tabweave;

import android.content.Context;
import android.content.SharedPreferences;

public class TabWeaveConfig {
    public SharedPreferences preferences;
    public SharedPreferences.Editor preferencesEdit;
    
    public TabWeaveConfig(Context c, String sConfigName) {
        this.preferences        = c.getSharedPreferences(sConfigName, 0);
        this.preferencesEdit    = this.preferences.edit();
    }
}
