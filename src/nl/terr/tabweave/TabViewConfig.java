package nl.terr.tabweave;

import android.content.Context;
import android.content.SharedPreferences;

public class TabViewConfig {
    public SharedPreferences preferences;
    public SharedPreferences.Editor preferencesEdit;
    
    public TabViewConfig(Context c, String strConfigName) {
        this.preferences = c.getSharedPreferences(strConfigName, 0);
        this.preferencesEdit = this.preferences.edit();
    }
}
