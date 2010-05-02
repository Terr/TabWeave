/**
 * Config class
 * 
 * Code inspired by Config class from TwitterDroid (http://github.com/fbrunel/twitterdroid/)
 */
package nl.terr.weave;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Config {

    public static final String PREFS_NAME = "TabWeavePrefs";
    
    private SharedPreferences settings;
    private Editor editor;

    public static Config getConfig(Context context) {
        return new Config(context);
    }

    private Config(Context context) {
        settings = context.getSharedPreferences(PREFS_NAME, 0);
        editor = settings.edit();
    }
    
    public void commit() {
        editor.commit();
    }

    public String getUsername() {
        return settings.getString("username", "");
    }
    
    public void setUsername(String sValue) {
        editor.putString("username", sValue);
    }
    
    public String getPassword() {
        return settings.getString("password", "");
    }
    
    public void setPassword(String sValue) {
        editor.putString("password", sValue);
    }
    
    public String getPassphrase() {
        return settings.getString("passphrase", "");
    }
    
    public void setPassphrase(String sValue) {
        editor.putString("passphrase", sValue);
    }
    
    public String getWeaveNode() {
        return settings.getString("weaveNode", "");
    }
    
    public void setWeaveNode(String sValue) {
        editor.putString("weaveNode", sValue);
    }
    
    public String getPrivateKey() {
        return settings.getString("storageKeysPrivkey", "");
    }
    
    public void setPrivateKey(String sValue) {
        editor.putString("storageKeysPrivkey", sValue);
    }
    
    public String getSymmetricKey() {
        return settings.getString("storageCryptoTabsSymmetricKey", "");
    }
    
    public void setSymmetricKey(String sValue) {
        editor.putString("storageCryptoTabsSymmetricKey", sValue);
    }
}